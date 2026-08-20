package ir.speakup.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * موجودیت‌های کاربر — تغییرپذیر، همگام‌شونده با سرور.
 * هر ردیف syncedAt دارد؛ ردیف‌های با syncedAt = null هنوز به سرور نرفته‌اند.
 */

/** وضعیت انجام یک فعالیت */
@Entity(tableName = "user_progress", indices = [Index("syncedAt")])
data class UserProgressEntity(
    @PrimaryKey val activityId: String,
    val status: String,                // NOT_STARTED | IN_PROGRESS | COMPLETED
    val score: Float? = null,
    /** برای «ادامه از همان‌جا» — سند ۰۷ اصلاح UX شماره ۵ */
    val lastItemIndex: Int = 0,
    val completedAt: Long? = null,
    val updatedAt: Long,
    val syncedAt: Long? = null,
)

/**
 * کارت لایتنر — الگوریتم ۵ جعبه (سند ۰۳ / F-07).
 * source نشان می‌دهد کارت از کجا آمده؛ AUTO_WRONG یعنی خودکار پس از پاسخ نادرست.
 */
@Entity(
    tableName = "leitner_cards",
    indices = [Index("dueAt"), Index("entryId"), Index("syncedAt")]
)
data class LeitnerCardEntity(
    @PrimaryKey val id: String,
    val entryId: String,               // → DictionaryEntryEntity.id
    val word: String,                  // کپی برای نمایش سریع بدون join
    val box: Int = 1,                  // ۱ تا ۵
    val dueAt: Long,
    val correctStreak: Int = 0,
    val totalReviews: Int = 0,
    val source: String,                // DICTIONARY | LESSON | READER | AUTO_WRONG
    val createdAt: Long,
    val updatedAt: Long,
    val syncedAt: Long? = null,
)

/**
 * زنجیره مطالعه (سند ۰۳ / F-10).
 * lastActiveDate بر پایه زمان سرور محاسبه می‌شود، نه ساعت دستگاه — ضدتقلب.
 */
@Entity(tableName = "streak")
data class StreakEntity(
    @PrimaryKey val id: Int = 1,       // تک‌ردیفی
    val currentLength: Int = 0,
    val longestLength: Int = 0,
    val lastActiveDate: String? = null, // yyyy-MM-dd به وقت سرور
    val freezeCount: Int = 0,           // سقف ۳
    val freezesUsedTotal: Int = 0,
    /** آخرین باری که زنجیره ترمیم شد — برای فاصله بین دو ترمیم رایگان */
    val lastRepairDate: String? = null,
    val repairsUsedTotal: Int = 0,
    val updatedAt: Long = 0,
)

@Entity(tableName = "streak_days")
data class StreakDayEntity(
    @PrimaryKey val date: String,       // yyyy-MM-dd
    val status: String,                 // ACTIVE | FROZEN | MISSED | REPAIRED
)

/**
 * تک‌تک پاسخ‌های کاربر.
 * پایه دو چیز است: متریک «نرخ تولید صحیح در مرور تأخیری» (سند ۰۷ بخش ۷.۷)
 * و تولید تمرین اختصاصی از روی خطاها.
 */
@Entity(tableName = "answer_log", indices = [Index("answeredAt"), Index("targetWord")])
data class AnswerLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: String,
    val activityId: String,
    /** true اگر تمرین تولیدی بود (تایپ/مرتب‌سازی/گفتار)، false اگر تشخیصی */
    val isProductive: Boolean,
    val userAnswer: String,
    val isCorrect: Boolean,
    val targetWord: String? = null,
    val answeredAt: Long,
    val syncedAt: Long? = null,
)

/** پیشرفت مطالعه کتاب */
@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val bookId: String,
    val locator: String,
    val percent: Float,
    val updatedAt: Long,
    val syncedAt: Long? = null,
)

/**
 * رویداد کسب امتیاز.
 *
 * XP «ارز مشترک» محصول است: هر فعالیت آن را می‌سازد، هدف روزانه و سطح
 * کاربر از آن تغذیه می‌کنند، و زنجیره کنارش معنا پیدا می‌کند. تحقیق روی
 * دولینگو نشان می‌دهد جدا بودن این سازوکارها از هم، حس «مجموعه‌ای از
 * قابلیت‌های بی‌ربط» می‌دهد؛ یکی بودن ارز، همه را یک سیستم می‌کند.
 *
 * ⚠️ رویدادمحور است و نه یک شمارنده ساده. اگر فقط جمع کل را نگه می‌داشتیم،
 * نه می‌شد هدف روزانه را حساب کرد، نه نمودار هفتگی ساخت، و نه در همگام‌سازی
 * فهمید کدام امتیاز قبلاً شمرده شده.
 */
@Entity(tableName = "xp_events", indices = [Index("dayKey"), Index("earnedAt")])
data class XpEventEntity(
    @PrimaryKey val id: String,
    val amount: Int,
    /** ACTIVITY | PERFECT | LEITNER | DAILY_GOAL */
    val reason: String,
    val activityId: String? = null,
    val earnedAt: Long,
    /** روز به وقت سرور — مبنای هدف روزانه، نه ساعت گوشی */
    val dayKey: String,
    val syncedAt: Long? = null,
)

/**
 * صف رویدادهای محصول.
 *
 * در دیتابیس صف می‌شوند و نه در حافظه: کاربری که آفلاین است یا اپ را
 * می‌بندد، رویدادش نباید گم شود — و دقیقاً همان کاربر است که در قیف
 * بیشتر از همه اهمیت دارد، چون احتمالاً همان‌جا رها می‌کند.
 */
@Entity(tableName = "event_queue")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val props: String? = null,
    /** ثانیه یونیکس */
    val at: Long,
)
