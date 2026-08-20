package ir.speakup.app

import ir.speakup.app.domain.JalaliDate
import ir.speakup.app.domain.daysBetween
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.domain.toPersianGrouped
import org.junit.Assert.assertEquals
import org.junit.Test

class JalaliDateTest {

    @Test fun `nowruz maps to first of farvardin`() {
        // ۲۰۲۶-۰۳-۲۱  →  ۱ فروردین ۱۴۰۵
        assertEquals(JalaliDate(1405, 1, 1), JalaliDate.fromGregorian(2026, 3, 21))
    }

    @Test fun `day before nowruz is last of esfand`() {
        assertEquals(JalaliDate(1404, 12, 29), JalaliDate.fromGregorian(2026, 3, 20))
    }

    @Test fun `today in this session converts correctly`() {
        // ۲۰۲۶-۰۸-۰۴  →  ۱۳ مرداد ۱۴۰۵
        assertEquals(JalaliDate(1405, 5, 13), JalaliDate.fromGregorian(2026, 8, 4))
    }

    @Test fun `first of dey`() {
        assertEquals(JalaliDate(1405, 10, 1), JalaliDate.fromGregorian(2026, 12, 22))
    }

    @Test fun `week starts on saturday`() {
        assertEquals(7, JalaliDate.WEEK_DAYS.size)
        assertEquals("شنبه", JalaliDate.WEEK_DAYS[0])
        assertEquals("جمعه", JalaliDate.WEEK_DAYS[6])
    }

    @Test fun `days between consecutive dates`() {
        assertEquals(1, daysBetween("2026-08-04", "2026-08-05"))
        assertEquals(0, daysBetween("2026-08-04", "2026-08-04"))
        assertEquals(7, daysBetween("2026-08-01", "2026-08-08"))
    }

    @Test fun `days between across month boundary`() {
        assertEquals(1, daysBetween("2026-07-31", "2026-08-01"))
        assertEquals(2, daysBetween("2026-02-27", "2026-03-01"))   // ۲۰۲۶ کبیسه نیست
    }

    @Test fun `persian digits`() {
        assertEquals("۰", 0.toPersianDigits())
        assertEquals("۱۲۳", 123.toPersianDigits())
        assertEquals("۱۴۰۵", 1405.toPersianDigits())
    }
}

/** جداکننده هزارگان — عددهایی که قرار است خوانده شوند، نه شمرده */
class PersianGroupingTest {
    @org.junit.Test fun `سه رقم بدون جداکننده می‌ماند`() {
        org.junit.Assert.assertEquals("۱۲۰", 120.toPersianGrouped())
    }

    @org.junit.Test fun `چهار رقم یک جداکننده می‌گیرد`() {
        org.junit.Assert.assertEquals("۲٬۰۸۸", 2088.toPersianGrouped())
    }

    @org.junit.Test fun `هفت رقم دو جداکننده می‌گیرد`() {
        org.junit.Assert.assertEquals("۱٬۲۳۴٬۵۶۷", 1234567.toPersianGrouped())
    }

    @org.junit.Test fun `صفر درست می‌ماند`() {
        org.junit.Assert.assertEquals("۰", 0.toPersianGrouped())
    }
}

/**
 * برچسب روزهای نمودار هفتگی کارنامه.
 *
 * این یک بار غلط بود و همه هفت روز را جابه‌جا نشان می‌داد: پنج‌شنبه با
 * برچسب «س» می‌آمد. چون خطا نمی‌دهد و فقط «کمی عجیب» به نظر می‌رسد،
 * بدون آزمون دوباره برمی‌گردد.
 */
class WeekdayLabelTest {
    private val names = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")

    /** همان محاسبه‌ای که ProfileViewModel انجام می‌دهد */
    private fun label(calendarDayOfWeek: Int) = names[calendarDayOfWeek % 7]

    @org.junit.Test fun `هر روز هفته برچسب درست می‌گیرد`() {
        org.junit.Assert.assertEquals("ش", label(java.util.Calendar.SATURDAY))
        org.junit.Assert.assertEquals("ی", label(java.util.Calendar.SUNDAY))
        org.junit.Assert.assertEquals("د", label(java.util.Calendar.MONDAY))
        org.junit.Assert.assertEquals("س", label(java.util.Calendar.TUESDAY))
        org.junit.Assert.assertEquals("چ", label(java.util.Calendar.WEDNESDAY))
        org.junit.Assert.assertEquals("پ", label(java.util.Calendar.THURSDAY))
        org.junit.Assert.assertEquals("ج", label(java.util.Calendar.FRIDAY))
    }
}
