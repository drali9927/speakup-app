package ir.speakup.app.domain

/**
 * آزمون تعیین سطح — منطق، جدا از رابط کاربری و دیتابیس.
 *
 * چرا لازم شد: تا امروز هر کاربری از A1 درس ۱ شروع می‌کرد، با
 * «He is a manager.» کسی که کمی انگلیسی بلد است همان‌جا اپ را می‌بندد —
 * و دقیقاً همان کسی است که بیشترین احتمال خرید را دارد، چون انگیزه‌اش
 * جدی‌تر است.
 *
 * تابع‌ها خالص‌اند تا آزمون واحد بتواند همان چیزی را بسنجد که اپ اجرا
 * می‌کند، نه یک نسخه بازنویسی‌شده از آن.
 */
object Placement {

    val LEVELS = listOf("A1", "A2", "B1", "B2")

    data class Question(
        val id: String,
        val level: String,
        val text: String,
        val options: List<String>,
        val answer: Int,
        val explanationFa: String,
    )

    data class Config(
        val perLevel: Int = 3,
        val stopAfterWrong: Int = 2,
        val passThreshold: Int = 2,
    )

    /**
     * آیا با این پاسخ‌ها باید ادامه داد.
     *
     * توقف زودهنگام مهم است: کاربری که به سقفش رسیده، اگر مجبور شود
     * شش پرسش دیگرِ سخت‌تر را هم غلط بزند، آزمون به‌جای راهنمایی تبدیل
     * می‌شود به تجربه‌ای که حس بی‌سوادی می‌دهد — درست در نخستین دقیقه
     * استفاده از اپ.
     */
    fun shouldStop(results: List<Boolean>, cfg: Config = Config()): Boolean {
        if (results.size < cfg.stopAfterWrong) return false
        return results.takeLast(cfg.stopAfterWrong).none { it }
    }

    /**
     * سطح پیشنهادی: بالاترین سطحی که در آن حد نصاب رعایت شده.
     *
     * سطح‌ها باید **پیوسته** پاس شده باشند. کسی که A1 را غلط زده ولی
     * تصادفاً دو پرسش B2 را درست حدس زده، نباید به B2 برود؛ آن دو پاسخ
     * از چهار گزینه، شانس هم می‌توانسته باشد.
     */
    fun suggest(
        questions: List<Question>,
        correctById: Map<String, Boolean>,
        cfg: Config = Config(),
    ): String {
        var best = LEVELS.first()
        for (level in LEVELS) {
            val asked = questions.filter { it.level == level && it.id in correctById }
            // سطحی که اصلاً پرسیده نشده، پاس‌شده حساب نمی‌شود
            if (asked.size < cfg.perLevel) break
            val right = asked.count { correctById[it.id] == true }
            if (right < cfg.passThreshold) break
            best = level
        }
        return best
    }

    /** پرسش‌های یک آزمون، به ترتیب دشواری */
    fun ordered(all: List<Question>): List<Question> =
        all.sortedBy { LEVELS.indexOf(it.level).let { i -> if (i < 0) LEVELS.size else i } }
}
