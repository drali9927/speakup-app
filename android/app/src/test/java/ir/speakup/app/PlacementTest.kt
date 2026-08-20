package ir.speakup.app

import ir.speakup.app.domain.Placement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlacementTest {

    private fun bank(): List<Placement.Question> =
        Placement.LEVELS.flatMap { lv ->
            (1..3).map { n ->
                Placement.Question("$lv-$n", lv, "___", listOf("a", "b", "c", "d"), 0, "")
            }
        }

    private fun answers(vararg pairs: Pair<String, Boolean>) = pairs.toMap()

    /** همه پرسش‌های سطوح تا `upTo` درست، بقیه غلط */
    private fun through(upTo: String): Map<String, Boolean> {
        val limit = Placement.LEVELS.indexOf(upTo)
        return bank().associate { q ->
            q.id to (Placement.LEVELS.indexOf(q.level) <= limit)
        }
    }

    // ---------- توقف زودهنگام ----------

    @Test fun `دو غلط پشت‌سرهم آزمون را تمام می‌کند`() {
        assertTrue(Placement.shouldStop(listOf(true, false, false)))
    }

    @Test fun `یک غلط تنها آزمون را تمام نمی‌کند`() {
        assertFalse(Placement.shouldStop(listOf(true, false)))
    }

    @Test fun `غلط‌های پراکنده آزمون را تمام نمی‌کنند`() {
        assertFalse(Placement.shouldStop(listOf(false, true, false, true)))
    }

    @Test fun `در شروع آزمون توقفی نیست`() {
        assertFalse(Placement.shouldStop(emptyList()))
        assertFalse(Placement.shouldStop(listOf(false)))
    }

    // ---------- سطح پیشنهادی ----------

    @Test fun `همه درست یعنی بالاترین سطح`() {
        assertEquals("B2", Placement.suggest(bank(), through("B2")))
    }

    @Test fun `تا B1 درست یعنی B1`() {
        assertEquals("B1", Placement.suggest(bank(), through("B1")))
    }

    @Test fun `همه غلط یعنی A1`() {
        assertEquals("A1", Placement.suggest(bank(), bank().associate { it.id to false }))
    }

    @Test fun `دو از سه برای عبور کافی است`() {
        val m = bank().associate { it.id to false }.toMutableMap()
        m["A1-1"] = true; m["A1-2"] = true          // ۲ از ۳
        assertEquals("A1", Placement.suggest(bank(), m))
    }

    @Test fun `یک از سه برای عبور کافی نیست`() {
        val m = bank().associate { it.id to false }.toMutableMap()
        m["A1-1"] = true
        assertEquals("A1", Placement.suggest(bank(), m))
    }

    @Test fun `پاس شدن باید پیوسته باشد`() {
        // A1 افتاده ولی B2 تصادفاً درست — نباید به B2 برود.
        // دو پاسخ درست از چهار گزینه، شانس هم می‌توانسته باشد.
        val m = bank().associate { it.id to false }.toMutableMap()
        m["B2-1"] = true; m["B2-2"] = true; m["B2-3"] = true
        assertEquals("A1", Placement.suggest(bank(), m))
    }

    @Test fun `سطحی که ناقص پرسیده شده پاس حساب نمی‌شود`() {
        // آزمون زود تمام شده و A2 فقط یک پرسش خورده
        val m = mutableMapOf<String, Boolean>()
        for (n in 1..3) m["A1-$n"] = true
        m["A2-1"] = true
        assertEquals("A1", Placement.suggest(bank(), m))
    }

    @Test fun `پرسش‌ها به ترتیب دشواری مرتب می‌شوند`() {
        val shuffled = bank().reversed()
        val levels = Placement.ordered(shuffled).map { it.level }.distinct()
        assertEquals(listOf("A1", "A2", "B1", "B2"), levels)
    }
}
