package ir.speakup.app.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.speakup.app.data.local.AnswerLogDao
import ir.speakup.app.data.local.XpDao
import ir.speakup.app.data.local.ContentDao
import ir.speakup.app.data.local.DictionaryDao
import ir.speakup.app.data.local.LeitnerDao
import ir.speakup.app.data.local.ProgressDao
import ir.speakup.app.data.local.SpeakUpDatabase
import ir.speakup.app.data.local.StreakDao
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {
    // پیاده‌سازی واقعی جای stub را گرفت. برای بازگشت به حالت آفلاین محض،
    // فقط همین دو خط به DeviceTimeSource و LocalAuthService برمی‌گردند.
    @Binds
    abstract fun timeSource(impl: ir.speakup.app.data.remote.ServerTimeSource): ir.speakup.app.domain.TimeSource

    @Binds
    abstract fun authService(impl: ir.speakup.app.data.remote.RemoteAuthService): ir.speakup.app.domain.AuthService

    /**
     * درگاه خرید هنوز وصل نیست و عمداً هیچ اشتراکی نمی‌دهد.
     * وقتی Poolakey اضافه شد، فقط همین خط عوض می‌شود.
     */
    @Binds
    abstract fun billingService(
        impl: ir.speakup.app.domain.UnavailableBillingService,
    ): ir.speakup.app.domain.BillingService
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SpeechModule {
    @Binds
    abstract fun speechRecognitionService(
        impl: ir.speakup.app.domain.AndroidSpeechRecognitionService,
    ): ir.speakup.app.domain.SpeechRecognitionService
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun database(@ApplicationContext ctx: Context): SpeakUpDatabase =
        Room.databaseBuilder(ctx, SpeakUpDatabase::class.java, SpeakUpDatabase.NAME)
            .fallbackToDestructiveMigration()   // فقط تا پیش از انتشار؛ بعد از آن باید Migration نوشته شود
            .build()

    @Provides fun contentDao(db: SpeakUpDatabase): ContentDao = db.contentDao()
    @Provides fun dictionaryDao(db: SpeakUpDatabase): DictionaryDao = db.dictionaryDao()
    @Provides fun progressDao(db: SpeakUpDatabase): ProgressDao = db.progressDao()
    @Provides fun leitnerDao(db: SpeakUpDatabase): LeitnerDao = db.leitnerDao()
    @Provides fun streakDao(db: SpeakUpDatabase): StreakDao = db.streakDao()
    @Provides fun answerLogDao(db: SpeakUpDatabase): AnswerLogDao = db.answerLogDao()
    @Provides fun xpDao(db: SpeakUpDatabase): XpDao = db.xpDao()

    @Provides
    @Singleton
    fun json(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}
