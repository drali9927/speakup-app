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

    /** گوینده مکالمه. از محتوا می‌آید، نه از شماره خط. */
    enum class Speaker { A, B }

    private var voiceA: android.speech.tts.Voice? = null
    private var voiceB: android.speech.tts.Voice? = null
    private var baseVoice: android.speech.tts.Voice? = null
    private var hasGenderedPair = false

    init { initEngine() }

    private fun initEngine() { initEngine(preferGoogle = true) }

    /**
     * @param preferGoogle موتور گوگل را ترجیح بده، اگر نصب باشد.
     *
     * چرا ترجیح: موتور پیش‌فرض سامسونگ روی همین گوشی آزمایشی **چهار**
     * صدای انگلیسی دارد که همه‌شان زنانه‌اند (`f00` و `default`) — یعنی
     * «علی» هرگز صدای مردانه نمی‌گیرد. موتور گوگل روی همان گوشی **۵۲**
     * صدای انگلیسی دارد، با خانواده‌های مردانه و زنانه مشخص.
     *
     * اگر گوگل نبود (که روی خیلی از گوشی‌های ایرانی نیست) بی‌سروصدا به
     * موتور پیش‌فرض دستگاه برمی‌گردیم.
     */
    private fun initEngine(preferGoogle: Boolean) {
        val engineName = GOOGLE_TTS.takeIf { preferGoogle && isInstalled(it) }
        val listener = TextToSpeech.OnInitListener { result ->
            if (result != TextToSpeech.SUCCESS) {
                // اگر گوگل بالا نیامد، یک بار با موتور پیش‌فرض دوباره
                if (engineName != null) {
                    Log.w(TAG, "google tts failed ($result) — falling back to default engine")
                    tts?.shutdown()
                    tts = null
                    initEngine(preferGoogle = false)
                    return@OnInitListener
                }
                _status.value = Status.NoEngine
                Log.w(TAG, "TTS engine init failed: $result")
                return@OnInitListener
            }
            val lang = tts?.setLanguage(Locale.US)
            _status.value = when (lang) {
                TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                    Log.w(TAG, "english voice not installed: $lang")
                    Status.MissingLanguage
                }
                else -> Status.Ready
            }
            if (_status.value == Status.Ready) pickDialogueVoices()
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) { _speakingId.value = utteranceId }
                override fun onDone(utteranceId: String?) { clearIfCurrent(utteranceId) }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) { clearIfCurrent(utteranceId) }
                override fun onError(utteranceId: String?, errorCode: Int) { clearIfCurrent(utteranceId) }
            })
        }
        tts = if (engineName != null) TextToSpeech(context, listener, engineName)
        else TextToSpeech(context, listener)
    }

    private fun isInstalled(pkg: String): Boolean = runCatching {
        context.packageManager.getPackageInfo(pkg, 0); true
    }.getOrDefault(false)

    private fun clearIfCurrent(id: String?) {
        if (_speakingId.value == id) _speakingId.value = null
    }

    /**
     * @param id شناسه یکتا برای اینکه UI بداند کدام دکمه در حال پخش است
     * @param rate سرعت پخش — ۰.۷ برای زبان‌آموز مبتدی مناسب‌تر از ۱.۰ است
     * @param speaker گوینده مکالمه، اگر این جمله بخشی از گفتگوست
     */
    fun speak(
        text: String,
        id: String,
        rate: Float = 1.0f,
        speaker: Speaker? = null,
    ) {
        if (_status.value != Status.Ready) return
        val clean = text.trim()
        if (clean.isEmpty()) return

        val engine = tts ?: return
        engine.setSpeechRate(rate)

        if (speaker == null) {
            baseVoice?.let { engine.voice = it }
            engine.setPitch(1.0f)
        } else {
            val chosen = when (speaker) {
                Speaker.A -> voiceA
                Speaker.B -> voiceB
            }
            chosen?.let { engine.voice = it }
            // گام، **علاوه بر** صدای متفاوت اعمال می‌شود و نه به‌جای آن.
            //
            // اگر دستگاه دو صدای انگلیسی داشته باشد، تفاوت اصلی از خود
            // صداست و گام فقط تأکیدش می‌کند. اگر فقط یک صدا داشته باشد،
            // گام تنها چیزی است که می‌ماند — و آن‌وقت باید به‌اندازه‌ای
            // باشد که واقعاً شنیده شود.
            val gap = if (hasGenderedPair) 0f else if (voiceA != null && voiceA != voiceB) NARROW_GAP else WIDE_GAP
            engine.setPitch(if (speaker == Speaker.A) 1f + gap else 1f - gap)
        }
        engine.speak(clean, TextToSpeech.QUEUE_FLUSH, null, id)
    }

    /**
     * دو صدای متفاوت برای دو نفرِ مکالمه.
     *
     * چرا اصلاً لازم شد: پیش از این فقط گام صدا ±۱۲٪ جابه‌جا می‌شد. یک
     * صدا که کمی زیر و بم شود، باز هم همان یک نفر است — کاربر درست
     * می‌گفت که «دو صدا نیست».
     *
     * دو نکته که در عمل به آن خوردیم:
     *
     * **۱. زبانِ صدا همیشه «en» نیست.** موتور سامسونگ صداهایش را با کد
     * سه‌حرفی می‌دهد (`eng_USA_f00`)، پس فیلتر `language == "en"` روی آن
     * دستگاه **صفر** نتیجه می‌دهد در حالی که چهار صدای انگلیسی موجود
     * است. هر دو حالت باید پذیرفته شود.
     *
     * **۲. هم‌کشور بودن مقدم است.** بین «دو صدای آمریکایی» و «یکی
     * آمریکایی یکی بریتانیایی»، اولی بهتر است: لهجه در یک گفتگوی واحد
     * نباید بی‌دلیل عوض شود، وقتی کل محتوا آمریکایی است.
     */
    private fun pickDialogueVoices() {
        val engine = tts ?: return
        baseVoice = engine.voice

        val english = runCatching {
            engine.voices.orEmpty().filter {
                val l = it.locale.language.lowercase()
                l == "en" || l == "eng"
            }
        }.getOrDefault(emptyList())

        if (english.isEmpty()) return

        // فقط صداهای روی دستگاه. صدای شبکه‌ای («network») به اینترنت
        // وابسته است و وسط مکالمه مکث می‌اندازد یا اصلاً نمی‌آید.
        val local = english.filterNot { it.isNetworkConnectionRequired }
            .ifEmpty { english }
        val us = local.filter { it.locale.country.uppercase() in setOf("US", "USA") }
            .ifEmpty { local }

        // به ترتیب اولویتِ سنجیده‌شده، نه هر صدایی که اول پیدا شود
        val female = FEMALE_VOICES.firstNotNullOfOrNull { fam ->
            us.firstOrNull { it.name.contains(fam) }
        }
        val male = MALE_VOICES.firstNotNullOfOrNull { fam ->
            us.firstOrNull { it.name.contains(fam) }
        }

        if (female != null && male != null) {
            voiceA = female
            voiceB = male
        } else {
            // دستگاه صدای مرد و زن مشخص ندارد. بهترین کارِ ممکن: دو صدای
            // متفاوت با کیفیت بالاتر، هم‌کشور، و گامِ بازتر روی آن.
            val ranked = us.sortedByDescending { it.quality }
            voiceA = ranked.getOrNull(0)
            voiceB = ranked.getOrNull(1) ?: local.firstOrNull { it != voiceA }
        }
        hasGenderedPair = female != null && male != null

        Log.i(TAG, "dialogue voices: A=${voiceA?.name} B=${voiceB?.name} gendered=$hasGenderedPair")
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
        /**
         * فاصله گام دو گوینده.
         *
         * وقتی دو صدای واقعاً متفاوت داریم، گام فقط تأکید است و باید کم
         * بماند تا صدا مصنوعی نشود. وقتی فقط یک صدا هست، گام تنها ابزار
         * تفکیک است و باید بازتر باشد — ±۱۲٪ قبلی به‌سختی شنیده می‌شد.
         */
        const val NARROW_GAP = 0.06f
        const val WIDE_GAP = 0.20f

        const val GOOGLE_TTS = "com.google.android.tts"

        /**
         * صداهای مردانه و زنانه موتور گوگل (en-US)، به ترتیب اولویت.
         *
         * این فهرست **اندازه‌گیری شده** و حدس نیست. همان جمله را با هر ده
         * صدای محلی en-US ساختیم و گام پایه‌شان را سنجیدیم:
         *
         *     iom 141 هرتز · tpd 144 · iol 155 · tpc 169
         *     iob 209 · tpf 224 · sfg 233 · iog 247
         *
         * چرا مهم بود: قاعده رایج در اینترنت می‌گوید «خانواده io مردانه و
         * tp زنانه است». اندازه‌گیری نشان داد این برای سه صدا **غلط**
         * است — `iob` و `iog` زنانه‌اند و `tpd` مردانه. اگر از روی همان
         * قاعده انتخاب می‌کردیم، «علی» صدای ۲۰۹ هرتزی می‌گرفت که کاربر
         * باز هم آن را زن می‌شنید؛ یعنی دقیقاً همان ایراد اول.
         *
         * جفت انتخابی: زن ۲۲۴ هرتز و مرد ۱۴۱ هرتز — نسبت ۱٫۶ برابر.
         */
        val MALE_VOICES = listOf("-x-iom-", "-x-tpd-", "-x-iol-")
        val FEMALE_VOICES = listOf("-x-tpf-", "-x-sfg-", "-x-iob-")

        const val TAG = "SpeechService"
    }
}
