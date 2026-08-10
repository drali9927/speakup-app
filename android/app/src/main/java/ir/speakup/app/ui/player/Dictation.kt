package ir.speakup.app.ui.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.domain.SpeechService
import ir.speakup.app.ui.common.SpeakButton
import ir.speakup.app.ui.theme.ltr

/**
 * دیکته — «آنچه می‌شنوی را بنویس».
 *
 * ⚠️ این تمرین **نباید** متن را نشان بدهد. صفحه متن‌آزادِ مشترک صورت سوال
 * را بالای کادر می‌نوشت؛ برای بقیه تمرین‌ها درست است، اما اینجا یعنی پاسخ
 * روی صفحه نوشته شده و کاربر فقط رونویسی می‌کند. تنها راه انجامش باید
 * شنیدن باشد.
 *
 * صوت با باز شدن تمرین خودکار پخش می‌شود (`PlayerViewModel.autoSpeak`)؛
 * دکمه بلندگو برای شنیدن دوباره است.
 */
@Composable
fun Dictation(
    s: PlayerUiState,
    vm: PlayerViewModel,
    item: ActivityItemEntity,
    speechStatus: SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onSpeechUnavailable: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.size(28.dp))
        Text(
            item.hintFa ?: "آنچه می‌شنوی را بنویس.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.size(24.dp))
        // دکمه بزرگ، چون تنها راه رسیدن به صورت سوال همین است
        SpeakButton(
            text = item.ttsText ?: item.prompt,
            id = item.id,
            size = 72,
            status = speechStatus,
            speakingId = speakingId,
            onSpeak = onSpeak,
            onUnavailable = onSpeechUnavailable,
        )
        Spacer(Modifier.size(10.dp))
        Text(
            "برای شنیدن دوباره بزن",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.size(28.dp))
        OutlinedTextField(
            value = s.input,
            onValueChange = vm::onInputChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = s.verdict == null,
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.ltr(),
            placeholder = { Text("آنچه شنیدی را بنویس…", style = MaterialTheme.typography.bodyLarge) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            shape = RoundedCornerShape(12.dp),
        )
    }
}
