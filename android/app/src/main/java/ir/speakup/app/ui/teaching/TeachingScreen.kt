package ir.speakup.app.ui.teaching

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.ui.theme.autoDir
import ir.speakup.app.ui.theme.ltr

/**
 * بخش آموزش — یک صفحه پیوسته، نه کارت‌های پشت‌سرهم.
 *
 * نسخه قبلی هر کارت آموزشی را یک فعالیت جدا می‌گرفت: کاربر باید «بعدی»
 * می‌زد، به صفحه درس برمی‌گشت، دوباره روی کارت می‌زد. برای بخش گرامر
 * که ۹ فعالیت دارد یعنی ده‌ها ضربه اضافه.
 *
 * حالا تمام محتوای آموزشی یک بخش روی یک صفحه اسکرول‌شونده می‌آید و
 * نوار روایت صوتی پایین صفحه آن را می‌خواند.
 */
@Composable
fun TeachingScreen(
    items: List<ActivityItemEntity>,
    isSpeaking: Boolean,
    speechReady: Boolean,
    rate: Float,
    onPlayToggle: () -> Unit,
    onRateChange: () -> Unit,
    onFinish: () -> Unit,
    onClose: () -> Unit,
    onCheckAnswered: (itemId: String, correct: Boolean) -> Unit = { _, _ -> },
) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    ) {
        Box(Modifier.fillMaxWidth().padding(8.dp)) {
            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.Close, "بستن")
            }
        }

        Column(
            Modifier.weight(1f).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            items.forEachIndexed { i, item ->
                if (i > 0) {
                    Spacer(Modifier.size(28.dp))
                    Box(
                        Modifier.fillMaxWidth().size(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Spacer(Modifier.size(28.dp))
                }
                TeachingBlock(item)

                // بررسی درک، بلافاصله بعد از همان توضیح — نه آخر بخش.
                // فاصله انداختن بین توضیح و پرسش، پیوندشان را از بین می‌برد.
                if (item.hasCheck) {
                    Spacer(Modifier.size(20.dp))
                    CheckCard(item, onAnswered = { correct -> onCheckAnswered(item.id, correct) })
                }
            }
            Spacer(Modifier.size(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onFinish),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    "خواندم، ادامه",
                    Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
            Spacer(Modifier.size(20.dp))
        }

        if (speechReady) {
            NarrationBar(isSpeaking, rate, onPlayToggle, onRateChange)
        }
    }
}

@Composable
private fun TeachingBlock(item: ActivityItemEntity) {
    Text(
        item.prompt,
        style = MaterialTheme.typography.headlineMedium.autoDir(item.prompt),
        fontWeight = FontWeight.Bold,
    )

    item.promptFa?.let { body ->
        Spacer(Modifier.size(14.dp))
        // خطوط توضیح — نقطه‌دار یا بندی، بسته به بلندی خودِ خط.
        //
        // نقطه‌ی فهرست به خطِ کوتاه می‌خورد: «یک قلم از یک سیاهه». بعد از
        // بازنویسیِ کارت‌های B1 و B2، میانه طول خط از ۴۵ به ۸۵ نویسه رسید
        // و پنج جمله کاملِ نقطه‌دار پشت سر هم، هم شلوغ دیده می‌شود و هم
        // وعده‌ی فهرستی می‌دهد که متن برآورده نمی‌کند.
        //
        // معیار روی خودِ خط است و نه روی سطح، چون کارت‌های آمیخته هم داریم:
        // چند بند توضیح و بعد یک سیاهه کوتاه از قیدها. هر خط شکل خودش را
        // می‌گیرد.
        body.split('\n').filter { it.isNotBlank() }.forEach { raw ->
            val line = raw.trim()
            if (line.length > PROSE_LINE) {
                Text(
                    emphasise(line),
                    Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 30.sp,
                )
            } else {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        Modifier.padding(top = 10.dp).size(5.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Text(emphasise(line), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    // مثال‌ها — هرکدام در جعبه خودش، همان الگوی خوانای رقیب
    item.correctAnswer?.split('\n')?.filter { it.isNotBlank() }?.let { examples ->
        if (examples.isEmpty()) return@let
        Spacer(Modifier.size(18.dp))
        examples.forEach { ex ->
            Surface(
                Modifier.fillMaxWidth().padding(vertical = 3.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Text(
                    ex.trim(),
                    Modifier.padding(horizontal = 14.dp, vertical = 10.dp).fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium.autoDir(ex),
                )
            }
        }
    }
}

/** نوار روایت — پخش/توقف و کنترل سرعت، بدون نیاز به هیچ ضربه دیگری */
@Composable
private fun NarrationBar(
    isSpeaking: Boolean,
    rate: Float,
    onPlayToggle: () -> Unit,
    onRateChange: () -> Unit,
) {
    Surface(
        Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 8.dp,
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier.size(52.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onPlayToggle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isSpeaking) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isSpeaking) "توقف" else "پخش",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }

            Text(
                if (isSpeaking) "در حال خواندن درس…" else "درس را برایت می‌خوانم",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )

            Surface(
                modifier = Modifier.clickable(onClick = onRateChange),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Text(
                    "${rate}x",
                    Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge.ltr(),
                )
            }
        }
    }
}

/**
 * مرز خطِ «سیاهه‌ای» و خطِ «بندی».
 *
 * از اندازه‌گیری خودِ محتوا آمده: میانه طول خط در A1 و A2 حدود ۴۵ نویسه
 * است و در B1 و B2 حدود ۸۰. شصت، درهٔ میان این دو است.
 */
private const val PROSE_LINE = 60
