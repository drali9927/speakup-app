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

enum class StreakDayStatus { ACTIVE, FROZEN, MISSED }

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
object StreakRules {
    const val MAX_FREEZES = 3
}
