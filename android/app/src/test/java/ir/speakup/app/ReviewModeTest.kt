package ir.speakup.app

import ir.speakup.app.domain.ReviewMode
import ir.speakup.app.domain.ReviewOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewModeTest {

    // ---------- انتخاب شکل پرسش ----------

    @Test fun `کارت تازه فقط بازشناسی می‌خواهد`() {
        assertEquals(ReviewMode.MEANING, ReviewMode.forBox(1, canType = true))
        assertEquals(ReviewMode.MEANING, ReviewMode.forBox(2, canType = true))
    }

    @Test fun `کارت میانی، یادآوری در جهت تولید`() {
        assertEquals(ReviewMode.WORD, ReviewMode.forBox(3, canType = true))
        assertEquals(ReviewMode.WORD, ReviewMode.forBox(4, canType = true))
    }

    @Test fun `کارت پخته، تایپ کامل`() {
        assertEquals(ReviewMode.TYPING, ReviewMode.forBox(5, canType = true))
    }

    @Test fun `واژه تایپ‌نشدنی هرگز تایپ خواسته نمی‌شود`() {
        // وگرنه کاربر به‌خاطر یک فاصله یا خط تیره جریمه می‌شود
        assertEquals(ReviewMode.WORD, ReviewMode.forBox(5, canType = false))
    }

    @Test fun `سختی با جعبه بالا می‌رود و پایین نمی‌آید`() {
        val order = listOf(ReviewMode.MEANING, ReviewMode.WORD, ReviewMode.TYPING)
        var last = -1
        for (box in 1..5) {
            val i = order.indexOf(ReviewMode.forBox(box, canType = true))
            assertTrue("جعبه $box سختی را کم کرد", i >= last)
            last = i
        }
    }

    // ---------- کدام واژه تایپ‌شدنی است ----------

    @Test fun `واژه ساده تایپ‌شدنی است`() {
        assertTrue(ReviewMode.typable("hello"))
        assertTrue(ReviewMode.typable("don't"))
    }

    @Test fun `عبارت چندکلمه‌ای تایپ‌شدنی نیست`() {
        assertFalse(ReviewMode.typable("police officer"))
        assertFalse(ReviewMode.typable("look forward to"))
    }

    @Test fun `واژه خیلی بلند یا خیلی کوتاه تایپ‌شدنی نیست`() {
        assertFalse(ReviewMode.typable("a"))
        assertFalse(ReviewMode.typable("internationalisation"))
    }

    // ---------- گزینه‌ها ----------

    private val pool = listOf("سلام", "خداحافظ", "صبح", "شب", "کتاب", "میز")

    @Test fun `سه گزینه ساخته می‌شود و پاسخ درست بینشان است`() {
        val o = ReviewOptions.build("سلام", pool, at = 0)
        assertEquals(3, o.size)
        assertTrue(o.contains("سلام"))
    }

    @Test fun `گزینه تکراری ساخته نمی‌شود`() {
        val o = ReviewOptions.build("سلام", pool, at = 2)
        assertEquals(o.size, o.toSet().size)
    }

    @Test fun `معنی با و بدون پرانتز با هم گزینه نمی‌شوند`() {
        // «سلام» و «سلام (خودمانی)» برای زبان‌آموز یک چیزند
        val p = listOf("سلام", "سلام (خودمانی)", "خداحافظ", "صبح")
        val o = ReviewOptions.build("سلام", p, at = 0)
        assertFalse(o.contains("سلام (خودمانی)"))
    }

    @Test fun `وقتی حواس‌پرت‌کن نیست، گزینه‌ای ساخته نمی‌شود`() {
        assertTrue(ReviewOptions.build("سلام", listOf("سلام"), at = 0).isEmpty())
    }

    @Test fun `جای پاسخ درست روی یک جایگاه گیر نمی‌کند`() {
        // با فرمول ساده، پاسخ در بیشتر پرسش‌ها یک‌جا می‌نشست و کاربر
        // بدون دانستن هیچ واژه‌ای نمره می‌گرفت
        val slots = (0 until 60).map {
            ReviewOptions.build(pool[it % pool.size], pool, at = it).indexOf(pool[it % pool.size])
        }
        val spread = slots.toSet()
        assertTrue("پاسخ همیشه در یک جایگاه بود: $spread", spread.size >= 3)
        val most = slots.groupingBy { it }.eachCount().values.max()
        assertTrue("پاسخ در ${most} از ۶۰ بار یک‌جا بود", most < 40)
    }
}
