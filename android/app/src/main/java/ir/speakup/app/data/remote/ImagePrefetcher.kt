package ir.speakup.app.data.remote

import android.content.Context
import coil.imageLoader
import coil.request.ImageRequest

/**
 * پیش‌بارگذاری تصویر کارت‌های بعدی.
 *
 * تصاویر درس‌های ۲ به بعد از سرور می‌آیند (نگاه کنید به [ImageSource]).
 * روی شبکه ایران این یعنی کاربر بعد از زدن «بعدی» یکی دو ثانیه کارت
 * خالی می‌بیند. اما زمان تماشای هر کارت — خواندن واژه و شنیدن صوت —
 * دقیقاً همان فرصتی است که برای گرفتن تصویر بعدی لازم داریم.
 *
 * درخواست‌ها به کش دیسک Coil می‌روند، پس اگر کاربر کارت را رد کرد هم
 * چیزی هدر نرفته: دفعه بعد از کش می‌آید.
 */
object ImagePrefetcher {

    /** چند کارت جلوتر را از قبل بگیریم */
    const val LOOKAHEAD = 3

    /**
     * @param fileNames نام فایل کارت‌های پیشِ رو، به ترتیب
     */
    fun prefetch(context: Context, fileNames: List<String?>) {
        val loader = context.imageLoader
        fileNames.asSequence()
            .take(LOOKAHEAD)
            .mapNotNull { ImageSource.resolve(context, it) }
            // تصویر همراه APK نیازی به گرم‌کردن ندارد
            .filter { !it.startsWith("file:///android_asset/") }
            .forEach { url ->
                loader.enqueue(
                    ImageRequest.Builder(context)
                        .data(url)
                        // فقط کش را گرم می‌کنیم؛ حافظه را با تصاویری که
                        // شاید هرگز دیده نشوند پر نمی‌کنیم.
                        .build()
                )
            }
    }
}
