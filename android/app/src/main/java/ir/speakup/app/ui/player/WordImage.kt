package ir.speakup.app.ui.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

/**
 * تصویر کارت واژه.
 *
 * تصاویر با ابزار جانبی `content/imagegen.py` یک‌بار تولید می‌شوند؛ درس اول
 * همراه APK می‌آید و بقیه از سرور (نگاه کنید به `ImageSource`).
 *
 * سه حالت جدا نمایش داده می‌شود، چون از دید کاربر سه چیز متفاوت‌اند:
 *   - در حال آمدن از شبکه → چرخنده، تا کارت خالی «خراب» به‌نظر نرسد
 *   - نیامد (آفلاین یا فایل روی سرور نبود) → پیام کوتاه
 *   - اصلاً تصویری برای این واژه ساخته نشده → همان پیام، بدون تلاش شبکه
 *
 * در هر سه حالت تمرین ادامه‌پذیر می‌ماند؛ نه محتوای ناقص و نه شبکه ضعیف
 * نباید جلوی یادگیری را بگیرد.
 */
@Composable
fun WordImage(
    fileName: String?,
    modifier: Modifier = Modifier.height(260.dp).aspectRatio(1f),
) {
    val ctx = LocalContext.current
    // بسته محتوا نام PNG را نگه می‌دارد چون خروجی خام تولید تصویر همان است،
    // اما نسخه بهینه WebP سرو می‌شود. هر دو امتحان می‌شود تا فراموش کردن
    // مرحله بهینه‌سازی، کارت واژه را خالی نکند.
    val resolved = remember(fileName) { ir.speakup.app.data.remote.ImageSource.resolve(ctx, fileName) }

    // اندازه را فراخوان تعیین می‌کند. وقتی کامپوننت اندازه خودش را تحمیل می‌کرد،
    // در «نمای واژگان» تصویر کل کارت را می‌بلعید و متن رویش می‌افتاد.
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        if (resolved == null) {
            Placeholder("تصویر هنوز ساخته نشده")
        } else {
            SubcomposeAsyncImage(
                model = resolved,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                loading = {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 3.dp)
                    }
                },
                error = { Placeholder("تصویر بارگذاری نشد") },
            )
        }
    }
}

@Composable
private fun Placeholder(text: String) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text(
            text,
            Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
