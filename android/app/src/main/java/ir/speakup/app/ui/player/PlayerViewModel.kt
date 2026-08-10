package ir.speakup.app.ui.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.data.local.ContentDao
import ir.speakup.app.data.local.optionKey
import ir.speakup.app.data.model.ActivityType
import ir.speakup.app.domain.AnswerChecker
import ir.speakup.app.domain.LearningRepository
import ir.speakup.app.domain.SpeechMatcher
import ir.speakup.app.domain.SpeechService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

/**
 * جای پاسخ درست میان گزینه‌ها.
 *
 * باید معین باشد (با هر بازسازی UI گزینه‌ها زیر انگشت کاربر جابه‌جا نشوند)
 * اما به هیچ چیزی که کاربر می‌بیند وابسته نباشد. نسخه‌های پیشین هر دو شرط
 * را با هم نداشتند:
 *
 * - `(متن + شناسه).hashCode()` ترتیب یکسانی برای همه آیتم‌ها می‌داد، چون
 *   شناسه‌های هم‌طول فقط یک عدد **ثابت** به هش همه نامزدها اضافه می‌کنند.
 * - `جایگاه + طول پاسخ` به طول واژه گره خورده بود — چیزی که کاربر می‌بیند،
 *   پس در اصل قابل بهره‌برداری است.
 * - مانده‌گیریِ مستقیم روی `hashCode` هم کار نمی‌کند: ضریب ۳۱ به پیمانه ۳
 *   برابر یک است، پس `hashCode() % 3` به جمعِ کدِ نویسه‌ها فرو می‌ریزد و
 *   دوباره با طول و الفبا همبسته می‌شود.
 *
 * آمیزه پایانیِ MurmurHash3 هر سه را حل می‌کند. اندازه‌گیری روی هر ۴۸۰
 * فعالیتِ چهار سطح: دنباله‌های پشت‌سرهمِ هم‌جایگاه ۱۳٪، دقیقاً هم‌تراز با
 * مبنای تصادفی واقعی (۱۲٪).
 */
internal fun answerSlot(answer: String, index: Int, slots: Int): Int {
    var h = answer.hashCode() xor (index * 0x9E3779B9L.toInt())
    h = h xor (h ushr 16); h *= 0x85EBCA6BL.toInt()
    h = h xor (h ushr 13); h *= 0xC2B2AE35L.toInt()
    h = h xor (h ushr 16)
    return (h ushr 1) % slots
}

/** مرحله افشای تدریجی کارت واژه — سند ۰۳ / F-02 */
enum class RevealStep {
    WORD,
    EXAMPLE,
    TRANSLATION,
    /**
     * آزمون کوتاه معنی — بلافاصله بعد از دیدن واژه.
     *
     * دیدن یک واژه حس یادگیری می‌دهد، اما تا وقتی از حافظه بازیابی نشود
     * چیزی نمی‌ماند. رقیب هم همین را دارد؛ تفاوت ما این است که پاسخ غلط
     * واژه را خودکار وارد لایتنر می‌کند.
     */
    CHECK,
}

data class PlayerUiState(
    val loading: Boolean = true,
    val title: String = "",
    val type: ActivityType = ActivityType.FLASHCARD,
    val items: List<ActivityItemEntity> = emptyList(),
    val index: Int = 0,
    val reveal: RevealStep = RevealStep.WORD,
    val input: String = "",
    /** ترتیب فعلی قطعات در تمرین مرتب‌سازی */
    val arranged: List<String> = emptyList(),
    val pool: List<String> = emptyList(),
    val verdict: AnswerChecker.Verdict? = null,
    val correctCount: Int = 0,
    /** وقتی پاسخ نادرست بود و واژه خودکار وارد لایتنر شد */
    val addedToLeitner: Boolean = false,
    val finished: Boolean = false,
    /** نمای واژگان باز است */
    val showWordList: Boolean = false,
    /** واژه‌هایی که همین حالا در جعبه لایتنر هستند — برای وضعیت دکمه افزودن */
    val inLeitner: Set<String> = emptySet(),
    /** بازخورد کلمه‌به‌کلمه تمرین گفتار — سبز/قرمز روی کلمات جمله هدف */
    val speechWords: List<SpeechMatcher.WordResult> = emptyList(),
    /** گزینه‌های آزمون معنی کارت واژه — به ترتیب نمایش */
    val wordCheckOptions: List<String> = emptyList(),
    /** گزینه‌ای که کاربر زد، یا null اگر هنوز نزده */
    val wordCheckPicked: String? = null,
    /** خط در حال پخش در بخش مکالمه */
    val dialogueLine: Int? = null,
    val dialoguePlaying: Boolean = false,
    val showTranslation: Boolean = true,
    /** جای حروف واژه در تمرین ساخت واژه */
    val letterSlots: List<Char?> = emptyList(),
    val letterPool: List<LetterTile> = emptyList(),
    /** کاربر صفحه‌کلید را ترجیح داده — کاشی‌ها کنار می‌روند */
    val useKeyboard: Boolean = false,
    /** کاشی‌های بازی جفت‌یابی — فقط دور جاری */
    val matchTiles: List<ir.speakup.app.ui.match.MatchTile> = emptyList(),
    val matchSelected: String? = null,
    val matchWrongPair: Pair<String, String>? = null,
    val matchRound: Int = 0,
    val matchRoundCount: Int = 0,
    /** امتیاز این نوبت — روی صفحه پایان نشان داده می‌شود */
    val earnedXp: Int = 0,
    /** با همین نوبت، هدف امروز کامل شد */
    val goalReached: Boolean = false,
    /**
     * فعالیت بعدیِ همین بخش، یا null اگر بخش تمام شده.
     *
     * وقتی پر است، صفحه پایان «ادامه» می‌دهد و کاربر بی‌آنکه به فهرست
     * درس برگردد به تمرین بعدی می‌رود. وقتی null است، بخش تمام شده و
     * همان‌جا جشن بخش گرفته می‌شود.
     */
    val nextInSection: String? = null,
    /** شماره فعالیت جاری در بخش و تعداد کل — «۳ از ۴» روی صفحه پایان */
    val stepInSection: Int = 0,
    val stepsInSection: Int = 0,
    /** آمار بخش تا پیش از این فعالیت — برای جمع‌بندیِ پایان بخش */
    val sectionCorrect: Int = 0,
    val sectionTotal: Int = 0,
    /** روایت صوتی صفحه آموزش در حال پخش است */
    val narrating: Boolean = false,
    val narrationRate: Float = 1.0f,
) {
    val current: ActivityItemEntity? get() = items.getOrNull(index)
    val progress: Float get() = if (items.isEmpty()) 0f else (index + 1f) / items.size
    val isLast: Boolean get() = index >= items.lastIndex
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val contentDao: ContentDao,
    private val repo: LearningRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val appContext: android.content.Context,
    val speech: ir.speakup.app.domain.SpeechService,
    val recognizer: ir.speakup.app.domain.SpeechRecognitionService,
    private val sound: ir.speakup.app.domain.FeedbackSound,
    savedState: SavedStateHandle,
) : ViewModel() {

    private val activityId: String = checkNotNull(savedState["activityId"])

    /**
     * پاسخ‌های درست و کلِ سوال‌های بخش، تا پیش از این فعالیت.
     *
     * از فعالیت قبلیِ همین بخش می‌آید. صفحه پایانِ بخش با این، آمارِ کلِ
     * بخش را می‌گوید و نه آمارِ تنها آخرین تمرین — که در گرامر «۱ از ۱»
     * می‌شد، برای بخشی که کاربر هفت سوالش را جواب داده بود.
     */
    private val carriedCorrect: Int = savedState["correct"] ?: 0
    private val carriedTotal: Int = savedState["total"] ?: 0

    /** وقتی چند کارت آموزشی یکجا نمایش داده می‌شوند، همه باید تمام‌شده علامت بخورند */
    private var siblingTeachingIds: List<String> = emptyList()

    private val _state = MutableStateFlow(PlayerUiState())
    val state: StateFlow<PlayerUiState> = _state.asStateFlow()

    /** انتظار برای آماده شدن TTS؛ با هر گام تازه لغو می‌شود */
    private var autoSpeakJob: Job? = null

    init {
        viewModelScope.launch {
            val activity = contentDao.activity(activityId)
            var items = contentDao.items(activityId)
            val type = ActivityType.fromOrNull(activity?.activityType) ?: inferType(items)

            // بخش آموزش یک صفحه پیوسته است، نه کارت‌های پشت‌سرهم:
            // همه کارت‌های آموزشی همان بخش را یکجا برمی‌داریم تا کاربر
            // مجبور نباشد بین آن‌ها از صفحه درس عبور کند.
            if (type == ActivityType.TEACHING && activity != null) {
                items = contentDao.itemsOfType(activity.sectionId, ActivityType.TEACHING.name)
                siblingTeachingIds = contentDao.activityIdsOfType(
                    activity.sectionId, ActivityType.TEACHING.name,
                )
            }
            // ادامه از همان‌جا که رها شد — نه از اول
            val resumeAt = if (type.isSequential && items.isNotEmpty()) {
                repo.resumeIndexFor(activityId).coerceIn(0, items.lastIndex)
            } else 0

            // فعالیت‌های هم‌بخش، برای زنجیره کردنشان بدون بازگشت به فهرست.
            //
            // بخش آموزش کنار گذاشته می‌شود چون خودش همه کارت‌های بخش را
            // یکجا نشان می‌دهد و «بعدی»اش معنا ندارد.
            val siblings = if (activity != null && type != ActivityType.TEACHING) {
                contentDao.activities(activity.sectionId)
            } else emptyList()
            val at = siblings.indexOfFirst { it.id == activityId }

            _state.value = PlayerUiState(
                loading = false,
                title = activity?.title.orEmpty(),
                type = type,
                items = items,
                index = resumeAt,
                nextInSection = siblings.getOrNull(at + 1)?.id,
                stepInSection = at + 1,
                stepsInSection = siblings.size,
                sectionCorrect = carriedCorrect,
                sectionTotal = carriedTotal,
            ).withItemSetup()
            repo.markActivityStarted(activityId, resumeAt)
            autoSpeak()
            refreshLeitnerSet()
        }
    }

    /** اگر به هر دلیل نوع فعالیت پیدا نشد، از شکل داده حدس می‌زنیم */
    private fun inferType(items: List<ActivityItemEntity>): ActivityType = when {
        items.any { it.imageFile != null } -> ActivityType.FLASHCARD
        items.any { it.optionsList().isNotEmpty() } -> ActivityType.MULTIPLE_CHOICE
        items.all { it.correctAnswer == null } -> ActivityType.TEACHING
        else -> ActivityType.FREE_TEXT
    }

    /** آماده‌سازی وضعیت برای آیتم جاری (مثلاً بُر زدن قطعات در مرتب‌سازی) */
    private fun PlayerUiState.withItemSetup(): PlayerUiState {
        val item = items.getOrNull(index) ?: return this

        // مرور واژگان: کاشی حروف به‌جای صفحه‌کلید
        if (type == ActivityType.TRANSLATE_TO_EN) return withLetterSetup(item)

        if (type != ActivityType.REORDER) return this
        // صورت سوال به شکل "is / my / this / friend" است
        val pieces = item.prompt.split('/').map { it.trim() }.filter { it.isNotEmpty() }
        // بُر زدن قطعی بر اساس شناسه آیتم تا با هر بازسازی UI تغییر نکند
        val shuffled = pieces.sortedBy { (it + item.id).hashCode() }
        return copy(arranged = emptyList(), pool = shuffled)
    }

    /**
     * ساخت کاشی‌های حروف برای یک واژه.
     *
     * حروف اضافی از حروف پرکاربرد انگلیسی می‌آیند و نه تصادفی محض؛ اگر
     * حرف اضافی «z» و «q» باشد، کاربر بدون دانستن واژه هم حذفشان می‌کند.
     *
     * ترتیب معین است (بر پایه شناسه آیتم) تا با هر بازسازی UI کاشی‌ها
     * زیر انگشت کاربر جابه‌جا نشوند.
     */
    private fun PlayerUiState.withLetterSetup(item: ActivityItemEntity): PlayerUiState {
        val word = item.correctAnswer.orEmpty().trim()
        if (word.isEmpty()) return this
        val letters = word.filter { !it.isWhitespace() }.lowercase().toList()

        // چند حرف اضافی؛ برای واژه بلند کمتر، وگرنه صفحه پر می‌شود
        val decoyCount = when {
            letters.size <= 4 -> 4
            letters.size <= 7 -> 3
            else -> 2
        }
        val common = "aeiorstnlcdum".filter { it !in letters }
        val decoys = common.toList().sortedBy { (it.toString() + item.id).hashCode() }.take(decoyCount)

        val pool = (letters + decoys)
            .sortedBy { (it.toString() + item.id + "p").hashCode() }
            .map { LetterTile(it) }

        return copy(
            letterSlots = List(letters.size) { null },
            letterPool = pool,
            input = "",
        )
    }

    fun pickLetter(poolIndex: Int) {
        val s = _state.value
        val tile = s.letterPool.getOrNull(poolIndex) ?: return
        if (tile.used || s.verdict != null) return
        val slot = s.letterSlots.indexOfFirst { it == null }
        if (slot < 0) return

        val slots = s.letterSlots.toMutableList().also { it[slot] = tile.ch }
        val pool = s.letterPool.toMutableList().also { it[poolIndex] = tile.copy(used = true) }
        _state.value = s.copy(
            letterSlots = slots,
            letterPool = pool,
            input = slots.filterNotNull().joinToString(""),
        )
    }

    fun unpickLetter(slotIndex: Int) {
        val s = _state.value
        val ch = s.letterSlots.getOrNull(slotIndex) ?: return
        if (s.verdict != null) return

        // نخستین کاشی مصرف‌شده با همان حرف آزاد می‌شود — کدام یکی مهم نیست
        val poolIndex = s.letterPool.indexOfFirst { it.used && it.ch == ch }
        val slots = s.letterSlots.toMutableList().also { it[slotIndex] = null }
        val pool = s.letterPool.toMutableList()
        if (poolIndex >= 0) pool[poolIndex] = pool[poolIndex].copy(used = false)

        _state.value = s.copy(
            letterSlots = slots,
            letterPool = pool,
            input = slots.filterNotNull().joinToString(""),
        )
    }

    /** برای کسی که تایپ کردن را ترجیح می‌دهد — همان الگوی دولینگو */
    fun toggleKeyboard() {
        val s = _state.value
        _state.value = if (s.useKeyboard) {
            s.copy(useKeyboard = false).withLetterSetup(s.current ?: return)
        } else {
            s.copy(useKeyboard = true, input = "")
        }
    }

    // ---------- کارت واژه ----------

    /** پخش صوت آیتم جاری — متن TTS اگر تعریف شده، وگرنه خود صورت سوال */
    fun speakCurrent(rate: Float = 1.0f) {
        val item = _state.value.current ?: return
        speech.speak(item.ttsText ?: item.prompt, item.id, rate)
    }

    /**
     * پخش خودکار صوت هر گام کارت واژه.
     *
     * رفتاری که کاربر از رقیب خواست و در بررسی دستگاه هم تأیید شد: تا کارت
     * باز می‌شود واژه خوانده می‌شود، با «بعدی» جمله مثال، و روی ترجمه فارسی
     * هیچ صدایی پخش نمی‌شود.
     *
     * چرا `ttsText` را نمی‌خوانیم: آن رشته واژه و جمله را با هم دارد
     * («family. I love my family.») و برای گام‌به‌گام خواندن به درد نمی‌خورد.
     */
    private fun autoSpeak() {
        val s = _state.value
        val item = s.current ?: return

        // تمرین شنیداری بدون شنیدن اصلاً قابل انجام نیست؛ رقیب هم اینجا
        // خودکار پخش می‌کند. مرحله افشا برایش معنا ندارد.
        if (s.type == ActivityType.LISTENING) {
            speakWhenReady(item.prompt, item.id)
            return
        }
        if (s.type != ActivityType.FLASHCARD) return
        // شناسه‌ها همان‌هایی‌اند که دکمه‌های بلندگو در `PlayerScreen` می‌سازند،
        // وگرنه هنگام پخش خودکار هیچ دکمه‌ای روشن نمی‌شود و کاربر نمی‌فهمد
        // صدا از کجا می‌آید.
        val (text, id) = when (s.reveal) {
            RevealStep.WORD -> item.prompt to item.id
            RevealStep.EXAMPLE -> item.exampleEn to "${item.id}-ex"
            // فارسی را موتور انگلیسی نباید بخواند؛ روی آزمون هم صدا مزاحم است
            RevealStep.TRANSLATION, RevealStep.CHECK -> null to ""
        }
        if (text.isNullOrBlank()) return
        speakWhenReady(text, id)
    }

    /**
     * در نخستین اجرا موتور TTS هنوز آماده نیست و `speak` بی‌صدا رد می‌شود.
     * منتظر آماده شدنش می‌مانیم، اما فقط اگر کاربر هنوز همان‌جا مانده باشد —
     * وگرنه چند ثانیه بعد صدای کارتی پخش می‌شود که رد شده است.
     */
    private fun speakWhenReady(text: String, id: String, pitch: Float = 1.0f) {
        if (speech.status.value == SpeechService.Status.Ready) {
            speech.speak(text, id, pitch = pitch)
            return
        }
        autoSpeakJob?.cancel()
        autoSpeakJob = viewModelScope.launch {
            val ready = withTimeoutOrNull(AUTO_SPEAK_WAIT_MS) {
                speech.status.first { it != SpeechService.Status.Initializing }
            }
            if (ready == SpeechService.Status.Ready && stillOn(id)) speech.speak(text, id, pitch = pitch)
        }
    }

    /** آیا کاربر هنوز روی همان چیزی است که قرار بود خوانده شود */
    private fun stillOn(id: String): Boolean {
        val item = _state.value.current ?: return false
        return id == item.id || id == "${item.id}-ex"
    }

    // ---------- بازی جفت‌یابی ----------

    /** پنج جفت در هر دور. بیشتر از این، صفحه شلوغ و انتخاب تصادفی می‌شود. */
    private val matchPerRound = 5

    /**
     * ساخت کاشی‌های یک دور.
     *
     * ترتیب معین است (بر پایه شناسه آیتم) تا با هر بازسازی UI کاشی‌ها
     * زیر انگشت کاربر جابه‌جا نشوند.
     */
    private fun buildMatchRound(round: Int) {
        val all = _state.value.items
        val slice = all.drop(round * matchPerRound).take(matchPerRound)
        if (slice.isEmpty()) {
            finishMatch()
            return
        }
        val en = slice.map {
            ir.speakup.app.ui.match.MatchTile(
                id = "en-${it.id}", text = it.prompt, pairKey = it.id, isPersian = false,
            )
        }.sortedBy { (it.text + round).hashCode() }
        val fa = slice.map {
            ir.speakup.app.ui.match.MatchTile(
                id = "fa-${it.id}", text = it.promptFa.orEmpty(), pairKey = it.id, isPersian = true,
            )
        }.sortedBy { (it.text + round + 7).hashCode() }

        _state.value = _state.value.copy(
            matchTiles = en + fa,
            matchSelected = null,
            matchWrongPair = null,
            matchRound = round,
            matchRoundCount = (all.size + matchPerRound - 1) / matchPerRound,
        )
    }

    fun startMatchIfNeeded() {
        if (_state.value.matchTiles.isEmpty()) buildMatchRound(0)
    }

    fun onMatchTap(tile: ir.speakup.app.ui.match.MatchTile) {
        val s = _state.value
        if (tile.matched || s.matchWrongPair != null) return

        val selected = s.matchSelected
        // نخستین ضربه، یا ضربه دوباره روی همان سمت → فقط انتخاب عوض می‌شود
        if (selected == null) {
            _state.value = s.copy(matchSelected = tile.id)
            speakTile(tile)
            return
        }
        if (selected == tile.id) {
            _state.value = s.copy(matchSelected = null)
            return
        }
        val first = s.matchTiles.firstOrNull { it.id == selected } ?: return
        if (first.isPersian == tile.isPersian) {
            _state.value = s.copy(matchSelected = tile.id)
            speakTile(tile)
            return
        }

        if (first.pairKey == tile.pairKey) {
            sound.correct()
            val tiles = s.matchTiles.map {
                if (it.id == first.id || it.id == tile.id) it.copy(matched = true) else it
            }
            _state.value = s.copy(
                matchTiles = tiles,
                matchSelected = null,
                correctCount = s.correctCount + 1,
            )
            if (tiles.all { it.matched }) {
                viewModelScope.launch {
                    delay(400)
                    buildMatchRound(s.matchRound + 1)
                }
            }
            return
        }

        // جفت غلط: هر دو کاشی قرمز می‌شوند و واژه وارد لایتنر می‌شود
        sound.wrong()
        _state.value = s.copy(matchWrongPair = first.id to tile.id, matchSelected = null)
        val item = s.items.firstOrNull { it.id == first.pairKey }
        if (item != null) {
            viewModelScope.launch {
                repo.recordAnswer(item, isProductive = false, userAnswer = tile.text, isCorrect = false)
                refreshLeitnerSet()
            }
        }
        viewModelScope.launch {
            delay(700)
            _state.value = _state.value.copy(matchWrongPair = null)
        }
    }

    private fun speakTile(tile: ir.speakup.app.ui.match.MatchTile) {
        if (tile.isPersian) return
        speech.speak(tile.text, tile.id)
    }

    private fun finishMatch() {
        viewModelScope.launch {
            val total = _state.value.items.size
            val score = if (total == 0) 1f else (_state.value.correctCount.toFloat() / total).coerceIn(0f, 1f)
            val done = repo.markActivityCompleted(activityId, score)
            _state.value = _state.value.copy(
                finished = true,
                earnedXp = done.xp?.amount ?: 0,
                goalReached = done.xp?.goalJustReached == true,
            )
            ir.speakup.app.data.remote.SyncWorker.syncNow(appContext)
        }
    }

    // ---------- مکالمه ----------

    private var dialogueJob: Job? = null

    fun toggleDialogue() {
        if (_state.value.dialoguePlaying) stopDialogue() else playDialogueFrom(_state.value.dialogueLine ?: 0)
    }

    fun dialogueNext() = playDialogueFrom((_state.value.dialogueLine ?: -1) + 1)

    fun dialoguePrev() = playDialogueFrom((_state.value.dialogueLine ?: 1) - 1)

    fun playLine(index: Int) = playDialogueFrom(index)

    fun toggleTranslation() {
        _state.value = _state.value.copy(showTranslation = !_state.value.showTranslation)
    }

    /**
     * پخش پیوسته از یک خط تا انتهای مکالمه.
     *
     * موتور TTS صف داخلی دارد، اما اگر همه خط‌ها را یکجا صف کنیم دیگر
     * نمی‌شود فهمید کدام خط در حال خواندن است و «توقف» هم دیر اثر می‌کند.
     * پس خط‌به‌خط پیش می‌رویم و منتظر تمام شدن هرکدام می‌مانیم.
     */
    private fun playDialogueFrom(start: Int) {
        val items = _state.value.items
        if (items.isEmpty()) return
        val from = start.coerceIn(0, items.lastIndex)

        dialogueJob?.cancel()
        speech.stop()
        _state.value = _state.value.copy(dialogueLine = from, dialoguePlaying = true)

        dialogueJob = viewModelScope.launch {
            for (i in from until items.size) {
                val item = items[i]
                _state.value = _state.value.copy(dialogueLine = i)
                val id = "dlg-${item.id}"
                speech.speak(item.ttsText ?: item.prompt, id, pitch = dialoguePitch(i))
                // منتظر شروع، سپس منتظر پایان همان خط
                withTimeoutOrNull(2_000) { speech.speakingId.first { it == id } }
                speech.speakingId.first { it != id }
                // مکث کوتاه بین دو نوبت گفتگو — بدون آن، صداها به‌هم می‌چسبند
                delay(350)
            }
            _state.value = _state.value.copy(dialoguePlaying = false)
        }
    }

    private fun stopDialogue() {
        dialogueJob?.cancel()
        speech.stop()
        _state.value = _state.value.copy(dialoguePlaying = false)
    }

    /** پایان بخش مکالمه — مثل بخش آموزش، نمره‌ای ندارد */
    fun finishDialogue() {
        stopDialogue()
        viewModelScope.launch {
            val done = repo.markActivityCompleted(activityId, 1f)
            _state.value = _state.value.copy(
                earnedXp = done.xp?.amount ?: 0,
                goalReached = done.xp?.goalJustReached == true,
            )
            if (done.xp?.goalJustReached == true) sound.celebrate()
            ir.speakup.app.data.remote.SyncWorker.syncNow(appContext)
        }
        _state.value = _state.value.copy(finished = true)
    }

    /**
     * پاسخ بررسی درک در بخش آموزش.
     *
     * ثبت می‌شود تا در متریک «نرخ تولید صحیح» دیده شود، اما امتیاز بخش را
     * عوض نمی‌کند: بخش آموزش نباید نمره بدهد، وگرنه کاربر به‌جای فکر کردن
     * محافظه‌کارانه حدس می‌زند.
     */
    fun onCheckAnswered(itemId: String, correct: Boolean) {
        val item = _state.value.items.firstOrNull { it.id == itemId } ?: return
        if (correct) sound.correct() else sound.wrong()
        viewModelScope.launch {
            repo.recordAnswer(item, isProductive = false, userAnswer = "", isCorrect = correct)
        }
    }

    fun openWordList() { _state.value = _state.value.copy(showWordList = true) }
    fun closeWordList() { _state.value = _state.value.copy(showWordList = false) }

    /** پرش مستقیم به یک واژه — از نمای واژگان یا شمارنده */
    fun goTo(index: Int) {
        val s = _state.value
        if (index !in s.items.indices) return
        _state.value = s.copy(
            index = index, reveal = RevealStep.WORD, input = "", verdict = null, speechWords = emptyList(),
            wordCheckOptions = emptyList(), wordCheckPicked = null, addedToLeitner = false,
        ).withItemSetup()
        recognizer.reset()
        autoSpeak()
    }

    fun addToLeitner(item: ActivityItemEntity) {
        val word = item.targetWord ?: item.prompt
        viewModelScope.launch {
            repo.addToLeitnerByWord(word)
            refreshLeitnerSet()
        }
    }

    private fun refreshLeitnerSet() {
        viewModelScope.launch {
            _state.value = _state.value.copy(inLeitner = repo.leitnerWords())
        }
    }

    /** پایان بخش آموزش — همه کارت‌های آموزشی بخش تمام‌شده علامت می‌خورند */
    fun finishTeaching() {
        viewModelScope.launch {
            val ids = siblingTeachingIds.ifEmpty { listOf(activityId) }
            val awards = ids.map { repo.markActivityCompleted(it, 1f) }
            _state.value = _state.value.copy(
                earnedXp = awards.sumOf { it.xp?.amount ?: 0 },
                goalReached = awards.any { it.xp?.goalJustReached == true },
            )
            if (awards.any { it.xp?.goalJustReached == true }) sound.celebrate()
            ir.speakup.app.data.remote.SyncWorker.syncNow(appContext)
        }
        _state.value = _state.value.copy(finished = true, narrating = false)
    }

    /** خواندن کل صفحه آموزش با صوت */
    fun toggleNarration() {
        val s = _state.value
        if (s.narrating) {
            speech.stop()
            _state.value = s.copy(narrating = false)
            return
        }
        val text = s.items.joinToString(". ") { listOfNotNull(it.prompt, it.correctAnswer).joinToString(". ") }
        speech.speak(text, "narration-$activityId", s.narrationRate)
        _state.value = s.copy(narrating = true)
    }

    fun cycleNarrationRate() {
        val next = when (_state.value.narrationRate) {
            1.0f -> 0.75f
            0.75f -> 1.25f
            else -> 1.0f
        }
        _state.value = _state.value.copy(narrationRate = next)
        if (_state.value.narrating) { speech.stop(); toggleNarration() }
    }

    fun revealNext() {
        val s = _state.value
        val next = when (s.reveal) {
            RevealStep.WORD -> RevealStep.EXAMPLE
            RevealStep.EXAMPLE -> RevealStep.TRANSLATION
            RevealStep.TRANSLATION -> RevealStep.CHECK
            // تا وقتی گزینه‌ای زده نشده، «بعدی» نباید از آزمون رد شود
            RevealStep.CHECK -> if (s.wordCheckPicked == null) return else return advance()
        }
        val item = s.current
        // اگر آیتم مثال ندارد، مرحله را رد کن
        if (next == RevealStep.EXAMPLE && item?.exampleEn.isNullOrBlank()) {
            return _state.value.let { _state.value = it.copy(reveal = RevealStep.TRANSLATION) }
        }
        if (next == RevealStep.CHECK) {
            val options = wordCheckOptionsFor(item)
            // بدون گزینه کافی، آزمون بی‌معناست — مستقیم به واژه بعد
            if (options.size < 2) return advance()
            _state.value = s.copy(reveal = next, wordCheckOptions = options, wordCheckPicked = null)
            return
        }
        _state.value = s.copy(reveal = next)
        autoSpeak()
    }

    /**
     * گزینه‌های آزمون معنی: پاسخ درست + دو معنی دیگر از همین درس.
     *
     * حواس‌پرت‌کن‌ها از واژه‌های همان درس می‌آیند نه از کل دیکشنری، چون
     * معنی‌های بی‌ربط تشخیص را بی‌اهمیت می‌کنند — کاربر بدون دانستن واژه
     * هم می‌تواند حدس بزند.
     *
     * **چرخش از جایگاه آیتم می‌آید و نه از هش.** نسخه پیشین با
     * `(متن + شناسه).hashCode()` مرتب می‌کرد و نتیجه‌اش این بود که در یک
     * فعالیت ده‌واژه‌ای، هشت آزمون دقیقاً همان دو حواس‌پرت‌کن را می‌گرفتند و
     * پاسخ درست هم در هشت‌تا سومین گزینه بود — یعنی زدنِ همیشگیِ گزینه سوم،
     * بدون دانستن هیچ واژه‌ای، ۸۰٪ می‌گرفت.
     *
     * دلیلش ریاضی است: `hash(متن + شناسه)` برابر است با
     * `hash(متن) × ۳۱^طول + hash(شناسه)`، و چون شناسه‌های یک فعالیت هم‌طولند،
     * جمله دوم عددی **ثابت** است که به همه نامزدها اضافه می‌شود و ترتیب را
     * عوض نمی‌کند.
     *
     * ترتیب همچنان معین است (تابعِ جایگاه آیتم) تا با هر بازسازی UI زیر
     * انگشت کاربر جابه‌جا نشود.
     */
    private fun wordCheckOptionsFor(item: ActivityItemEntity?): List<String> {
        val all = _state.value.items
        val answer = item?.promptFa?.trim()?.takeIf { it.isNotEmpty() } ?: return emptyList()
        val here = all.indexOfFirst { it.id == item.id }.coerceAtLeast(0)

        // یکتایی روی شکل دیداری، نه رشته خام: دو معنی ممکن است فقط در
        // نیم‌فاصله یا «ي» عربی فرق کنند و روی صفحه دقیقاً یکی دیده شوند.
        val seen = mutableSetOf(answer.optionKey())
        val distractors = ArrayList<String>(2)
        for (step in 1 until all.size) {
            val cand = all[(here + step) % all.size].promptFa?.trim().orEmpty()
            if (cand.isEmpty() || !seen.add(cand.optionKey())) continue
            distractors += cand
            if (distractors.size == 2) break
        }
        if (distractors.isEmpty()) return emptyList()

        // جای پاسخ درست هم می‌چرخد، وگرنه ترتیبِ ثابتْ خودش پاسخ را لو می‌دهد.
        //
        // طول پاسخ در جابه‌جایی می‌آید تا دنباله جای‌ها چرخه ساده ۱،۲،۳
        // نشود؛ با چرخه، کاربر بی‌آنکه واژه‌ای بداند جای بعدی را می‌داند.
        return ArrayList(distractors).apply {
            add(answerSlot(answer, here, size + 1), answer)
        }
    }

    /**
     * پاسخ آزمون معنی.
     *
     * پاسخ غلط واژه را خودکار وارد لایتنر می‌کند — همان تمایز اصلی محصول،
     * فقط این بار در لحظه‌ای که واژه تازه دیده شده و بیشترین اثر را دارد.
     */
    fun pickWordCheck(option: String) {
        val s = _state.value
        if (s.wordCheckPicked != null) return
        val item = s.current ?: return
        val correct = option == item.promptFa?.trim()
        if (correct) sound.correct() else sound.wrong()

        _state.value = s.copy(
            wordCheckPicked = option,
            correctCount = if (correct) s.correctCount + 1 else s.correctCount,
        )
        viewModelScope.launch {
            val added = repo.recordAnswer(item, isProductive = false, userAnswer = option, isCorrect = correct)
            if (added) {
                _state.value = _state.value.copy(addedToLeitner = true)
                refreshLeitnerSet()
            }
        }
    }

    // ---------- ورودی کاربر ----------

    fun onInputChange(v: String) { _state.value = _state.value.copy(input = v) }

    fun pick(piece: String) {
        val s = _state.value
        val i = s.pool.indexOf(piece)
        if (i < 0) return
        _state.value = s.copy(
            arranged = s.arranged + piece,
            pool = s.pool.toMutableList().also { it.removeAt(i) },
        )
    }

    fun unpick(index: Int) {
        val s = _state.value
        val piece = s.arranged.getOrNull(index) ?: return
        _state.value = s.copy(
            arranged = s.arranged.toMutableList().also { it.removeAt(index) },
            pool = s.pool + piece,
        )
    }

    // ---------- تصحیح ----------

    fun submit(answer: String? = null) {
        val s = _state.value
        val item = s.current ?: return
        if (s.verdict != null) return   // قبلاً تصحیح شده

        val userAnswer = answer ?: when (s.type) {
            ActivityType.REORDER -> s.arranged.joinToString(" ")
            else -> s.input
        }
        if (userAnswer.isBlank()) return

        val verdict = AnswerChecker.check(item, userAnswer)
        _state.value = s.copy(
            verdict = verdict,
            correctCount = s.correctCount + if (verdict.correct) 1 else 0,
        )
        if (verdict.correct) sound.correct() else sound.wrong()

        viewModelScope.launch {
            val added = repo.recordAnswer(
                item = item,
                isProductive = s.type.isProductive,
                userAnswer = userAnswer,
                isCorrect = verdict.correct,
            )
            if (added) _state.value = _state.value.copy(addedToLeitner = true)
        }
    }

    /**
     * تصحیح تمرین گفتار.
     *
     * برخلاف بقیه انواع تمرین، اینجا مقایسه کل‌جمله (AnswerChecker.check) به‌کار
     * نمی‌آید — تشخیص گفتار کامل نیست و یک کلمه جاافتاده کل جمله را رد می‌کند.
     * SpeechMatcher هر کلمه هدف را جدا می‌سنجد و normalize یکسان AnswerChecker
     * را دوباره استفاده می‌کند؛ درست‌بودن کلی = همه کلمه‌های هدف پیدا شدند.
     */
    fun submitSpeech(transcript: String) {
        val s = _state.value
        val item = s.current ?: return
        if (s.verdict != null) return
        if (transcript.isBlank()) return

        val target = item.correctAnswer?.takeIf { it.isNotBlank() } ?: item.prompt
        val match = SpeechMatcher.match(target, transcript)
        val verdict = AnswerChecker.Verdict(correct = match.allCorrect, expected = target, userAnswer = transcript)
        _state.value = s.copy(
            verdict = verdict,
            correctCount = s.correctCount + if (verdict.correct) 1 else 0,
            speechWords = match.words,
        )

        viewModelScope.launch {
            val added = repo.recordAnswer(
                item = item,
                isProductive = s.type.isProductive,
                userAnswer = transcript,
                isCorrect = verdict.correct,
            )
            if (added) _state.value = _state.value.copy(addedToLeitner = true)
        }
    }

    // ---------- پیمایش ----------

    fun advance() {
        val s = _state.value
        if (s.isLast) {
            viewModelScope.launch {
                val score = if (s.items.isEmpty()) 1f else s.correctCount.toFloat() / s.items.size
                val ids = siblingTeachingIds.ifEmpty { listOf(activityId) }
                val awards = ids.map { repo.markActivityCompleted(it, score) }
                _state.value = _state.value.copy(
                    earnedXp = awards.sumOf { it.xp?.amount ?: 0 },
                    goalReached = awards.any { it.xp?.goalJustReached == true },
                )
                if (awards.any { it.xp?.goalJustReached == true }) sound.celebrate()
                ir.speakup.app.data.remote.SyncWorker.syncNow(appContext)
            }
            _state.value = s.copy(finished = true)
            return
        }
        val next = s.copy(
            index = s.index + 1,
            reveal = RevealStep.WORD,
            input = "",
            verdict = null,
            addedToLeitner = false,
            speechWords = emptyList(),
            wordCheckOptions = emptyList(), wordCheckPicked = null,
        ).withItemSetup()
        _state.value = next
        recognizer.reset()
        autoSpeak()
        viewModelScope.launch { repo.markActivityStarted(activityId, next.index) }
    }

    private companion object {
        /** بیش از این منتظر موتور TTS نمی‌مانیم؛ کاربر تا آن موقع رفته است */
        const val AUTO_SPEAK_WAIT_MS = 3_000L
    }

    override fun onCleared() {
        // فقط stopListening کافی نیست: اتصال به سرویس تشخیص باز می‌ماند
        // و نشانگر میکروفون سیستم روشن می‌ماند تا پایان عمر برنامه.
        (recognizer as? ir.speakup.app.domain.AndroidSpeechRecognitionService)?.release()
            ?: recognizer.stopListening()
        speech.stop()
        super.onCleared()
    }
}

/**
 * گام صدای هر نوبت مکالمه.
 *
 * گوینده از ترتیب خط درمی‌آید و نه از داده: مکالمه‌ها در محتوا همیشه
 * یک‌درمیان‌اند (نفر اول، نفر دوم، نفر اول…). ستون «صدای TTS» در کاربرگ
 * هم همین را می‌گوید، اما به بسته خروجی منتقل نمی‌شود؛ تا وقتی منتقل
 * شود، ترتیب خط همان اطلاعات را بدون تغییر شِما می‌دهد.
 */
private fun dialoguePitch(index: Int): Float =
    if (index % 2 == 0) SpeechService.PITCH_A else SpeechService.PITCH_B
