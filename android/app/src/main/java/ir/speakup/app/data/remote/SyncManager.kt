package ir.speakup.app.data.remote

import android.util.Log
import ir.speakup.app.data.local.AnswerLogDao
import ir.speakup.app.data.local.LeitnerCardEntity
import ir.speakup.app.data.local.LeitnerDao
import ir.speakup.app.data.local.ProgressDao
import ir.speakup.app.data.local.UserProgressEntity
import ir.speakup.app.data.prefs.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * همگام‌سازی با سرور.
 *
 * دیتابیس محلی منبع حقیقت رابط کاربری است و هیچ صفحه‌ای منتظر شبکه
 * نمی‌ماند (سند ۰۵ بخش ۵.۸). این کلاس فقط در پس‌زمینه اجرا می‌شود؛
 * شکستش هرگز به کاربر نشان داده نمی‌شود و کار او را متوقف نمی‌کند.
 */
@Singleton
class SyncManager @Inject constructor(
    private val api: Api,
    private val prefs: AppPreferences,
    private val progressDao: ProgressDao,
    private val leitnerDao: LeitnerDao,
    private val answerLogDao: AnswerLogDao,
) {

    sealed interface Result {
        data class Ok(val pushed: Int, val pulled: Int) : Result
        data object NotLoggedIn : Result
        data class Failed(val reason: String) : Result
    }

    suspend fun sync(): Result = withContext(Dispatchers.IO) {
        if (prefs.token.first() == null) return@withContext Result.NotLoggedIn

        runCatching {
            val since = prefs.lastSyncAt.first()

            val progress = progressDao.unsynced().map {
                RemoteProgress(
                    activityId = it.activityId, status = it.status, score = it.score,
                    lastItem = it.lastItemIndex, completedAt = it.completedAt?.div(1000),
                    updatedAt = it.updatedAt / 1000,
                )
            }
            val leitner = leitnerDao.unsynced().map {
                RemoteLeitner(
                    entryId = it.entryId, word = it.word, box = it.box,
                    dueAt = it.dueAt / 1000, correctStreak = it.correctStreak,
                    totalReviews = it.totalReviews, source = it.source,
                    updatedAt = it.updatedAt / 1000,
                )
            }
            val answers = answerLogDao.unsynced().map {
                RemoteAnswer(
                    itemId = it.itemId, activityId = it.activityId,
                    isProductive = it.isProductive, userAnswer = it.userAnswer,
                    isCorrect = it.isCorrect, targetWord = it.targetWord,
                    answeredAt = it.answeredAt / 1000,
                )
            }

            val res = api.sync(SyncRequest(since, progress, leitner, answers))
            if (!res.isSuccessful) throw ApiException("http_${res.code()}", res.code())
            val body = res.body() ?: throw ApiException("empty_response", res.code())

            applyServerState(body)

            // فقط بعد از موفقیت کامل علامت می‌زنیم — اگر وسط کار قطع شود،
            // همان ردیف‌ها دفعه بعد دوباره فرستاده می‌شوند و چیزی گم نمی‌شود.
            val nowMs = body.now * 1000
            progressDao.markSynced(nowMs)
            leitnerDao.markSynced(nowMs)
            answerLogDao.markSynced(nowMs)
            prefs.setLastSyncAt(body.now)

            Log.i(TAG, "sync ok: pushed=${progress.size + leitner.size + answers.size} pulled=${body.progress.size + body.leitner.size}")
            Result.Ok(
                pushed = progress.size + leitner.size + answers.size,
                pulled = body.progress.size + body.leitner.size,
            )
        }.getOrElse {
            Log.w(TAG, "sync failed: ${it.message}")
            Result.Failed(it.message ?: "unknown")
        }
    }

    /** وضعیت سرور روی دیتابیس محلی نوشته می‌شود — سرور در تعارض برنده است */
    private suspend fun applyServerState(body: SyncResponse) {
        body.progress.forEach { p ->
            progressDao.upsert(
                UserProgressEntity(
                    activityId = p.activityId, status = p.status, score = p.score,
                    lastItemIndex = p.lastItem, completedAt = p.completedAt?.times(1000),
                    updatedAt = p.updatedAt * 1000, syncedAt = p.updatedAt * 1000,
                )
            )
        }
        body.leitner.forEach { c ->
            val existing = leitnerDao.byEntry(c.entryId)
            leitnerDao.upsert(
                LeitnerCardEntity(
                    id = existing?.id ?: java.util.UUID.randomUUID().toString(),
                    entryId = c.entryId, word = c.word, box = c.box,
                    dueAt = c.dueAt * 1000, correctStreak = c.correctStreak,
                    totalReviews = c.totalReviews, source = c.source,
                    createdAt = existing?.createdAt ?: (c.updatedAt * 1000),
                    updatedAt = c.updatedAt * 1000, syncedAt = c.updatedAt * 1000,
                )
            )
        }
    }

    private companion object { const val TAG = "SyncManager" }
}
