package ir.speakup.app.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.speakup.app.BuildConfig
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.data.remote.Api
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * توکن به هر درخواست اضافه می‌شود.
     *
     * `runBlocking` اینجا عمدی و بی‌خطر است: OkHttp روی نخ پس‌زمینه خودش
     * اجرا می‌شود و خواندن یک کلید از DataStore میکروثانیه‌ای است.
     * جایگزینش نگه داشتن توکن در حافظه است که با چند فرایند ناسازگار می‌شود.
     */
    @Provides
    @Singleton
    fun okHttp(prefs: AppPreferences): OkHttpClient {
        val auth = Interceptor { chain ->
            val token = runBlocking { prefs.token.first() }
            val req = chain.request().newBuilder().apply {
                if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
            }.build()
            chain.proceed(req)
        }

        return OkHttpClient.Builder()
            .addInterceptor(auth)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
                    )
                }
            }
            // شبکه ایران کند و پرنوسان است؛ مهلت‌های سخاوتمندانه بهتر از
            // شکست زودهنگام‌اند، چون اپ آفلاین‌محور است و عجله‌ای ندارد.
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun retrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun api(retrofit: Retrofit): Api = retrofit.create(Api::class.java)
}
