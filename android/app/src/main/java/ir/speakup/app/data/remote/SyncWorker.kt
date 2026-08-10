package ir.speakup.app.data.remote

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * همگام‌سازی پس‌زمینه.
 *
 * اپ آفلاین‌محور است: کاربر هیچ‌وقت منتظر شبکه نمی‌ماند و شکست همگام‌سازی
 * هرگز به او نشان داده نمی‌شود. WorkManager خودش صف، تلاش دوباره و
 * محدودیت شبکه را مدیریت می‌کند.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: SyncManager,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = when (val r = syncManager.sync()) {
        is SyncManager.Result.Ok -> {
            Log.i(TAG, "synced push=${r.pushed} pull=${r.pulled}")
            Result.success()
        }
        // کاربر هنوز وارد نشده — تلاش دوباره بی‌فایده است
        SyncManager.Result.NotLoggedIn -> Result.success()
        is SyncManager.Result.Failed -> {
            Log.w(TAG, "sync failed: ${r.reason}")
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "SyncWorker"
        private const val PERIODIC = "speakup-sync-periodic"
        private const val ONE_SHOT = "speakup-sync-now"

        private val networkRequired = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        /** زمان‌بندی دوره‌ای — یک بار در راه‌اندازی اپ فراخوانی می‌شود */
        fun schedulePeriodic(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
                    .setConstraints(networkRequired)
                    .build(),
            )
        }

        /**
         * همگام‌سازی فوری پس از لحظه‌های مهم — اتمام فعالیت، ورود، بازگشت به اپ.
         *
         * REPLACE یعنی اگر کاربر پشت‌سرهم چند فعالیت تمام کند، فقط آخرین
         * درخواست می‌ماند و شبکه بی‌جهت اشغال نمی‌شود.
         */
        fun syncNow(context: Context) {
            WorkManager.getInstance(context).enqueueUniqueWork(
                ONE_SHOT,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<SyncWorker>()
                    .setConstraints(networkRequired)
                    .build(),
            )
        }
    }
}
