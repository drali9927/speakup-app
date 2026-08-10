package ir.speakup.app

import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.domain.AnswerChecker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnswerCheckerTest {

    private fun item(correct: String, alternatives: String? = null) = ActivityItemEntity(
        id = "t", activityId = "a", sortOrder = 1, prompt = "p",
        correctAnswer = correct, alternatives = alternatives,
    )

    // --- پذیرش تفاوت‌های بی‌اهمیت ---

    @Test fun `exact match passes`() {
        assertTrue(AnswerChecker.check(item("I'm Ali."), "I'm Ali.").correct)
    }

    @Test fun `case is ignored`() {
        assertTrue(AnswerChecker.check(item("I'm Ali."), "i'm ali.").correct)
    }

    @Test fun `trailing period is optional`() {
        assertTrue(AnswerChecker.check(item("I'm Ali."), "I'm Ali").correct)
    }

    @Test fun `question mark is optional`() {
        assertTrue(AnswerChecker.check(item("Who is he?"), "Who is he").correct)
    }

    @Test fun `curly apostrophe is accepted`() {
        // کیبورد فارسی سامسونگ آپاستروف فرفری تولید می‌کند
        assertTrue(AnswerChecker.check(item("I'm Ali."), "I’m Ali.").correct)
    }

    @Test fun `extra spaces are collapsed`() {
        assertTrue(AnswerChecker.check(item("This is my friend."), "  This   is  my friend  ").correct)
    }

    @Test fun `declared alternative is accepted`() {
        val i = item("He is a doctor.", "He's a doctor.")
        assertTrue(AnswerChecker.check(i, "He's a doctor.").correct)
    }

    @Test fun `multiple alternatives all accepted`() {
        val i = item("What time is it?", "What's the time?|What is the time?")
        assertTrue(AnswerChecker.check(i, "What's the time?").correct)
        assertTrue(AnswerChecker.check(i, "What is the time?").correct)
    }

    // --- رد کردن پاسخ‌هایی که نکته آموزشی درس را رعایت نکرده‌اند ---

    @Test fun `unexpanded form is rejected when contraction is the lesson`() {
        // سوال «مخفف کن» است؛ "I am Ali" نباید پذیرفته شود
        assertFalse(AnswerChecker.check(item("I'm Ali."), "I am Ali.").correct)
    }

    @Test fun `wrong word is rejected`() {
        assertFalse(AnswerChecker.check(item("He is a doctor."), "He is a teacher.").correct)
    }

    @Test fun `empty answer is rejected`() {
        assertFalse(AnswerChecker.check(item("hello"), "   ").correct)
    }

    @Test fun `persian translation is never accepted as english answer`() {
        // رگرسیون: پیش‌تر ترجمه فارسی اشتباهاً در ستون alternatives ذخیره می‌شد
        assertFalse(AnswerChecker.check(item("He is a doctor."), "او یک پزشک است.").correct)
    }

    // --- آیتم‌های بدون پاسخ (کارت آموزشی) ---

    @Test fun `item without correct answer always passes`() {
        assertTrue(AnswerChecker.check(item(""), "anything").correct)
    }

    // --- نرمال‌سازی ---

    @Test fun `normalize removes space before punctuation`() {
        assertEquals("hello, world", AnswerChecker.normalize("Hello , World"))
    }
}
