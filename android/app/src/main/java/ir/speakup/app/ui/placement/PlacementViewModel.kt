package ir.speakup.app.ui.placement

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.domain.Analytics
import ir.speakup.app.domain.Placement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
private data class Bank(
    val perLevel: Int = 3,
    val stopAfterWrong: Int = 2,
    val passThreshold: Int = 2,
    val questions: List<Q> = emptyList(),
) {
    @Serializable
    data class Q(
        val id: String,
        val level: String,
        val text: String,
        val options: List<String>,
        val answer: Int,
        val explanationFa: String,
    )
}

data class PlacementUiState(
    val loading: Boolean = true,
    val questions: List<Placement.Question> = emptyList(),
    val index: Int = 0,
    /** گزینه‌ای که کاربر زد، یا null اگر هنوز نزده */
    val picked: Int? = null,
    val results: List<Boolean> = emptyList(),
    val correctById: Map<String, Boolean> = emptyMap(),
    val finished: Boolean = false,
    val suggested: String? = null,
    /** برای صفحه نتیجه: هدف روزانه و انگیزه‌ای که کاربر انتخاب کرده */
    val goalXp: Int = 50,
    val motive: String? = null,
) {
    val current: Placement.Question? get() = questions.getOrNull(index)
    val total: Int get() = questions.size
}

@HiltViewModel
class PlacementViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferences,
    private val analytics: Analytics,
) : ViewModel() {

    private val _state = MutableStateFlow(PlacementUiState())
    val state: StateFlow<PlacementUiState> = _state.asStateFlow()

    private var cfg = Placement.Config()

    init {
        analytics.track(EV_START)
        // `update` و نه `_state.value = _state.value.copy(...)`.
        //
        // این دو کوروتین هم‌زمان اجرا می‌شوند. با خواندن-تغییر-نوشتنِ
        // معمولی، این یکی می‌توانست مقدارِ پیش از بارگذاری را بخواند و
        // بعد از آن بنویسد — یعنی `loading = false` را پاک کند و صفحه
        // برای همیشه روی چرخنده بماند. روی گوشی دقیقاً همین شد.
        viewModelScope.launch {
            val goal = prefs.dailyGoalXp.first()
            val motive = prefs.motive.first()
            _state.update { it.copy(goalXp = goal, motive = motive) }
        }
        viewModelScope.launch {
            val bank = withContext(Dispatchers.IO) {
                runCatching {
                    val raw = context.assets.open("placement.json")
                        .bufferedReader().use { it.readText() }
                    json.decodeFromString<Bank>(raw)
                }.getOrElse { Bank() }
            }
            cfg = Placement.Config(bank.perLevel, bank.stopAfterWrong, bank.passThreshold)
            val qs = Placement.ordered(
                bank.questions.map {
                    Placement.Question(it.id, it.level, it.text, it.options, it.answer, it.explanationFa)
                },
            )
            _state.update { it.copy(loading = false, questions = qs) }
        }
    }

    fun pick(option: Int) {
        val s = _state.value
        if (s.picked != null) return
        val q = s.current ?: return
        val correct = option == q.answer
        _state.value = s.copy(
            picked = option,
            results = s.results + correct,
            correctById = s.correctById + (q.id to correct),
        )
    }

    fun next() {
        val s = _state.value
        if (s.picked == null) return

        // توقف زودهنگام: کاربری که به سقفش رسیده نباید مجبور شود
        // پرسش‌های سخت‌ترِ بعدی را هم غلط بزند.
        val stop = Placement.shouldStop(s.results, cfg)
        val last = s.index >= s.questions.lastIndex

        if (stop || last) {
            val level = Placement.suggest(s.questions, s.correctById, cfg)
            _state.value = s.copy(finished = true, suggested = level, picked = null)
            analytics.track(EV_DONE, mapOf("level" to level, "asked" to s.results.size.toString()))
        } else {
            _state.value = s.copy(index = s.index + 1, picked = null)
        }
    }

    /** سطح پیشنهادی را ذخیره می‌کند و آزمون را انجام‌شده علامت می‌زند */
    fun accept(onDone: () -> Unit) {
        val level = _state.value.suggested ?: Placement.LEVELS.first()
        viewModelScope.launch {
            prefs.setCurrentLevel(level)
            prefs.setPlaced()
            onDone()
        }
    }

    /**
     * رد کردن آزمون.
     *
     * سطح دست نمی‌خورد و از A1 شروع می‌کند، ولی «انجام‌شده» علامت
     * می‌خورد تا هر بار باز کردن اپ دوباره جلوی کاربر سبز نشود.
     */
    fun skip(onDone: () -> Unit) {
        analytics.track(EV_SKIP)
        viewModelScope.launch {
            prefs.setPlaced()
            onDone()
        }
    }

    private companion object {
        const val EV_START = "placement_start"
        const val EV_DONE = "placement_done"
        const val EV_SKIP = "placement_skip"
        val json = Json { ignoreUnknownKeys = true }
    }
}
