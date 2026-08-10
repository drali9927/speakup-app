package ir.speakup.app

import ir.speakup.app.data.model.ActivityType
import ir.speakup.app.data.model.LeitnerSchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LeitnerScheduleTest {

    @Test fun `correct answer promotes one box`() {
        assertEquals(2, LeitnerSchedule.nextBox(1, correct = true))
        assertEquals(5, LeitnerSchedule.nextBox(4, correct = true))
    }

    @Test fun `promotion stops at max box`() {
        assertEquals(5, LeitnerSchedule.nextBox(5, correct = true))
    }

    @Test fun `wrong answer resets to first box from anywhere`() {
        assertEquals(1, LeitnerSchedule.nextBox(2, correct = false))
        assertEquals(1, LeitnerSchedule.nextBox(5, correct = false))
    }

    @Test fun `intervals grow with box number`() {
        val intervals = (1..5).map { LeitnerSchedule.intervalDays(it) }
        assertEquals(listOf(0, 1, 3, 7, 21), intervals)
        assertEquals(intervals.sorted(), intervals)
    }

    @Test fun `only last box counts as learned`() {
        assertFalse(LeitnerSchedule.isLearned(4))
        assertTrue(LeitnerSchedule.isLearned(5))
    }
}

/**
 * قاعده سند ۰۷ تمایز ۱: حداقل ۶۰٪ تمرین‌ها باید تولیدی باشند.
 * این تست تضمین می‌کند کسی به‌اشتباه نوع تشخیصی را تولیدی علامت نزند.
 */
class ActivityTypeTest {

    @Test fun `recognition types are not marked productive`() {
        listOf(
            ActivityType.MULTIPLE_CHOICE,
            ActivityType.MATCHING,
            ActivityType.FLASHCARD,
            ActivityType.TEACHING,
        ).forEach { assertFalse("${it.name} باید تشخیصی باشد", it.isProductive) }
    }

    @Test fun `production types are marked productive`() {
        listOf(
            ActivityType.FREE_TEXT,
            ActivityType.FILL_BLANK,
            ActivityType.REORDER,
            ActivityType.TRANSLATE_TO_EN,
            ActivityType.SPEAKING,
        ).forEach { assertTrue("${it.name} باید تولیدی باشد", it.isProductive) }
    }

    @Test fun `unknown type name resolves to null`() {
        assertEquals(null, ActivityType.fromOrNull("NOT_A_TYPE"))
        assertEquals(null, ActivityType.fromOrNull(null))
    }
}
