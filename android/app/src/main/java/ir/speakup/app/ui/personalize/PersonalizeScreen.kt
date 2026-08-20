package ir.speakup.app.ui.personalize

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.speakup.app.domain.toPersianDigits
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * چهار پرسش کوتاه، هرکدام در یک صفحه.
 *
 * یک پرسش در هر صفحه و نه فهرستی از چهار تا: پرسشِ تنها، جواب دادن را
 * سبک نشان می‌دهد. فهرست، حس فرم پر کردن می‌دهد.
 *
 * زدن روی گزینه خودش جلو می‌برد — دکمه «بعدی» جداگانه، یک ضربه اضافه
 * در هر مرحله است، یعنی چهار ضربه اضافه در کل.
 */
@Composable
fun PersonalizeScreen(
    onDone: (needsPlacement: Boolean) -> Unit,
    vm: PersonalizeViewModel = hiltViewModel(),
) {
    val s by vm.state.collectAsStateWithLifecycle()
    val scheme = MaterialTheme.colorScheme

    LaunchedEffect(s.done) { if (s.done) onDone(s.needsPlacement) }

    val step = s.step ?: return

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (s.index > 0) {
                TextButton(onClick = vm::back) { Text("برگرد") }
            } else {
                Spacer(Modifier.size(1.dp))
            }
            Text(
                "${(s.index + 1).toPersianDigits()} از ${s.total.toPersianDigits()}",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 14.dp),
            )
        }

        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (s.index + 1f) / s.total },
            modifier = Modifier.fillMaxWidth().height(8.dp),
        )

        Spacer(Modifier.height(36.dp))
        Text(
            step.question,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        step.hint?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(28.dp))
        step.choices.forEach { c ->
            Surface(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .border(2.dp, scheme.outlineVariant, RoundedCornerShape(14.dp))
                    .clickable { vm.pick(c.key) },
                shape = RoundedCornerShape(14.dp),
                color = scheme.surface,
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        c.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    c.subtitle?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Text(
            // بدون این، کاربر حس می‌کند دارد تعهد می‌دهد
            "همه این‌ها را بعداً از پروفایل می‌توانی عوض کنی.",
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
