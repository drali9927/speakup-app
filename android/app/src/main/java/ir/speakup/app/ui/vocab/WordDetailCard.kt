package ir.speakup.app.ui.vocab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.domain.SpeechService
import ir.speakup.app.ui.common.SpeakButton
import ir.speakup.app.ui.player.WordImage
import ir.speakup.app.ui.theme.ltr

/**
 * کارت کامل یک واژه.
 *
 * یک جا نوشته شده و سه جا استفاده می‌شود: نمای واژگان، شیت دیکشنری،
 * و مرور لایتنر. اگر جای دیگری هم لازم شد، همین را بردارید — نه یک کپی تازه.
 */
@Composable
fun WordDetailCard(
    item: ActivityItemEntity,
    speechStatus: SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onUnavailable: () -> Unit,
    modifier: Modifier = Modifier,
    showImage: Boolean = true,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showImage) {
            WordImage(item.imageFile, Modifier.height(160.dp).aspectRatio(1f))
            Spacer(Modifier.size(18.dp))
        }

        // واژه + بلندگو
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SpeakButton(
                text = item.prompt, id = item.id, size = 38,
                status = speechStatus, speakingId = speakingId,
                onSpeak = onSpeak, onUnavailable = onUnavailable,
            )
            Text(
                item.prompt,
                style = MaterialTheme.typography.headlineMedium.ltr(),
                fontWeight = FontWeight.Bold,
            )
        }

        // بخش کلام و تلفظ
        item.hintFa?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyLarge.ltr(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        // معنی فارسی
        item.promptFa?.let {
            Text(
                it,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        // جمله مثال با بلندگوی خودش
        item.exampleEn?.let { ex ->
            Spacer(Modifier.size(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SpeakButton(
                    text = ex, id = item.id + "-ex", size = 32,
                    status = speechStatus, speakingId = speakingId,
                    onSpeak = onSpeak, onUnavailable = onUnavailable,
                )
                Text(ex, style = MaterialTheme.typography.titleMedium.ltr())
            }
            item.exampleFa?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, start = 42.dp),
                )
            }
        }
    }
}
