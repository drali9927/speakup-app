package ir.speakup.app.data.remote

import android.util.Log
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.domain.TimeSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * وضعیت اشتراک — همیشه از سرور.
 *
 * پیش‌تر یک بولین در DataStore بود که خود اپ روشنش می‌کرد. یعنی برای
 * گرفتن اشتراک رایگان کافی بود یک فایل تنظیمات دستکاری شود. حالا اپ فقط
 * چیزی را نگه می‌دارد که `/v1/me` گفته و تصمیم نهایی هم آنجاست.
 *
 * مقدار ذخیره‌شده صرفاً کش است تا صفحه دروس منتظر شبکه نماند. اگر کاربر
 * ساعت گوشی را عقب ببرد کش منقضی‌شده دوباره معتبر به‌نظر می‌رسد — به همین
 * دلیل از [TimeSource] استفاده می‌کنیم که پس از نخستین همگام‌سازی زمان
 * سرور را می‌شناسد، نه از `System.currentTimeMillis`.
 */
@Singleton
class SubscriptionRepository @Inject constructor(
    private val api: Api,
    private val prefs: AppPreferences,
    private val time: TimeSource,
) {

    /** آیا هم‌اکنون اشتراک فعال است */
    val isSubscribed: Flow<Boolean> =
        prefs.subscriptionExpiresAt.map { it > time.nowMillis() / 1000 }

    val plan: Flow<String?> = prefs.subscriptionPlan

    /**
     * تازه‌سازی از سرور. شکست بی‌صداست: کاربر آفلاین نباید ناگهان
     * اشتراکش را از دست بدهد، و کش قبلی تا انقضایش معتبر می‌ماند.
     */
    suspend fun refresh() {
        runCatching {
            val res = api.me()
            if (!res.isSuccessful) return
            val sub = res.body()?.subscription
            prefs.setSubscription(sub?.plan, sub?.expiresAt)
        }.onFailure { Log.d(TAG, "subscription refresh skipped: ${it.message}") }
    }

    /**
     * ثبت خرید: توکن به سرور می‌رود و سرور با کافه‌بازار راستی‌آزمایی
     * می‌کند. اپ در این مسیر هیچ تصمیمی نمی‌گیرد.
     */
    suspend fun redeem(sku: String, purchaseToken: String): Result<Unit> = runCatching {
        val res = api.verifyPurchase(PurchaseRequest(sku, purchaseToken))
        if (!res.isSuccessful) throw ApiException(res.code().toString(), res.code())
        val b = res.body() ?: throw ApiException("empty_response", res.code())
        prefs.setSubscription(b.plan, b.expiresAt)
    }

    private companion object { const val TAG = "Subscription" }
}
