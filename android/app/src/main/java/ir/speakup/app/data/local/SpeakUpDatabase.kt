package ir.speakup.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        // محتوا
        LevelEntity::class,
        LessonEntity::class,
        SectionEntity::class,
        ActivityEntity::class,
        ActivityItemEntity::class,
        DictionaryEntryEntity::class,
        WordFormEntity::class,
        // کاربر
        UserProgressEntity::class,
        LeitnerCardEntity::class,
        StreakEntity::class,
        StreakDayEntity::class,
        AnswerLogEntity::class,
        XpEventEntity::class,
        ReadingProgressEntity::class,
    ],
    // ۲: ستون‌های بررسی درک به activity_items اضافه شد
    // ۴: ستون voice — جنسیت گوینده مکالمه
    // ۵: ستون‌های ترمیم زنجیره
    version = 5,
    exportSchema = true,
)
abstract class SpeakUpDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun progressDao(): ProgressDao
    abstract fun leitnerDao(): LeitnerDao
    abstract fun streakDao(): StreakDao
    abstract fun answerLogDao(): AnswerLogDao
    abstract fun xpDao(): XpDao

    companion object {
        const val NAME = "speakup.db"
    }
}
