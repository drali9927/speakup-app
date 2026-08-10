package ir.speakup.app.ui.leitner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.domain.SpeechService
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.common.SpeakButton
import ir.speakup.app.ui.common.SpeechUnavailableDialog
import ir.speakup.app.ui.theme.ltr
import ir.speakup.app.ui.common.DuoButton
import ir.speakup.app.ui.common.DuoButtonStyle

private val Green = Color(0xFF2E7D32)
private val Red = Color(0xFFC62828)

@Composable
fun LeitnerScreen(vm: LeitnerViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    val speechStatus by vm.speech.status.collectAsStateWithLifecycle()
    val speakingId by vm.speech.speakingId.collectAsStateWithLifecycle()
    var showSpeechHelp by remember { mutableStateOf(false) }

    if (showSpeechHelp) {
        SpeechUnavailableDialog(
            status = speechStatus,
            onDismiss = { showSpeechHelp = false },
            onRetry = { vm.speech.retry() },
        )
    }

    when {
        s.finished -> SessionFinished(s.correctCount, s.session.size) { vm.exitSession() }
        s.inSession -> ReviewSession(
            s, vm, speechStatus, speakingId,
            onSpeak = { t, id -> vm.speech.speak(t, id) },
            onUnavailable = { showSpeechHelp = true },
        )
        else -> Summary(s) { vm.startSession() }
    }
}

// ---------- خلاصه جعبه ----------

@Composable
private fun Summary(s: LeitnerUiState, onStart: () -> Unit) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(20.dp),
    ) {
        Text("جعبه لایتنر", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "واژه‌هایی که در تمرین‌ها اشتباه جواب دادی، خودکار اینجا جمع می‌شوند.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp),
        )

        Spacer(Modifier.size(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Counter("آموخته شده", s.learnedCount, Modifier.weight(1f))
            Counter("کارت‌های امروز", s.dueCount, Modifier.weight(1f))
            Counter("در حال آموزش", s.learningCount, Modifier.weight(1f))
        }

        Spacer(Modifier.weight(1f))

        if (s.isEmpty) {
            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                Text(
                    "جعبه لایتنر شما خالی است!\nیک درس را شروع کنید — واژه‌هایی که اشتباه\nجواب بدهید خودشان به اینجا اضافه می‌شوند.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.weight(1f))
        }

        DuoButton(
            text = if (s.dueCount > 0) "مرور ${s.dueCount.toPersianDigits()} کارت" else "کارتی برای مرور نیست",
            onClick = onStart,
            enabled = s.dueCount > 0,
            height = 56.dp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Counter(label: String, value: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.size(6.dp))
            Text(
                value.toPersianDigits(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ---------- جلسه مرور ----------

@Composable
private fun ReviewSession(
    s: LeitnerUiState,
    vm: LeitnerViewModel,
    speechStatus: SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onUnavailable: () -> Unit,
) {
    val rc = s.current ?: return
    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LinearProgressIndicator(progress = { s.progress }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SpeakButton(
                text = rc.card.word, id = rc.card.id,
                status = speechStatus, speakingId = speakingId,
                onSpeak = onSpeak, onUnavailable = onUnavailable,
            )
            Text(
                rc.card.word,
                style = MaterialTheme.typography.displaySmall.ltr(),
                fontWeight = FontWeight.Bold,
            )
        }
        rc.entry?.ipaUk?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge.ltr(),
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (s.revealed) {
            Spacer(Modifier.size(20.dp))
            Text(
                rc.entry?.translationFa.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            rc.entry?.exampleEn?.let {
                Spacer(Modifier.size(12.dp))
                Text(it, style = MaterialTheme.typography.bodyLarge.ltr(), textAlign = TextAlign.Center)
            }
            rc.entry?.exampleFa?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        if (!s.revealed) {
            DuoButton(
                text = "نمایش معنی",
                onClick = vm::reveal,
                height = 56.dp,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DuoButton(
                    text = "بلد نبودم",
                    onClick = { vm.answer(false) },
                    style = DuoButtonStyle.Danger,
                    height = 56.dp,
                    modifier = Modifier.weight(1f),
                )
                DuoButton(
                    text = "بلد بودم",
                    onClick = { vm.answer(true) },
                    height = 56.dp,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

// ---------- پایان جلسه ----------

@Composable
private fun SessionFinished(correct: Int, total: Int, onDone: () -> Unit) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(96.dp).clip(RoundedCornerShape(48.dp)).background(Green.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) { Text("✓", style = MaterialTheme.typography.displaySmall, color = Green) }

        Text("مرور تمام شد", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "${correct.toPersianDigits()} از ${total.toPersianDigits()} واژه را بلد بودی",
            style = MaterialTheme.typography.bodyLarge,
        )
        DuoButton(
            text = "بازگشت",
            onClick = onDone,
            height = 56.dp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
