package ir.speakup.app.ui.common

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import ir.speakup.app.domain.SpeechService

/**
 * دکمه بلندگو.
 *
 * وقتی موتور صوت آماده نیست، دکمه خاموش نمایش داده می‌شود و ضربه روی آن
 * راهنما را باز می‌کند — نه اینکه بی‌صدا هیچ کاری نکند.
 */
@Composable
fun SpeakButton(
    text: String?,
    id: String,
    status: SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onUnavailable: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 44,
) {
    if (text.isNullOrBlank()) return

    val ready = status == SpeechService.Status.Ready
    val speaking = speakingId == id

    // نبض ملایم حین پخش تا کاربر بداند صدا در حال پخش است
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (speaking) 1.15f else 1f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "pulseScale",
    )

    Box(
        modifier = modifier
            .size(size.dp)
            .scale(if (speaking) pulse else 1f)
            .clip(CircleShape)
            .background(
                if (ready) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { if (ready) onSpeak(text, id) else onUnavailable() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (ready) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
            contentDescription = "پخش صوت",
            modifier = Modifier.size((size * 0.5).dp),
            tint = if (ready) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
