package ir.speakup.app.domain

import ir.speakup.app.data.local.StreakDao
import ir.speakup.app.data.local.StreakDayEntity
import ir.speakup.app.data.local.StreakEntity
import ir.speakup.app.data.model.StreakDayStatus
import ir.speakup.app.data.model.StreakRules
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * زنجیره مطالعه — مهم‌ترین مکانیزم نگهداشت محصول (سند ۰۳ / F-10).
 *
 * قاعده ثبت روز عمداً ساده است: **اتمام یک فعالیت = یک روز زنجیره**.
 * دوولینگو بعد از بیش از ۶۰۰ آزمایش روی همین قابلیت به این نتیجه رسید
 * که قاعده ساده و قابل فهم بهترین نتیجه را می‌دهد (سند ۰۷ بخش ۷.۴).
 *
 * باز کردن اپ کافی نیست — این معیار صادقانه‌تر است و متریک را سالم نگه می‌دارد.
 */
@Singleton
class StreakRepository @Inject constructor(
    private val streakDao: StreakDao,
    private val time: TimeSource,
) {

    fun observe(): Flow<StreakEntity?> = streakDao.observe()

    suspend fun state(): StreakEntity = streakDao.get() ?: StreakEntity()

    /** وضعیت ۷ روز اخیر برای نوار هفتگی، از قدیمی به جدید */
    /**
     * هفته جاری شمسی، از شنبه تا جمعه.
     *
     * پیش‌تر «هفت روز گذشته» برمی‌گشت که امروز آخرینش بود — یک پنجره
     * غلتان و نه یک هفته. کاربر تقویم نمی‌دید: امروز همیشه روی یک لبه
     * می‌نشست، روزهای پیشِ‌رو اصلاً دیده نمی‌شدند، و اینکه «این هفته چقدرش
     * مانده» از صفحه درنمی‌آمد. سند ۰۳ هم همین را خواسته بود:
     * «۷ روز اخیر با تقویم شمسی، **شنبه‌محور**، RTL».
     *
     * روزهای نیامده هم برمی‌گردند تا هفته کامل دیده شود؛ تفکیکشان با
     * [DayCell.isFuture] است.
     */
    suspend fun currentWeek(): List<DayCell> {
        val today = time.today()
        // شنبه = ۰، پس با کم کردن همین عدد به ابتدای هفته می‌رسیم
        val saturday = shiftDays(today, -JalaliDate.weekDayIndex(epochOf(today)))
        val stored = streakDao.daysSince(saturday).associateBy { it.date }
        return (0..6).map { i ->
            val date = shiftDays(saturday, i)
            DayCell(
                date = date,
                jalali = JalaliDate.fromEpoch(epochOf(date)),
                weekDayIndex = i,
                status = stored[date]?.status?.let { runCatching { StreakDayStatus.valueOf(it) }.getOrNull() },
                isToday = date == today,
                isFuture = date > today,
            )
        }
    }

    /**
     * ثبت فعالیت امروز.
     *
     * @return نتیجه، برای اینکه UI بتواند لحظه رشد زنجیره را جشن بگیرد
     */
    suspend fun checkIn(): CheckInResult {
        val today = time.today()
        val s = state()

        if (s.lastActiveDate == today) return CheckInResult.AlreadyToday(s.currentLength)

        val gap = s.lastActiveDate?.let { daysBetween(it, today) }
        val o = StreakRules.decide(
            gapDays = gap,
            currentLength = s.currentLength,
            freezes = s.freezeCount,
            sinceLastRepair = s.lastRepairDate?.let { daysBetween(it, today) },
        )
        val missed = if (gap == null) 0 else (gap - 1).coerceAtLeast(0)

        // روزهای پوشش‌داده‌شده به‌عنوان فریزشده ثبت می‌شوند
        for (i in 1..o.freezesUsed) {
            streakDao.upsertDay(StreakDayEntity(shiftDays(today, -i), StreakDayStatus.FROZEN.name))
        }
        // روزی که ترمیم پوشاندش نشان خودش را می‌گیرد و نه نشان فریز: کاربر
        // باید در تقویم ببیند این روز از دست رفته بود و برگردانده شد.
        // یکسان نشان دادنشان، ارزش ترمیم را پنهان می‌کند.
        val rest = (o.freezesUsed + 1)..missed
        if (o.repaired) {
            for (i in rest) {
                streakDao.upsertDay(StreakDayEntity(shiftDays(today, -i), StreakDayStatus.REPAIRED.name))
            }
        } else if (o.broken) {
            for (i in rest) {
                streakDao.upsertDay(StreakDayEntity(shiftDays(today, -i), StreakDayStatus.MISSED.name))
            }
        }
        streakDao.upsertDay(StreakDayEntity(today, StreakDayStatus.ACTIVE.name))

        streakDao.upsert(
            s.copy(
                currentLength = o.newLength,
                longestLength = maxOf(s.longestLength, o.newLength),
                lastActiveDate = today,
                freezeCount = o.freezesLeft,
                freezesUsedTotal = s.freezesUsedTotal + o.freezesUsed,
                lastRepairDate = if (o.repaired) today else s.lastRepairDate,
                repairsUsedTotal = s.repairsUsedTotal + if (o.repaired) 1 else 0,
                updatedAt = time.nowMillis(),
            )
        )

        return when {
            o.broken -> CheckInResult.Broken(o.newLength)
            o.repaired -> CheckInResult.Repaired(o.newLength, o.repairedDays, o.earnedFreeze)
            o.freezesUsed > 0 -> CheckInResult.Frozen(o.newLength, o.freezesUsed, o.earnedFreeze)
            else -> CheckInResult.Extended(o.newLength, o.earnedFreeze)
        }
    }

    /**
     * پاداش فریز — با تلاش به‌دست می‌آید، تا سقف ۳ عدد ذخیره می‌شود.
     * @return true اگر فریز اضافه شد (انبار پر نبود)
     */
    suspend fun earnFreeze(): Boolean {
        val s = state()
        if (s.freezeCount >= StreakRules.MAX_FREEZES) return false
        streakDao.upsert(s.copy(freezeCount = s.freezeCount + 1, updatedAt = time.nowMillis()))
        return true
    }

    /** آیا ترمیم رایگان الان در دسترس است */
    suspend fun repairAvailable(): Boolean {
        val s = state()
        val last = s.lastRepairDate ?: return true
        return daysBetween(last, time.today()) >= StreakRules.REPAIR_COOLDOWN_DAYS
    }

    // ---------- کمکی ----------

    private fun epochOf(date: String): Long =
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(date)?.time ?: 0L

    private fun shiftDays(date: String, delta: Int): String {
        val f = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return f.format(java.util.Date((f.parse(date)?.time ?: 0L) + delta * 86_400_000L))
    }

    data class DayCell(
        val date: String,
        val jalali: JalaliDate,
        val weekDayIndex: Int,
        val status: StreakDayStatus?,
        val isToday: Boolean,
        /** روزی از همین هفته که هنوز نرسیده — خالی و کم‌رنگ نشان داده می‌شود */
        val isFuture: Boolean = false,
    )

    sealed interface CheckInResult {
        val length: Int
        /** فریز تازه‌ای که همین حالا جایزه گرفته شد */
        val earnedFreeze: Boolean get() = false

        data class Extended(
            override val length: Int,
            override val earnedFreeze: Boolean = false,
        ) : CheckInResult

        data class Frozen(
            override val length: Int,
            val freezesUsed: Int,
            override val earnedFreeze: Boolean = false,
        ) : CheckInResult

        /** زنجیره داشت می‌شکست و ترمیم شد */
        data class Repaired(
            override val length: Int,
            val daysRepaired: Int,
            override val earnedFreeze: Boolean = false,
        ) : CheckInResult

        data class Broken(override val length: Int) : CheckInResult
        data class AlreadyToday(override val length: Int) : CheckInResult
    }
}
