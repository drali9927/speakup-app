package ir.speakup.app

import ir.speakup.app.data.model.StreakRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * منطق نگهداری زنجیره.
 *
 * این محاسبه تنها جای محصول است که یک اشتباه محاسباتی مستقیماً اعتماد
 * کاربر را از بین می‌برد: زنجیره‌ای که ناحق پاره شود، دلیل رها کردن اپ
 * است.
 *
 * نسخه قبلی این آزمون همین محاسبه را **دوباره نوشته بود** و نسخه خودش
 * را می‌سنجید. یعنی می‌شد منطق واقعی را خراب کرد و آزمون سبز بماند. حالا
 * مستقیم `StreakRules.decide` را صدا می‌زند — همان تابعی که خودِ اپ
 * استفاده می‌کند.
 */
class StreakLogicTest {

    private fun decide(gap: Int?, len: Int, freezes: Int, sinceRepair: Int? = null) =
        StreakRules.decide(gap, len, freezes, sinceRepair)

    // ---------- پایه ----------

    @Test fun `نخستین روز، زنجیره از یک شروع می‌شود`() {
        val o = decide(null, 0, 0)
        assertEquals(1, o.newLength)
        assertFalse(o.broken)
    }

    @Test fun `روز پیاپی زنجیره را یکی زیاد می‌کند`() {
        val o = decide(1, 5, 0)
        assertEquals(6, o.newLength)
        assertFalse(o.broken)
    }

    @Test fun `ساعت عقب‌رفته دستگاه زنجیره را نمی‌شکند`() {
        val o = decide(-3, 9, 0)
        assertEquals(10, o.newLength)
        assertFalse(o.broken)
    }

    // ---------- فریز ----------

    @Test fun `یک روز جامانده با یک فریز پوشیده می‌شود`() {
        val o = decide(2, 5, 1)
        assertEquals("زنجیره باید ادامه یابد", 6, o.newLength)
        assertEquals(1, o.freezesUsed)
        assertEquals(0, o.freezesLeft)
        assertFalse(o.broken)
    }

    @Test fun `دو روز جامانده با دو فریز پوشیده می‌شود`() {
        val o = decide(3, 8, 2)
        assertEquals(9, o.newLength)
        assertEquals(2, o.freezesUsed)
        assertFalse(o.broken)
    }

    @Test fun `فریز به ازای هر پنج روز پیاپی جایزه می‌گیرد`() {
        val o = decide(1, StreakRules.FREEZE_EVERY_DAYS - 1, 0)
        assertTrue("روز پنجم باید فریز بدهد", o.earnedFreeze)
        assertEquals(1, o.freezesLeft)
    }

    @Test fun `روزی که مضرب پنج نیست فریز نمی‌دهد`() {
        assertFalse(decide(1, 5, 0).earnedFreeze)   // می‌شود ۶
    }

    @Test fun `وقتی انبار فریز پر است جایزه‌ای اضافه نمی‌شود`() {
        val o = decide(1, StreakRules.FREEZE_EVERY_DAYS - 1, StreakRules.MAX_FREEZES)
        assertFalse(o.earnedFreeze)
        assertEquals(StreakRules.MAX_FREEZES, o.freezesLeft)
    }

    // ---------- ترمیم ----------

    @Test fun `بدون فریز، یک روز جامانده ترمیم می‌شود نه شکسته`() {
        val o = decide(2, 12, 0)
        assertFalse("ترمیم باید جلوی شکستن را بگیرد", o.broken)
        assertTrue(o.repaired)
        assertEquals("زنجیره باید ادامه یابد", 13, o.newLength)
    }

    @Test fun `ترمیم فقط تا سقف روزهای مجاز کار می‌کند`() {
        val tooMany = StreakRules.REPAIR_MAX_MISSED + 1
        val o = decide(tooMany + 1, 30, 0)
        assertTrue("غیبت طولانی نباید ترمیم شود", o.broken)
        assertEquals(1, o.newLength)
    }

    @Test fun `ترمیم دوم پیش از پایان فاصله انجام نمی‌شود`() {
        val o = decide(2, 12, 0, sinceRepair = StreakRules.REPAIR_COOLDOWN_DAYS - 1)
        assertTrue("هنوز زود است", o.broken)
        assertEquals(1, o.newLength)
    }

    @Test fun `پس از پایان فاصله، ترمیم دوباره ممکن است`() {
        val o = decide(2, 12, 0, sinceRepair = StreakRules.REPAIR_COOLDOWN_DAYS)
        assertFalse(o.broken)
        assertTrue(o.repaired)
    }

    @Test fun `فریز اول خرج می‌شود و ترمیم دست‌نخورده می‌ماند`() {
        // دو روز جامانده، یک فریز: فریز یکی را می‌پوشاند، ترمیم دیگری را
        val o = decide(3, 10, 1)
        assertEquals(1, o.freezesUsed)
        assertEquals(1, o.repairedDays)
        assertFalse(o.broken)
        assertEquals(11, o.newLength)
    }

    @Test fun `کاربر بدون زنجیره ترمیم نمی‌گیرد`() {
        val o = decide(2, 0, 0)
        assertTrue(o.broken)
    }
}
