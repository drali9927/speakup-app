package ir.speakup.app.domain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * تشخیص گفتار برای تمرین گفتار.
 *
 * دو لایه، درست مثل `SpeechService` برای صوت:
 *   ۱. موتور سرور (دقت بالاتر، همه زبان‌ها) — هنوز پیاده نشده
 *   ۲. `SpeechRecognizer` آفلاین خود اندروید — همیشه در دسترس روی گوشی‌هایی
 *      که برنامه گوگل نصب دارند، بدون هزینه و بدون نیاز به اینترنت
 *
 * فعلاً فقط لایه دوم فعال است. وقتی موتور سرور آماده شد، پیاده‌سازی تازه
 * پشت همین واسط می‌نشیند و هیچ‌جای UI عوض نمی‌شود — همان الگوی SpeechService.
 */
interface SpeechRecognitionService {
    val state: StateFlow<RecognitionState>

    /** آیا سرویس تشخیص گفتار (برنامه گوگل یا معادل آن) روی این دستگاه نصب است */
    fun isAvailable(): Boolean

    fun startListening()
    fun stopListening()

    /** بازگشت به حالت اولیه — قبل از تلاش دوباره */
    fun reset()
}

sealed interface RecognitionState {
    data object Idle : RecognitionState
    data object Listening : RecognitionState
    data class Result(val transcript: String) : RecognitionState
    data class Error(val messageFa: String) : RecognitionState
}

@Singleton
class AndroidSpeechRecognitionService @Inject constructor(
    @ApplicationContext private val context: Context,
) : SpeechRecognitionService {

    private val _state = MutableStateFlow<RecognitionState>(RecognitionState.Idle)
    override val state: StateFlow<RecognitionState> = _state.asStateFlow()

    private var recognizer: SpeechRecognizer? = null

    /**
     * آیا تلاش جاری با موتور آفلاین بوده.
     * بسته زبان انگلیسی آفلاین روی بسیاری از گوشی‌ها نصب نیست؛ بدون این
     * پرچم، تشخیص همیشه شکست می‌خورد و کاربر فکر می‌کند قابلیت خراب است.
     */
    private var triedOffline = false

    override fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    private fun ensureRecognizer(): SpeechRecognizer {
        recognizer?.let { return it }
        val r = SpeechRecognizer.createSpeechRecognizer(context)
        r.setRecognitionListener(listener)
        recognizer = r
        return r
    }

    override fun startListening() {
        triedOffline = true
        launch(preferOffline = true)
    }

    private fun launch(preferOffline: Boolean) {
        if (!isAvailable()) {
            _state.value = RecognitionState.Error("موتور تشخیص گفتار روی این گوشی پیدا نشد.")
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, preferOffline)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }
        _state.value = RecognitionState.Listening
        runCatching { ensureRecognizer().startListening(intent) }
            .onFailure { _state.value = RecognitionState.Error("شروع ضبط ممکن نشد.") }
    }

    override fun stopListening() {
        runCatching { recognizer?.stopListening() }
    }

    override fun reset() {
        _state.value = RecognitionState.Idle
    }

    /**
     * آزادسازی موتور.
     *
     * SpeechRecognizer یک اتصال زنده به سرویس تشخیص نگه می‌دارد؛ بدون destroy
     * تا پایان عمر برنامه باز می‌ماند و نشانگر میکروفون سیستم روشن می‌ماند.
     */
    fun release() {
        runCatching { recognizer?.destroy() }
        recognizer = null
        _state.value = RecognitionState.Idle
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) { _state.value = RecognitionState.Listening }
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            // اگر تلاش آفلاین شکست خورد، یک بار آنلاین امتحان کن.
            // نبودِ بسته زبان آفلاین رایج‌ترین علت شکست است و کاربر
            // نباید بابتش پیام «چیزی شنیده نشد» بگیرد.
            val offlineMayHaveFailed = error in setOf(
                SpeechRecognizer.ERROR_NO_MATCH,
                SpeechRecognizer.ERROR_SERVER,
                SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE,
                SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED,
            )
            if (triedOffline && offlineMayHaveFailed) {
                triedOffline = false
                launch(preferOffline = false)
                return
            }

            // پیام دوستانه فارسی به‌جای کد خام خطا — رقیب دقیقاً همینجا خطای خام نشان می‌دهد
            val msg = when (error) {
                SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                    "چیزی شنیده نشد. دوباره لمس کن و واضح‌تر بگو."
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                    "دسترسی میکروفون لازم است."
                SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                    "مشکل شبکه — دوباره امتحان کن."
                SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE, SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED ->
                    "زبان انگلیسی برای تشخیص گفتار روی این گوشی در دسترس نیست."
                else -> "مشکلی در تشخیص گفتار پیش آمد. دوباره امتحان کن."
            }
            _state.value = RecognitionState.Error(msg)
        }

        override fun onResults(results: Bundle?) {
            val text = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                .orEmpty()
            // رونوشت خالی یعنی چیزی شنیده نشد، نه پاسخ غلط —
            // وگرنه کل جمله قرمز می‌شود و واژه بی‌دلیل وارد لایتنر می‌شود
            _state.value = if (text.isBlank()) {
                RecognitionState.Error("چیزی شنیده نشد. دوباره لمس کن و واضح‌تر بگو.")
            } else {
                RecognitionState.Result(text)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}
