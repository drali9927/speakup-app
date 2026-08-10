package ir.speakup.app.domain

import ir.speakup.app.data.local.ActivityItemEntity

/**
 * تصحیح پاسخ کاربر.
 *
 * اصل طراحی: کاربر نباید بابت تفاوت‌های بی‌اهمیت جریمه شود.
 * "I'm Ali" ، "I'm Ali." و "i'm ali" هر سه درست‌اند.
 * اما "I am Ali" وقتی سوال «مخفف کن» است، نادرست است — چون آن نکته آموزشی درس است.
 */
object AnswerChecker {

    /** انواع آپاستروف که کیبوردهای مختلف تولید می‌کنند */
    private val APOSTROPHES = charArrayOf('’', 'ʼ', '´', '`')

    /** نشانه‌های پایانی که در مقایسه نادیده گرفته می‌شوند */
    private const val TRAILING = ".!?؟۔"

    fun normalize(raw: String): String {
        var s = raw.trim().lowercase()
        APOSTROPHES.forEach { s = s.replace(it, '\'') }
        s = s.replace(Regex("\\s+"), " ")
        s = s.trimEnd { it in TRAILING }.trim()
        // فاصله پیش از نشانه‌گذاری حذف می‌شود: "hello ,"  →  "hello,"
        s = s.replace(Regex("\\s+([,;:])"), "$1")
        return s
    }

    fun check(item: ActivityItemEntity, userAnswer: String): Verdict {
        val expected = item.correctAnswer.orEmpty()
        if (expected.isBlank()) return Verdict(correct = true, expected = "", userAnswer = userAnswer)

        val user = normalize(userAnswer)
        val accepted = buildList {
            add(normalize(expected))
            item.alternativesList().forEach { add(normalize(it)) }
        }

        return Verdict(
            correct = user.isNotEmpty() && user in accepted,
            expected = expected,
            userAnswer = userAnswer,
        )
    }

    data class Verdict(
        val correct: Boolean,
        val expected: String,
        val userAnswer: String,
    )
}
