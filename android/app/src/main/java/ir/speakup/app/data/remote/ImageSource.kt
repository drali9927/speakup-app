package ir.speakup.app.data.remote

import android.content.Context
import ir.speakup.app.BuildConfig

/**
 * منبع تصویر کارت واژه.
 *
 * سه لایه، به این ترتیب — همان الگوی صوت و محتوا:
 *   ۱. تصویر همراه APK (فقط درس اول) — بدون شبکه و فوری
 *   ۲. سرور، با کش دیسک Coil؛ بار دوم از شبکه نمی‌آید
 *   ۳. جای خالی محترمانه، اگر نام فایلی در محتوا نبود
 *
 * چرا فقط درس اول همراه APK می‌ماند: با رشد محتوا نمی‌توان همه تصاویر را
 * در APK گذاشت. سطح A1 با ۲۰۰ تصویر ۹ مگابایت است؛ پنج سطح یعنی حدود
 * ۵۰ مگابایت که برای کاربر ایرانی و بازبینی کافه‌بازار قابل قبول نیست.
 * اما نخستین اجرا نباید به اینترنت وابسته باشد، پس درس اول می‌ماند.
 */
object ImageSource {

    /** تصاویری که برای شروع آفلاین همراه APK می‌مانند */
    private const val BUNDLED_PREFIX = "a1_l01_"

    /**
     * نشانی قابل بارگذاری برای Coil.
     *
     * @param fileName نام فایل از بسته محتوا (ممکن است پسوند png داشته باشد)
     * @return نشانی سرور، مسیر asset، یا null اگر هیچ‌کدام در دسترس نبود
     */
    fun resolve(context: Context, fileName: String?): String? {
        val name = fileName?.takeIf { it.isNotBlank() } ?: return null
        val webp = name.substringBeforeLast('.') + ".webp"

        // تصویر همراه APK اگر باشد، بدون شبکه و فوری است
        if (assetExists(context, "images/$webp")) return "file:///android_asset/images/$webp"
        if (assetExists(context, "images/$name")) return "file:///android_asset/images/$name"

        // وگرنه از سرور — Coil خودش کش دیسک را مدیریت می‌کند
        return BuildConfig.API_BASE_URL.trimEnd('/') + "/images/" + webp
    }

    fun isBundled(fileName: String?): Boolean = fileName?.startsWith(BUNDLED_PREFIX) == true

    private fun assetExists(context: Context, path: String): Boolean =
        runCatching { context.assets.open(path).close(); true }.getOrDefault(false)
}
