package ir.speakup.app.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.speakup.app.data.local.ActivityItemEntity
import androidx.compose.ui.graphics.Color
import ir.speakup.app.ui.theme.ltr

// همان رنگ‌های بازخورد بقیه تمرین‌ها — سبز درست، قرمز نادرست
private val Green = Color(0xFF2E7D32)
private val Red = Color(0xFFC62828)

/**
 * آزمون کوتاه معنی — بلافاصله بعد از دیدن واژه.
 *
 * دیدنِ واژه حس یادگیری می‌دهد، اما تا وقتی از حافظه بازیابی نشود چیزی
 * نمی‌ماند. رقیب هم همین آزمون را دارد؛ تفاوت ما این است که پاسخ غلط
 * واژه را **خودکار وارد لایتنر** می‌کند — درست در لحظه‌ای که ضعف دیده شده.
 *
 * ⚠️ این صفحه عمداً جای کارت واژه را می‌گیرد و آن را کنار نمی‌زند: روی
 * کارت، معنی فارسی و ترجمه جمله و تصویر همگی پاسخ را لو می‌دهند. اگر روزی
 * خواستید آزمون را زیر کارت بگذارید، اول باید آن سه را پنهان کنید.
 */
@Composable
fun WordCheck(
    item: ActivityItemEntity,
    options: List<String>,
    picked: String?,
    addedToLeitner: Boolean,
    onPick: (String) -> Unit,
) {
    val answer = item.promptFa?.trim()

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.size(24.dp))
        Text(
            "معنی این واژه کدام است؟",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.size(14.dp))
        Text(
            item.prompt,
            style = MaterialTheme.typography.displaySmall.ltr(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.size(28.dp))
        options.forEach { opt ->
            val isCorrect = opt == answer
            val chosen = picked == opt
            // بعد از پاسخ، گزینه درست همیشه سبز می‌شود — حتی اگر کاربر
            // گزینه دیگری زده باشد. پاسخ درست نباید پنهان بماند.
            val show = chosen || (picked != null && isCorrect)
            val accent = when {
                !show -> MaterialTheme.colorScheme.outlineVariant
                isCorrect -> Green
                else -> Red
            }

            Surface(
                Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    .border(if (show) 2.dp else 1.dp, accent, RoundedCornerShape(14.dp))
                    .clickable(enabled = picked == null) { onPick(opt) },
                shape = RoundedCornerShape(14.dp),
                color = if (show) accent.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface,
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(opt, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                    if (show) {
                        Icon(
                            if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                            null, Modifier.size(22.dp), tint = accent,
                        )
                    }
                }
            }
        }

        // همان پیامی که در بقیه تمرین‌ها هست — تمایز محصول باید دیده شود
        AnimatedVisibility(visible = addedToLeitner) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.size(14.dp))
                Text(
                    "به جعبه لایتنر اضافه شد",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
