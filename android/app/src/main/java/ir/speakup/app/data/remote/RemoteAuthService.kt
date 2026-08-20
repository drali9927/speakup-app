package ir.speakup.app.data.remote

import ir.speakup.app.domain.AuthService
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * احراز هویت واقعی — جایگزین `LocalAuthService`.
 *
 * چیزی جز این کلاس عوض نشد؛ `AuthViewModel` همان واسط `AuthService` را
 * می‌بیند. جدا کردن واسط دقیقاً برای همین لحظه بود.
 */
@Singleton
class RemoteAuthService @Inject constructor(
    private val api: Api,
    private val json: Json,
) : AuthService {

    override suspend fun requestCode(phone: String): Result<Boolean> = runCatching {
        val res = api.requestCode(PhoneRequest(phone))
        if (!res.isSuccessful) throw ApiException(errorOf(res.errorBody()?.string()), res.code())
        res.body()?.skipCode == true
    }.recoverCatching { throw it.asUserFacing() }

    override suspend fun verifyCode(phone: String, code: String): Result<AuthService.Session> = runCatching {
        val res = api.verify(VerifyRequest(phone, code))
        if (!res.isSuccessful) throw ApiException(errorOf(res.errorBody()?.string()), res.code())
        val b = res.body() ?: throw ApiException("empty_response", res.code())
        AuthService.Session(
            phone = b.user.phone,
            token = b.token,
            referralCode = b.user.referralCode,
        )
    }.recoverCatching { throw it.asUserFacing() }

    private fun errorOf(raw: String?): String =
        raw?.let { runCatching { json.decodeFromString<ApiError>(it).error }.getOrNull() } ?: "unknown"
}

class ApiException(val code: String, val httpStatus: Int) : Exception(code)

/**
 * تبدیل خطای فنی به پیام فارسی.
 * کاربر نباید کد خام خطا یا نام کلاس استثنا ببیند.
 */
fun Throwable.asUserFacing(): Throwable = when {
    this is ApiException -> Exception(
        when (code) {
            "invalid_phone" -> "شماره موبایل معتبر نیست"
            "too_soon" -> "کمی صبر کن و دوباره امتحان کن"
            "wrong_code" -> "کد وارد شده درست نیست"
            "expired" -> "کد منقضی شده؛ کد تازه بگیر"
            "no_code" -> "کدی برای این شماره ثبت نشده؛ دوباره درخواست بده"
            "too_many_attempts" -> "تعداد تلاش‌ها زیاد شد؛ کد تازه بگیر"
            // سرور کد را ساخت ولی پیامک نرفت. کاربر باید بداند تقصیر او
            // نیست و دوباره تلاش کردن منطقی است — نه اینکه شماره‌اش را
            // اشتباه بداند و بی‌نتیجه عوضش کند.
            "sms_failed" -> "ارسال پیامک ناموفق بود؛ چند لحظه بعد دوباره تلاش کن"
            "daily_limit" -> "امروز چند بار کد گرفتی؛ فردا دوباره امتحان کن"
            else -> "مشکلی پیش آمد، دوباره امتحان کن"
        }
    )
    // هر خطای ورودی/خروجی یعنی مشکل شبکه.
    //
    // پیش‌تر فقط سه استثنای مشخص گرفته می‌شد و بقیه به پیام مبهم «مشکلی
    // پیش آمد» می‌افتادند. با خاموش بودن سرور روی دستگاه آزمایش شد و
    // دقیقاً همان اتفاق افتاد: کاربر پیامی می‌دید که نمی‌گفت تقصیر
    // اینترنت است، تقصیر ماست، یا شماره‌اش غلط بوده.
    //
    // OkHttp همه خطاهای لایه انتقال را در IOException می‌پیچد — قطع
    // اتصال، ریست، تایم‌اوت، DNS. خطاهای خودِ API از این مسیر نمی‌آیند
    // و ApiException می‌شوند، پس این شرط چیزی را اشتباه دسته‌بندی
    // نمی‌کند.
    this is java.io.IOException ->
        Exception("اتصال به سرور برقرار نشد؛ اینترنت را بررسی کن")
    else -> Exception("مشکلی پیش آمد، دوباره امتحان کن")
}
