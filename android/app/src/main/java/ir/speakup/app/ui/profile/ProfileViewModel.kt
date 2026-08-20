package ir.speakup.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.data.local.AnswerLogDao
import ir.speakup.app.data.local.ContentDao
import ir.speakup.app.data.local.LeitnerDao
import ir.speakup.app.data.local.ProgressDao
import ir.speakup.app.data.local.StreakDao
import ir.speakup.app.data.model.LeitnerSchedule
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.domain.XpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val xp: XpRepository,
    private val prefs: AppPreferences,
    private val streakDao: StreakDao,
    private val leitnerDao: LeitnerDao,
    private val progressDao: ProgressDao,
    private val contentDao: ContentDao,
    private val answerLogDao: AnswerLogDao,
) : ViewModel() {

    data class DayBar(val label: String, val xp: Int)

    data class UiState(
        val phone: String? = null,
        val planTitle: String? = null,
        val level: Int = 1,
        val totalXp: Int = 0,
        val streakDays: Int = 0,
        val wordsLearned: Int = 0,
        val lessonsDone: Int = 0,
        val weekly: List<DayBar> = emptyList(),
        val remindersEnabled: Boolean = true,
        val reminderHour: Int = 20,
        val accuracy: Float? = null,
        val avatarId: Int = 0,
        /** SYSTEM | LIGHT | DARK */
        val themeMode: String = "SYSTEM",
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val dayNames = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")

    init {
        viewModelScope.launch {
            combine(xp.totalXp, xp.level, streakDao.observe(), prefs.phone) { total, lvl, streak, phone ->
                UiState(
                    phone = phone,
                    level = lvl,
                    totalXp = total,
                    streakDays = streak?.currentLength ?: 0,
                )
            }.collect { base ->
                _state.value = _state.value.copy(
                    phone = base.phone,
                    level = base.level,
                    totalXp = base.totalXp,
                    streakDays = base.streakDays,
                )
            }
        }
        viewModelScope.launch {
            prefs.avatarId.collect { _state.value = _state.value.copy(avatarId = it) }
        }
        viewModelScope.launch {
            prefs.themeMode.collect { _state.value = _state.value.copy(themeMode = it) }
        }
        viewModelScope.launch { loadSlowStats() }
        viewModelScope.launch {
            combine(prefs.remindersEnabled, prefs.reminderHour) { on, h -> on to h }
                .collect { (on, h) ->
                    _state.value = _state.value.copy(remindersEnabled = on, reminderHour = h)
                }
        }
        viewModelScope.launch {
            // نمودار هفتگی — روزهای بدون فعالیت هم باید ستون خالی داشته
            // باشند، وگرنه هفته پرکار و هفته تنبل شبیه هم دیده می‌شوند.
            xp.lastDays(7).collect { rows ->
                val byDay = rows.associate { it.day to it.xp }
                val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val cal = java.util.Calendar.getInstance()
                cal.add(java.util.Calendar.DAY_OF_YEAR, -6)
                val bars = (0 until 7).map {
                    val key = fmt.format(cal.time)
                    // Calendar.SATURDAY برابر ۷ است، پس باقیمانده بر ۷
                    // مستقیماً شنبه را به خانه صفر می‌برد. فرمول قبلی
                    // «+۵» داشت و **هر هفت روز را جابه‌جا می‌کرد**:
                    // پنج‌شنبه با برچسب «س» نشان داده می‌شد.
                    val idx = cal.get(java.util.Calendar.DAY_OF_WEEK) % 7
                    cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    DayBar(dayNames[idx], byDay[key] ?: 0)
                }
                _state.value = _state.value.copy(weekly = bars)
            }
        }
    }

    /** آمارهایی که Flow ندارند و یک بار خوانده می‌شوند */
    private suspend fun loadSlowStats() {
        val learned = leitnerDao.countLearned(LeitnerSchedule.MAX_BOX)
        val done = progressDao.observeAll().first()
            .filter { it.status == "COMPLETED" }.map { it.activityId }.toSet()
        val lessons = contentDao.observeLessons("A1").first()
        val lessonsDone = lessons.count { lesson ->
            val acts = contentDao.sections(lesson.id)
                .flatMap { contentDao.activities(it.id) }.map { it.id }
            acts.isNotEmpty() && acts.all { it in done }
        }
        // فقط پاسخ‌های سی روز اخیر — نرخ کل با گذشت زمان بی‌حرکت می‌شود
        val since = System.currentTimeMillis() - 30L * 86_400_000
        _state.value = _state.value.copy(
            wordsLearned = learned,
            lessonsDone = lessonsDone,
            accuracy = answerLogDao.productiveAccuracy(since),
        )
    }

    fun setAvatar(id: Int) = viewModelScope.launch { prefs.setAvatar(id) }

    fun setTheme(mode: String) = viewModelScope.launch { prefs.setThemeMode(mode) }

    fun setReminders(on: Boolean) = viewModelScope.launch {
        prefs.setRemindersEnabled(on)
    }

    fun setReminderHour(hour: Int) = viewModelScope.launch {
        prefs.setReminderHour(hour)
    }
}
