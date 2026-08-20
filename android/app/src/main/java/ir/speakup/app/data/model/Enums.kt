package ir.speakup.app.data.model

/** شش بخش ثابت هر درس — سند ۰۳ / F-01 */
enum class SectionType(val titleEn: String, val titleFa: String) {
    VOCABULARY("Vocabulary", "واژگان"),
    CONVERSATION("Conversation", "مکالمه"),
    GRAMMAR("Grammar", "گرامر"),
    SPEAKING("Speaking", "گفتار"),
    LISTENING("Listening", "شنیدار"),
    VOCAB_REVIEW("Vocabulary Review", "مرور واژگان"),

    /**
     * داستانک — متن کوتاه و پیوسته، بعد پرسش‌های درک مطلب.
     *
     * تنها بخشی است که کاربر انگلیسی را **پیوسته** می‌خواند و نه
     * جمله‌جمله. همه بخش‌های دیگر روی یک جمله یا یک واژه کار می‌کنند؛
     * این‌جا باید معنی را از دلِ متن بیرون بکشد. فقط روی دروسی می‌آید
     * که واژه کافی پشتشان جمع شده باشد.
     */
    STORY("Story", "داستانک"),
}

/**
 * انواع فعالیت.
 * isProductive تعیین می‌کند تمرین «تولیدی» است یا «تشخیصی».
 * قاعده سند ۰۷ تمایز ۱: حداقل ۶۰٪ تمرین‌های هر درس باید تولیدی باشند.
 */
enum class ActivityType(val isProductive: Boolean) {
    /** کارت آموزشی — تمرین نیست، در محاسبه نسبت شمرده نمی‌شود */
    TEACHING(false),
    FLASHCARD(false),
    MULTIPLE_CHOICE(false),
    MATCHING(false),
    /** مکالمه دو نفره — صفحه گفتگو، نه تمرین */
    DIALOGUE(false),
    /** بازی جفت‌یابی واژه و معنی — سریع، بدون تایپ */
    WORD_MATCH(false),
    /** متن داستانک — خواندنی، سوالی ندارد؛ پرسش‌ها فعالیت جدا هستند */
    STORY(false),

    FILL_BLANK(true),
    FREE_TEXT(true),
    REORDER(true),
    TRANSLATE_TO_EN(true),
    SPEAKING(true),
    LISTENING(true);

    /**
     * آیا این نوع فعالیت امتیاز دارد.
     * کارت آموزشی و کارت واژه سوالی نمی‌پرسند، پس «۰ از ۱ پاسخ درست»
     * برایشان بی‌معنا و دلسردکننده است.
     */
    val isScored: Boolean get() =
        this != TEACHING && this != FLASHCARD && this != DIALOGUE && this != STORY

    /**
     * آیا این نوع، آیتم‌به‌آیتم پیش می‌رود و شماره آیتمش معنا دارد.
     *
     * فقط این‌ها را می‌شود «از همان‌جا که رها شد» ادامه داد. بقیه صفحه
     * خودشان را دارند و جای ذخیره‌شده برایشان بی‌معناست: آموزش همه کارت‌ها
     * را یکجا نشان می‌دهد، جفت‌یابی با دورِ پنج‌تایی پیش می‌رود، و مکالمه
     * و داستانک شماره خط دارند نه شماره آیتم.
     */
    val isSequential: Boolean get() =
        this != TEACHING && this != WORD_MATCH && this != DIALOGUE && this != STORY

    companion object {
        fun fromOrNull(name: String?): ActivityType? = entries.firstOrNull { it.name == name }
    }
}

enum class ProgressStatus { NOT_STARTED, IN_PROGRESS, COMPLETED }

enum class LeitnerSource { DICTIONARY, LESSON, READER, AUTO_WRONG }

enum class StreakDayStatus { ACTIVE, FROZEN, MISSED, REPAIRED }

/**
 * فاصله مرور هر جعبه لایتنر بر حسب روز — سند ۰۳ / F-07.
 * پاسخ درست ← جعبه بعدی · پاسخ نادرست ← بازگشت به جعبه ۱
 */
object LeitnerSchedule {
    const val MAX_BOX = 5
    private val INTERVAL_DAYS = intArrayOf(0, 1, 3, 7, 21)

    fun intervalDays(box: Int): Int = INTERVAL_DAYS[box.coerceIn(1, MAX_BOX) - 1]

    fun nextBox(currentBox: Int, correct: Boolean): Int =
        if (correct) (currentBox + 1).coerceAtMost(MAX_BOX) else 1

    fun isLearned(box: Int): Boolean = box >= MAX_BOX
}

/** سقف فریز زنجیره — سند ۰۳ / F-10 */
/**
 * قواعد نگهداری زنجیره.
 *
 * زنجیره مهم‌ترین اهرم نگهداشت است، و **لحظه شکستنش دقیقاً همان لحظه‌ای
 * است که کاربر اپ را کنار می‌گذارد**. کسی که ۴۰ روز ساخته و در روز ۴۱
 * صفر می‌بیند، معمولاً برنمی‌گردد. دو سپر در برابر این لحظه گذاشته شده:
 *
 * **فریز** — پیشگیرانه. با تلاش جمع می‌شود و خودش خرج می‌شود.
 * **ترمیم** — درمانی. وقتی فریز تمام شده و زنجیره دارد می‌شکند.
 *
 * چرا هر دو و نه یکی: فریز فقط به کسی می‌رسد که قبلاً مرتب بوده. کاربر
 * تازه‌کاری که در هفته دوم دو روز جا می‌ماند هنوز فریزی ندارد — و
 * دقیقاً همان کسی است که بیشتر از همه در خطر رها کردن است.
 */
object StreakRules {
    const val MAX_FREEZES = 3

    /** به ازای هر این تعداد روز پیاپی، یک فریز جایزه می‌گیرد */
    const val FREEZE_EVERY_DAYS = 5

    /**
     * ترمیم حداکثر این تعداد روزِ جامانده را می‌پوشاند.
     *
     * محدود است چون ترمیمِ بی‌مرز، زنجیره را بی‌معنا می‌کند: کسی که دو
     * ماه نیامده نباید زنجیره ۵۰ روزه‌اش را پس بگیرد.
     */
    const val REPAIR_MAX_MISSED = 2

    /** فاصله بین دو ترمیم رایگان، به روز */
    const val REPAIR_COOLDOWN_DAYS = 30

    /**
     * تصمیمِ ثبتِ روز — تابع خالص، جدا از دیتابیس و ساعت.
     *
     * چرا جدا شد: آزمونِ قبلی همین محاسبه را **دوباره نوشته بود** و
     * نسخه خودش را می‌سنجید نه کد واقعی را. یعنی می‌شد منطق را خراب کرد
     * و آزمون همچنان سبز بماند. حالا کد و آزمون یک تابع را صدا می‌زنند.
     *
     * @param gapDays فاصله روزِ آخرین فعالیت تا امروز؛ null یعنی نخستین روز
     * @param sinceLastRepair روز گذشته از آخرین ترمیم؛ null یعنی هرگز
     */
    fun decide(
        gapDays: Int?,
        currentLength: Int,
        freezes: Int,
        sinceLastRepair: Int?,
    ): Outcome {
        val missed = when {
            gapDays == null -> 0
            gapDays <= 0 -> 0          // ساعت دستگاه عقب رفته — نادیده
            else -> gapDays - 1
        }
        val covered = minOf(missed, freezes)
        val uncovered = missed - covered

        val repaired = uncovered in 1..REPAIR_MAX_MISSED &&
            currentLength > 0 &&
            (sinceLastRepair == null || sinceLastRepair >= REPAIR_COOLDOWN_DAYS)

        val broken = uncovered > 0 && !repaired
        val newLength = when {
            gapDays == null -> 1
            broken -> 1
            else -> currentLength + 1
        }
        val remaining = freezes - covered
        val earned = !broken &&
            newLength % FREEZE_EVERY_DAYS == 0 &&
            remaining < MAX_FREEZES

        return Outcome(
            newLength = newLength,
            freezesUsed = covered,
            freezesLeft = (remaining + if (earned) 1 else 0).coerceAtMost(MAX_FREEZES),
            earnedFreeze = earned,
            repairedDays = if (repaired) uncovered else 0,
            broken = broken,
        )
    }

    data class Outcome(
        val newLength: Int,
        val freezesUsed: Int,
        val freezesLeft: Int,
        val earnedFreeze: Boolean,
        val repairedDays: Int,
        val broken: Boolean,
    ) {
        val repaired: Boolean get() = repairedDays > 0
    }
}
