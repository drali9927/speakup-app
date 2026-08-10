package ir.speakup.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.theme.ltr

/**
 * کارنامه کاربر.
 *
 * چرا لازم شد: زنجیره، امتیاز، سطح و اشتراک هر کدام جای متفاوتی بودند و
 * کاربر هیچ‌جا «همه‌ی آنچه ساخته‌ام» را یکجا نمی‌دید. برای محصولی که
 * انگیزه‌اش بر انباشت پیشرفت بنا شده، این صفحه خودش یک قلاب نگهداشت است.
 */
@Composable
fun ProfileScreen(vm: ProfileViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    val scheme = MaterialTheme.colorScheme

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier.size(84.dp).clip(CircleShape).background(scheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "سطح ${s.level.toPersianDigits()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = scheme.onPrimaryContainer,
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            s.phone ?: "کاربر",
            style = MaterialTheme.typography.titleMedium.ltr(),
            fontWeight = FontWeight.Bold,
        )
        s.planTitle?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = scheme.primary)
        } ?: Text(
            "بدون اشتراک",
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Stat("🔥", s.streakDays.toPersianDigits(), "روز پیاپی", Modifier.weight(1f))
            Stat("⭐", s.totalXp.toPersianDigits(), "امتیاز کل", Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Stat("📚", s.wordsLearned.toPersianDigits(), "واژه آموخته", Modifier.weight(1f))
            Stat("✅", s.lessonsDone.toPersianDigits(), "درس تمام‌شده", Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))
        ReminderSettings(
            enabled = s.remindersEnabled,
            hour = s.reminderHour,
            onEnabledChange = vm::setReminders,
            onHourChange = vm::setReminderHour,
        )

        Spacer(Modifier.height(24.dp))
        WeeklyChart(s.weekly)

        // متریک داوری محصول (سند ۰۷ بخش ۷.۷). به کاربر هم نشانش می‌دهیم
        // چون «چند درصد را درست جواب دادی» ملموس‌تر از تعداد درس است.
        s.accuracy?.let {
            Spacer(Modifier.height(20.dp))
            Surface(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = scheme.surfaceVariant,
            ) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(it * 100).toInt().toPersianDigits()}٪",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = scheme.primary,
                    )
                    Text(
                        "پاسخ درست در تمرین‌های تولیدی",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Stat(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            Modifier.padding(vertical = 16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * نمودار هفت روز گذشته.
 *
 * ستون‌های ساده و بدون محور: هدف مقایسه دقیق نیست، دیدن «کدام روزها
 * کار کردم» است — همان چیزی که خالی بودنش انگیزه پر کردن می‌دهد.
 */
@Composable
private fun WeeklyChart(days: List<ProfileViewModel.DayBar>) {
    if (days.isEmpty()) return
    val max = (days.maxOfOrNull { it.xp } ?: 0).coerceAtLeast(1)
    val scheme = MaterialTheme.colorScheme

    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = scheme.surfaceVariant) {
        Column(Modifier.padding(16.dp)) {
            Text("هفت روز گذشته", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            Row(
                Modifier.fillMaxWidth().height(110.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                days.forEach { d ->
                    Column(
                        Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        if (d.xp > 0) {
                            Text(
                                d.xp.toPersianDigits(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = scheme.primary,
                            )
                        }
                        Spacer(
                            Modifier.fillMaxWidth()
                                .height((78 * d.xp / max).dp.coerceAtLeast(4.dp))
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (d.xp > 0) scheme.primary else scheme.outlineVariant)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(d.label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
