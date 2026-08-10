package ir.speakup.app.ui.common

import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ir.speakup.app.domain.SpeechService

/**
 * راهنمای فعال کردن صوت.
 *
 * روی بسیاری از گوشی‌های ایرانی بسته زبان انگلیسی گوگل نصب نیست.
 * رقیب در این حالت فقط یک جمله خشک نشان می‌دهد؛ ما مستقیم کاربر را
 * به همان صفحه تنظیمات می‌بریم.
 */
@Composable
fun SpeechUnavailableDialog(
    status: SpeechService.Status,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    val ctx = LocalContext.current

    val (title, body) = when (status) {
        SpeechService.Status.MissingLanguage ->
            "صدای انگلیسی نصب نیست" to
                "برای شنیدن تلفظ، بسته صوتی زبان انگلیسی باید روی گوشی نصب باشد.\n\n" +
                "در تنظیمات گوشی به بخش «تبدیل متن به گفتار» بروید و زبان انگلیسی را نصب کنید."
        SpeechService.Status.NoEngine ->
            "موتور صوت در دسترس نیست" to
                "روی این گوشی موتور تبدیل متن به گفتار پیدا نشد.\n\n" +
                "می‌توانید «Google Text-to-Speech» را از فروشگاه نصب کنید."
        else ->
            "در حال آماده‌سازی صدا" to "چند لحظه صبر کنید و دوباره امتحان کنید."
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            if (status == SpeechService.Status.MissingLanguage) {
                TextButton(onClick = {
                    // مسیر مستقیم به تنظیمات تبدیل متن به گفتار
                    runCatching {
                        ctx.startActivity(
                            Intent("com.android.settings.TTS_SETTINGS")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                    onDismiss()
                }) { Text("رفتن به تنظیمات") }
            } else {
                TextButton(onClick = { onRetry(); onDismiss() }) { Text("تلاش دوباره") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("بستن") } },
    )
}
