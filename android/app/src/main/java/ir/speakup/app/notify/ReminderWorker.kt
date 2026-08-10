package ir.speakup.app.notify

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ir.speakup.app.data.local.LeitnerDao
import ir.speakup.app.data.local.XpDao
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.domain.StreakRepository
import ir.speakup.app.domain.TimeSource
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * تصمیم روزانه درباره یادآور.
 *
 * کار اصلی این کلاس **نفرستادن** است، نه فرستادن. یادآوری که در لحظه
 * غلط برسد، از نبودنش بدتر است: کاربر اعلان اپ را می‌بندد و آن‌وقت هیچ
 * راه دیگری برای برگرداندنش نداریم.
 *
 * چهار قفل پشت سر هم:
 *
 * ۱. **کاربر تمرین امروز را کرده؟** اگر به هدف رسیده، امروز ساکتیم. این
 *    مهم‌ترین قفل است — تنبیه کردنِ کاربرِ خوب سریع‌ترین راه از دست دادنش است.
 * ۲. **ساعت مناسب است؟** بیرون از ۸ صبح تا ۲۲، هیچ.
 * ۳. **امروز فرستاده‌ایم؟** حداکثر یکی در روز.
 * ۴. **چقدر دور شده؟** هرچه دورتر، کم‌رفت‌وآمدتر — بعد از یک هفته هر سه
 *    روز یک بار و بعد از یک ماه هفته‌ای یک بار. اصرارِ روزانه به کاربرِ
 *    رفته، فقط حذف نصب را جلو می‌اندازد.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val streak: StreakRepository,
    private val xpDao: XpDao,
    private val leitnerDao: LeitnerDao,
    private val prefs: AppPreferences,
    private val time: TimeSource,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!prefs.remindersEnabled.first()) return Result.success()
        if (!Notifications.allowed(applicationContext)) return Result.success()

        val today = time.today()
        // قفل ۳ — یکی در روز
        if (prefs.lastReminderDay.first() == today) return Result.success()

        // قفل ۲ — ساعت آرام
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val target = prefs.reminderHour.first()
        if (hour < QUIET_START || hour >= QUIET_END) return Result.success()
        if (hour < target) return Result.success()

        val s = snapshot()

        // قفل ۱ — کارِ امروز انجام شده
        if (s.goalReached) return Result.success()

        // قفل ۴ — فاصله‌گذاری بر پایه دوری
        if (!dueForCadence(s.daysAway, today)) return Result.success()

        val reminder = ReminderCopy.pick(s, variant = today.hashCode().and(0xFF))
            ?: return Result.success()

        Notifications.show(applicationContext, reminder)
        prefs.setLastReminderDay(today)
        Log.i(TAG, "reminder sent: ${reminder.title} (daysAway=${s.daysAway})")
        return Result.success()
    }

    private suspend fun snapshot(): ReminderState {
        val st = streak.state()
        val now = time.nowMillis()
        return ReminderState(
            streak = st.currentLength,
            longestStreak = st.longestLength,
            freezes = st.freezeCount,
            todayXp = xpDao.forDay(time.today()),
            goalXp = prefs.dailyGoalXp.first(),
            dueCount = leitnerDao.observeDueCount(now).first(),
            daysAway = daysBetween(st.lastActiveDate, time.today()),
        )
    }

    /**
     * هرچه کاربر دورتر شده، فاصله یادآورها بیشتر.
     *
     * روز تاریخ به‌عنوان شمارنده استفاده می‌شود و نه یک شمارنده ذخیره‌شده،
     * تا اگر کاربر اپ را پاک و دوباره نصب کند هم الگو به هم نریزد.
     */
    private fun dueForCadence(daysAway: Int, today: String): Boolean {
        val n = today.replace("-", "").toLongOrNull() ?: return true
        return when {
            daysAway <= 7 -> true              // هفته اول: هر روز
            daysAway <= 30 -> n % 3 == 0L      // تا یک ماه: هر سه روز
            else -> n % 7 == 0L                // بعد از آن: هفته‌ای یک بار
        }
    }

    private fun daysBetween(from: String?, to: String): Int {
        if (from == null) return 0
        val f = runCatching { epoch(from) }.getOrNull() ?: return 0
        val t = runCatching { epoch(to) }.getOrNull() ?: return 0
        return ((t - f) / 86_400_000L).toInt().coerceAtLeast(0)
    }

    private fun epoch(date: String): Long {
        val (y, m, d) = date.split("-").map(String::toInt)
        return Calendar.getInstance().apply {
            clear(); set(y, m - 1, d)
        }.timeInMillis
    }

    companion object {
        private const val TAG = "ReminderWorker"
        private const val WORK = "speakup-reminder"

        /** ساعت‌هایی که هرگز اعلان نمی‌فرستیم */
        private const val QUIET_START = 8
        private const val QUIET_END = 22

        /**
         * بررسی هر سه ساعت و نه یک بار در روز.
         *
         * WorkManager زمان دقیق را تضمین نمی‌کند و یک کارِ روزانه ممکن است
         * ساعت‌ها دیر اجرا شود — یعنی درست وقتی کاربر خواب است. بررسی مکرر
         * با قفل «یکی در روز» همان نتیجه را با اطمینان بیشتر می‌دهد.
         */
        fun schedule(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK,
                ExistingPeriodicWorkPolicy.UPDATE,
                PeriodicWorkRequestBuilder<ReminderWorker>(3, TimeUnit.HOURS).build(),
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK)
        }
    }
}
