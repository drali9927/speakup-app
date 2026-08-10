package ir.speakup.app.data.content

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.data.remote.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * به‌روزرسانی محتوا از سرور.
 *
 * تا امروز محتوا فقط داخل APK بود؛ برای ۱۰ درس قابل قبول بود اما با
 * رشد محتوا نه حجم APK قابل قبول می‌ماند و نه هر اصلاح تایپی ارزش
 * انتشار نسخه تازه دارد.
 *
 * ترتیب اولویت — همان الگوی WebP/PNG و صوت:
 *   ۱. بسته دانلودشده در حافظه دستگاه
 *   ۲. بسته همراه APK (تا اپ در نخستین اجرای بدون اینترنت هم کار کند)
 *
 * نسخه از هش خود فایل می‌آید، پس اگر دانلود نیمه‌کاره بماند، نسخه ذخیره
 * نمی‌شود و دفعه بعد دوباره تلاش می‌شود.
 */
@Singleton
class ContentUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: Api,
    private val prefs: AppPreferences,
    private val importer: ContentImporter,
    private val json: Json,
) {

    sealed interface Result {
        data object UpToDate : Result
        data class Updated(val level: String, val version: String) : Result
        data class Failed(val reason: String) : Result
    }

    private fun fileFor(level: String) = File(context.filesDir, "content_${level.lowercase()}.json")

    /** بسته محلی، اگر دانلود شده باشد */
    fun downloadedBundle(level: String): File? = fileFor(level).takeIf { it.exists() && it.length() > 0 }

    /**
     * همه سطوحی که سرور اعلام کرده را بررسی می‌کند.
     *
     * پیش‌تر فقط سطح انتخاب‌شده را می‌گرفت؛ یعنی اگر کاربر روی A1 بود،
     * اصلاحات A2 هرگز به دستش نمی‌رسید تا وقتی خودش سطح را عوض کند.
     */
    suspend fun checkAndUpdateAll(): List<Result> = withContext(Dispatchers.IO) {
        val manifest = runCatching { api.contentManifest() }.getOrNull()
        val levels = manifest?.body()?.bundles?.map { it.level }.orEmpty()
        levels.map { checkAndUpdate(it) }
    }

    suspend fun checkAndUpdate(level: String = "A1"): Result = withContext(Dispatchers.IO) {
        runCatching {
            val manifest = api.contentManifest()
            if (!manifest.isSuccessful) return@runCatching Result.Failed("http_${manifest.code()}")

            val remote = manifest.body()?.bundles?.firstOrNull { it.level.equals(level, true) }
                ?: return@runCatching Result.Failed("bundle_not_listed")

            val localVersion = prefs.contentVersion(level).first()
            if (localVersion == remote.version && downloadedBundle(level) != null) {
                return@runCatching Result.UpToDate
            }

            val res = api.contentBundle(level)
            if (!res.isSuccessful) return@runCatching Result.Failed("http_${res.code()}")
            val body = res.body()?.string() ?: return@runCatching Result.Failed("empty_body")

            // پیش از نوشتن، معتبر بودن بسته را می‌سنجیم. نوشتن یک فایل
            // خراب روی بسته سالم، اپ را برای همیشه بی‌محتوا می‌کند.
            val parsed = json.decodeFromString<ContentBundle>(body)
            if (parsed.lessons.isEmpty() || parsed.items.isEmpty()) {
                return@runCatching Result.Failed("bundle_empty")
            }

            // نوشتن اتمی: اول فایل موقت، بعد جابه‌جایی
            val tmp = File(context.filesDir, "content_${level.lowercase()}.tmp")
            tmp.writeText(body)
            if (!tmp.renameTo(fileFor(level))) {
                tmp.delete()
                return@runCatching Result.Failed("write_failed")
            }

            importer.importBundle(parsed)
            prefs.setContentVersion(level, remote.version)
            Log.i(TAG, "content updated: $level → ${remote.version} (${parsed.lessons.size} lessons)")
            Result.Updated(level, remote.version)
        }.getOrElse {
            Log.w(TAG, "content update failed: ${it.message}")
            Result.Failed(it.message ?: "unknown")
        }
    }

    private companion object { const val TAG = "ContentUpdater" }
}
