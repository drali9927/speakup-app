package ir.speakup.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ir.speakup.app.R

/**
 * تایپوگرافی — بر پایه همان منطق دولینگو.
 *
 * تفاوت اصلی با نسخه قبل: **تقریباً همه‌چیز ضخیم است**. در دولینگو حتی
 * متن بدنه هم نیم‌ضخیم است و متن نازک تقریباً وجود ندارد. دلیلش زیبایی
 * نیست؛ اپ روی صفحه کوچک و اغلب در حال حرکت خوانده می‌شود و متن ضخیم
 * در یک نگاه کوتاه خوانده می‌شود.
 *
 * ارتفاع خط هم کم شده. خط‌های باز قبلی متن را «مقاله‌ای» می‌کرد، در حالی
 * که اینجا هر بلوک باید یک تکه فشرده و قابل بلعیدن باشد.
 *
 * فونت: وزیرمتن.
 *
 * پیش‌تر فونت پیش‌فرض سیستم بود و روی هر گوشی فرق می‌کرد — روی سامسونگ
 * یک شکل، روی شیائومی شکل دیگر. برای اپی که تمام هویتش متن است، این
 * یعنی نداشتن هویت. وزیرمتن هم پنج وزن دارد (که این تایپوگرافی ضخیم
 * به آن نیاز دارد) و هم پروانه آزاد SIL OFL دارد.
 *
 * فونت لاتین جدا نگذاشته‌ایم: وزیرمتن حروف لاتین را هم پوشش می‌دهد و
 * جمله‌های دوزبانه (که در این اپ همه‌جا هستند) با یک فونت یکدست‌ترند.
 */
private val Fa = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.vazirmatn_bold, FontWeight.Bold),
    Font(R.font.vazirmatn_extrabold, FontWeight.ExtraBold),
)

val SpeakUpTypography = Typography(
    displaySmall = TextStyle(fontFamily = Fa, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, lineHeight = 38.sp),
    headlineMedium = TextStyle(fontFamily = Fa, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 30.sp),
    headlineSmall = TextStyle(fontFamily = Fa, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleLarge = TextStyle(fontFamily = Fa, fontWeight = FontWeight.Bold, fontSize = 19.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = Fa, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = Fa, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Fa, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(fontFamily = Fa, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = Fa, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 17.sp),
)
