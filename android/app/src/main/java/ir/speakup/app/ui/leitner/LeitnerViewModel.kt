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
import ir.speakup.app.domain.ReviewMode
import ir.speakup.app.domain.ReviewOptions
import ir.speakup.app.domain.TimeSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewCard(
    val card: LeitnerCardEntity,
    val entry: DictionaryEntryEntity?,
    val mode: ReviewMode = ReviewMode.MEANING,
    /** گزینه‌ها در حالت چندگزینه‌ای؛ در حالت تایپ خالی است */
    val options: List<String> = emptyList(),
) {
    /** پاسخ درست، بسته به شکل پرسش */
    val answer: String
        get() = when (mode) {
            ReviewMode.MEANING -> entry?.translationFa.orEmpty()
            ReviewMode.WORD, ReviewMode.TYPING -> card.word
        }
}

data class LeitnerUiState(
    val loading: Boolean = true,
    val learnedCount: Int = 0,
    val dueCount: Int = 0,
    val learningCount: Int = 0,
    /** جلسه مرور جاری؛ خالی یعنی روی صفحه خلاصه هستیم */
    val session: List<ReviewCard> = emptyList(),
    val index: Int = 0,
    val revealed: Boolean = false,
    /** گزینه‌ای که کاربر زد، یا null اگر هنوز نزده */
    val picked: String? = null,
    /** آنچه در حالت تایپ نوشته شده */
    val typed: String = "",
    val correctCount: Int = 0,
    val finished: Boolean = false,
    // --- واژه‌نامه ---
    val query: String = "",
    val results: List<DictionaryEntryEntity> = emptyList(),
    /** واژه‌ای که کاربر از نتایج واژه‌نامه انتخاب کرده — جدا از `picked` که پاسخ مرور است */
    val pickedWord: DictionaryEntryEntity? = null,
    /** واژه‌های داخل جعبه لایتنر — برای وضعیت دکمه افزودن */
    val inBox: Set<String> = emptySet(),
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
            val entries = cards.map { dictionaryDao.byId(it.entryId) }

            // حواس‌پرت‌کن‌ها نخست از خودِ جلسه می‌آیند — واژه‌هایی که کاربر
            // همین حالا دارد مرورشان می‌کند، باورپذیرترین گزینه‌اند.
            val sessionMeanings = entries.mapNotNull { it?.translationFa?.takeIf(String::isNotBlank) }
            val sessionWords = cards.map { it.word }

            val session = cards.mapIndexed { i, card ->
                val entry = entries[i]
                val mode = ReviewMode.forBox(card.box, ReviewMode.typable(card.word))

                // جلسهٔ کوچک به‌تنهایی گزینه کم دارد و همان دو-سه معنی در
                // همهٔ پرسش‌ها تکرار می‌شوند — روی گوشی دیده شد: سه کارتِ
                // book/pen/pencil هر سه دقیقاً یک مجموعه گزینه داشتند.
                // پس پول را با واژه‌های هم‌ردهٔ دیکشنری محلی پُر می‌کنیم.
                val extra = if (sessionMeanings.size >= MIN_POOL) emptyList() else
                    dictionaryDao.distractors(
                        excludeId = card.entryId,
                        pos = entry?.pos.orEmpty(),
                        rank = entry?.frequencyRank ?: 0,
                    )

                // ترتیب پول مهم است: واژه‌های دیکشنری **جلوتر** می‌آیند.
                // اگر معنی‌های جلسه اول باشند، در جلسهٔ سه‌کارتی هر پرسش
                // باز هم به همان دو معنیِ همسایه می‌رسد و تکرار حل نمی‌شود.
                // فهرست دیکشنری برای هر کارت جداست (هم‌بخشِ کلام و
                // هم‌ردهٔ بسامدِ خودش)، پس هر پرسش گزینه‌های خودش را دارد.
                val meaningPool = extra.map { it.translationFa } + sessionMeanings
                val wordPool = extra.map { it.word } + sessionWords

                val options = when (mode) {
                    ReviewMode.MEANING ->
                        ReviewOptions.build(entry?.translationFa.orEmpty(), meaningPool, i)
                    ReviewMode.WORD ->
                        ReviewOptions.build(card.word, wordPool, i)
                    ReviewMode.TYPING -> emptyList()
                }

                // مدخل دیکشنری گم‌شده یعنی معنی فارسی‌ای وجود ندارد، پس
                // پرسشِ «معنی کدام است؟» گزینهٔ درست ندارد. حالت WORD تنها
                // شکلی است که بدون دیکشنری هم پاسخ درست دارد (خودِ واژه)،
                // و برخلاف TYPING عبارت‌های چندکلمه‌ای را جریمه نمی‌کند.
                val fallback = mode == ReviewMode.MEANING && options.isEmpty()
                val safeMode = if (fallback) ReviewMode.WORD else mode
                val safeOptions =
                    if (!fallback) options else ReviewOptions.build(card.word, wordPool, i)
                ReviewCard(
                    card = card,
                    entry = entry,
                    mode = safeMode,
                    options = safeOptions,
                )
            }

            _state.value = _state.value.copy(
                session = session,
                index = 0,
                revealed = false,
                picked = null,
                typed = "",
                correctCount = 0,
                finished = false,
            )
        }
    }

    fun pick(option: String) {
        val s = _state.value
        if (s.picked != null || s.revealed) return
        _state.value = s.copy(picked = option, revealed = true)
    }

    fun type(text: String) {
        if (_state.value.revealed) return
        _state.value = _state.value.copy(typed = text)
    }

    /** تأیید پاسخ تایپ‌شده */
    fun submitTyped() {
        val s = _state.value
        if (s.revealed || s.typed.isBlank()) return
        _state.value = s.copy(revealed = true)
    }

    /**
     * آیا پاسخ داده‌شده درست بود.
     *
     * در حالت تایپ، فاصله و بزرگی حروف نادیده گرفته می‌شود: هدف
     * سنجش واژه است نه دقت تایپ.
     */
    fun isCorrect(): Boolean {
        val s = _state.value
        val c = s.current ?: return false
        return when (c.mode) {
            ReviewMode.TYPING -> s.typed.trim().equals(c.answer.trim(), ignoreCase = true)
            else -> s.picked == c.answer
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
                picked = null,
                typed = "",
                correctCount = s.correctCount + if (knew) 1 else 0,
            )
        }
        if (next >= s.session.size) refresh()
    }

    // ---------------------------------------------------------- واژه‌نامه

    private var searchJob: kotlinx.coroutines.Job? = null

    fun search(q: String) {
        _state.value = _state.value.copy(query = q, pickedWord = null)
        searchJob?.cancel()
        if (q.isBlank()) {
            _state.value = _state.value.copy(results = emptyList())
            return
        }
        // مکث کوتاه: با هر حرفی که تایپ می‌شود یک پرس‌وجو نزنیم
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(180)
            val rows = dictionaryDao.lookup(q.trim())
            _state.value = _state.value.copy(results = rows)
        }
    }

    fun pickWord(e: DictionaryEntryEntity) {
        _state.value = _state.value.copy(pickedWord = e)
        viewModelScope.launch {
            _state.value = _state.value.copy(inBox = repo.leitnerWords())
        }
    }

    fun closeWord() { _state.value = _state.value.copy(pickedWord = null) }

    /** افزودن واژه‌نامه‌ای به جعبه لایتنر */
    fun addPicked() {
        val e = _state.value.pickedWord ?: return
        viewModelScope.launch {
            repo.addToLeitnerByWord(e.word)
            _state.value = _state.value.copy(inBox = repo.leitnerWords())
            refresh()
        }
    }

    fun exitSession() {
        _state.value = _state.value.copy(session = emptyList(), finished = false)
        refresh()
    }

    private companion object {
        const val SESSION_SIZE = 20
        /** زیر این تعداد، پول جلسه برای ساختن گزینه‌های متنوع کافی نیست */
        const val MIN_POOL = 8
    }
}
