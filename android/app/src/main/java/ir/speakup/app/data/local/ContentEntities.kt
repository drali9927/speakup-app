package ir.speakup.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * موجودیت‌های محتوا — فقط‌خواندنی از دید کاربر.
 * از بسته محتوای سرور پر می‌شوند و هرگز روی دستگاه تغییر نمی‌کنند.
 */

/** سطح CEFR — تاکسونومی واحد در کل محصول (سند ۰۲ بخش ۲.۳) */
@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey val code: String,      // starter, A1, A2, B1, B2, C1
    val titleFa: String,
    val titleEn: String,
    val sortOrder: Int,
)

@Entity(
    tableName = "lessons",
    foreignKeys = [ForeignKey(
        entity = LevelEntity::class,
        parentColumns = ["code"], childColumns = ["levelCode"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("levelCode")]
)
data class LessonEntity(
    @PrimaryKey val id: String,        // A1-L01
    val levelCode: String,
    val number: Int,
    val titleEn: String,               // "He is a manager."
    val grammarTopicFa: String,
    val themeFa: String,
    val isFree: Boolean,
    val estimatedMinutes: Int,
    val colorHex: String,
)

@Entity(
    tableName = "sections",
    foreignKeys = [ForeignKey(
        entity = LessonEntity::class,
        parentColumns = ["id"], childColumns = ["lessonId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("lessonId")]
)
data class SectionEntity(
    @PrimaryKey val id: String,        // A1-L01-S1
    val lessonId: String,
    val type: String,                  // SectionType.name
    val sortOrder: Int,
    val estimatedMinutes: Int,
)

@Entity(
    tableName = "activities",
    foreignKeys = [ForeignKey(
        entity = SectionEntity::class,
        parentColumns = ["id"], childColumns = ["sectionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sectionId")]
)
data class ActivityEntity(
    @PrimaryKey val id: String,        // A1-L01-S3-A2
    val sectionId: String,
    val title: String,                 // "Grammar 1" / "Exercise 3"
    /**
     * هیچ‌جای اپ نمایش داده نمی‌شود.
     *
     * از JSON خوانده و در پایگاه داده ذخیره می‌شود، اما هیچ صفحه‌ای آن را
     * نمی‌خواند — خودِ صفحه‌ها متن واقعی‌شان را دارند (عنوان گرامر، توضیح،
     * مثال‌ها) و این جمله‌ها قالبی‌اند: «بعد از گرامر، تمرین‌هایش را انجام
     * بده.» روی ۸۴۰ ردیف تکرار شده. نمایششان فقط شلوغی اضافه می‌کرد.
     *
     * اینجا نوشته شد چون یک‌بار برای یکدست‌کردن لحن، همین متن‌ها در خط
     * تولید محتوا ویرایش شدند — کاری که هیچ اثری روی صفحه نداشت. اگر
     * روزی خواستی متنی به کاربر نشان دهی، hintFa همان است که رندر می‌شود.
     */
    val descriptionFa: String,
    val activityType: String,          // ActivityType.name
    val sortOrder: Int,
)

/**
 * یک آیتم = یک کارت واژه، یک کارت گرامر، یا یک سوال.
 * targetWord ستون کلیدی برای «لایتنر خودکار» است (سند ۰۷ تمایز ۲):
 * پاسخ نادرست ← این واژه خودکار وارد جعبه لایتنر می‌شود.
 */
@Entity(
    tableName = "activity_items",
    foreignKeys = [ForeignKey(
        entity = ActivityEntity::class,
        parentColumns = ["id"], childColumns = ["activityId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("activityId")]
)
data class ActivityItemEntity(
    @PrimaryKey val id: String,
    val activityId: String,
    val sortOrder: Int,
    val prompt: String,
    val promptFa: String? = null,
    /** جمله مثال — فقط نمایشی، هرگز با پاسخ کاربر مقایسه نمی‌شود */
    val exampleEn: String? = null,
    val exampleFa: String? = null,
    val correctAnswer: String? = null,
    /** پاسخ‌های درست جایگزین، جداشده با | — کاربر بابت "He's" به‌جای "He is" جریمه نمی‌شود */
    val alternatives: String? = null,
    /** گزینه‌های چندگزینه‌ای، جداشده با | */
    val options: String? = null,
    val hintFa: String? = null,
    val voice: String? = null,
    val imageFile: String? = null,     // a1_l01_w01_hello.png
    val ttsText: String? = null,       // متن ورودی موتور صوت
    val targetWord: String? = null,    // برای لایتنر خودکار

    // --- بررسی درک، فقط برای کارت‌های گرامر ---
    // پرسشی که بلافاصله بعد از توضیح می‌آید. رقیب گرامر را با سه دقیقه
    // روایت صوتی آموزش می‌دهد و کاربر تا آخر بخش هیچ کاری نمی‌کند.
    val checkPrompt: String? = null,
    val checkAnswer: String? = null,
    /** گزینه‌ها، جداشده با | */
    val checkOptions: String? = null,
    /**
     * نکته هدفمند هر گزینه غلط، به شکل «گزینه::نکته» و جداشده با ||.
     *
     * این قلب بخش آموزش است: پژوهش دولینگو نشان داده توضیحِ کوتاهِ بعد از
     * اشتباهِ مشخص، خطای بعدی را کم می‌کند. «اشتباه است» چنین اثری ندارد.
     */
    val checkTips: String? = null,
) {
    fun alternativesList(): List<String> = alternatives?.split('|')?.map(String::trim)?.filter(String::isNotEmpty).orEmpty()
    fun optionsList(): List<String> = options.splitOptions()

    fun checkOptionsList(): List<String> = checkOptions.splitOptions()

    /** نکته مربوط به گزینه‌ای که کاربر زد، یا null اگر نکته‌ای نبود */
    fun tipFor(option: String): String? = checkTips
        ?.split("||")
        ?.mapNotNull { part ->
            val i = part.indexOf("::")
            if (i < 0) null else part.take(i).trim() to part.drop(i + 2).trim()
        }
        ?.firstOrNull { it.first == option }
        ?.second
        ?.takeIf { it.isNotBlank() }

    val hasCheck: Boolean
        get() = !checkPrompt.isNullOrBlank() && !checkAnswer.isNullOrBlank() &&
            checkOptionsList().size >= 2
}

/** مدخل دیکشنری (سند ۰۳ / F-05) */
@Entity(
    tableName = "dictionary_entries",
    indices = [Index("word"), Index("lemma")]
)
data class DictionaryEntryEntity(
    @PrimaryKey val id: String,
    val word: String,
    val lemma: String,
    val pos: String,
    val ipaUk: String?,
    val ipaUs: String?,
    val definitionEn: String?,
    val translationFa: String,
    val exampleEn: String?,
    val exampleFa: String?,
    val frequencyRank: Int = 0,
)

/**
 * نگاشت صورت صرف‌شده به ریشه — پشتیبان lemmatization در دیکشنری هوشمند.
 * مثال: went → go ، children → child ، don't → do
 */
@Entity(tableName = "word_forms")
data class WordFormEntity(
    @PrimaryKey val form: String,
    val lemma: String,
)

/**
 * رشته گزینه‌ها را می‌شکند و گزینه‌های **یکسانِ دیداری** را حذف می‌کند.
 *
 * چرا محافظ لازم است: دو گزینه ممکن است رشته‌شان متفاوت باشد اما روی
 * صفحه دقیقاً یکی دیده شوند — نیم‌فاصله، «ي» و «ك» عربی به‌جای فارسی،
 * یا فاصله تکراری. کاربر آن وقت دو دکمه یکسان می‌بیند و هر کدام را
 * بزند ممکن است غلط حساب شود.
 *
 * مقایسه روی شکل نرمال‌شده انجام می‌شود، اما آنچه نمایش داده می‌شود
 * همان نسخه اصلی است؛ متن گزینه نباید تغییر کند.
 */
private fun String?.splitOptions(): List<String> {
    val seen = mutableSetOf<String>()
    return this?.split('|')
        ?.map(String::trim)
        ?.filter(String::isNotEmpty)
        ?.filter { seen.add(it.normaliseForCompare()) }
        .orEmpty()
}

/**
 * کلید یکتایی گزینه‌ها. گزینه‌هایی که در زمان اجرا ساخته می‌شوند هم باید
 * با همین معیار یکتا شوند، نه با برابریِ رشته — وگرنه محافظ فقط نیمی از
 * مسیرها را می‌گیرد.
 */
internal fun String.optionKey(): String = normaliseForCompare().lowercase()

/**
 * کلید مقایسه معنی، **بدون** توضیح داخل پرانتز.
 *
 * چرا جدا از [optionKey]: در آزمون معنیِ واژه، «سلام» و «سلام (خودمانی)»
 * دو رشته متفاوت‌اند ولی برای زبان‌آموز یک چیزند. وقتی هر دو در گزینه‌ها
 * بیایند، آزمون دیگر دانش را نمی‌سنجد — کاربر باید بین دو پاسخ درست یکی
 * را حدس بزند. این دقیقاً در درس اول سطح A1 اتفاق می‌افتاد، یعنی نخستین
 * آزمونی که هر کاربر در عمرش با این اپ می‌بیند.
 *
 * پرانتز فقط در **مقایسه** حذف می‌شود و نه در نمایش؛ وقتی گزینه تنها
 * نشان داده می‌شود، «(خودمانی)» اطلاعات مفیدی است.
 */
internal fun String.glossKey(): String =
    replace(Regex("[(（][^)）]*[)）]"), " ")
        .normaliseForCompare()
        .trim(' ', '،', ',', '-', '–')

private fun String.normaliseForCompare(): String = this
    .replace('\u200c', ' ')   // نیم‌فاصله
    .replace('\u064a', '\u06cc')  // ي عربی → ی فارسی
    .replace('\u0643', '\u06a9')  // ك عربی → ک فارسی
    .replace(Regex("\\s+"), " ")
    .trim()
    .lowercase()
