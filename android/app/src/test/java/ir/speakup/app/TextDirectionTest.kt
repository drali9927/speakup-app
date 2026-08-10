package ir.speakup.app

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import ir.speakup.app.ui.theme.autoDir
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * رگرسیون برای باگی که دو بار روی دستگاه دیده شد:
 * متن انگلیسی داخل چیدمان راست‌به‌چپ به شکل «‎.Nice to meet you» رندر می‌شد.
 */
class TextDirectionTest {

    private fun dirOf(text: String): TextDirection = TextStyle().autoDir(text).textDirection

    @Test fun `english sentence gets ltr`() {
        assertEquals(TextDirection.Ltr, dirOf("Nice to meet you."))
    }

    @Test fun `persian sentence gets rtl`() {
        assertEquals(TextDirection.Rtl, dirOf("او یک پزشک است."))
    }

    @Test fun `persian prompt containing english keeps rtl`() {
        // "با شکل مخفف بنویسید: I am Ali." — نخستین حرف فارسی است
        assertEquals(TextDirection.Rtl, dirOf("با شکل مخفف بنویسید: I am Ali."))
    }

    @Test fun `english prompt containing persian keeps ltr`() {
        assertEquals(TextDirection.Ltr, dirOf("Hello! یعنی سلام"))
    }

    @Test fun `leading punctuation does not decide direction`() {
        assertEquals(TextDirection.Ltr, dirOf("\"Nice to meet you.\""))
        assertEquals(TextDirection.Rtl, dirOf("«سلام»"))
    }

    @Test fun `leading digits do not decide direction`() {
        assertEquals(TextDirection.Ltr, dirOf("1. He is a manager."))
        assertEquals(TextDirection.Rtl, dirOf("۱. او مدیر است."))
    }

    @Test fun `single english word gets ltr`() {
        assertEquals(TextDirection.Ltr, dirOf("hello"))
    }

    @Test fun `single persian word gets rtl`() {
        assertEquals(TextDirection.Rtl, dirOf("سلام"))
    }
}
