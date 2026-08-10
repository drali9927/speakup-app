package ir.speakup.app.ui.leitner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.data.local.DictionaryDao
import ir.speakup.app.data.local.DictionaryEntryEntity
import ir.speakup.app.data.local.LeitnerCardEntity
import ir.speakup.app.data.local.LeitnerDao
import ir.speakup.app.data.model.LeitnerSchedule
import ir.speakup.app.domain.LearningRepository
import ir.speakup.app.domain.TimeSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewCard(
    val card: LeitnerCardEntity,
    val entry: DictionaryEntryEntity?,
)

data class LeitnerUiState(
    val loading: Boolean = true,
    val learnedCount: Int = 0,
    val dueCount: Int = 0,
    val learningCount: Int = 0,
    /** جلسه مرور جاری؛ خالی یعنی روی صفحه خلاصه هستیم */
    val session: List<ReviewCard> = emptyList(),
    val index: Int = 0,
    val revealed: Boolean = false,
    val correctCount: Int = 0,
    val finished: Boolean = false,
) {
    val current: ReviewCard? get() = session.getOrNull(index)
    val inSession: Boolean get() = session.isNotEmpty() && !finished
    val progress: Float get() = if (session.isEmpty()) 0f else (index + 1f) / session.size
    val isEmpty: Boolean get() = learnedCount + dueCount + learningCount == 0
}

@HiltViewModel
class LeitnerViewModel @Inject constructor(
    private val leitnerDao: LeitnerDao,
    private val dictionaryDao: DictionaryDao,
    private val repo: LearningRepository,
    private val time: TimeSource,
    val speech: ir.speakup.app.domain.SpeechService,
) : ViewModel() {

    private val _state = MutableStateFlow(LeitnerUiState())
    val state: StateFlow<LeitnerUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val now = time.nowMillis()
            val all = leitnerDao.due(now, limit = 1000)
            _state.value = _state.value.copy(
                loading = false,
                dueCount = all.size,
                learnedCount = leitnerDao.countLearned(LeitnerSchedule.MAX_BOX),
                learningCount = leitnerDao.countLearning(LeitnerSchedule.MAX_BOX),
            )
        }
    }

    /** شروع جلسه مرور — حداکثر ۲۰ کارت در هر نوبت تا جلسه کوتاه بماند */
    fun startSession() {
        viewModelScope.launch {
            val cards = leitnerDao.due(time.nowMillis(), limit = SESSION_SIZE)
            val withEntries = cards.map { ReviewCard(it, dictionaryDao.byId(it.entryId)) }
            _state.value = _state.value.copy(
                session = withEntries,
                index = 0,
                revealed = false,
                correctCount = 0,
                finished = false,
            )
        }
    }

    fun reveal() { _state.value = _state.value.copy(revealed = true) }

    /** @param knew آیا کاربر واژه را بلد بود */
    fun answer(knew: Boolean) {
        val s = _state.value
        val card = s.current ?: return
        viewModelScope.launch { repo.reviewLeitnerCard(card.card, knew) }

        val next = s.index + 1
        _state.value = if (next >= s.session.size) {
            s.copy(correctCount = s.correctCount + if (knew) 1 else 0, finished = true)
        } else {
            s.copy(
                index = next,
                revealed = false,
                correctCount = s.correctCount + if (knew) 1 else 0,
            )
        }
        if (next >= s.session.size) refresh()
    }

    fun exitSession() {
        _state.value = _state.value.copy(session = emptyList(), finished = false)
        refresh()
    }

    private companion object { const val SESSION_SIZE = 20 }
}
