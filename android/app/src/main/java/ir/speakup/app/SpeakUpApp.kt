package ir.speakup.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ir.speakup.app.data.remote.SyncWorker
import ir.speakup.app.notify.Notifications
import ir.speakup.app.notify.ReminderWorker
import javax.inject.Inject

@HiltAndroidApp
class SpeakUpApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        // زمان‌بندی دوره‌ای؛ KEEP یعنی با هر بار باز شدن اپ دوباره صف نمی‌شود
        SyncWorker.schedulePeriodic(this)

        // کانال باید پیش از نخستین اعلان ساخته شده باشد، وگرنه اعلان
        // بی‌صدا دور ریخته می‌شود و هیچ خطایی هم دیده نمی‌شود.
        Notifications.ensureChannel(this)
        ReminderWorker.schedule(this)

        // ابزارک با چرخه نیم‌ساعته خودش هم تازه می‌شود، اما بعد از پاک شدن
        // داده اپ یا نصب دوباره، تا نخستین چرخه با چیدمان خامِ خودش روی
        // صفحه می‌ماند. یک به‌روزرسانی در آغاز اپ آن فاصله را می‌بندد.
        ir.speakup.app.widget.StreakWidget.refreshAll(this)
    }
}
