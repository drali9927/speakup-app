package ir.speakup.app.domain

import ir.speakup.app.data.local.DayXp
import ir.speakup.app.data.local.XpDao
import ir.speakup.app.data.local.XpEventEntity
import ir.speakup.app.data.model.ActivityType
import ir.speakup.app.data.prefs.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * کسب و مشاهده امتیاز.
 *
 * روز از [TimeSource] گرفته می‌شود و نه از ساعت گوشی: هدف روزانه هم مثل
 * زنجیره، چیزی است که با جلو بردن ساعت می‌شود دورش زد.
 */
@Singleton
class XpRepository @Inject constructor(
    private val xpDao: XpDao,
    private val prefs: AppPreferences,
    private val time: TimeSource,
) {

    /** نتیجه یک بار کسب امتیاز — برای نمایش بازخورد به کاربر */
    data class Award(
        val amount: Int,
        /** آیا با همین امتیاز، هدف امروز کامل شد */
        val goalJustReached: Boolean = false,
    )

    val totalXp: Flow<Int> = xpDao.observeTotal()
    val level: Flow<Int> = totalXp.map(XpRules::levelFor)
    val dailyGoal: Flow<Int> = prefs.dailyGoalXp

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todayXp: Flow<Int> = kotlinx.coroutines.flow.flow { emit(time.today()) }
        .flatMapLatest { xpDao.observeForDay(it) }

    fun lastDays(count: Int): Flow<List<DayXp>> =
        xpDao.observeDaily(time.daysAgo(count))

    /** امتیاز یک فعالیت تمام‌شده */
    suspend fun awardActivity(activityId: String, type: ActivityType, score: Float): Award =
        award(XpRules.forActivity(type, score), reason = "ACTIVITY", activityId = activityId)

    /** امتیاز یک نوبت مرور لایتنر */
    suspend fun awardLeitnerSession(): Award =
        award(XpRules.LEITNER_SESSION, reason = "LEITNER")

    private suspend fun award(amount: Int, reason: String, activityId: String? = null): Award {
        if (amount <= 0) return Award(0)
        val day = time.today()
        val before = xpDao.forDay(day)

        xpDao.insert(
            XpEventEntity(
                id = UUID.randomUUID().toString(),
                amount = amount,
                reason = reason,
                activityId = activityId,
                earnedAt = time.nowMillis(),
                dayKey = day,
            )
        )

        // پاداش هدف روزانه فقط یک بار در روز، و فقط در همان لحظه‌ای که
        // از خط عبور می‌کنیم — نه هر بار که کاربر بعدش امتیاز می‌گیرد.
        val goal = prefs.dailyGoalXp.first()
        val after = before + amount
        val justReached = before < goal && after >= goal && xpDao.goalBonusCount(day) == 0
        if (justReached) {
            xpDao.insert(
                XpEventEntity(
                    id = UUID.randomUUID().toString(),
                    amount = XpRules.DAILY_GOAL_BONUS,
                    reason = "DAILY_GOAL",
                    earnedAt = time.nowMillis(),
                    dayKey = day,
                )
            )
        }
        return Award(amount, goalJustReached = justReached)
    }

    suspend fun setDailyGoal(xp: Int) = prefs.setDailyGoalXp(xp)
}
