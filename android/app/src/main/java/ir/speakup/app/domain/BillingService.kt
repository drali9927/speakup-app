package ir.speakup.app.domain

import android.app.Activity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * درگاه خرید درون‌برنامه‌ای.
 *
 * مثل بقیه لایه‌های بیرونی (صوت، تشخیص گفتار، احراز هویت) پشت یک واسط
 * است. پیاده‌سازی واقعی به کتابخانه Poolakey و یک حساب فعال کافه‌بازار با
 * محصولات تعریف‌شده نیاز دارد؛ تا آن زمان [UnavailableBillingService]
 * بسته می‌شود.
 *
 * ⚠️ نکته‌ای که نباید عوض شود: نتیجه این سرویس فقط یک **توکن خرید** است،
 * نه «کاربر مشترک شد». تصمیم درباره اشتراک را سرور می‌گیرد
 * (`POST /v1/purchase/verify`). اگر روزی این کلاس مستقیم اشتراک را فعال
 * کرد، با یک APK دستکاری‌شده اشتراک رایگان می‌شود.
 */
interface BillingService {

    sealed interface Result {
        /** خرید موفق — توکن باید برای راستی‌آزمایی به سرور برود */
        data class Purchased(val sku: String, val purchaseToken: String) : Result
        data object Cancelled : Result
        /** درگاه در دسترس نیست: بازار نصب نیست، یا هنوز وصل نشده */
        data class Unavailable(val reason: String) : Result
        data class Failed(val message: String) : Result
    }

    suspend fun purchase(activity: Activity, sku: String): Result
}

/**
 * پیاده‌سازی موقت تا زمان اتصال Poolakey.
 *
 * عمداً هیچ اشتراکی نمی‌دهد. نسخه قبلی این جریان یک فلگ محلی می‌گذاشت و
 * پی‌وال را باز می‌کرد؛ اگر همان‌طور منتشر می‌شد، دکمه «خرید» بدون هیچ
 * پرداختی همه دروس را باز می‌کرد.
 */
@Singleton
class UnavailableBillingService @Inject constructor() : BillingService {
    override suspend fun purchase(activity: Activity, sku: String): BillingService.Result =
        BillingService.Result.Unavailable("درگاه پرداخت هنوز فعال نشده است")
}
