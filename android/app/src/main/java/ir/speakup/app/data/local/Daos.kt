package ir.speakup.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {

    @Query("SELECT * FROM levels ORDER BY sortOrder")
    fun observeLevels(): Flow<List<LevelEntity>>

    @Query("SELECT * FROM lessons WHERE levelCode = :level ORDER BY number")
    fun observeLessons(level: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun lesson(id: String): LessonEntity?

    @Query("SELECT * FROM sections WHERE lessonId = :lessonId ORDER BY sortOrder")
    suspend fun sections(lessonId: String): List<SectionEntity>

    @Query("SELECT * FROM activities WHERE sectionId = :sectionId ORDER BY sortOrder")
    suspend fun activities(sectionId: String): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun activity(id: String): ActivityEntity?

    /** آیتم‌های همه فعالیت‌های یک بخش که نوع مشخصی دارند، به ترتیب */
    @Query("""
        SELECT i.* FROM activity_items i
        JOIN activities a ON a.id = i.activityId
        WHERE a.sectionId = :sectionId AND a.activityType = :type
        ORDER BY a.sortOrder, i.sortOrder
    """)
    suspend fun itemsOfType(sectionId: String, type: String): List<ActivityItemEntity>

    @Query("SELECT id FROM activities WHERE sectionId = :sectionId AND activityType = :type ORDER BY sortOrder")
    suspend fun activityIdsOfType(sectionId: String, type: String): List<String>

    @Query("SELECT * FROM activity_items WHERE activityId = :activityId ORDER BY sortOrder")
    suspend fun items(activityId: String): List<ActivityItemEntity>

    /**
     * نام فایل تصاویر یک درس — برای بنر سطح و پس‌زمینه کارت بخش‌ها.
     *
     * پیش‌تر این فهرست از پیمایش `assets/images` می‌آمد، اما وقتی تصاویر
     * به سرور منتقل شدند فقط درس اول در assets ماند و بقیه دروس بی‌تصویر
     * شدند. منبع درست، خودِ محتواست: هر واژه نام فایل تصویرش را دارد.
     */
    @Query("""
        SELECT DISTINCT i.imageFile FROM activity_items i
        JOIN activities a ON a.id = i.activityId
        JOIN sections s ON s.id = a.sectionId
        WHERE s.lessonId = :lessonId AND i.imageFile IS NOT NULL AND i.imageFile != ''
        ORDER BY i.imageFile
    """)
    suspend fun lessonImageFiles(lessonId: String): List<String>

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun lessonCount(): Int

    /**
     * شمار دروس یک سطح — برای تصمیم واردکردن بسته‌های همراه APK.
     *
     * با `lessonCount()` کلی، افزودن سطح تازه به نسخه بعدی اپ برای کسی
     * که از قبل نصب داشت هیچ اثری نداشت: چون دروس A1 موجود بود، ورود
     * کلاً رد می‌شد و A2 هرگز وارد نمی‌شد.
     */
    @Query("SELECT COUNT(*) FROM lessons WHERE levelCode = :level")
    suspend fun lessonCount(level: String): Int

    /** مجموع دقیقه‌های یک سطح — برای شفاف کردن اندازه سطح پیش از انتخابش */
    @Query("SELECT COALESCE(SUM(estimatedMinutes), 0) FROM lessons WHERE levelCode = :level")
    suspend fun levelMinutes(level: String): Int

    /** مجموع دقیقه همه سطوح — برای نشان دادن اندازه واقعی محتوا در پی‌وال */
    @Query("SELECT COALESCE(SUM(estimatedMinutes), 0) FROM lessons")
    suspend fun totalMinutes(): Int

    /**
     * فقط سطوحی که واقعاً درس دارند.
     *
     * فهرست سطوح شش‌تایی و ثابت است (تا C1)، اما نمایش سطحی که هنوز
     * محتوا ندارد یعنی کاربر رویش می‌زند و صفحه خالی می‌بیند. پیش‌تر
     * این فیلتر در ViewModel و دستی بود (A1 و A2)، و با افزوده‌شدن
     * B1 و B2 از قلم افتاد.
     */
    @Query("""
        SELECT l.* FROM levels l
        WHERE EXISTS (SELECT 1 FROM lessons WHERE levelCode = l.code)
        ORDER BY l.sortOrder
    """)
    suspend fun levelsWithContent(): List<LevelEntity>

    /**
     * ⚠️ عمداً IGNORE و نه REPLACE.
     *
     * `lessons.levelCode` کلید خارجی با `onDelete = CASCADE` است. REPLACE
     * در SQLite یعنی «حذف سطر قدیم، درج سطر تازه» — و آن حذف، همه دروس
     * آن سطح را هم با خودش می‌برد.
     *
     * با یک بسته محتوا این نامرئی بود (دروس بلافاصله بعدش دوباره درج
     * می‌شدند). با دو سطح، وارد کردن بسته دوم دروس بسته اول را پاک می‌کرد.
     *
     * فهرست سطوح ثابت است و نیازی به به‌روزرسانی ندارد.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertLevels(x: List<LevelEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertLessons(x: List<LessonEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSections(x: List<SectionEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertActivities(x: List<ActivityEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertItems(x: List<ActivityItemEntity>)
}

@Dao
interface DictionaryDao {

    @Query("SELECT * FROM dictionary_entries WHERE word LIKE :prefix || '%' ORDER BY frequencyRank, word LIMIT :limit")
    suspend fun search(prefix: String, limit: Int = 50): List<DictionaryEntryEntity>

    @Query("SELECT * FROM dictionary_entries WHERE word = :word LIMIT 1")
    suspend fun byWord(word: String): DictionaryEntryEntity?

    @Query("SELECT * FROM dictionary_entries WHERE id = :id")
    suspend fun byId(id: String): DictionaryEntryEntity?

    @Query("SELECT lemma FROM word_forms WHERE form = :form LIMIT 1")
    suspend fun lemmaOf(form: String): String?

    @Query("SELECT COUNT(*) FROM dictionary_entries")
    suspend fun entryCount(): Int

    /**
     * ریشه‌یابی برای دیکشنری هوشمند (سند ۰۳ / F-06):
     * ابتدا واژه عیناً، سپس نگاشت صورت‌های صرفی، سپس ریشه.
     */
    @Transaction
    suspend fun resolve(raw: String): DictionaryEntryEntity? {
        val w = raw.trim().lowercase().trim('.', ',', '!', '?', ';', ':', '"', '’', '\'')
        if (w.isEmpty()) return null
        byWord(w)?.let { return it }
        lemmaOf(w)?.let { lemma -> byWord(lemma)?.let { return it } }
        return null
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertEntries(x: List<DictionaryEntryEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertForms(x: List<WordFormEntity>)
}

@Dao
interface ProgressDao {

    @Query("SELECT * FROM user_progress WHERE activityId = :id")
    suspend fun get(id: String): UserProgressEntity?

    @Query("SELECT * FROM user_progress")
    fun observeAll(): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE status = 'IN_PROGRESS' ORDER BY updatedAt DESC LIMIT 1")
    fun observeResumePoint(): Flow<UserProgressEntity?>

    @Upsert suspend fun upsert(x: UserProgressEntity)

    @Query("SELECT * FROM user_progress WHERE syncedAt IS NULL")
    suspend fun unsynced(): List<UserProgressEntity>

    @Query("UPDATE user_progress SET syncedAt = :at WHERE syncedAt IS NULL")
    suspend fun markSynced(at: Long)
}

@Dao
interface LeitnerDao {

    @Query("SELECT * FROM leitner_cards WHERE dueAt <= :now ORDER BY box, dueAt LIMIT :limit")
    suspend fun due(now: Long, limit: Int = 20): List<LeitnerCardEntity>

    @Query("SELECT COUNT(*) FROM leitner_cards WHERE dueAt <= :now")
    fun observeDueCount(now: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM leitner_cards WHERE box >= :maxBox")
    fun observeLearnedCount(maxBox: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM leitner_cards WHERE box < :maxBox")
    fun observeLearningCount(maxBox: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM leitner_cards WHERE box >= :maxBox")
    suspend fun countLearned(maxBox: Int): Int

    @Query("SELECT COUNT(*) FROM leitner_cards WHERE box < :maxBox")
    suspend fun countLearning(maxBox: Int): Int

    @Query("SELECT word FROM leitner_cards")
    suspend fun allWords(): List<String>

    @Query("SELECT * FROM leitner_cards WHERE entryId = :entryId LIMIT 1")
    suspend fun byEntry(entryId: String): LeitnerCardEntity?

    @Upsert suspend fun upsert(x: LeitnerCardEntity)

    @Query("SELECT * FROM leitner_cards WHERE syncedAt IS NULL")
    suspend fun unsynced(): List<LeitnerCardEntity>

    @Query("UPDATE leitner_cards SET syncedAt = :at WHERE syncedAt IS NULL")
    suspend fun markSynced(at: Long)

    @Query("DELETE FROM leitner_cards")
    suspend fun clear()
}

@Dao
interface StreakDao {

    @Query("SELECT * FROM streak WHERE id = 1")
    fun observe(): Flow<StreakEntity?>

    @Query("SELECT * FROM streak WHERE id = 1")
    suspend fun get(): StreakEntity?

    @Upsert suspend fun upsert(x: StreakEntity)

    @Query("SELECT * FROM streak_days WHERE date >= :fromDate ORDER BY date")
    suspend fun daysSince(fromDate: String): List<StreakDayEntity>

    @Upsert suspend fun upsertDay(x: StreakDayEntity)
}

@Dao
interface AnswerLogDao {

    @Insert suspend fun insert(x: AnswerLogEntity)

    @Query("SELECT * FROM answer_log WHERE syncedAt IS NULL LIMIT :limit")
    suspend fun unsynced(limit: Int = 500): List<AnswerLogEntity>

    @Query("UPDATE answer_log SET syncedAt = :at WHERE syncedAt IS NULL")
    suspend fun markSynced(at: Long)

    /**
     * متریک داوری محصول (سند ۰۷ بخش ۷.۷):
     * نرخ تولید صحیح در مرور تأخیری — فقط تمرین‌های تولیدی، فقط بعد از فاصله مشخص.
     */
    @Query("""
        SELECT CAST(SUM(CASE WHEN isCorrect THEN 1 ELSE 0 END) AS FLOAT) / COUNT(*)
        FROM answer_log
        WHERE isProductive = 1 AND answeredAt >= :since
    """)
    suspend fun productiveAccuracy(since: Long): Float?

    /** پرتکرارترین واژه‌های اشتباه — ورودی تولید تمرین اختصاصی */
    @Query("""
        SELECT targetWord FROM answer_log
        WHERE isCorrect = 0 AND targetWord IS NOT NULL
        GROUP BY targetWord ORDER BY COUNT(*) DESC LIMIT :limit
    """)
    suspend fun weakestWords(limit: Int = 10): List<String>
}

/**
 * امتیاز کاربر.
 *
 * همه پرس‌وجوها روی رویدادها کار می‌کنند و نه روی یک شمارنده، چون هدف
 * روزانه و نمودار هفتگی بدون تاریخچه ساختنی نیستند.
 */
@Dao
interface XpDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: XpEventEntity)

    @Query("SELECT COALESCE(SUM(amount), 0) FROM xp_events")
    fun observeTotal(): Flow<Int>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM xp_events WHERE dayKey = :dayKey")
    fun observeForDay(dayKey: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM xp_events WHERE dayKey = :dayKey")
    suspend fun forDay(dayKey: String): Int

    /** امتیاز هر روز در بازه — برای نمودار هفتگی */
    @Query("""
        SELECT dayKey AS day, SUM(amount) AS xp FROM xp_events
        WHERE dayKey >= :fromDay GROUP BY dayKey ORDER BY dayKey
    """)
    fun observeDaily(fromDay: String): Flow<List<DayXp>>

    /** آیا پاداش هدف روزانه امروز داده شده — تا دو بار داده نشود */
    @Query("SELECT COUNT(*) FROM xp_events WHERE dayKey = :dayKey AND reason = 'DAILY_GOAL'")
    suspend fun goalBonusCount(dayKey: String): Int

    @Query("SELECT * FROM xp_events WHERE syncedAt IS NULL")
    suspend fun unsynced(): List<XpEventEntity>

    @Query("UPDATE xp_events SET syncedAt = :at WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>, at: Long)
}

data class DayXp(val day: String, val xp: Int)
