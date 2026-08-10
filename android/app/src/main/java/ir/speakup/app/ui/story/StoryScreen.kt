package ir.speakup.app.ui.story

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.ui.common.DuoButton
import ir.speakup.app.ui.theme.DuoBlueSoft
import ir.speakup.app.ui.theme.ltr

/**
 * صفحه داستانک.
 *
 * تنها جایی در اپ که کاربر انگلیسی را **پیوسته** می‌خواند. بقیه بخش‌ها
 * روی یک جمله یا یک واژه کار می‌کنند؛ اینجا باید معنی را از دلِ متن
 * بیرون بکشد — همان مهارتی که در واقعیت لازم است و با تمرین جمله‌ای
 * ساخته نمی‌شود.
 *
 * سه تصمیم که ساختار صفحه را می‌سازد:
 *
 * ۱. **ترجمه پیش‌فرض پنهان است.** اگر کنار هر بند باشد، چشم مستقیم
 *    سراغش می‌رود و متن انگلیسی اصلاً خوانده نمی‌شود. با یک دکمه
 *    باز می‌شود — بعد از تلاش، نه پیش از آن.
 *
 * ۲. **بند در حال خواندن هایلایت می‌شود.** صوت و متن باید به هم گره
 *    بخورند، وگرنه شنیدن و خواندن دو کار جدا می‌شوند.
 *
 * ۳. **پرسش‌ها اینجا نیستند.** بعد از این صفحه، به‌عنوان فعالیت‌های
 *    جدا می‌آیند تا کاربر نتواند حین پاسخ دادن متن را نگاه کند.
 */
@Composable
fun StoryScreen(
    title: String,
    items: List<ActivityItemEntity>,
    playingIndex: Int,
    isPlaying: Boolean,
    showTranslation: Boolean,
    onPlayToggle: () -> Unit,
    onLineTap: (Int) -> Unit,
    onToggleTranslation: () -> Unit,
    onFinish: () -> Unit,
    onClose: () -> Unit,
) {
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, "بستن") }
            Text(
                title,
                Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium.ltr(),
                maxLines = 1,
            )
            IconButton(onClick = onToggleTranslation) {
                Icon(
                    Icons.Default.Translate,
                    if (showTranslation) "پنهان کردن ترجمه" else "نمایش ترجمه",
                    tint = if (showTranslation) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onPlayToggle) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    if (isPlaying) "توقف" else "پخش داستان",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            items.forEachIndexed { i, item ->
                val active = i == playingIndex && isPlaying
                Box(
                    Modifier.fillMaxWidth().padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) DuoBlueSoft else MaterialTheme.colorScheme.surface)
                        .clickable { onLineTap(i) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    Column {
                        Text(
                            item.prompt,
                            style = MaterialTheme.typography.bodyLarge.ltr(),
                        )
                        if (showTranslation && !item.promptFa.isNullOrBlank()) {
                            Spacer(Modifier.size(6.dp))
                            Text(
                                item.promptFa!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.size(20.dp))
        }

        DuoButton(
            text = "به پرسش‌ها برو",
            onClick = onFinish,
            height = 56.dp,
            modifier = Modifier.fillMaxWidth().padding(20.dp),
        )
    }
}
