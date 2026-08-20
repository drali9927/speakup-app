package ir.speakup.app

import ir.speakup.app.data.local.glossKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * کلید مقایسه معنی واژه.
 *
 * این تعیین می‌کند دو معنی می‌توانند با هم در گزینه‌های یک آزمون بیایند
 * یا نه. اگر خراب شود، آزمون بی‌سروصدا از سنجش به حدس‌زدن تبدیل می‌شود.
 */
class GlossKeyTest {

    @Test fun `معنی با و بدون پرانتز یکی حساب می‌شوند`() {
        // «سلام» و «سلام (خودمانی)» در درس اول A1 با هم گزینه می‌شدند
        assertEquals("سلام".glossKey(), "سلام (خودمانی)".glossKey())
    }

    @Test fun `دو توضیح متفاوت روی یک معنی هم یکی‌اند`() {
        assertEquals("همسر (زن)".glossKey(), "همسر (مرد)".glossKey())
        assertEquals("باید (توصیه)".glossKey(), "باید (الزام)".glossKey())
        assertEquals("که (برای افراد)".glossKey(), "که".glossKey())
    }

    @Test fun `معنی‌های واقعاً متفاوت جدا می‌مانند`() {
        assertNotEquals("سلام".glossKey(), "خداحافظ".glossKey())
        assertNotEquals("پزشک".glossKey(), "معلم".glossKey())
    }

    @Test fun `نیم‌فاصله و حروف عربی یکسان‌سازی می‌شوند`() {
        assertEquals("دانش‌آموز".glossKey(), "دانش آموز".glossKey())
        assertEquals("یکی".glossKey(), "يکی".glossKey())
        assertEquals("کتاب".glossKey(), "كتاب".glossKey())
    }

    @Test fun `ویرگول و خط تیره ته معنی حذف می‌شوند`() {
        assertEquals("گفت".glossKey(), "گفت (به کسی)،".glossKey())
    }

    @Test fun `پرانتز فارسی هم شناخته می‌شود`() {
        assertEquals("رسمی".glossKey(), "رسمی （سبک）".glossKey())
    }
}
