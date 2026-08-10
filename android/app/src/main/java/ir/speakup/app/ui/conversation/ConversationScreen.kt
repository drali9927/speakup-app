package ir.speakup.app.ui.conversation

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.ui.common.DuoButton
import ir.speakup.app.ui.theme.ltr

/**
 * بخش مکالمه — گفتگوی دو نفره.
 *
 * ⚠️ این بخش **تمرین نیست**. پیش‌تر نوعش `LISTENING` بود و با صفحه دیکته
 * رندر می‌شد؛ یعنی از کاربر خواسته می‌شد جمله‌های چندبخشی مکالمه را
 * بشنود و تایپ کند. نه شدنی بود، نه هدف این بخش. هدف اینجا شنیدن گفتار
 * پیوسته و دنبال کردن متن است.
 *
 * چیدمان حبابی با دو طرف، همان قراردادی است که هر کاربر ایرانی از
 * پیام‌رسان‌ها می‌شناسد — نیازی به یاد گرفتن ندارد.
 *
 * ترجمه پیش‌فرض **نمایش داده می‌شود** چون سطح A1 است و بدون آن کاربر
 * چیزی نمی‌فهمد؛ اما قابل خاموش کردن است تا کسی که آماده است بتواند
 * فقط با انگلیسی تمرین کند.
 */
@Composable
fun ConversationScreen(
    items: List<ActivityItemEntity>,
    playingIndex: Int?,
    isPlaying: Boolean,
    showTranslation: Boolean,
    onPlayToggle: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onLineTap: (Int) -> Unit,
    onToggleTranslation: () -> Unit,
    onFinish: () -> Unit,
    onClose: () -> Unit,
) {
    val listState = rememberLazyListState()

    // خط در حال پخش همیشه دیده شود، وگرنه کاربر باید همراه صدا اسکرول کند
    LaunchedEffect(playingIndex) {
        playingIndex?.let { listState.animateScrollToItem(it) }
    }

    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, "بستن") }
            Text(
                "مکالمه",
                Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            IconButton(onClick = onToggleTranslation) {
                Icon(
                    Icons.Default.Translate,
                    if (showTranslation) "پنهان کردن ترجمه" else "نمایش ترجمه",
                    tint = if (showTranslation) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(items, key = { _, it -> it.id }) { i, item ->
                Bubble(
                    item = item,
                    // «نفر اول» سمت راست (شروع خط در RTL)، «نفر دوم» سمت چپ
                    isFirstSpeaker = item.hintFa?.contains("اول") != false,
                    highlighted = i == playingIndex,
                    showTranslation = showTranslation,
                    onClick = { onLineTap(i) },
                )
            }
        }

        PlayerBar(isPlaying = isPlaying, onPlayToggle = onPlayToggle, onPrev = onPrev, onNext = onNext)

        DuoButton(
            text = "خواندم، ادامه",
            onClick = onFinish,
            height = 56.dp,
            modifier = Modifier.fillMaxWidth().padding(20.dp),
        )
    }
}

@Composable
private fun Bubble(
    item: ActivityItemEntity,
    isFirstSpeaker: Boolean,
    highlighted: Boolean,
    showTranslation: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    // دو رنگ به‌اندازه کافی متفاوت. نسخه اول هر دو را از خانواده container
    // با آلفای کم گرفته بود و روی گوشی تقریباً یک‌رنگ دیده می‌شدند.
    val bubble = if (isFirstSpeaker) scheme.primaryContainer.copy(alpha = 0.45f)
    else scheme.secondaryContainer

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFirstSpeaker) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.Top,
    ) {
        if (isFirstSpeaker) SpeakerDot(isFirstSpeaker)
        Spacer(Modifier.size(8.dp))
        Surface(
            Modifier.weight(1f, fill = false).clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            // خط در حال پخش با رنگ پررنگ‌تر مشخص می‌شود، نه با حاشیه —
            // حاشیه در فهرست بلند شلوغ به‌نظر می‌رسد
            color = if (highlighted) scheme.primary.copy(alpha = 0.18f) else bubble,
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    item.prompt,
                    style = MaterialTheme.typography.titleMedium.ltr(),
                    fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
                )
                if (showTranslation) {
                    item.promptFa?.let {
                        Spacer(Modifier.size(4.dp))
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant)
                    }
                }
            }
        }
        Spacer(Modifier.size(8.dp))
        if (!isFirstSpeaker) SpeakerDot(isFirstSpeaker)
    }
}

/** نشانگر گوینده — دو رنگ ثابت تا کاربر بدون خواندن هم دو طرف را تفکیک کند */
@Composable
private fun SpeakerDot(isFirstSpeaker: Boolean) {
    val scheme = MaterialTheme.colorScheme
    Box(
        Modifier.size(36.dp).clip(CircleShape)
            .background(if (isFirstSpeaker) scheme.primary else scheme.secondary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            if (isFirstSpeaker) "۱" else "۲",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isFirstSpeaker) scheme.onPrimary else scheme.onSecondary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PlayerBar(
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Surface(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(onClick = onPrev) { Icon(Icons.Default.KeyboardArrowUp, "خط قبل") }
            Surface(
                Modifier.size(52.dp).clickable(onClick = onPlayToggle),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        if (isPlaying) "توقف" else "پخش",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
            IconButton(onClick = onNext) { Icon(Icons.Default.KeyboardArrowDown, "خط بعد") }
        }
    }
}
