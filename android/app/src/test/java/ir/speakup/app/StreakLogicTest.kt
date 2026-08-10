package ir.speakup.app

import ir.speakup.app.data.model.StreakRules
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * منطق فریز زنجیره، مستقل از دیتابیس.
 *
 * این محاسبه تنها جای محصول است که یک اشتباه محاسباتی، مستقیماً
 * اعتماد کاربر را از بین می‌برد: زنجیره‌ای که ناحق پاره شود،
 * دلیل رها کردن اپ است.
 */
class StreakLogicTest {

    /** همان محاسبه‌ای که StreakRepository.checkIn انجام می‌دهد */
    private fun simulate(
        lastActiveGapDays: Int?,
        currentLength: Int,
        freezes: Int,
    ): Triple<Int, Int, Boolean> {
        val missed = when {
            lastActiveGapDays == null -> 0
            lastActiveGapDays <= 0 -> 0
            else -> lastActiveGapDays - 1
        }
        val covered = minOf(missed, freezes)
        val broken = missed > covered
        val newLength = when {
            lastActiveGapDays == null -> 1
            broken -> 1
            else -> currentLength + 1
        }
        return Triple(newLength, freezes - covered, broken)
    }

    @Test fun `first ever day starts streak at one`() {
        val (len, _, broken) = simulate(null, 0, 0)
        assertEquals(1, len)
        assertEquals(false, broken)
    }

    @Test fun `consecutive day extends streak`() {
        val (len, freezes, broken) = simulate(1, 5, 0)
        assertEquals(6, len)
        assertEquals(0, freezes)
        assertEquals(false, broken)
    }

    @Test fun `one missed day is covered by one freeze`() {
        val (len, freezes, broken) = simulate(2, 5, 1)
        assertEquals("زنجیره باید ادامه یابد", 6, len)
        assertEquals("یک فریز مصرف می‌شود", 0, freezes)
        assertEquals(false, broken)
    }

    @Test fun `one missed day without freeze breaks streak`() {
        val (len, _, broken) = simulate(2, 12, 0)
        assertEquals(1, len)
        assertEquals(true, broken)
    }

    @Test fun `three missed days covered by three freezes`() {
        val (len, freezes, broken) = simulate(4, 9, 3)
        assertEquals(10, len)
        assertEquals(0, freezes)
        assertEquals(false, broken)
    }

    @Test fun `four missed days with three freezes still breaks`() {
        val (len, freezes, broken) = simulate(5, 20, 3)
        assertEquals(1, len)
        assertEquals("همه فریزها مصرف می‌شوند", 0, freezes)
        assertEquals(true, broken)
    }

    @Test fun `partial freeze coverage still breaks streak`() {
        // دو روز غیبت، فقط یک فریز
        val (len, _, broken) = simulate(3, 8, 1)
        assertEquals(1, len)
        assertEquals(true, broken)
    }

    @Test fun `clock moved backwards does not break streak`() {
        // اگر ساعت دستگاه عقب برود، gap منفی می‌شود — نباید زنجیره پاره شود
        val (len, freezes, broken) = simulate(-2, 7, 2)
        assertEquals(8, len)
        assertEquals(2, freezes)
        assertEquals(false, broken)
    }

    @Test fun `freeze storage is capped at three`() {
        assertEquals(3, StreakRules.MAX_FREEZES)
    }
}
