package ir.speakup.app.domain

import ir.speakup.app.data.model.ActivityType

/**
 * قواعد امتیاز.
 *
 * اعداد اینجا جمع‌اند تا در یک نگاه قابل بحث باشند و در ده جای کد پخش نشوند.
 *
 * سه اصل که از تحقیق روی دولینگو گرفته شده و نباید بی‌دلیل عوض شوند:
 *
 * **۱. امتیاز برای انجام دادن است، نه برای بی‌نقص بودن.** کاربری که غلط
 * می‌زند هم امتیاز می‌گیرد؛ پاداش بی‌نقصی جداست و کوچک. اگر امتیاز به
 * درستی گره بخورد، کاربر محافظه‌کار می‌شود و از تمرین سخت فرار می‌کند —
 * دقیقاً برعکس چیزی که یادگیری لازم دارد.
 *
 * **۲. مرور لایتنر امتیاز کامل می‌گیرد.** وگرنه کاربر همیشه درس تازه را
 * انتخاب می‌کند و مرور — که اثرش بر یادسپاری بیشتر است — رها می‌شود.
 *
 * **۳. رسیدن به هدف روزانه پاداش جهشی دارد.** آخرین چند امتیاز باید
 * ارزش تلاش داشته باشد، وگرنه کاربر وسط راه رها می‌کند.
 */
object XpRules {

    /** امتیاز پایه هر فعالیت تمام‌شده */
    const val ACTIVITY = 10

    /** پاداش بی‌نقصی — کوچک و عمدی */
    const val PERFECT_BONUS = 5

    /** یک نوبت مرور لایتنر */
    const val LEITNER_SESSION = 10

    /** پاداش رسیدن به هدف روزانه */
    const val DAILY_GOAL_BONUS = 20

    /** بخش آموزش و مکالمه سوالی ندارند؛ امتیازشان کمتر است */
    const val PASSIVE_ACTIVITY = 5

    /** گزینه‌های هدف روزانه — با نام فارسی که خودِ کاربر انتخاب می‌کند */
    val GOALS = listOf(
        Goal(20, "آرام", "حدود ۵ دقیقه در روز"),
        Goal(50, "معمولی", "حدود ۱۰ دقیقه در روز"),
        Goal(100, "جدی", "حدود ۲۰ دقیقه در روز"),
    )
    const val DEFAULT_GOAL = 50

    data class Goal(val xp: Int, val title: String, val subtitle: String)

    /**
     * امتیاز یک فعالیت تمام‌شده.
     *
     * @param score نسبت پاسخ‌های درست، ۰ تا ۱
     */
    fun forActivity(type: ActivityType, score: Float): Int {
        val base = if (type.isScored) ACTIVITY else PASSIVE_ACTIVITY
        val perfect = if (type.isScored && score >= 1f) PERFECT_BONUS else 0
        return base + perfect
    }

    /**
     * سطح کاربر از مجموع امتیاز.
     *
     * فاصله سطوح عمداً رشد می‌کند: سطح‌های اول باید زود برسند تا کاربر
     * تازه‌وارد حس پیشرفت کند، و سطح‌های بالا باید ارزش داشته باشند.
     */
    fun levelFor(totalXp: Int): Int {
        var level = 1
        var need = 100
        var remaining = totalXp
        while (remaining >= need) {
            remaining -= need
            level++
            need = (need * 1.35).toInt()
        }
        return level
    }

    /** امتیاز لازم برای رسیدن به سطح بعد، و امتیاز کسب‌شده در سطح فعلی */
    fun levelProgress(totalXp: Int): Pair<Int, Int> {
        var need = 100
        var remaining = totalXp
        while (remaining >= need) {
            remaining -= need
            need = (need * 1.35).toInt()
        }
        return remaining to need
    }
}
