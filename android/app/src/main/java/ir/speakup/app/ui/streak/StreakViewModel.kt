package ir.speakup.app.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.domain.StreakRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StreakUiState(
    val loading: Boolean = true,
    val currentLength: Int = 0,
    val longestLength: Int = 0,
    val freezeCount: Int = 0,
    val week: List<StreakRepository.DayCell> = emptyList(),
)

@HiltViewModel
class StreakViewModel @Inject constructor(
    private val repo: StreakRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StreakUiState())
    val state: StateFlow<StreakUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val s = repo.state()
            _state.value = StreakUiState(
                loading = false,
                currentLength = s.currentLength,
                longestLength = s.longestLength,
                freezeCount = s.freezeCount,
                week = repo.lastWeek(),
            )
        }
    }
}
