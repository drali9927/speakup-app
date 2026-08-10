package ir.speakup.app.domain

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * منبع زمان برای زنجیره مطالعه.
 *
 * زنجیره تنها چیزی در محصول است که ارزش تقلب دارد: کاربر می‌تواند
 * ساعت گوشی را جلو ببرد و زنجیره بسازد. رقیب برای همین از NTP
 * اختصاصی استفاده می‌کند (سند ۰۵ بخش ۵.۲).
 *
 * تا وقتی بک‌اند آماده نیست، از ساعت دستگاه استفاده می‌شود اما
 * پشت این واسط، تا جایگزینی با NTP فقط تعویض پیاده‌سازی باشد
 * و هیچ‌جای دیگر اپ تغییر نکند.
 */
interface TimeSource {
    fun nowMillis(): Long

    /** تاریخ امروز به شکل yyyy-MM-dd — کلید ردیف‌های زنجیره */
    fun today(): String

    /** آیا زمان از منبع قابل اعتماد آمده یا از ساعت دستگاه */
    val isTrusted: Boolean

    /** تاریخ n روز پیش به شکل yyyy-MM-dd — برای نمودار امتیاز */
    fun daysAgo(n: Int): String {
        val f = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getDefault() }
        return f.format(nowMillis() - n * 86_400_000L)
    }
}

@Singleton
class DeviceTimeSource @Inject constructor() : TimeSource {

    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getDefault()
    }

    override fun nowMillis(): Long = System.currentTimeMillis()

    override fun today(): String = fmt.format(nowMillis())

    /**
     * ⚠️ قابل دستکاری است. پیش از انتشار عمومی باید با پیاده‌سازی
     * مبتنی بر NTP جایگزین شود، وگرنه زنجیره بی‌معنا می‌شود.
     */
    override val isTrusted: Boolean = false
}

/** تعداد روز اختلاف بین دو تاریخ yyyy-MM-dd */
fun daysBetween(from: String, to: String): Int {
    val f = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getDefault() }
    val a = f.parse(from)?.time ?: return 0
    val b = f.parse(to)?.time ?: return 0
    return ((b - a) / 86_400_000L).toInt()
}
