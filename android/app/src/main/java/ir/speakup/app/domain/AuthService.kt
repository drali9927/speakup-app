package ir.speakup.app.domain

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * احراز هویت با شماره موبایل.
 *
 * مثل بقیه لایه‌های بیرونی اپ (صوت، تشخیص گفتار، محتوا) پشت یک واسط
 * نشسته تا وقتی بک‌اند آماده شد فقط پیاده‌سازی عوض شود.
 *
 * ⚠️ `LocalAuthService` فقط برای توسعه است: کد تأیید را نمی‌فرستد و
 * هر کدی را می‌پذیرد. پیش از انتشار باید با پیاده‌سازی واقعی جایگزین شود،
 * وگرنه هر کسی با هر شماره‌ای وارد می‌شود.
 */
interface AuthService {
    /** ارسال کد تأیید به شماره */
    /**
     * درخواست کد.
     *
     * `true` یعنی این شماره حساب تست است و سرور کدی نفرستاده؛ اپ باید
     * مستقیم وارد شود. در سرور تولید هرگز `true` برنمی‌گردد.
     */
    suspend fun requestCode(phone: String): Result<Boolean>

    /** بررسی کد — در موفقیت، نشست ساخته می‌شود */
    suspend fun verifyCode(phone: String, code: String): Result<Session>

    data class Session(val phone: String, val token: String, val referralCode: String)

    companion object {
        const val CODE_LENGTH = 5
        const val RESEND_SECONDS = 80

        /** شماره ایرانی: با ۰۹ شروع می‌شود و ۱۱ رقم است */
        fun isValidPhone(raw: String): Boolean {
            val d = raw.filter(Char::isDigit)
            return d.length == 11 && d.startsWith("09")
        }

        /** نمایش خوانا: ۰۹۱۲ ۳۴۵ ۶۷۸۹ */
        fun formatPhone(raw: String): String {
            val d = raw.filter(Char::isDigit).take(11)
            return buildString {
                d.forEachIndexed { i, c ->
                    if (i == 4 || i == 7) append(' ')
                    append(c)
                }
            }
        }
    }
}

@Singleton
class LocalAuthService @Inject constructor() : AuthService {

    override suspend fun requestCode(phone: String): Result<Boolean> {
        if (!AuthService.isValidPhone(phone)) {
            return Result.failure(IllegalArgumentException("شماره موبایل معتبر نیست"))
        }
        delay(600)   // شبیه‌سازی رفت‌وبرگشت شبکه تا حالت بارگذاری واقعی دیده شود
        return Result.success(false)
    }

    override suspend fun verifyCode(phone: String, code: String): Result<AuthService.Session> {
        delay(600)
        if (code.filter(Char::isDigit).length != AuthService.CODE_LENGTH) {
            return Result.failure(IllegalArgumentException("کد تأیید کامل نیست"))
        }
        return Result.success(
            AuthService.Session(
                phone = phone,
                token = "local-dev-token",
                referralCode = referralCodeFor(phone),
            )
        )
    }

    /** کد معرف ۶ کاراکتری، معین بر اساس شماره — تا با هر ورود عوض نشود */
    private fun referralCodeFor(phone: String): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        var h = phone.filter(Char::isDigit).fold(7L) { acc, c -> acc * 31 + c.code }
        return buildString {
            repeat(6) {
                append(alphabet[((h % alphabet.length) + alphabet.length).toInt() % alphabet.length])
                h /= alphabet.length
            }
        }
    }
}
