package ir.speakup.app.domain

import android.util.Log
import ir.speakup.app.data.local.EventDao
import ir.speakup.app.data.local.EventEntity
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.data.remote.Api
import ir.speakup.app.data.remote.EventDto
import ir.speakup.app.data.remote.EventsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * رویدادهای محصول — قیف.
 *
 * چرا لازم است: بدون آن، بعد از انتشار **نمی‌دانیم کاربر کجا رها
 * می‌کند**. ممکن است ماه‌ها روی قیمت کار کنیم در حالی که نیمی از
 * کاربران در درس اول رفته‌اند.
 *
 * چرا Firebase نه: Firebase Analytics بدون Google Play Services کار
 * نمی‌کند و روی بخشی از گوشی‌های بازار هدف ما در دسترس نیست. یعنی
 * دقیقاً همان کاربرانی که باید بشماریم شمرده نمی‌شوند و قیف سوگیری
 * پیدا می‌کند — که از نداشتنش بدتر است، چون به عددِ غلط اعتماد می‌کنیم.
 *
 * سه قاعده:
 *
 * **۱. هرگز جلوی کاربر را نگیرد.** روی اسکوپ جدا، و هر خطایی بی‌صدا.
 * **۲. رویداد در صف بماند تا فرستاده شود.** آفلاین چیزی گم نمی‌شود.
 * **۳. هیچ داده شخصی در props نرود.** شماره تلفن، متن پاسخ کاربر، هیچ.
 */
@Singleton
class Analytics @Inject constructor(
    private val dao: EventDao,
    private val api: Api,
    private val prefs: AppPreferences,
    private val time: TimeSource,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val flushLock = Mutex()

    fun track(name: String, props: Map<String, String> = emptyMap()) {
        scope.launch { store(name, props) }
    }

    /**
     * ثبت و بلافاصله ارسال.
     *
     * ثبت و ارسال باید در **یک** کوروتین پشت‌سرهم انجام شوند. اگر هرکدام
     * جدا اجرا شوند، ارسال می‌تواند پیش از نشستنِ ردیف در دیتابیس شروع
     * شود و همان رویدادی که می‌خواستیم فوری برسد، در صف جا بماند تا
     * دفعه بعد — که برای رویدادی مثل «خرید» یعنی گزارش با یک نشست تأخیر.
     */
    fun trackAndFlush(name: String, props: Map<String, String> = emptyMap()) {
        scope.launch {
            store(name, props)
            runCatching { flushNow() }
        }
    }

    private suspend fun store(name: String, props: Map<String, String>) {
        runCatching {
            dao.insert(
                EventEntity(
                    name = name,
                    props = if (props.isEmpty()) null else json.encodeToString(props),
                    at = time.nowMillis() / 1000,
                ),
            )
        }
    }

    fun flush() {
        scope.launch { runCatching { flushNow() } }
    }

    private suspend fun flushNow() {
        // قفل تا دو فراخوان همزمان یک دسته را دوبار نفرستند
        flushLock.withLock {
            val installId = installId()
            while (true) {
                val batch = dao.oldest(BATCH)
                if (batch.isEmpty()) return
                val res = api.sendEvents(
                    EventsRequest(
                        installId = installId,
                        events = batch.map {
                            EventDto(
                                name = it.name,
                                at = it.at,
                                props = it.props?.let { p ->
                                    runCatching {
                                        json.decodeFromString<Map<String, String>>(p)
                                    }.getOrNull()
                                },
                            )
                        },
                    ),
                )
                if (!res.isSuccessful) {
                    Log.w(TAG, "ارسال رویداد نشد: ${res.code()}")
                    return
                }
                dao.deleteUpTo(batch.last().id)
                if (batch.size < BATCH) return
            }
        }
    }

    /**
     * شناسه دستگاه.
     *
     * تصادفی و ساخته خودمان است، نه ANDROID_ID یا هر شناسه سخت‌افزاری:
     * آن‌ها بین اپ‌ها مشترک‌اند و برای شمردن قیف لازم نیستند. با پاک
     * کردن اپ، این هم می‌رود — که درست است، چون آن نصب واقعاً تمام شده.
     */
    private suspend fun installId(): String {
        prefs.installId()?.let { return it }
        val fresh = UUID.randomUUID().toString()
        prefs.setInstallId(fresh)
        return fresh
    }

    private companion object {
        const val TAG = "Analytics"
        const val BATCH = 100
        val json = Json { encodeDefaults = true }
    }
}

/** نام رویدادها — ثابت‌اند و **نباید عوض شوند**، وگرنه قیف تاریخی می‌شکند */
object Ev {
    const val APP_OPEN = "app_open"
    const val ONBOARDING_DONE = "onboarding_done"
    const val AUTH_DONE = "auth_done"
    const val ACTIVITY_START = "activity_start"
    const val ACTIVITY_DONE = "activity_done"
    const val LESSON_DONE = "lesson_done"
    const val PAYWALL_VIEW = "paywall_view"
    const val PURCHASE_DONE = "purchase_done"
}
