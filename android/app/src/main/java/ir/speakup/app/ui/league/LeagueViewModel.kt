package ir.speakup.app.ui.league

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.data.remote.Api
import ir.speakup.app.data.remote.LeagueRow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeagueUiState(
    val loading: Boolean = true,
    val tierName: String = "",
    val cohort: Int = 1,
    val rows: List<LeagueRow> = emptyList(),
    /** رده بعدی، برای متنِ «برو به …» */
    val nextTierName: String? = null,
    /** پایان دوره به ثانیه — شمارش معکوس از این حساب می‌شود */
    val endsAt: Long = 0,
    val promoteCount: Int = 0,
    val relegateCount: Int = 0,
    val error: String? = null,
)

@HiltViewModel
class LeagueViewModel @Inject constructor(private val api: Api) : ViewModel() {

    private val _state = MutableStateFlow(LeagueUiState())
    val state: StateFlow<LeagueUiState> = _state.asStateFlow()

    init { refresh() }

    /**
     * جدول همیشه از سرور می‌آید و کش نمی‌شود.
     *
     * رتبه‌ای که چند دقیقه قدیمی باشد بدتر از نبودنش است: کاربر می‌بیند
     * جلو افتاده، در حالی که نیفتاده.
     */
    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { api.league() }
                .onSuccess { res ->
                    val b = res.body()
                    _state.value = if (res.isSuccessful && b != null) {
                        LeagueUiState(
                            loading = false,
                            tierName = b.tierName,
                            cohort = b.cohort,
                            rows = b.rows,
                            nextTierName = b.nextTierName,
                            endsAt = b.endsAt,
                            promoteCount = b.promoteCount,
                            relegateCount = b.relegateCount,
                        )
                    } else {
                        _state.value.copy(loading = false, error = "جدول لیگ در دسترس نیست")
                    }
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = "اتصال به سرور برقرار نشد",
                    )
                }
        }
    }
}
