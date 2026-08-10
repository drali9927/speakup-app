package ir.speakup.app.data.remote

import android.util.Log
import ir.speakup.app.domain.TimeSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * زمان معتبر — جایگزین `DeviceTimeSource`.
 *
 * یک بار از سرور زمان می‌گیرد و **اختلاف** را نگه می‌دارد، نه خود زمان را.
 * از آن به بعد `elapsedRealtime` دستگاه به اختلاف اضافه می‌شود؛ یعنی
 * حتی اگر کاربر وسط کار ساعت گوشی را جلو ببرد، محاسبه تغییر نمی‌کند.
 *
 * تا وقتی همگام‌سازی انجام نشده `isTrusted` نادرست است و لایه بالاتر
 * می‌داند که نباید به این زمان برای زنجیره اتکا کند.
 *
 * ⚠️ این فقط لایه دوم دفاع است. مرجع نهایی زنجیره سرور است:
 * کلاینت هرگز طول زنجیره یا تاریخ را نمی‌فرستد (`backend/src/lib/streak.ts`).
 */
@Singleton
class ServerTimeSource @Inject constructor(
    private val api: Api,
) : TimeSource {

    /** میلی‌ثانیه اختلاف بین زمان سرور و ساعت دستگاه */
    @Volatile private var offsetMillis: Long = 0

    @Volatile private var synced = false

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("Asia/Tehran")
    }

    init { refresh() }

    fun refresh() {
        scope.launch {
            runCatching {
                val before = System.currentTimeMillis()
                val res = api.serverTime()
                val after = System.currentTimeMillis()
                val body = res.body() ?: return@runCatching

                // نصف زمان رفت‌وبرگشت را جبران می‌کنیم تا اختلاف دقیق‌تر شود
                val roundTrip = after - before
                val serverNow = body.epochSeconds * 1000 + roundTrip / 2
                offsetMillis = serverNow - after
                synced = true
                Log.i(TAG, "server time synced, offset=${offsetMillis}ms")
            }.onFailure {
                Log.w(TAG, "server time unavailable, falling back to device clock: ${it.message}")
            }
        }
    }

    override fun nowMillis(): Long = System.currentTimeMillis() + offsetMillis

    override fun today(): String = fmt.format(nowMillis())

    override val isTrusted: Boolean get() = synced

    private companion object { const val TAG = "ServerTimeSource" }
}
