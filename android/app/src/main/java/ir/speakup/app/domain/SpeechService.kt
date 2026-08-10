package ir.speakup.app.domain

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * پخش صوت انگلیسی.
 *
 * دو منبع، به این ترتیب:
 *   ۱. فایل صوتی از پیش ساخته‌شده در بسته اپ — کیفیت یکدست و کنترل‌شده
 *   ۲. موتور TTS خود دستگاه — همیشه در دسترس، بدون هزینه و بدون حجم
 *
 * فعلاً فقط لایه دوم فعال است. وقتی فایل‌های صوتی سرور آماده شدند،
 * لایه اول بدون تغییر در UI اضافه می‌شود.
 *
 * ⚠️ موتور TTS انگلیسی روی همه گوشی‌های ایرانی نصب نیست. اگر نبود،
 * وضعیت MissingLanguage می‌شود و UI باید راهنمای دقیق نشان دهد،
 * نه یک خطای خام — این دقیقاً جایی است که رقیب ضعیف عمل می‌کند.
 */
@Singleton
class SpeechService @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    sealed interface Status {
        data object Initializing : Status
        data object Ready : Status
        /** موتور هست اما بسته زبان انگلیسی نصب نیست */
        data object MissingLanguage : Status
        /** هیچ موتور TTS روی دستگاه نیست */
        data object NoEngine : Status
    }

    private val _status = MutableStateFlow<Status>(Status.Initializing)
    val status: StateFlow<Status> = _status.asStateFlow()

    private val _speakingId = MutableStateFlow<String?>(null)
    /** شناسه عبارتی که همین حالا پخش می‌شود — برای انیمیشن دکمه بلندگو */
    val speakingId: StateFlow<String?> = _speakingId.asStateFlow()

    private var tts: TextToSpeech? = null

    init { initEngine() }

    private fun initEngine() {
        tts = TextToSpeech(context) { result ->
            if (result != TextToSpeech.SUCCESS) {
                _status.value = Status.NoEngine
                Log.w(TAG, "TTS engine init failed: $result")
                return@TextToSpeech
            }
            val lang = tts?.setLanguage(Locale.US)
            _status.value = when (lang) {
                TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                    Log.w(TAG, "english voice not installed: $lang")
                    Status.MissingLanguage
                }
                else -> Status.Ready
            }
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) { _speakingId.value = utteranceId }
                override fun onDone(utteranceId: String?) { clearIfCurrent(utteranceId) }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) { clearIfCurrent(utteranceId) }
                override fun onError(utteranceId: String?, errorCode: Int) { clearIfCurrent(utteranceId) }
            })
        }
    }

    private fun clearIfCurrent(id: String?) {
        if (_speakingId.value == id) _speakingId.value = null
    }

    /**
     * @param id شناسه یکتا برای اینکه UI بداند کدام دکمه در حال پخش است
     * @param rate سرعت پخش — ۰.۷ برای زبان‌آموز مبتدی مناسب‌تر از ۱.۰ است
     */
    /**
     * @param pitch گام صدا. برای تفکیک گوینده‌ها در مکالمه.
     *
     * چرا گام و نه صدای متفاوت: بیشتر گوشی‌ها فقط یک صدای انگلیسی نصب
     * دارند، پس انتخاب Voice دوم روی خیلی از دستگاه‌ها بی‌اثر می‌ماند و
     * دو نفر باز هم یکسان به گوش می‌رسند. تغییر گام همیشه کار می‌کند.
     */
    fun speak(text: String, id: String, rate: Float = 1.0f, pitch: Float = 1.0f) {
        if (_status.value != Status.Ready) return
        val clean = text.trim()
        if (clean.isEmpty()) return
        tts?.setSpeechRate(rate)
        tts?.setPitch(pitch)
        tts?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, id)
    }



    fun stop() {
        tts?.stop()
        _speakingId.value = null
    }

    /** تلاش دوباره پس از اینکه کاربر بسته زبان را نصب کرد */
    fun retry() {
        tts?.shutdown()
        tts = null
        _status.value = Status.Initializing
        initEngine()
    }

    companion object {
        /** گام دو گوینده مکالمه — به‌اندازه‌ای فاصله دارند که تفکیک شوند، نه آن‌قدر که مصنوعی. */
        const val PITCH_A = 1.12f
        const val PITCH_B = 0.88f
 const val TAG = "SpeechService" }
}
