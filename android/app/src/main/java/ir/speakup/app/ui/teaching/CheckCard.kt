package ir.speakup.app.ui.teaching

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.ui.theme.autoDir

private val Green = Color(0xFF2E7D32)

/**
 * بررسی درک — پرسش کوتاهی که بلافاصله بعد از هر کارت گرامر می‌آید.
 *
 * چرا این هست: رقیب گرامر را با سه دقیقه روایت صوتی آموزش می‌دهد و کاربر
 * تا پایان بخش هیچ کاری نمی‌کند؛ خواندن و شنیدن حس یادگیری می‌دهد بدون
 * اینکه یادگیری اتفاق بیفتد. پژوهش دولینگو نشان داده توضیح کوتاهِ **بعد از
 * اشتباه مشخص** خطای بعدی را کم می‌کند. پس:
 *
 *   توضیح کوتاه  →  همان‌جا یک پرسش  →  نکته‌ای که دقیقاً همان اشتباه را بگیرد
 *
 * دو تصمیم عمدی:
 *
 * **گزینه غلط پاک نمی‌شود.** انتخاب اشتباه روی صفحه می‌ماند و نکته زیرش
 * می‌نشیند، تا کاربر پیوند «این را زدم چون این را اشتباه فهمیده بودم» را
 * ببیند. اگر صفحه ریست می‌شد، آن پیوند از بین می‌رفت.
 *
 * **امتیازی در کار نیست.** این بخش آموزش است نه آزمون؛ اشتباه اینجا باید
 * بی‌هزینه باشد وگرنه کاربر به‌جای فکر کردن، حدس محافظه‌کارانه می‌زند.
 */
@Composable
fun CheckCard(item: ActivityItemEntity, onAnswered: (Boolean) -> Unit) {
    val options = remember(item.id) { item.checkOptionsList() }
    // مجموعه، نه یک مقدار: تلاش‌های اشتباه باید بعد از یافتن پاسخ درست هم
    // روی صفحه بمانند تا کاربر «این را غلط زدم چون…» و «درستش این است» را
    // کنار هم ببیند. با یک مقدار تکی، نکته خطا با اولین انتخاب بعدی محو می‌شد.
    val wrongPicks = remember(item.id) { mutableStateListOf<String>() }
    var solved by remember(item.id) { mutableStateOf(false) }

    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    Modifier.size(22.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Lightbulb, null,
                        Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Text(
                    "بررسی کن",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.size(12.dp))
            item.checkPrompt?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.titleMedium.autoDir(it),
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.size(12.dp))
            options.forEach { opt ->
                val isCorrect = opt == item.checkAnswer
                val wasWrong = opt in wrongPicks
                // پس از حل شدن، گزینه درست هم سبز می‌شود حتی اگر کاربر
                // خودش نزده باشد — پاسخ درست نباید پنهان بماند.
                val show = wasWrong || (solved && isCorrect)
                val accent = when {
                    !show -> MaterialTheme.colorScheme.outlineVariant
                    isCorrect -> Green
                    else -> MaterialTheme.colorScheme.error
                }

                Surface(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .border(if (show) 2.dp else 1.dp, accent, RoundedCornerShape(12.dp))
                        .clickable(enabled = !solved && !wasWrong) {
                            if (isCorrect) solved = true else wrongPicks += opt
                            onAnswered(isCorrect)
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (show) accent.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface,
                ) {
                    Row(
                        Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            opt,
                            Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge.autoDir(opt),
                        )
                        if (show) {
                            Icon(
                                if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                                null, Modifier.size(20.dp), tint = accent,
                            )
                        }
                    }
                }

                // نکته هدفمند — همان چیزی که این بخش را از «اشتباه است» جدا می‌کند
                val tip = if (wasWrong) item.tipFor(opt) else null
                AnimatedVisibility(visible = tip != null) {
                    Surface(
                        Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                    ) {
                        Text(
                            tip.orEmpty(),
                            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            if (!solved && wrongPicks.isNotEmpty()) {
                Spacer(Modifier.size(4.dp))
                Text(
                    "دوباره انتخاب کن.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
