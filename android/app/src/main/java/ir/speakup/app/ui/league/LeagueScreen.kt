package ir.speakup.app.ui.league

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import ir.speakup.app.ui.theme.DuoRed
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Surface
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.data.remote.LeagueRow
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.theme.DuoBorder
import ir.speakup.app.ui.theme.DuoGold
import ir.speakup.app.ui.theme.DuoGreen
import ir.speakup.app.ui.theme.DuoGreenSoft
import ir.speakup.app.ui.theme.ltr

/**
 * صفحه لیگ.
 *
 * چرا این سازوکار کار می‌کند: زنجیره کاربر را با خودش مقایسه می‌کند،
 * لیگ با دیگران. برای بخشی از کاربران، همین مقایسه بیرونی تنها چیزی
 * است که هر روز برشان می‌گرداند.
 *
 * دو تصمیم در نمایش:
 *
 * ۱. **ردیف خودِ کاربر پررنگ است** — در فهرست سی‌نفره، اولین چیزی که
 *    دنبالش می‌گردی خودت هستی.
 * ۲. **سه رتبه اول فقط رنگ طلایی می‌گیرند، نه نشان جداگانه.** شلوغ
 *    کردن جدول با مدال و آیکن، خواندنش را سخت می‌کند.
 */
@Composable
fun LeagueScreen(vm: LeagueViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                if (s.tierName.isBlank()) "لیگ" else "لیگ ${s.tierName}",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.size(4.dp))
            // سه تکه در سه Text جدا، و جداکننده «،» و نه «·».
            //
            // دو اشکال جدا داشت. **یکم:** در یک رشته، الگوریتم دوسویه
            // جداکننده را کنارِ عددها جابه‌جا می‌کرد و روی صفحه «گروه
            // ۱۴۰۱ نفر ۳۰ · روز تا پایان» خوانده می‌شد — یعنی «۳۰ نفر»
            // وارونه. همان اشکالی که پیش‌تر «۱۴ دقیقه» را «۱۴۰ دقیقه»
            // می‌کرد؛ درمانش تکه‌تکه کردن Text است.
            //
            // **دوم:** صفر فارسی «۰» خودش یک نقطه است و از «·» تشخیص
            // داده نمی‌شود. «گروه ۱ · ۱۶ نفر» روی صفحه شبیه یک عدد
            // بلند دیده می‌شد. ویرگول فارسی این ابهام را ندارد.
            if (s.rows.isEmpty()) {
                Text(
                    "این هفته هنوز کسی امتیازی نگرفته",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    val dim = MaterialTheme.colorScheme.onSurfaceVariant
                    val body = MaterialTheme.typography.bodyMedium
                    Text("گروه ${s.cohort.toPersianDigits()}", style = body, color = dim)
                    Text("،", style = body, color = dim)
                    Text("${s.rows.size.toPersianDigits()} نفر", style = body, color = dim)
                    Text("،", style = body, color = dim)
                    Text(remaining(s.endsAt), style = body, color = dim, maxLines = 1)
                }
            }

            // فاصله تا رتبه بعد — همان چیزی که باعث می‌شود کاربر یک درس
            // دیگر بزند. «رتبه هفتم» به‌تنهایی هیچ کاری با کسی نمی‌کند؛
            // «۳۵ امتیاز تا رتبه ششم» کار می‌کند.
            val me = s.rows.firstOrNull { it.isMe }
            if (me != null) {
                val ahead = s.rows.firstOrNull { it.rank == me.rank - 1 }
                val gap = ahead?.let { it.xp - me.xp + 1 }
                val promoting = me.rank <= s.promoteCount
                val falling = s.relegateCount > 0 && me.rank > s.rows.size - s.relegateCount

                // رنگ باید با حالِ کاربر بخواند. نسخه اول همه‌چیز را سبز
                // می‌کرد و «در منطقه سقوط» با رنگ جشن نوشته می‌شد — یعنی
                // دقیقاً وارونه‌ی چیزی که باید حس شود.
                val accent = when {
                    promoting -> DuoGreen
                    falling -> DuoRed
                    else -> MaterialTheme.colorScheme.primary
                }

                Spacer(Modifier.size(12.dp))
                Surface(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = accent.copy(alpha = 0.12f),
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            when {
                                promoting && s.nextTierName != null ->
                                    "در منطقه صعود به لیگ ${s.nextTierName}"
                                promoting -> "در بالاترین رده، صدرنشین"
                                falling -> "در منطقه سقوط"
                                else -> "رتبه ${me.rank.toPersianDigits()} از ${s.rows.size.toPersianDigits()}"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = accent,
                        )
                        if (gap != null && gap > 0) {
                            Spacer(Modifier.size(2.dp))
                            Text(
                                "${gap.toPersianDigits()} امتیاز تا رتبه ${(me.rank - 1).toPersianDigits()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        when {
            s.loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            s.error != null -> Text(
                s.error!!,
                Modifier.fillMaxWidth().padding(24.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
            else -> LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                // مرزِ منطقه‌ها به‌شکل خط، درست همان‌جا که رخ می‌دهد.
                // بدون این، «پنج نفر اول صعود می‌کنند» یک جمله است؛ با
                // این، کاربر می‌بیند دقیقاً چند ردیف با آن فاصله دارد.
                itemsIndexed(s.rows) { i, row ->
                    if (s.nextTierName != null && i == s.promoteCount && s.promoteCount > 0) {
                        ZoneDivider("صعود به لیگ ${s.nextTierName}", DuoGreen)
                    }
                    if (s.relegateCount > 0 && i == s.rows.size - s.relegateCount &&
                        i > s.promoteCount
                    ) {
                        ZoneDivider("منطقه سقوط", DuoRed)
                    }
                    LeagueRowView(row)
                }
                item { Spacer(Modifier.size(24.dp)) }
            }
        }
    }
}

@Composable
private fun LeagueRowView(row: LeagueRow) {
    val gold = row.rank <= 3
    androidx.compose.foundation.layout.Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (row.isMe) DuoGreenSoft else MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(34.dp).clip(CircleShape)
                .background(if (gold) DuoGold else DuoBorder),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                row.rank.toPersianDigits(),
                style = MaterialTheme.typography.titleMedium,
                color = if (gold) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            if (row.isMe) "${row.name} (تو)" else row.name,
            Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            "${row.xp.toPersianDigits()} ⭐",
            style = MaterialTheme.typography.titleMedium.ltr(),
            color = if (row.isMe) DuoGreen else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * خط مرزِ منطقه صعود یا سقوط.
 *
 * نامش بالای خط می‌آید و نه پایینش: ردیف‌های **بالای** این خط آن اتفاق
 * را دارند، و چشم فارسی‌زبان هم از بالا به پایین می‌خواند.
 */
@Composable
private fun ZoneDivider(label: String, color: Color) {
    androidx.compose.foundation.layout.Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.weight(1f).size(2.dp).background(color.copy(alpha = 0.35f)))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Box(Modifier.weight(1f).size(2.dp).background(color.copy(alpha = 0.35f)))
    }
}

/**
 * زمان باقی‌مانده دوره، به زبان آدمیزاد.
 *
 * ساعت و دقیقهٔ دقیق لازم نیست و فقط شلوغ می‌کند؛ آنچه کاربر باید بفهمد
 * این است که «هنوز وقت هست» یا «امروز آخرین فرصت است».
 */
private fun remaining(endsAt: Long): String {
    if (endsAt <= 0) return ""
    val left = endsAt - System.currentTimeMillis() / 1000
    if (left <= 0) return "دوره تمام شد"
    val days = left / 86_400
    val hours = (left % 86_400) / 3_600
    return when {
        days > 0 -> "${days.toInt().toPersianDigits()} روز تا پایان"
        hours > 0 -> "${hours.toInt().toPersianDigits()} ساعت تا پایان"
        else -> "کمتر از یک ساعت تا پایان"
    }
}
