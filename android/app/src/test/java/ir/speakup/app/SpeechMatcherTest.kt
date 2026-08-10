package ir.speakup.app

import ir.speakup.app.domain.SpeechMatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpeechMatcherTest {

    @Test fun `exact transcript is all correct`() {
        val r = SpeechMatcher.match("He is a manager.", "he is a manager")
        assertTrue(r.allCorrect)
        assertTrue(r.words.all { it.correct })
    }

    @Test fun `missing word is marked wrong, rest stay correct`() {
        val r = SpeechMatcher.match("He is a manager.", "he a manager")
        assertFalse(r.allCorrect)
        assertEquals(listOf(true, false, true, true), r.words.map { it.correct })
    }

    @Test fun `word order does not matter`() {
        // تشخیص گفتار گاهی با مکث طبیعی جمله را کمی جابه‌جا برمی‌گرداند
        val r = SpeechMatcher.match("nice to meet you", "meet you nice to")
        assertTrue(r.allCorrect)
    }

    @Test fun `punctuation and case in transcript are ignored`() {
        val r = SpeechMatcher.match("I'm Ali.", "i'm ali")
        assertTrue(r.allCorrect)
    }

    @Test fun `extra words spoken do not break already-matched target words`() {
        val r = SpeechMatcher.match("hello", "um hello there")
        assertTrue(r.allCorrect)
    }

    @Test fun `empty transcript marks every target word wrong`() {
        val r = SpeechMatcher.match("good morning", "")
        assertFalse(r.allCorrect)
        assertTrue(r.words.none { it.correct })
    }

    @Test fun `duplicate target word only consumes one matching said word`() {
        // "bye bye" باید هر دو "bye" را جدا مصرف کند، نه یکی را دوبار بشمارد
        val r = SpeechMatcher.match("bye bye", "bye")
        assertEquals(listOf(true, false), r.words.map { it.correct })
    }

    @Test fun `blank target produces no words and is not correct`() {
        val r = SpeechMatcher.match("", "anything")
        assertTrue(r.words.isEmpty())
        assertFalse(r.allCorrect)
    }
}
