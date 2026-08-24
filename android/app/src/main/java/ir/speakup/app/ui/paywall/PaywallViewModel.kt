package ir.speakup.app.ui.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.data.remote.Api
import ir.speakup.app.data.remote.RemotePlan
import ir.speakup.app.data.remote.SubscriptionRepository
import ir.speakup.app.domain.BillingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * طرح‌های نمایش‌داده‌شده وقتی سرور در دسترس نیست.
 *
 * پی‌وال بدون فهرست، صفحه‌ای خالی است. منبع حقیقت
 * `backend/src/lib/purchase.ts` است و این فقط آینه‌اش.
 *
 * ⚠️ `sku`ها باید با محصولات پنل کافه‌بازار یکی باشند. یادداشت پیشین
 * اینجا می‌گفت قیمتِ کهنه «نمی‌تواند به خرید اشتباه منجر شود» — که
 * دربارهٔ قیمت درست بود، ولی دربارهٔ خودِ sku نه: اگر شناسه غلط باشد،
 * بازار می‌گوید چنین محصولی ندارم و خرید همان‌جا می‌ایستد. پس این فهرست
 * هم باید با پنل هم‌تراز بماند، نه فقط با سند.
 */
private val FALLBACK_PLANS = listOf(
    RemotePlan("monthly", "30d", "اشتراک ۳۰ روزه", 30, 3_990_000, null, "برای شروع"),
    RemotePlan("quarterly", "90d", "اشتراک ۹۰ روزه", 90, 5_990_000, "پرطرفدارترین", null),
    RemotePlan("yearly", "365d", "اشتراک یک‌ساله", 365, 7_990_000, "به‌صرفه‌ترین", null),
)

data class PaywallUiState(
    val plans: List<RemotePlan> = FALLBACK_PLANS,
    /** پیش‌فرض روی طرح نشان‌دار «پرطرفدارترین» — همان قصدِ فهرست چهارتایی قبلی */
    val selected: Int = 1,
    val busy: Boolean = false,
    val message: String? = null,
    val purchased: Boolean = false,
    /**
     * اندازه واقعی محتوا، از خودِ دیتابیس.
     *
     * عدد ثابت ننوشتیم چون با هر سطح تازه‌ای که اضافه شود کهنه می‌شود و
     * کسی هم یادش نمی‌ماند دستی درستش کند — و آن‌وقت روی صفحه پرداخت،
     * عددی می‌ماند که دیگر راست نیست.
     */
    val lessons: Int = 0,
    val words: Int = 0,
    val hours: Int = 0,
)

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val api: Api,
    private val billing: BillingService,
    private val subscriptions: SubscriptionRepository,
    private val contentDao: ir.speakup.app.data.local.ContentDao,
    private val dictionaryDao: ir.speakup.app.data.local.DictionaryDao,
    private val analytics: ir.speakup.app.domain.Analytics,
) : ViewModel() {

    private val _state = MutableStateFlow(PaywallUiState())
    val state: StateFlow<PaywallUiState> = _state.asStateFlow()

    init {
        analytics.track(ir.speakup.app.domain.Ev.PAYWALL_VIEW)
        viewModelScope.launch {
            _state.value = _state.value.copy(
                lessons = contentDao.lessonCount(),
                words = dictionaryDao.entryCount(),
                hours = contentDao.totalMinutes() / 60,
            )
        }
        viewModelScope.launch {
            // قیمت از سرور می‌آید تا تغییرش نیازمند انتشار نسخه نباشد
            runCatching { api.plans() }
                .getOrNull()
                ?.body()
                ?.plans
                ?.takeIf { it.isNotEmpty() }
                ?.let { list ->
                    val keep = _state.value.selected.coerceIn(0, list.lastIndex)
                    _state.value = _state.value.copy(plans = list, selected = keep)
                }
        }
    }

    fun select(index: Int) {
        _state.value = _state.value.copy(selected = index, message = null)
    }

    /**
     * خرید: توکن از درگاه، تأیید از سرور.
     *
     * اپ خودش هرگز اشتراک را فعال نمی‌کند — حتی وقتی درگاه می‌گوید خرید
     * موفق بود. تنها راه فعال شدن، پاسخ مثبت `/v1/purchase/verify` است.
     */
    fun purchase(activity: Activity) {
        val s = _state.value
        if (s.busy) return
        val plan = s.plans.getOrNull(s.selected) ?: return

        _state.value = s.copy(busy = true, message = null)
        viewModelScope.launch {
            when (val r = billing.purchase(activity, plan.sku)) {
                is BillingService.Result.Purchased ->
                    subscriptions.redeem(r.sku, r.purchaseToken)
                        .onSuccess {
                            analytics.trackAndFlush(ir.speakup.app.domain.Ev.PURCHASE_DONE)
                            _state.value = _state.value.copy(busy = false, purchased = true)
                        }
                        .onFailure {
                            // پول کم شده ولی اشتراک ثبت نشده — بدترین حالت ممکن.
                            // کاربر باید بداند که خریدش گم نشده و با اجرای بعدی
                            // (یا لمس دوباره خرید) دوباره تلاش می‌شود؛ توکن در
                            // سرور یک‌بارمصرف است، پس دوباره فرستادنش امن است.
                            _state.value = _state.value.copy(
                                busy = false,
                                message = "پرداخت انجام شد ولی ثبت نشد. اینترنت را بررسی کن و دوباره بزن؛ دوباره از تو پول کم نمی‌شود.",
                            )
                        }

                is BillingService.Result.Cancelled ->
                    _state.value = _state.value.copy(busy = false)

                is BillingService.Result.Unavailable ->
                    _state.value = _state.value.copy(busy = false, message = r.reason)

                is BillingService.Result.Failed ->
                    _state.value = _state.value.copy(busy = false, message = r.message)
            }
        }
    }
}
