package ir.speakup.app.ui.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.data.model.StreakDayStatus
import ir.speakup.app.data.model.StreakRules
import ir.speakup.app.domain.JalaliDate
import ir.speakup.app.domain.StreakRepository
import ir.speakup.app.domain.toPersianDigits
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.alpha

private val Flame = Color(0xFFE58A00)
/** کرمی گرم سرصفحه — همان نقشی که در الگوی مرجع دارد: جدا کردن ناحیه جشن */
private val StreakCream = Color(0xFFFFF4D6)
private val FreezeBlue = Color(0xFF42A5F5)

/**
 * صفحه زنجیره — بازنویسی‌شده روی الگوی دولینگو.
 *
 * نسخه قبلی همه‌چیز را وسط صفحه و هم‌وزن می‌چید؛ نتیجه‌اش این بود که
 * عدد زنجیره — تنها چیزی که کاربر برای دیدنش می‌آید — با متن توضیح
 * رقابت می‌کرد. الگوی دولینگو سه ناحیه دارد و هر کدام یک کار می‌کند:
 *
 *   ۱. **سرصفحه گرم** با عدد غول‌آسا و شعله محو در پس‌زمینه.
 *      رنگ کرمی، سطح را از بقیه اپ جدا می‌کند و حس «جشن» می‌دهد.
 *   ۲. **کارت فریز** روی همان زمینه — سفید، پس بلافاصله دیده می‌شود.
 *   ۳. **تقویم** روی سفید، جایی که کاربر تاریخچه‌اش را می‌بیند.
 *
 * چرا عدد این‌قدر بزرگ است: زنجیره تنها سازوکاری است که بازگشت فردا را
 * می‌سازد. هرچه ملموس‌تر باشد، شکستنش سخت‌تر حس می‌شود.
 */
@Composable
fun StreakScreen(vm: StreakViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        // --- ناحیه گرم
        Box(
            Modifier.fillMaxWidth().background(StreakCream)
                .statusBarsPadding().padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 22.dp)
        ) {
            Column {
                Text(
                    "زنجیره مطالعه",
                    style = MaterialTheme.typography.titleLarge,
                    color = Flame,
                )
                Spacer(Modifier.size(12.dp))
                // عدد و شعله کنار هم.
                //
                // شعله پیش‌تر پس‌زمینه‌ای غول‌آسا بود و درست زیر متن
                // می‌افتاد؛ هم متن را ناخوانا می‌کرد و هم صفر فارسی («٠»)
                // که فقط یک نقطه است، کنارش گم می‌شد.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("🔥", fontSize = 72.sp)
                    // صفر بزرگ نشان نمی‌دهیم.
                    //
                    // دو دلیل: صفر فارسی («۰») در اندازه بزرگ فقط یک لکه
                    // است و شکسته به‌نظر می‌رسد؛ و مهم‌تر، عددِ صفرِ
                    // غول‌آسا اولین چیزی است که کاربر تازه می‌بیند و
                    // دلسردکننده است. جایش دعوت به شروع می‌گذاریم.
                    if (s.currentLength == 0) {
                        Text(
                            "زنجیره‌ات را\nامروز شروع کن",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Flame,
                        )
                    } else {
                        Column {
                            Text(
                                s.currentLength.toPersianDigits(),
                                fontSize = 68.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Flame,
                            )
                            Text(
                                "روز پشت‌سرهم",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Flame,
                            )
                        }
                    }
                }
                if (s.longestLength > s.currentLength) {
                    Spacer(Modifier.size(4.dp))
                    Text(
                        "رکوردت: ${s.longestLength.toPersianDigits()} روز",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Flame.copy(alpha = 0.75f),
                    )
                }

                Spacer(Modifier.size(20.dp))
                // کارت فریز — سفید روی کرمی، پس خودش را نشان می‌دهد
                Surface(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text("❄", fontSize = 30.sp, color = FreezeBlue)
                            Text(
                                if (s.freezeCount > 0)
                                    "${s.freezeCount.toPersianDigits()} فریز داری"
                                else "فریزی نداری",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        Spacer(Modifier.size(8.dp))
                        Text(
                            if (s.freezeCount > 0)
                                "اگر یک روز جا بماند، فریز خودش خرج می‌شود و زنجیره‌ات نمی‌شکند."
                            else "با تمام کردن درس، فریز جمع می‌شود — تا " +
                                "${StreakRules.MAX_FREEZES.toPersianDigits()} تا.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.size(12.dp))
                        FreezeSlots(s.freezeCount)
                    }
                }
            }
        }

        // --- تقویم روی سفید
        Column(Modifier.padding(20.dp)) {
            Text("این هفته", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.size(14.dp))
            WeekStrip(s.week)
        }
    }
}

@Composable
private fun WeekStrip(week: List<StreakRepository.DayCell>) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        week.forEach { d ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    // حرف اول روز، نه نام کاملش: «سه‌شنبه» در ستون باریک
                    // به دو خط می‌شکست و کل ردیف را به هم می‌ریخت.
                    JalaliDate.WEEK_DAYS[d.weekDayIndex].take(1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (d.isToday) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (d.isToday) FontWeight.Bold else FontWeight.Normal,
                )
                Spacer(Modifier.size(8.dp))
                DayDot(d)
            }
        }
    }
}

@Composable
private fun DayDot(d: StreakRepository.DayCell) {
    val (bg, content) = when (d.status) {
        StreakDayStatus.ACTIVE -> Flame to "✓"
        StreakDayStatus.FROZEN -> FreezeBlue to "❄"
        StreakDayStatus.MISSED -> MaterialTheme.colorScheme.surfaceVariant to ""
        null -> MaterialTheme.colorScheme.surfaceVariant to ""
    }
    Box(
        Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(bg)
            .then(
                if (d.isToday && d.status == null)
                    Modifier.border(2.dp, Flame, CircleShape) else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            content.ifEmpty { d.jalali.day.toPersianDigits() },
            style = MaterialTheme.typography.bodyLarge,
            color = if (d.status == null) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
            fontWeight = if (d.isToday) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

/** سه جایگاه فریز — پرشده آبی، خالی خاکستری */
@Composable
private fun FreezeSlots(count: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(StreakRules.MAX_FREEZES) { i ->
            val filled = i < count
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (filled) FreezeBlue.copy(alpha = 0.18f)
                        else MaterialTheme.colorScheme.surface
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text("❄", fontSize = 20.sp, color = if (filled) FreezeBlue else Color(0xFFBDBDBD))
            }
        }
    }
}
