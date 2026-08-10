package ir.speakup.app.ui.common

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * راهنمای «موتور تشخیص گفتار نصب نیست» — معادل SpeechUnavailableDialog برای صوت.
 *
 * روی خیلی از گوشی‌های ایرانی برنامه گوگل (که موتور تشخیص گفتار آفلاین را
 * فراهم می‌کند) نصب نیست یا غیرفعال شده. تمرین گفتار نباید همینجا بن‌بست
 * شود — کاربر مستقیم به تنظیمات ورودی صوتی یا صفحه نصب برنامه گوگل می‌رود؛
 * اگر آن هم به نتیجه نرسید، حالت شنیدنی-فقط همیشه در دسترس می‌ماند.
 */
@Composable
fun SpeechRecognizerUnavailableDialog(onDismiss: () -> Unit) {
    val ctx = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تشخیص گفتار در دسترس نیست") },
        text = {
            Text(
                "برای سنجش تلفظ، باید سرویس تشخیص گفتار گوگل روی گوشی نصب و فعال باشد.\n\n" +
                    "می‌توانی از فروشگاه برنامه گوگل را نصب کنی یا در تنظیمات ورودی صوتی را " +
                    "بررسی کنی. تا آن زمان، همین تمرین را به‌صورت شنیدن و نوشتن ادامه می‌دهی."
            )
        },
        confirmButton = {
            TextButton(onClick = {
                runCatching {
                    ctx.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.googlequicksearchbox"))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
                onDismiss()
            }) { Text("نصب برنامه گوگل") }
        },
        dismissButton = {
            TextButton(onClick = {
                runCatching {
                    ctx.startActivity(
                        Intent("android.settings.VOICE_INPUT_SETTINGS")
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
                onDismiss()
            }) { Text("تنظیمات ورودی صوتی") }
        },
    )
}
