package ir.speakup.app.ui.match

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.theme.ltr

private val Green = Color(0xFF2E7D32)
private val Red = Color(0xFFC62828)

/**
 * بازی جفت‌یابی — واژه را به معنی‌اش وصل کن.
 *
 * چرا این تمرین اضافه شد: کارت واژه و آزمون سه‌گزینه‌ای هر دو «یکی در
 * یک لحظه» هستند و ریتم کندی دارند. اینجا کاربر ده کاشی را جلوی چشمش
 * می‌بیند و در چند ثانیه پنج جفت را می‌زند — همان چیزی که در دولینگو
 * «tap the pairs» نام دارد و پرتکرارترین تمرین واژگانش است.
 *
 * دو تصمیم که اثر یادگیری دارند:
 *
 * **حریف‌ها از همان دور می‌آیند.** پنج واژه در هر دور یعنی هر انتخاب
 * غلط اطلاعات می‌دهد؛ با بیست کاشی، کاربر فقط شلوغی می‌بیند.
 *
 * **کاشی درست ناپدید می‌شود، نه اینکه فقط سبز شود.** کم شدن تدریجی
 * صفحه همان چیزی است که حس پیشرفت را در چند ثانیه می‌سازد.
 */
@Composable
fun WordMatchScreen(
    tiles: List<MatchTile>,
    selectedId: String?,
    wrongPair: Pair<String, String>?,
    roundIndex: Int,
    roundCount: Int,
    onTap: (MatchTile) -> Unit,
    onClose: () -> Unit,
) {
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, "بستن") }
            LinearProgressIndicator(
                progress = { if (roundCount == 0) 0f else roundIndex.toFloat() / roundCount },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            )
            Text(
                "${(roundIndex + 1).toPersianDigits()}/${roundCount.toPersianDigits()}",
                Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Text(
            "واژه را به معنی‌اش وصل کن",
            Modifier.fillMaxWidth().padding(vertical = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        // دو ستون: انگلیسی و فارسی. ستونی بودن باعث می‌شود کاربر بداند
        // از کدام سمت انتخاب کند و دنبال جفت در کل صفحه نگردد.
        Row(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tiles.filter { !it.isPersian }.forEach { tile ->
                    Tile(tile, selectedId, wrongPair, onTap)
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tiles.filter { it.isPersian }.forEach { tile ->
                    Tile(tile, selectedId, wrongPair, onTap)
                }
            }
        }
        Spacer(Modifier.size(20.dp))
    }
}

@Composable
private fun Tile(
    tile: MatchTile,
    selectedId: String?,
    wrongPair: Pair<String, String>?,
    onTap: (MatchTile) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val isWrong = wrongPair != null && (tile.id == wrongPair.first || tile.id == wrongPair.second)
    val isSelected = tile.id == selectedId

    val border by animateColorAsState(
        when {
            isWrong -> Red
            isSelected -> scheme.primary
            else -> scheme.outlineVariant
        },
        label = "tileBorder",
    )
    val fill by animateColorAsState(
        when {
            isWrong -> Red.copy(alpha = 0.10f)
            isSelected -> scheme.primary.copy(alpha = 0.12f)
            else -> scheme.surface
        },
        label = "tileFill",
    )

    // جای کاشی حل‌شده خالی می‌ماند تا بقیه کاشی‌ها زیر انگشت کاربر
    // بالا و پایین نپرند — جابه‌جا شدنشان باعث ضربه اشتباه می‌شود.
    if (tile.matched) {
        Spacer(Modifier.fillMaxWidth().height(62.dp))
        return
    }

    Surface(
        Modifier.fillMaxWidth().height(62.dp)
            .border(if (isSelected || isWrong) 2.dp else 1.dp, border, RoundedCornerShape(14.dp))
            .clickable { onTap(tile) },
        shape = RoundedCornerShape(14.dp),
        color = fill,
    ) {
        Box(Modifier.fillMaxSize().padding(horizontal = 10.dp), Alignment.Center) {
            Text(
                tile.text,
                style = if (tile.isPersian) MaterialTheme.typography.titleMedium
                else MaterialTheme.typography.titleMedium.ltr(),
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

/** یک کاشی روی صفحه */
data class MatchTile(
    val id: String,
    val text: String,
    /** کلید جفت — دو کاشی با یک pairKey جفت هم‌اند */
    val pairKey: String,
    val isPersian: Boolean,
    val matched: Boolean = false,
)
