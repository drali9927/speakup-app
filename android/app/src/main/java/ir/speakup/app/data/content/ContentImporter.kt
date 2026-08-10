package ir.speakup.app.data.content

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.speakup.app.data.local.ActivityEntity
import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.data.local.ContentDao
import ir.speakup.app.data.local.DictionaryDao
import ir.speakup.app.data.local.DictionaryEntryEntity
import ir.speakup.app.data.local.LessonEntity
import ir.speakup.app.data.local.LevelEntity
import ir.speakup.app.data.local.SectionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * بسته محتوای همراه اپ را وارد دیتابیس می‌کند.
 *
 * فعلاً محتوا از assets خوانده می‌شود تا اپ بدون بک‌اند هم کامل کار کند.
 * وقتی سرویس محتوا آماده شد، همین کلاس بسته دانلودشده را می‌خواند —
 * بقیه اپ نیازی به تغییر ندارد.
 */
@Singleton
class ContentImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentDao: ContentDao,
    private val dictionaryDao: DictionaryDao,
    private val json: Json,
) {

    /**
     * وارد کردن بسته‌های همراه APK.
     *
     * همه سطوحی که فایلشان در assets هست وارد می‌شوند، نه فقط A1. بدون
     * این، کاربر A2 را در فهرست سطوح می‌دید ولی درسی نداشت.
     *
     * نبودن فایل یک سطح خطا نیست: سطوح بالاتر ممکن است هنوز نوشته نشده
     * باشند یا فقط از سرور بیایند.
     */
    suspend fun importIfNeeded(): Result = withContext(Dispatchers.IO) {
        runCatching {
            var lessons = 0
            var items = 0
            var skipped = 0
            for (asset in ASSETS) {
                val raw = runCatching {
                    context.assets.open(asset).bufferedReader().use { it.readText() }
                }.getOrNull() ?: continue
                val bundle = json.decodeFromString<ContentBundle>(raw)

                // تصمیم برای هر سطح جداگانه گرفته می‌شود و نه یک‌بار برای
                // کل دیتابیس؛ وگرنه سطح تازه در نسخه بعدی اپ به دست
                // کاربرانِ قدیمی نمی‌رسد.
                if (contentDao.lessonCount(bundle.levelCode) > 0) {
                    skipped++
                    continue
                }
                write(bundle)
                lessons += bundle.lessons.size
                items += bundle.items.size
                Log.i(TAG, "imported $asset: ${bundle.lessons.size} lessons")
            }
            if (lessons == 0) {
                if (skipped > 0) return@runCatching Result.AlreadyPresent
                error("هیچ بسته محتوایی در assets پیدا نشد")
            }
            Result.Imported(lessons, items)
        }.getOrElse {
            Log.e(TAG, "content import failed", it)
            Result.Failed(it)
        }
    }

    /** وارد کردن بسته‌ای که از سرور آمده — جایگزین کامل محتوای فعلی */
    suspend fun importBundle(bundle: ContentBundle) = withContext(Dispatchers.IO) {
        write(bundle)
    }

    /** برای توسعه: محتوا را دوباره از assets می‌خواند حتی اگر قبلاً وارد شده باشد. */
    suspend fun forceReimport(): Result = withContext(Dispatchers.IO) {
        runCatching {
            var lessons = 0
            var items = 0
            for (asset in ASSETS) {
                val raw = runCatching {
                    context.assets.open(asset).bufferedReader().use { it.readText() }
                }.getOrNull() ?: continue
                val bundle = json.decodeFromString<ContentBundle>(raw)
                write(bundle)
                lessons += bundle.lessons.size
                items += bundle.items.size
            }
            Result.Imported(lessons, items)
        }.getOrElse { Result.Failed(it) }
    }

    private suspend fun write(b: ContentBundle) {
        // ترتیب درج مهم است — کلیدهای خارجی به والد نیاز دارند
        contentDao.insertLevels(b.levels.map { LevelEntity(it.code, it.titleFa, it.titleEn, it.sortOrder) })
        contentDao.insertLessons(b.lessons.map {
            LessonEntity(it.id, it.levelCode, it.number, it.titleEn, it.grammarTopicFa,
                it.themeFa, it.isFree, it.estimatedMinutes, it.colorHex)
        })
        contentDao.insertSections(b.sections.map {
            SectionEntity(it.id, it.lessonId, it.type, it.sortOrder, it.estimatedMinutes)
        })
        contentDao.insertActivities(b.activities.map {
            ActivityEntity(it.id, it.sectionId, it.title, it.descriptionFa, it.activityType, it.sortOrder)
        })
        contentDao.insertItems(b.items.map {
            ActivityItemEntity(
                id = it.id, activityId = it.activityId, sortOrder = it.sortOrder,
                prompt = it.prompt, promptFa = it.promptFa,
                exampleEn = it.exampleEn, exampleFa = it.exampleFa,
                correctAnswer = it.correctAnswer,
                alternatives = it.alternatives, options = it.options, hintFa = it.hintFa,
                imageFile = it.imageFile, ttsText = it.ttsText, targetWord = it.targetWord,
                checkPrompt = it.checkPrompt, checkAnswer = it.checkAnswer,
                checkOptions = it.checkOptions, checkTips = it.checkTips,
            )
        })
        dictionaryDao.insertEntries(b.dictionary.map {
            DictionaryEntryEntity(
                id = it.id, word = it.word, lemma = it.lemma, pos = it.pos,
                ipaUk = it.ipaUk, ipaUs = it.ipaUs, definitionEn = it.definitionEn,
                translationFa = it.translationFa, exampleEn = it.exampleEn,
                exampleFa = it.exampleFa, frequencyRank = it.frequencyRank,
            )
        })
    }

    sealed interface Result {
        data object AlreadyPresent : Result
        data class Imported(val lessons: Int, val items: Int) : Result
        data class Failed(val cause: Throwable) : Result
    }

    private companion object {
        val ASSETS = listOf(
            "content_a1.json", "content_a2.json",
            "content_b1.json", "content_b2.json",
        )
        const val TAG = "ContentImporter"
    }
}
