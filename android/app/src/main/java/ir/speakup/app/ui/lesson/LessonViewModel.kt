package ir.speakup.app.ui.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.data.local.ActivityEntity
import ir.speakup.app.data.local.ContentDao
import ir.speakup.app.data.local.LessonEntity
import ir.speakup.app.data.local.ProgressDao
import ir.speakup.app.data.model.ActivityType
import ir.speakup.app.data.model.ProgressStatus
import ir.speakup.app.data.model.SectionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SectionCard(
    val id: String,
    val type: SectionType,
    val estimatedMinutes: Int,
    val activities: List<ActivityEntity>,
    val completedIds: Set<String>,
) {
    /**
     * گام‌های بخش، آن‌طور که کاربر طی می‌کند.
     *
     * کارت‌های آموزشِ یک بخش **یک گام‌اند** و نه چند تا: صفحه آموزش همه را
     * یکجا نشان می‌دهد و با هم تمام‌شده علامتشان می‌زند. کارت گرامر تا امروز
     * «۹ فعالیت» می‌گفت در حالی که کاربر ۸ گام طی می‌کرد و بعد از یک صفحه،
     * شمارنده از صفر به دو می‌پرید.
     */
    private val groups: List<List<ActivityEntity>>
        get() {
            val out = mutableListOf<MutableList<ActivityEntity>>()
            for (a in activities) {
                val teach = a.activityType == ActivityType.TEACHING.name
                val prev = out.lastOrNull()
                if (teach && prev != null && prev.first().activityType == ActivityType.TEACHING.name) {
                    prev += a
                } else {
                    out += mutableListOf(a)
                }
            }
            return out
        }

    val stepCount: Int get() = groups.size
    val completedCount: Int get() = groups.count { g -> g.all { it.id in completedIds } }
    val isComplete: Boolean get() = activities.isNotEmpty() && activities.all { it.id in completedIds }

    /**
     * فعالیتی که ضربه روی کارت باید باز کند: نخستین فعالیت انجام‌نشده.
     * بدون این، کاربر برای همیشه در فعالیت اول بخش گیر می‌کند —
     * گرامر ۹ فعالیت دارد و فقط کارت آموزش اولش قابل دسترسی می‌شد.
     */
    val nextActivity: ActivityEntity?
        get() = activities.firstOrNull { it.id !in completedIds } ?: activities.firstOrNull()

    /** نشان‌های نوع تمرین روی کارت — کاربر پیش از ورود می‌داند چه در انتظارش است */
    val activityTypes: List<ActivityType> get() = activities.mapNotNull { ActivityType.fromOrNull(it.activityType) }

    /**
     * گام‌ها با وضعیت هرکدام.
     *
     * چرا لازم شد: کارت واژگان چهار فعالیت دارد — کارت واژه، جفت‌یابی، و
     * دوباره همان دو تا برای ده واژه بعدی. کاربر روی کارت می‌زد، واژه‌ها را
     * می‌دید، بیرون می‌آمد، دوباره می‌زد و این بار چیز **دیگری** می‌آمد و
     * هیچ‌جا معلوم نبود کجای بخش است.
     */
    val steps: List<Step>
        get() {
            val nextId = nextActivity?.id
            return groups.mapNotNull { g ->
                ActivityType.fromOrNull(g.first().activityType)?.let { t ->
                    Step(
                        type = t,
                        done = g.all { it.id in completedIds },
                        current = g.any { it.id == nextId } && !isComplete,
                    )
                }
            }
        }

    data class Step(val type: ActivityType, val done: Boolean, val current: Boolean)
}

data class LessonUiState(
    val loading: Boolean = true,
    val lesson: LessonEntity? = null,
    val sections: List<SectionCard> = emptyList(),
    /** تصاویر همین درس — پس‌زمینه کارت بخش‌ها از این فهرست انتخاب می‌شود */
    val artFiles: List<String> = emptyList(),
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val contentDao: ContentDao,
    private val progressDao: ProgressDao,
    savedState: SavedStateHandle,
) : ViewModel() {

    private val lessonId: String = checkNotNull(savedState["lessonId"])

    private val _state = MutableStateFlow(LessonUiState())
    val state: StateFlow<LessonUiState> = _state.asStateFlow()

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            val lesson = contentDao.lesson(lessonId)
            val cards = contentDao.sections(lessonId).map { s ->
                val acts = contentDao.activities(s.id)
                val completed = acts.filter { a ->
                    progressDao.get(a.id)?.status == ProgressStatus.COMPLETED.name
                }.map { it.id }.toSet()
                SectionCard(
                    id = s.id,
                    type = runCatching { SectionType.valueOf(s.type) }.getOrDefault(SectionType.VOCABULARY),
                    estimatedMinutes = s.estimatedMinutes,
                    activities = acts,
                    completedIds = completed,
                )
            }
            _state.value = LessonUiState(
                loading = false, lesson = lesson, sections = cards,
                artFiles = contentDao.lessonImageFiles(lessonId),
            )
        }
    }
}
