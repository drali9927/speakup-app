package ir.speakup.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.data.remote.SubscriptionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * وضعیتی که کل ناوبری به آن نیاز دارد.
 *
 * فعلاً فقط اشتراک. دلیل وجودش این است که تصمیم «این درس باز است یا نه»
 * نباید به ساعت گوشی وابسته باشد؛ [SubscriptionRepository] از `TimeSource`
 * استفاده می‌کند که پس از نخستین همگام‌سازی، زمان سرور را می‌شناسد.
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val subscriptions: SubscriptionRepository,
) : ViewModel() {

    val isSubscribed: StateFlow<Boolean> =
        subscriptions.isSubscribed.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        // هر بار باز شدن اپ یک بار تازه می‌شود. اشتراکی که دیروز منقضی شده
        // نباید تا همگام‌سازی بعدی باز بماند، و اشتراکی که روی گوشی دیگر
        // خریداری شده باید همین‌جا هم شناخته شود.
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { subscriptions.refresh() }
    }
}
