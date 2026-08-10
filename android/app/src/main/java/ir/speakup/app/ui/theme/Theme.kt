package ir.speakup.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * پالت — بازنویسی‌شده بر پایه زبان بصری دولینگو.
 *
 * بنفش قبلی زیبا بود اما «نرم» بود: همه‌چیز هم‌وزن به نظر می‌رسید و هیچ
 * عنصری کاربر را به جلو هل نمی‌داد. زبان بصری دولینگو دقیقاً برعکس است
 * و سه قاعده دارد که همه‌شان را اینجا برداشته‌ایم:
 *
 *   ۱. **رنگ اشباع و کم‌تعداد** — یک سبز پیشرو، یک آبی برای مسیر دوم،
 *      قرمز و طلایی فقط برای معنی مشخص. خاکستری‌ها واقعاً خنثی‌اند.
 *   ۲. **هر رنگ یک سایه تیره‌تر دارد** — زیرِ دکمه‌ها و گره‌ها. همین
 *      یک چیز است که حس «برجسته و قابل فشار دادن» را می‌سازد.
 *   ۳. **مرز ضخیم به‌جای سایه محو** — کارت‌ها ۲ پیکسل خط دارند، نه
 *      سایه نرم. عناصر مثل برچسبِ چسبانده‌شده روی کاغذ دیده می‌شوند.
 *
 * ⚠️ چیزی که عمداً برنداشته‌ایم: نشان و شخصیت کارتونی دولینگو، فونت
 * اختصاصی‌شان (Feather)، و تصویرسازی‌هایشان. آن‌ها هویت ثبت‌شده‌اند؛
 * الگوی چیدمان و منطق رنگ آزاد است.
 */

// --- رنگ‌های پایه
val DuoGreen = Color(0xFF58CC02)          // کنش اصلی، درست بودن، پیشرفت
val DuoGreenDark = Color(0xFF48A800)      // زیرِ دکمه سبز
val DuoGreenSoft = Color(0xFFD7FFB8)      // پس‌زمینه پیام موفقیت

val DuoBlue = Color(0xFF1CB0F6)           // کنش دوم، پیوند، بخش‌های اطلاعاتی
val DuoBlueDark = Color(0xFF1899D6)
val DuoBlueSoft = Color(0xFFDDF4FF)

val DuoRed = Color(0xFFFF4B4B)            // اشتباه
val DuoRedDark = Color(0xFFE04343)
val DuoRedSoft = Color(0xFFFFDFE0)

val DuoGold = Color(0xFFFFC800)           // امتیاز، زنجیره، جایزه
val DuoGoldDark = Color(0xFFE6A500)

val DuoPurple = Color(0xFFCE82FF)         // بخش لایتنر و مرور
val DuoPurpleDark = Color(0xFFB35BE8)

// --- خاکستری‌ها
val DuoInk = Color(0xFF4B4B4B)            // متن اصلی — سیاه خالص نیست
val DuoMuted = Color(0xFF777777)          // متن فرعی
val DuoDisabled = Color(0xFFAFAFAF)       // غیرفعال
val DuoBorder = Color(0xFFE5E5E5)         // مرز کارت‌ها و دکمه‌های خنثی
val DuoSurfaceAlt = Color(0xFFF7F7F7)     // پس‌زمینه کارت خنثی

private val LightScheme = lightColorScheme(
    primary = DuoGreen,
    onPrimary = Color.White,
    primaryContainer = DuoGreenSoft,
    onPrimaryContainer = Color(0xFF2B6A00),
    secondary = DuoBlue,
    onSecondary = Color.White,
    secondaryContainer = DuoBlueSoft,
    onSecondaryContainer = Color(0xFF0A5E88),
    tertiary = DuoGold,
    onTertiary = Color(0xFF4B3B00),
    error = DuoRed,
    onError = Color.White,
    errorContainer = DuoRedSoft,
    onErrorContainer = Color(0xFF8E1B1B),
    background = Color.White,
    onBackground = DuoInk,
    surface = Color.White,
    onSurface = DuoInk,
    surfaceVariant = DuoSurfaceAlt,
    onSurfaceVariant = DuoMuted,
    outline = DuoDisabled,
    outlineVariant = DuoBorder,
)

private val DarkScheme = darkColorScheme(
    primary = DuoGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1F3D0A),
    onPrimaryContainer = DuoGreenSoft,
    secondary = DuoBlue,
    onSecondary = Color.White,
    tertiary = DuoGold,
    error = DuoRed,
    background = Color(0xFF131F24),
    onBackground = Color(0xFFF1F7FB),
    surface = Color(0xFF1B2A31),
    onSurface = Color(0xFFF1F7FB),
    surfaceVariant = Color(0xFF223540),
    onSurfaceVariant = Color(0xFFA8BCC6),
    outline = Color(0xFF3C5563),
    outlineVariant = Color(0xFF2E4552),
)

@Composable
fun SpeakUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = SpeakUpTypography,
        content = content,
    )
}
