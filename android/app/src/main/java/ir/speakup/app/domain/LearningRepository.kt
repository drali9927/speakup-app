package ir.speakup.app.domain

import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.data.local.AnswerLogDao
import ir.speakup.app.data.local.AnswerLogEntity
import ir.speakup.app.data.local.DictionaryDao
import ir.speakup.app.data.local.LeitnerCardEntity
import ir.speakup.app.data.local.LeitnerDao
import ir.speakup.app.data.local.ProgressDao
import ir.speakup.app.data.local.UserProgressEntity
import ir.speakup.app.data.model.LeitnerSchedule
import ir.speakup.app.data.model.LeitnerSource
import ir.speakup.app.data.model.ActivityType
import ir.speakup.app.data.model.ProgressStatus
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * مغز حلقه یادگیری.
 *
 * مهم‌ترین رفتار اینجا **لایتنر خودکار** است (سند ۰۷ تمایز ۲):
 * کاربر لازم نیست تشخیص دهد کدام واژه را بلد نیست و دستی اضافه کند.
 * هر پاسخ نادرست، واژه هدف را خودش وارد جعبه لایتنر می‌کند.
 *
 * در رقیب این کار دستی است و عملاً انجام نمی‌شود — در حساب تست، هر سه
 * شمارنده لایتنر روی صفر مانده بود.
 */
@Singleton
class LearningRepository @Inject constructor(
    private val progressDao: ProgressDao,
    private val leitnerDao: LeitnerDao,
    private val answerLogDao: AnswerLogDao,
    private val dictionaryDao: DictionaryDao,
    private val streak: StreakRepository,
    private val contentDao: ir.speakup.app.data.local.ContentDao,
    private val xp: XpRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext
    private val appContext: android.content.Context,
) {

    /**
     * یک پاسخ را ثبت می‌کند و همه اثرات جانبی را اعمال می‌کند.
     * @return true اگر واژه‌ای به‌صورت خودکار وارد لایتنر شد
     */
    suspend fun recordAnswer(
        item: ActivityItemEntity,
        isProductive: Boolean,
        userAnswer: String,
        isCorrect: Boolean,
    ): Boolean {
        val now = System.currentTimeMillis()

        answerLogDao.insert(
            AnswerLogEntity(
                itemId = item.id,
                activityId = item.activityId,
                isProductive = isProductive,
                userAnswer = userAnswer,
                isCorrect = isCorrect,
                targetWord = item.targetWord,
                answeredAt = now,
            )
        )

        if (isCorrect) return false
        return autoAddToLeitner(item.targetWord, now)
    }

    /**
     * واژه هدف را به جعبه لایتنر اضافه می‌کند.
     * اگر کارت از قبل وجود داشته باشد، به جعبه ۱ برمی‌گردد — یعنی
     * واژه‌ای که دوباره اشتباه شده، دوباره از اول مرور می‌شود.
     */
    private suspend fun autoAddToLeitner(targetWord: String?, now: Long): Boolean {
        val word = targetWord?.trim()?.lowercase()?.takeIf { it.isNotEmpty() } ?: return false
        val entry = dictionaryDao.resolve(word) ?: return false

        val existing = leitnerDao.byEntry(entry.id)
        if (existing != null) {
            leitnerDao.upsert(
                existing.copy(
                    box = 1,
                    dueAt = now,
                    correctStreak = 0,
                    updatedAt = now,
                    syncedAt = null,
                )
            )
            return false   // کارت جدیدی اضافه نشد
        }

        leitnerDao.upsert(
            LeitnerCardEntity(
                id = UUID.randomUUID().toString(),
                entryId = entry.id,
                word = entry.word,
                box = 1,
                dueAt = now,
                source = LeitnerSource.AUTO_WRONG.name,
                createdAt = now,
                updatedAt = now,
            )
        )
        return true
    }

    /** افزودن دستی از دیکشنری یا ریدر */
    suspend fun addToLeitner(entryId: String, word: String, source: LeitnerSource): Boolean {
        if (leitnerDao.byEntry(entryId) != null) return false
        val now = System.currentTimeMillis()
        leitnerDao.upsert(
            LeitnerCardEntity(
                id = UUID.randomUUID().toString(),
                entryId = entryId, word = word, box = 1, dueAt = now,
                source = source.name, createdAt = now, updatedAt = now,
            )
        )
        return true
    }

    /** افزودن دستی با خود واژه — ریشه‌یابی و پیدا کردن مدخل بر عهده اینجاست */
    suspend fun addToLeitnerByWord(word: String): Boolean {
        val entry = dictionaryDao.resolve(word) ?: return false
        return addToLeitner(entry.id, entry.word, LeitnerSource.DICTIONARY)
    }

    /** واژه‌های موجود در جعبه — برای نمایش وضعیت دکمه افزودن */
    suspend fun leitnerWords(): Set<String> = leitnerDao.allWords().map { it.lowercase() }.toSet()

    /** نتیجه مرور یک کارت لایتنر — جعبه و زمان سررسید بعدی را به‌روز می‌کند */
    suspend fun reviewLeitnerCard(card: LeitnerCardEntity, correct: Boolean) {
        val now = System.currentTimeMillis()
        val nextBox = LeitnerSchedule.nextBox(card.box, correct)
        val dueAt = now + TimeUnit.DAYS.toMillis(LeitnerSchedule.intervalDays(nextBox).toLong())
        leitnerDao.upsert(
            card.copy(
                box = nextBox,
                dueAt = dueAt,
                correctStreak = if (correct) card.correctStreak + 1 else 0,
                totalReviews = card.totalReviews + 1,
                updatedAt = now,
                syncedAt = null,
            )
        )
    }

    /**
     * جایی که کاربر فعالیت را رها کرده بود.
     *
     * تا امروز این عدد نوشته می‌شد اما هیچ‌جا خوانده نمی‌شد، و بدتر از آن،
     * [markActivityStarted] با هر بار باز شدن صفرش می‌کرد. یعنی کسی که در
     * آیتم ۱۸ از ۲۰ بیرون می‌رفت، برمی‌گشت و از یک شروع می‌کرد — و همان
     * لحظه‌ای است که کاربر برای همیشه می‌رود.
     */
    suspend fun resumeIndexFor(activityId: String): Int {
        val p = progressDao.get(activityId) ?: return 0
        if (p.status != ProgressStatus.IN_PROGRESS.name) return 0
        return p.lastItemIndex.coerceAtLeast(0)
    }

    suspend fun markActivityStarted(activityId: String, itemIndex: Int) {
        val now = System.currentTimeMillis()
        val current = progressDao.get(activityId)
        if (current?.status == ProgressStatus.COMPLETED.name) return
        progressDao.upsert(
            UserProgressEntity(
                activityId = activityId,
                status = ProgressStatus.IN_PROGRESS.name,
                lastItemIndex = itemIndex,
                updatedAt = now,
            )
        )
    }

    /**
     * اتمام فعالیت — همین‌جا روز زنجیره هم ثبت می‌شود.
     * قاعده: اتمام یک فعالیت = یک روز زنجیره. باز کردن اپ کافی نیست.
     */
    /**
     * اتمام یک فعالیت: ثبت پیشرفت، امتیاز، و ثبت فعالیت روز در زنجیره.
     *
     * امتیاز فقط **بار اول** داده می‌شود. بدون این شرط، کاربر می‌توانست یک
     * فعالیت آسان را ده بار تمام کند و به هدف روزانه برسد؛ آن وقت امتیاز
     * دیگر نشانه یادگیری نبود.
     */
    suspend fun markActivityCompleted(activityId: String, score: Float): Completion {
        val now = System.currentTimeMillis()
        val alreadyDone = progressDao.get(activityId)?.status == ProgressStatus.COMPLETED.name

        progressDao.upsert(
            UserProgressEntity(
                activityId = activityId,
                status = ProgressStatus.COMPLETED.name,
                score = score,
                completedAt = now,
                updatedAt = now,
            )
        )

        val award = if (alreadyDone) null else {
            val type = ActivityType.fromOrNull(contentDao.activity(activityId)?.activityType)
                ?: ActivityType.FREE_TEXT
            xp.awardActivity(activityId, type, score)
        }
        val completion = Completion(streak = streak.checkIn(), xp = award)
        // ابزارک صفحه خانه باید همین حالا تازه شود. چرخه خودکارش نیم‌ساعته
        // است و کاربری که تمرین کرده و شعله را هنوز خاکستری می‌بیند،
        // نتیجه می‌گیرد ابزارک کار نمی‌کند.
        ir.speakup.app.widget.StreakWidget.refreshAll(appContext)
        return completion
    }

    data class Completion(
        val streak: StreakRepository.CheckInResult,
        /** null یعنی این فعالیت قبلاً تمام شده بود و امتیاز دوباره ندارد */
        val xp: XpRepository.Award?,
    )
}
