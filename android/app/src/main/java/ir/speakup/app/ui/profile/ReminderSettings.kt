package ir.speakup.app.ui.profile

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.notify.Notifications

/**
 * تنظیم یادآور.
 *
 * مجوز اعلان **همین‌جا** خواسته می‌شود و نه در نخستین اجرای اپ. کاربری که
 * هنوز هیچ درسی نداده، دلیلی برای «بله» گفتن ندارد و رد کردنِ مجوز در
 * اندروید دائمی است — یک بار «نه»، یعنی برای همیشه نه. پس تا وقتی کاربر
 * خودش سراغ یادآور نیامده، چیزی نمی‌پرسیم.
 */
@Composable
fun ReminderSettings(
    enabled: Boolean,
    hour: Int,
    onEnabledChange: (Boolean) -> Unit,
    onHourChange: (Int) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val ctx = LocalContext.current
    var denied by remember { mutableStateOf(false) }

    val ask = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        denied = !granted
        onEnabledChange(granted)
    }

    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = scheme.surfaceVariant) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "یادآور روزانه",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "فقط روزهایی که تمرین نکرده‌ای، و حداکثر یکی در روز",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = { on ->
                        denied = false
                        // روشن کردن ممکن است به مجوز نیاز داشته باشد؛ خاموش
                        // کردن هرگز. پس فقط در مسیر روشن شدن می‌پرسیم.
                        if (on && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            !Notifications.allowed(ctx)
                        ) {
                            ask.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onEnabledChange(on)
                        }
                    },
                )
            }

            if (denied) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "اعلان‌های اپ در تنظیمات گوشی بسته است. بدون آن یادآور فرستاده نمی‌شود.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.error,
                )
            }

            if (enabled) {
                Spacer(Modifier.height(14.dp))
                Text(
                    "زودترین ساعت",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // چند ساعتِ آماده به‌جای انتخابگر زمان: تصمیمِ کمتر،
                    // و هر سه گزینه در عمل کافی‌اند.
                    listOf(12, 17, 20).forEach { h ->
                        val on = hour == h
                        Surface(
                            Modifier.weight(1f).clickable { onHourChange(h) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (on) scheme.primary else scheme.surface,
                        ) {
                            Text(
                                "${h.toPersianDigits()}:۰۰",
                                Modifier.padding(vertical = 10.dp).fillMaxWidth(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (on) scheme.onPrimary else scheme.onSurface,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}
