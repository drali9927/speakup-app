package ir.speakup.app.domain

/**
 * تطبیق رونوشت گفتار با جمله هدف — برای بازخورد کلمه‌به‌کلمه تمرین گفتار.
 *
 * تشخیص گفتار همیشه کامل نیست؛ تطبیق کل جمله (مثل AnswerChecker.check) اکثر
 * تلاش‌های درست را هم رد می‌کند چون یک کلمه جا افتاده یا اضافه می‌آید.
 * به‌جای آن، هر کلمه هدف را جدا بررسی می‌کنیم: اگر در رونوشت آمده، سبز؛
 * وگرنه قرمز. ترتیب کلمات مهم نیست — گفتار طبیعی گاهی با مکث جابه‌جا می‌شود.
 *
 * درست‌بودن کلی = همه کلمه‌های هدف در رونوشت پیدا شدند. همان قاعده‌ای که
 * برای امتیازدهی و لایتنر خودکار استفاده می‌شود.
 */
object SpeechMatcher {

    data class WordResult(val word: String, val correct: Boolean)
    data class MatchResult(val words: List<WordResult>, val allCorrect: Boolean)

    fun match(target: String, transcript: String): MatchResult {
        val targetWords = tokenize(target)
        val saidWords = tokenize(transcript).toMutableList()

        val results = targetWords.map { targetWord ->
            val normalizedTarget = AnswerChecker.normalize(targetWord)
            val idx = saidWords.indexOfFirst { AnswerChecker.normalize(it) == normalizedTarget }
            if (idx >= 0) {
                saidWords.removeAt(idx)
                WordResult(targetWord, correct = true)
            } else {
                WordResult(targetWord, correct = false)
            }
        }
        return MatchResult(results, allCorrect = results.isNotEmpty() && results.all { it.correct })
    }

    private fun tokenize(s: String): List<String> =
        s.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
}
