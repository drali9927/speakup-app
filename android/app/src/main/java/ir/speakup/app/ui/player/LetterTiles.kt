package ir.speakup.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.speakup.app.ui.theme.ltr

/**
 * ساخت واژه با کاشی حروف.
 *
 * چرا این‌طور و نه تایپ آزاد، و نه چهارگزینه‌ای:
 *
 * **تایپ آزاد** درست بود ولی روی گوشی فارسی، عوض کردن زبان صفحه‌کلید برای
 * هر واژه، تمرین را از حوصله خارج می‌کرد.
 *
 * **چهارگزینه‌ای** آسان است اما «تشخیصی» است نه «تولیدی» — کاربر واژه را
 * از بین گزینه‌ها می‌شناسد بدون اینکه از حافظه بسازد. قاعده ۶۰٪ تولیدی
 * (سند ۰۷، تمایز ۱) با این کار می‌شکست.
 *
 * **کاشی حروف** هر دو را حل می‌کند: کاربر باید املای واژه را بداند، اما
 * صفحه‌کلیدی در کار نیست. همان راهی که دولینگو برای این مسئله رفته.
 *
 * حروف اضافی عمداً از خودِ حروف انگلیسی نزدیک انتخاب می‌شوند تا حدس زدن
 * از روی شکل کاشی‌ها ممکن نباشد.
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun LetterTiles(
    slots: List<Char?>,
    pool: List<LetterTile>,
    enabled: Boolean,
    onPick: (Int) -> Unit,
    onUnpick: (Int) -> Unit,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        // جای واژه — همیشه به تعداد حروف، تا طول واژه سرنخ باشد.
        //
        // ⚠️ جهت اجباراً چپ‌به‌راست است. کل اپ در RTL اجرا می‌شود و بدون
        // این، نخستین حرف واژه سمت راست می‌نشست و واژه برعکس ساخته می‌شد.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            slots.forEachIndexed { i, ch ->
                Surface(
                    Modifier.padding(2.dp).size(width = 34.dp, height = 46.dp)
                        .clickable(enabled = enabled && ch != null) { onUnpick(i) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (ch == null) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Box(Modifier.fillMaxWidth(), Alignment.Center) {
                        Text(
                            ch?.toString().orEmpty(),
                            Modifier.padding(top = 10.dp),
                            style = MaterialTheme.typography.titleLarge.ltr(),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
        }

        Spacer(Modifier.height(28.dp))

        // مخزن هم چپ‌به‌راست، تا ترتیب دیداری کاشی‌ها با جای واژه بخواند
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        FlowRow(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            pool.forEachIndexed { i, tile ->
                // کاشی مصرف‌شده جایش خالی می‌ماند تا بقیه زیر انگشت نپرند
                if (tile.used) {
                    Spacer(Modifier.padding(3.dp).size(width = 42.dp, height = 50.dp))
                } else {
                    Surface(
                        Modifier.padding(3.dp).size(width = 42.dp, height = 50.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                            .clickable(enabled = enabled) { onPick(i) },
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Box(Modifier.fillMaxWidth(), Alignment.Center) {
                            Text(
                                tile.ch.toString(),
                                Modifier.padding(top = 12.dp),
                                style = MaterialTheme.typography.titleLarge.ltr(),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

/** یک حرف در مخزن */
data class LetterTile(val ch: Char, val used: Boolean = false)
