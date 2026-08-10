package ir.speakup.app.ui.league

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
            Text(
                if (s.rows.isEmpty()) "این هفته هنوز کسی امتیازی نگرفته"
                else "گروه ${s.cohort.toPersianDigits()} · " +
                    "${s.rows.size.toPersianDigits()} نفر این هفته",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
                items(s.rows) { row -> LeagueRowView(row) }
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
