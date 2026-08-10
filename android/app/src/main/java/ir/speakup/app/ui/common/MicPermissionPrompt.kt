package ir.speakup.app.ui.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

fun hasRecordAudioPermission(ctx: Context): Boolean =
    ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

/**
 * صفحه توضیح پیش از دیالوگ سیستمی مجوز میکروفون.
 *
 * رقیب مستقیم دیالوگ خام اندروید را نشان می‌دهد — کاربر بدون context دکمه
 * «رد» را می‌زند چون نمی‌داند چرا لازم است (سند ۰۱ / نقاط ضعف رقیب).
 * اینجا اول دلیل را فارسی توضیح می‌دهیم، بعد دیالوگ سیستم باز می‌شود.
 * رد کردن هم بن‌بست نیست: تمرین با شنیدن و نوشتن ادامه پیدا می‌کند.
 */
@Composable
fun MicPermissionPrompt(
    onAllow: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(96.dp).background(
                MaterialTheme.colorScheme.primaryContainer,
                CircleShape,
            ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Mic,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(Modifier.size(24.dp))
        Text(
            "برای تمرین گفتار، به میکروفون نیاز داریم",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.size(12.dp))
        Text(
            "با میکروفون گوشی جمله انگلیسی را بلند می‌خوانی و اپ تلفظت را با جمله هدف " +
                "مقایسه می‌کند — دقیقاً مثل بقیه تمرین‌ها، اما این‌بار با صدای خودت.\n\n" +
                "صدای تو فقط برای تشخیص گفتار همین لحظه استفاده می‌شود.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.size(32.dp))
        Button(
            onClick = onAllow,
            modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
            shape = RoundedCornerShape(14.dp),
        ) { Text("اجازه بده", style = MaterialTheme.typography.labelLarge) }

        Spacer(Modifier.size(8.dp))
        TextButton(onClick = onSkip) {
            Text("فعلاً نه، فقط بشنوم و بنویسم", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

