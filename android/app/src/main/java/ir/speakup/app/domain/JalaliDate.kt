package ir.speakup.app.domain

import java.util.Calendar
import java.util.TimeZone

/**
 * تبدیل تاریخ میلادی به هجری شمسی.
 *
 * اندروید تقویم شمسی داخلی ندارد و کشیدن یک کتابخانه کامل تقویم
 * برای نمایش نوار هفتگی زنجیره، اضافه است.
 * الگوریتم استاندارد تبدیل، بدون وابستگی بیرونی.
 */
data class JalaliDate(val year: Int, val month: Int, val day: Int) {

    /** نام روز هفته به فارسی */
    fun weekDayName(dayOfWeek: Int): String = WEEK_DAYS[dayOfWeek]

    companion object {
        /** شنبه‌محور — ترتیب هفته ایرانی */
        val WEEK_DAYS = listOf("شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه")

        /** نام ماه‌های شمسی — نمایه از صفر، پس `MONTHS[month - 1]` */
        val MONTHS = listOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند",
        )

        private val G_DAYS = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        private val J_DAYS = intArrayOf(0, 31, 62, 93, 124, 155, 186, 216, 246, 276, 306, 336)

        fun fromGregorian(gy: Int, gm: Int, gd: Int): JalaliDate {
            val gy2 = if (gm > 2) gy + 1 else gy
            var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) +
                ((gy2 + 399) / 400) + gd + G_DAYS[gm - 1]

            var jy = -1595 + (33 * (days / 12053))
            days %= 12053
            jy += 4 * (days / 1461)
            days %= 1461

            if (days > 365) {
                jy += (days - 1) / 365
                days = (days - 1) % 365
            }

            val jm: Int
            val jd: Int
            if (days < 186) {
                jm = 1 + (days / 31)
                jd = 1 + (days % 31)
            } else {
                jm = 7 + ((days - 186) / 30)
                jd = 1 + ((days - 186) % 30)
            }
            return JalaliDate(jy, jm, jd)
        }

        fun fromEpoch(epochMillis: Long, tz: TimeZone = TimeZone.getDefault()): JalaliDate {
            val c = Calendar.getInstance(tz).apply { timeInMillis = epochMillis }
            return fromGregorian(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
        }

        /**
         * شماره روز هفته با مبنای شنبه = ۰.
         * Calendar.SATURDAY برابر ۷ است، پس نگاشت لازم دارد.
         */
        fun weekDayIndex(epochMillis: Long, tz: TimeZone = TimeZone.getDefault()): Int {
            val c = Calendar.getInstance(tz).apply { timeInMillis = epochMillis }
            return (c.get(Calendar.DAY_OF_WEEK) + 1) % 7
        }
    }
}

/** ارقام فارسی برای نمایش — اپ در همه‌جا اعداد فارسی نشان می‌دهد */
fun Int.toPersianDigits(): String = toString().map { PERSIAN_DIGITS[it - '0'] }.joinToString("")

/**
 * عدد با جداکننده هزارگان — «۲٬۰۸۸» و نه «۲۰۸۸».
 *
 * برای عددهای چهاررقمی به بالا که قرار است **خوانده** شوند، نه شمرده.
 * «۲۰۸۸ واژه» روی صفحه پرداخت یک رشته رقم است؛ چشم اندازه‌اش را
 * نمی‌گیرد و همان کاری را نمی‌کند که از عدد انتظار داریم.
 */
fun Int.toPersianGrouped(): String =
    toString().reversed().chunked(3).joinToString("\u066C").reversed()
        .map { if (it in '0'..'9') PERSIAN_DIGITS[it - '0'] else it }
        .joinToString("")

private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
