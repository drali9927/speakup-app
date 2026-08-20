package ir.speakup.app.ui.lessons

import androidx.lifecycle.ViewModel
import ir.speakup.app.domain.toPersianDigits
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.data.content.ContentImporter
import ir.speakup.app.data.local.ContentDao
import ir.speakup.app.data.local.LessonEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonsUiState(
    val loading: Boolean = true,
    val levelCode: String = "A1",
    val levelTitle: String = "سطح مقدماتی ۱",
    /** سطوحی که محتوا دارند — برای انتخابگر سطح */
    val availableLevels: List<ir.speakup.app.data.local.LevelEntity> = emptyList(),
    /**
     * اندازه هر سطح: «۳۰ درس · ۷ ساعت».
     *
     * پیش از انتخاب سطح باید معلوم باشد چقدر محتوا دارد. کاربری که
     * انتظار دارد یک سطح او را «به B2 برساند» و بعد می‌بیند هفت ساعت
     * است، بهتر است همان اول بداند تا بعد از خرید.
     */
    val levelSizes: Map<String, String> = emptyMap(),
    val lessons: List<LessonEntity> = emptyList(),
    /** تصویر بنر سطح — از محتوای درس اول، نه از assets */
    val bannerImage: String? = null,
    /** یک تصویر شاخص برای هر درس — تزئین کنار مسیر */
    val lessonArt: Map<String, String> = emptyMap(),
    val todayXp: Int = 0,
    val goalXp: Int = ir.speakup.app.domain.XpRules.DEFAULT_GOAL,
    val level: Int = 1,
    val streakDays: Int = 0,
    /** شناسه درس‌هایی که همه فعالیت‌هایشان تمام شده */
    val completedLessonIds: Set<String> = emptySet(),
    /** نسبت فعالیت‌های تمام‌شده هر درس — برای نشان پیشرفت روی مسیر */
    val lessonProgress: Map<String, Float> = emptyMap(),
    /** واژه‌های سررسیدشده لایتنر — روی ایستگاه مرور نشان داده می‌شود */
    val dueCount: Int = 0,
    val error: String? = null,
)

@HiltViewModel
class LessonsViewModel @Inject constructor(
    private val contentDao: ContentDao,
    private val importer: ContentImporter,
    private val updater: ir.speakup.app.data.content.ContentUpdater,
    private val xp: ir.speakup.app.domain.XpRepository,
    private val streakDao: ir.speakup.app.data.local.StreakDao,
    private val progressDao: ir.speakup.app.data.local.ProgressDao,
    private val leitnerDao: ir.speakup.app.data.local.LeitnerDao,
    private val prefs: ir.speakup.app.data.prefs.AppPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(LessonsUiState())
    val state: StateFlow<LessonsUiState> = _state.asStateFlow()

    /** سطحی که همین حالا دیده می‌شود؛ از تنظیمات می‌آید و کاربر عوضش می‌کند */
    private val level = MutableStateFlow("A1")

    init {
        viewModelScope.launch { prefs.currentLevel.collect { level.value = it } }
        viewModelScope.launch {
            when (val r = importer.importIfNeeded()) {
                is ContentImporter.Result.Failed ->
                    _state.value = LessonsUiState(loading = false, error = r.cause.message ?: "خطای ناشناخته")
                else -> observeLessons()
            }
            // بعد از نمایش محتوای محلی، در پس‌زمینه به‌روزرسانی را چک می‌کنیم.
            // کاربر هرگز منتظر شبکه نمی‌ماند؛ اگر بسته تازه‌ای بود، فهرست
            // خودش از طریق Flow دیتابیس به‌روز می‌شود.
            updater.checkAndUpdateAll()
        }
        observeGamification()
        observeCompletion()
        viewModelScope.launch {
            leitnerDao.observeDueCount(System.currentTimeMillis()).collect {
                _state.value = _state.value.copy(dueCount = it)
            }
        }
    }

    /**
     * کدام درس‌ها تمام شده‌اند — مبنای وضعیت گره‌های مسیر.
     *
     * ملاک «همه فعالیت‌ها تمام شده» است و نه «یکی شروع شده»: مسیر باید
     * پیشرفت واقعی را نشان بدهد، وگرنه تیک‌ها بی‌معنا می‌شوند.
     */
    private fun observeCompletion() {
        viewModelScope.launch {
            progressDao.observeAll().collect { rows ->
                val done = rows.filter { it.status == "COMPLETED" }.map { it.activityId }.toSet()
                val lessons = _state.value.lessons
                val progress = mutableMapOf<String, Float>()
                val completed = mutableSetOf<String>()
                lessons.forEach { lesson ->
                    val acts = contentDao.sections(lesson.id)
                        .flatMap { contentDao.activities(it.id) }
                        .map { it.id }
                    if (acts.isEmpty()) return@forEach
                    val doneCount = acts.count { it in done }
                    progress[lesson.id] = doneCount.toFloat() / acts.size
                    if (doneCount == acts.size) completed += lesson.id
                }
                _state.value = _state.value.copy(
                    completedLessonIds = completed,
                    lessonProgress = progress,
                )
            }
        }
    }

    /**
     * امتیاز، سطح و زنجیره کنار هم — تا کارت هدف روزانه یکجا به‌روز شود.
     * جدا جمع کردنشان باعث می‌شد کارت چند بار پشت‌سرهم پرش کند.
     */
    private fun observeGamification() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                xp.todayXp, xp.dailyGoal, xp.level, streakDao.observe(),
            ) { today, goal, lvl, streak ->
                listOf(today, goal, lvl, streak?.currentLength ?: 0)
            }.collect { (today, goal, lvl, days) ->
                _state.value = _state.value.copy(
                    todayXp = today, goalXp = goal, level = lvl, streakDays = days,
                )
            }
        }
    }

    fun selectLevel(code: String) {
        viewModelScope.launch { prefs.setCurrentLevel(code) }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeLessons() {
        viewModelScope.launch {
            level.flatMapLatest { contentDao.observeLessons(it) }.collect { list ->
                val banner = list.firstOrNull()
                    ?.let { contentDao.lessonImageFiles(it.id) }
                    ?.let { files -> files.getOrNull(12.mod(files.size.coerceAtLeast(1))) }
                val levels = contentDao.levelsWithContent()
                // یک تصویر برای هر درس. سی پرس‌وجوی کوچک است و فقط با
                // تعویض سطح تکرار می‌شود، پس روی پیمایش اثری ندارد.
                val art = list.associate { lesson ->
                    val files = contentDao.lessonImageFiles(lesson.id)
                    lesson.id to (files.getOrNull(files.size / 2) ?: "")
                }.filterValues { it.isNotEmpty() }
                _state.value = _state.value.copy(
                    loading = false,
                    lessons = list,
                    bannerImage = banner,
                    lessonArt = art,
                    levelCode = level.value,
                    levelTitle = levels.firstOrNull { it.code == level.value }?.titleFa
                        ?: _state.value.levelTitle,
                    availableLevels = levels,
                    levelSizes = levels.associate { lv ->
                        val n = contentDao.lessonCount(lv.code)
                        val h = contentDao.levelMinutes(lv.code) / 60
                        lv.code to when {
                            n == 0 -> "به‌زودی"
                            h > 0 -> "${n.toPersianDigits()} درس، حدود ${h.toPersianDigits()} ساعت"
                            else -> "${n.toPersianDigits()} درس"
                        }
                    },
                )
            }
        }
    }


}
