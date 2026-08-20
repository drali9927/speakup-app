package ir.speakup.app.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("speakup_prefs")

/** تنظیمات و وضعیت نشست — کوچک و کلید-مقدار، جدا از دیتابیس محتوا */
@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val PHONE = stringPreferencesKey("phone")
        val TOKEN = stringPreferencesKey("token")
        val REFERRAL = stringPreferencesKey("referral_code")
        val SUB_PLAN = stringPreferencesKey("subscription_plan")
        val SUB_EXPIRES = longPreferencesKey("subscription_expires_at")
        val DAILY_GOAL = intPreferencesKey("daily_goal_xp")
        val LEVEL = stringPreferencesKey("current_level")
        val LAST_SYNC = longPreferencesKey("last_sync_at")
        val REMINDERS_ON = booleanPreferencesKey("reminders_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val LAST_REMINDER_DAY = stringPreferencesKey("last_reminder_day")
        val AVATAR = intPreferencesKey("avatar_id")
        val INSTALL_ID = stringPreferencesKey("install_id")
        val PLACED = booleanPreferencesKey("placement_done")
    }

    val onboarded: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDED] ?: false }
    val token: Flow<String?> = context.dataStore.data.map { it[Keys.TOKEN] }
    val phone: Flow<String?> = context.dataStore.data.map { it[Keys.PHONE] }

    /**
     * شناسه چهره انتخابی، یا صفر یعنی هنوز انتخاب نکرده.
     *
     * صفر را «بدون چهره» نمی‌گیریم: صفحه کارنامه با یک جای خالی شروع
     * شود، هم بی‌ریخت است و هم کاربر تازه‌وارد را می‌فرستد سراغ کاری که
     * حالا حوصله‌اش را ندارد. به‌جایش از شماره‌اش یکی را برمی‌داریم که
     * ثابت بماند و هر بار عوض نشود — بعداً هر وقت خواست تغییرش می‌دهد.
     */
    val avatarId: Flow<Int> = context.dataStore.data.map { it[Keys.AVATAR] ?: 0 }
    val referralCode: Flow<String?> = context.dataStore.data.map { it[Keys.REFERRAL] }
    /**
     * تاریخ انقضای اشتراک، به ثانیه. صفر یعنی اشتراکی نیست.
     *
     * این مقدار فقط از `/v1/me` می‌آید و اپ خودش هرگز آن را نمی‌سازد.
     * پیش‌تر یک بولین محلی بود که خود اپ روشنش می‌کرد — یعنی هر کسی با
     * دستکاری فایل تنظیمات اشتراک می‌گرفت.
     */
    val subscriptionExpiresAt: Flow<Long> = context.dataStore.data.map { it[Keys.SUB_EXPIRES] ?: 0L }
    val subscriptionPlan: Flow<String?> = context.dataStore.data.map { it[Keys.SUB_PLAN] }
    /** هدف امتیاز روزانه — خودِ کاربر در معرفی انتخابش می‌کند */
    val dailyGoalXp: Flow<Int> = context.dataStore.data.map {
        it[Keys.DAILY_GOAL] ?: ir.speakup.app.domain.XpRules.DEFAULT_GOAL
    }
    /** سطح فعلی کاربر — پیش‌فرض A1 */
    val currentLevel: Flow<String> = context.dataStore.data.map { it[Keys.LEVEL] ?: "A1" }
    val lastSyncAt: Flow<Long> = context.dataStore.data.map { it[Keys.LAST_SYNC] ?: 0L }

    /** یادآور پیش‌فرض روشن است — کاربری که خودش روشنش کند تقریباً وجود ندارد */
    val remindersEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.REMINDERS_ON] ?: true }

    /** زودترین ساعتی که یادآور امروز می‌تواند برود — یکی از گزینه‌های صفحه پروفایل */
    val reminderHour: Flow<Int> = context.dataStore.data.map { it[Keys.REMINDER_HOUR] ?: 20 }

    /** روزی که آخرین یادآور رفت — قفلِ «یکی در روز» */
    val lastReminderDay: Flow<String?> = context.dataStore.data.map { it[Keys.LAST_REMINDER_DAY] }

    suspend fun setRemindersEnabled(on: Boolean) {
        context.dataStore.edit { it[Keys.REMINDERS_ON] = on }
    }

    /**
     * شناسه نصب — تصادفی و ساخته خودمان، نه شناسه سخت‌افزاری.
     *
     * فقط برای شمردن قیف است. با پاک شدن اپ می‌رود، که درست است: آن نصب
     * واقعاً تمام شده و نباید در آمار بازگشت شمرده شود.
     */
    suspend fun installId(): String? = context.dataStore.data.map { it[Keys.INSTALL_ID] }.first()

    /** آزمون تعیین سطح انجام یا رد شده — تا دوباره جلوی کاربر سبز نشود */
    val placed: Flow<Boolean> = context.dataStore.data.map { it[Keys.PLACED] ?: false }

    suspend fun setPlaced() {
        context.dataStore.edit { it[Keys.PLACED] = true }
    }

    suspend fun setInstallId(id: String) {
        context.dataStore.edit { it[Keys.INSTALL_ID] = id }
    }

    suspend fun setAvatar(id: Int) {
        context.dataStore.edit { it[Keys.AVATAR] = id }
    }

    suspend fun setReminderHour(hour: Int) {
        context.dataStore.edit { it[Keys.REMINDER_HOUR] = hour.coerceIn(8, 21) }
    }

    suspend fun setLastReminderDay(day: String) {
        context.dataStore.edit { it[Keys.LAST_REMINDER_DAY] = day }
    }

    suspend fun setOnboarded() {
        context.dataStore.edit { it[Keys.ONBOARDED] = true }
    }

    suspend fun saveSession(phone: String, token: String, referralCode: String) {
        context.dataStore.edit {
            it[Keys.PHONE] = phone
            it[Keys.TOKEN] = token
            it[Keys.REFERRAL] = referralCode
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it.remove(Keys.PHONE); it.remove(Keys.TOKEN); it.remove(Keys.REFERRAL)
        }
    }

    /** نسخه بسته محتوای یک سطح — کلید پویا چون سطوح اضافه می‌شوند */
    fun contentVersion(level: String): Flow<String?> =
        context.dataStore.data.map { it[stringPreferencesKey("content_v_${'$'}{level.lowercase()}")] }

    suspend fun setContentVersion(level: String, version: String) {
        context.dataStore.edit { it[stringPreferencesKey("content_v_${'$'}{level.lowercase()}")] = version }
    }

    suspend fun setCurrentLevel(code: String) {
        context.dataStore.edit { it[Keys.LEVEL] = code }
    }

    suspend fun setDailyGoalXp(xp: Int) {
        context.dataStore.edit { it[Keys.DAILY_GOAL] = xp }
    }

    suspend fun setLastSyncAt(epochSeconds: Long) {
        context.dataStore.edit { it[Keys.LAST_SYNC] = epochSeconds }
    }

    /** فقط از پاسخ سرور فراخوانی می‌شود */
    suspend fun setSubscription(plan: String?, expiresAt: Long?) {
        context.dataStore.edit {
            if (plan == null) {
                it.remove(Keys.SUB_PLAN); it.remove(Keys.SUB_EXPIRES)
            } else {
                it[Keys.SUB_PLAN] = plan
                it[Keys.SUB_EXPIRES] = expiresAt ?: Long.MAX_VALUE   // null یعنی مادام‌العمر
            }
        }
    }
}
