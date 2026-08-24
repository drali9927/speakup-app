package ir.speakup.app.ui.personalize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.domain.Analytics
import ir.speakup.app.domain.XpRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * شخصی‌سازی آغازین.
 *
 * چهار پرسش، نه پانزده تا. الگوی رایج در اپ‌های خارجی، آنبوردینگِ
 * ۱۵ مرحله‌ای است تا کاربر حس کند محصول برایش ساخته شده. آن الگو روی
 * محصولی جواب می‌دهد که پشتش **تست رایگان با کارت ثبت‌شده** باشد؛
 * اینجا آن ابزار نیست و پانزده پرسش فقط فاصله تا نخستین درس را زیاد
 * می‌کند — در حالی که رقیب در چهار ضربه داخل درس است.
 *
 * پس فقط پرسش‌هایی مانده‌اند که **جوابشان واقعاً چیزی را عوض می‌کند**:
 *
 * | پرسش | چه چیزی را تنظیم می‌کند |
 * |---|---|
 * | چقدر وقت داری | هدف روزانه — تا امروز برای همه ۵۰ بود |
 * | کِی یادت بیندازیم | ساعت یادآور — تا امروز برای همه ۲۰:۰۰ بود |
 * | چرا انگلیسی | متن‌های انگیزشی، و داده تصمیم محتوا |
 * | از کجا شروع کنیم | رفتن یا نرفتن به آزمون تعیین سطح |
 *
 * پرسشی که جوابش هیچ‌جا اثر ندارد، پرسش نیست — تشریفات است.
 */
@HiltViewModel
class PersonalizeViewModel @Inject constructor(
    private val prefs: AppPreferences,
    private val analytics: Analytics,
) : ViewModel() {

    data class Choice(val key: String, val title: String, val subtitle: String? = null)

    data class Step(val question: String, val hint: String?, val choices: List<Choice>)

    data class UiState(
        val index: Int = 0,
        val answers: Map<Int, String> = emptyMap(),
        val done: Boolean = false,
        /** آیا کاربر گفت سطحش را نمی‌داند — یعنی باید آزمون بدهد */
        val needsPlacement: Boolean = true,
    ) {
        val step: Step? get() = STEPS.getOrNull(index)
        val total: Int get() = STEPS.size
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init { analytics.track("personalize_start") }

    fun pick(key: String) {
        val s = _state.value
        val answers = s.answers + (s.index to key)
        apply(s.index, key)

        if (s.index >= STEPS.lastIndex) {
            val needs = answers[3] != "know"
            _state.value = s.copy(answers = answers, done = true, needsPlacement = needs)
            analytics.track("personalize_done", mapOf("motive" to (answers[2] ?: "")))
        } else {
            _state.value = s.copy(answers = answers, index = s.index + 1)
        }
    }

    fun back() {
        val s = _state.value
        if (s.index > 0) _state.value = s.copy(index = s.index - 1)
    }

    /** هر پاسخ، همان لحظه اعمال می‌شود تا اگر کاربر وسط راه بست، چیزی گم نشود */
    private fun apply(index: Int, key: String) {
        viewModelScope.launch {
            when (index) {
                0 -> key.toIntOrNull()?.let { prefs.setDailyGoalXp(it) }
                1 -> key.toIntOrNull()?.let { prefs.setReminderHour(it) }
                2 -> prefs.setMotive(key)
                else -> Unit
            }
        }
    }

    companion object {
        val STEPS = listOf(
            Step(
                question = "روزی چقدر وقت داری؟",
                hint = "هر وقت خواستی می‌توانی عوضش کنی.",
                choices = XpRules.GOALS.map { Choice(it.xp.toString(), it.subtitle, it.title) },
            ),
            Step(
                question = "کِی یادت بیندازیم؟",
                hint = "یک یادآور کوتاه، فقط روزهایی که تمرین نکرده باشی.",
                choices = listOf(
                    Choice("8", "صبح", "ساعت ۸"),
                    Choice("12", "ظهر", "ساعت ۱۲"),
                    Choice("17", "عصر", "ساعت ۱۷"),
                    Choice("20", "شب", "ساعت ۲۰"),
                ),
            ),
            Step(
                question = "انگلیسی را برای چه می‌خواهی؟",
                hint = null,
                choices = listOf(
                    Choice("work", "کار و شغل"),
                    Choice("travel", "سفر"),
                    Choice("study", "درس و دانشگاه"),
                    Choice("migrate", "مهاجرت"),
                    Choice("personal", "علاقه شخصی"),
                ),
            ),
            Step(
                question = "از کجا شروع کنیم؟",
                hint = null,
                choices = listOf(
                    Choice("test", "اول سطحم را بسنجیم", "چند سؤال کوتاه"),
                    Choice("know", "از اول شروع می‌کنم", "از سطح مقدماتی"),
                ),
            ),
        )

        /** متن انگیزشی صفحه نتیجه، بر پایه انگیزه کاربر */
        fun motiveLine(motive: String?): String = when (motive) {
            "work" -> "سر کار انگلیسی حرف بزنی"
            "travel" -> "در سفر راحت باشی"
            "study" -> "متن‌های درسی‌ات را بفهمی"
            "migrate" -> "برای مصاحبه آماده باشی"
            else -> "روان انگلیسی حرف بزنی"
        }
    }
}
