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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedTextField
import ir.speakup.app.domain.ReviewMode

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
    val scheme = MaterialTheme.colorScheme

    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LinearProgressIndicator(progress = { s.progress }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.size(20.dp))
        Text(
            when (rc.mode) {
                ReviewMode.MEANING -> "معنی این واژه کدام است؟"
                ReviewMode.WORD -> "کدام واژه این معنی را می‌دهد؟"
                ReviewMode.TYPING -> "بشنو و بنویس"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = scheme.onSurfaceVariant,
        )

        Spacer(Modifier.weight(1f))

        // صورت پرسش
        when (rc.mode) {
            ReviewMode.MEANING -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
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

            ReviewMode.WORD -> Text(
                rc.entry?.translationFa.orEmpty(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            // در حالت تایپ، واژه نوشته نمی‌شود — فقط شنیده می‌شود.
            // نوشتنش کل پرسش را بی‌معنا می‌کرد.
            ReviewMode.TYPING -> SpeakButton(
                text = rc.card.word, id = rc.card.id,
                status = speechStatus, speakingId = speakingId,
                onSpeak = onSpeak, onUnavailable = onUnavailable,
            )
        }

        Spacer(Modifier.size(28.dp))

        if (rc.mode == ReviewMode.TYPING) {
            OutlinedTextField(
                value = s.typed,
                onValueChange = vm::type,
                enabled = !s.revealed,
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge.ltr(),
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            rc.options.forEach { opt ->
                val correct = opt == rc.answer
                val bg = when {
                    !s.revealed -> scheme.surface
                    correct -> Green.copy(alpha = 0.15f)
                    opt == s.picked -> Red.copy(alpha = 0.12f)
                    else -> scheme.surface
                }
                val border = when {
                    !s.revealed -> scheme.outlineVariant
                    correct -> Green
                    opt == s.picked -> Red
                    else -> scheme.outlineVariant
                }
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .border(2.dp, border, RoundedCornerShape(14.dp))
                        .clickable(enabled = !s.revealed) { vm.pick(opt) },
                    shape = RoundedCornerShape(14.dp),
                    color = bg,
                ) {
                    Text(
                        opt,
                        style = if (rc.mode == ReviewMode.WORD)
                            MaterialTheme.typography.titleMedium.ltr()
                        else MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // پس از پاسخ: واژه، معنی و مثال با هم — همان چیزی که کاربر
        // برای درست کردن حافظه‌اش لازم دارد.
        if (s.revealed) {
            Spacer(Modifier.size(18.dp))
            Text(
                if (vm.isCorrect()) "درست بود" else "پاسخ درست: ${rc.answer}",
                style = MaterialTheme.typography.titleMedium,
                color = if (vm.isCorrect()) Green else Red,
                textAlign = TextAlign.Center,
            )
            rc.entry?.exampleEn?.let {
                Spacer(Modifier.size(8.dp))
                Text(it, style = MaterialTheme.typography.bodyLarge.ltr(), textAlign = TextAlign.Center)
            }
            rc.entry?.exampleFa?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        when {
            // تایپ: تا وقتی چیزی ننوشته، دکمه خاموش
            !s.revealed && rc.mode == ReviewMode.TYPING -> DuoButton(
                text = "بررسی",
                onClick = vm::submitTyped,
                enabled = s.typed.isNotBlank(),
                height = 56.dp,
                modifier = Modifier.fillMaxWidth(),
            )
            // چندگزینه‌ای: با زدن گزینه خودش جلو می‌رود، دکمه‌ای لازم نیست
            !s.revealed -> Spacer(Modifier.size(56.dp))
            else -> DuoButton(
                text = "ادامه",
                onClick = { vm.answer(vm.isCorrect()) },
                height = 56.dp,
                modifier = Modifier.fillMaxWidth(),
            )
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
