package ir.speakup.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import ir.speakup.app.MainActivity
import ir.speakup.app.R
import ir.speakup.app.data.local.LeitnerDao
import ir.speakup.app.data.local.StreakDao
import ir.speakup.app.data.local.XpDao
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.domain.TimeSource
import ir.speakup.app.domain.toPersianDigits
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ابزارک زنجیره و هدف روزانه.
 *
 * کاری که ابزارک دولینگو می‌کند و ما هم می‌خواهیم: **وضعیت را از حالتِ
 * تصویر عوض کند، نه فقط عدد را به‌روز کند.** شعله وقتی امروز تمرین کرده‌ای
 * روشن است و وقتی نکرده‌ای خاکستری؛ کاربر بدون خواندنِ حتی یک کلمه
 * می‌فهمد کارِ امروز مانده یا نه. همین «نگاهِ بی‌واسطه» است که ابزارک را
 * از یک میان‌بُر ساده جدا می‌کند.
 *
 * سه لایه اطلاعات، به ترتیب اهمیت: زنجیره، پیام وضعیت، نوار هدف روزانه.
 *
 * زدن روی ابزارک اپ را باز می‌کند — تنها کنش ممکن، و همان که لازم است.
 */
class StreakWidget : AppWidgetProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Deps {
        fun streakDao(): StreakDao
        fun xpDao(): XpDao
        fun leitnerDao(): LeitnerDao
        fun prefs(): AppPreferences
        fun time(): TimeSource
    }

    override fun onUpdate(
        context: Context,
        manager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        refresh(context, manager, appWidgetIds)
    }

    companion object {
        // AppWidgetProvider یک BroadcastReceiver است و عمرش کوتاه؛ خواندن
        // از دیتابیس باید بیرون از چرخه عمرش انجام شود.
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        /** به‌روزرسانی همه نمونه‌های ابزارک — بعد از هر تمرین صدا زده می‌شود */
        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context) ?: return
            val ids = manager.getAppWidgetIds(ComponentName(context, StreakWidget::class.java))
            if (ids.isEmpty()) return
            refresh(context, manager, ids)
        }

        private fun refresh(context: Context, manager: AppWidgetManager, ids: IntArray) {
            val app = context.applicationContext
            scope.launch {
                val views = buildViews(app)
                ids.forEach { manager.updateAppWidget(it, views) }
            }
        }

        /**
         * ساخت نمای ابزارک از وضعیت فعلی.
         *
         * جدا از [refresh] است تا بشود بدون AppWidgetManager هم ساختش و
         * دیدش؛ خطای RemoteViews فقط در لحظه apply معلوم می‌شود و بدون
         * راهی برای دیدن آن، هر اشتباه چیدمانی تا روی گوشی کاربر می‌ماند.
         */
        suspend fun buildViews(context: Context): RemoteViews {
            val app = context.applicationContext
            val d = EntryPointAccessors.fromApplication(app, Deps::class.java)
            val time = d.time()
            val today = time.today()
            val streak = d.streakDao().get()
            val todayXp = d.xpDao().forDay(today)
            val goal = d.prefs().dailyGoalXp.first()
            val due = d.leitnerDao().observeDueCount(time.nowMillis()).first()
            val activeToday = streak?.lastActiveDate == today

            return RemoteViews(app.packageName, R.layout.widget_streak).apply {
                setTextViewText(
                    R.id.widget_streak,
                    "${(streak?.currentLength ?: 0).toPersianDigits()} روز",
                )
                // شعله خاموش = کارِ امروز مانده. تنها نشانه‌ای که از فاصله
                // چند متری هم خوانده می‌شود، پس باید برداری و پررنگ باشد و
                // نه اموجی که روی هر گوشی شکل دیگری دارد.
                setImageViewResource(
                    R.id.widget_flame,
                    if (activeToday) R.drawable.ic_flame_lit else R.drawable.ic_flame_dim,
                )
                setTextViewText(R.id.widget_message, message(activeToday, todayXp, goal, due))

                val pct = if (goal <= 0) 100 else (todayXp * 100 / goal).coerceIn(0, 100)
                setProgressBar(R.id.widget_bar, 100, pct, false)
                setTextViewText(
                    R.id.widget_goal,
                    "${todayXp.toPersianDigits()} از ${goal.toPersianDigits()} امتیاز امروز",
                )

                setOnClickPendingIntent(R.id.widget_root, openApp(app))
            }
        }

        private fun message(activeToday: Boolean, xp: Int, goal: Int, due: Int): String = when {
            activeToday && xp >= goal -> "هدف امروز کامل شد"
            activeToday -> "${(goal - xp).toPersianDigits()} امتیاز تا هدف امروز"
            due > 0 -> "${due.toPersianDigits()} واژه آماده مرور است"
            else -> "امروز هنوز تمرین نکرده‌ای"
        }

        private fun openApp(context: Context): PendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }
}
