package ir.speakup.app.domain

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * صدای بازخورد پاسخ درست و نادرست.
 *
 * چرا با کد ساخته می‌شود و نه فایل صوتی: هر فایل، حجم APK را بالا می‌برد
 * و باید در بازبینی کافه‌بازار هم توضیح داده شود. این آهنگ‌ها چند صد بایت
 * محاسبه‌اند و صفر بایت روی دیسک.
 *
 * طراحی صدا عمدی است و از رفتار دولینگو و لرنیت گرفته شده:
 *
 * **درست** = دو نت بالارونده، کوتاه و روشن. بالا رفتن گام، حس «پیش رفتن»
 * می‌دهد.
 *
 * **نادرست** = یک نت پایین و کوتاه، نه بوق خشن. صدای تنبیه‌گر باعث می‌شود
 * کاربر از خطر کردن پرهیز کند — دقیقاً برعکس چیزی که یادگیری لازم دارد.
 *
 * هر دو صدا کوتاه‌اند (زیر ۳۰۰ میلی‌ثانیه)؛ صدای بلند در تمرین سریع
 * آزاردهنده می‌شود.
 *
 * ⚠️ اگر کاربر گوشی را روی بی‌صدا گذاشته، هیچ صدایی پخش نمی‌شود — همان
 * انتظار درست از یک اپ. لرزش کوتاه جایش را می‌گیرد.
 */
@Singleton
class FeedbackSound @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val sampleRate = 22_050

    private val audioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun correct() {
        play(listOf(Note(660f, 90), Note(880f, 150)))
        vibrate(20)
    }

    fun wrong() {
        play(listOf(Note(200f, 180)))
        vibrate(45)
    }

    /** رسیدن به هدف روزانه — سه نت بالارونده، کمی جشن‌گونه */
    fun celebrate() {
        play(listOf(Note(660f, 90), Note(880f, 90), Note(1174f, 220)))
        vibrate(30)
    }

    private data class Note(val hz: Float, val ms: Int)

    private fun play(notes: List<Note>) {
        if (isSilent()) return
        scope.launch {
            runCatching {
                val samples = notes.flatMap { tone(it) }.toShortArray()
                writeAndPlay(samples)
            }
        }
    }

    /**
     * یک نت با پوشش نمایی.
     *
     * بدون محو شدن انتهای موج، هر نت با یک «کلیک» تمام می‌شود — قطع ناگهانی
     * موج سینوسی، پرش دامنه است و بلندگو آن را به‌صورت تق می‌شنواند.
     */
    private fun tone(note: Note): List<Short> {
        val count = sampleRate * note.ms / 1000
        return List(count) { i ->
            val t = i.toFloat() / sampleRate
            val envelope = exp(-3.5f * i / count)
            (sin(2.0 * PI * note.hz * t) * envelope * Short.MAX_VALUE * 0.35).toInt().toShort()
        }
    }

    private fun writeAndPlay(samples: ShortArray) {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    // USAGE_ASSISTANCE_SONIFICATION یعنی «صدای رابط کاربری»:
                    // با موسیقی کاربر قاطی نمی‌شود و آن را قطع نمی‌کند.
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(samples, 0, samples.size)
        track.setNotificationMarkerPosition(samples.size)
        track.setPlaybackPositionUpdateListener(
            object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(t: AudioTrack?) { runCatching { t?.release() } }
                override fun onPeriodicNotification(t: AudioTrack?) {}
            }
        )
        track.play()
    }

    /** حالت بی‌صدا و لرزش هر دو باید ساکت بمانند */
    private fun isSilent(): Boolean =
        audioManager?.ringerMode != AudioManager.RINGER_MODE_NORMAL

    private fun vibrate(ms: Long) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        runCatching {
            v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
