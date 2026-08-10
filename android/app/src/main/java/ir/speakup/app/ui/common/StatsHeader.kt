package ir.speakup.app.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.theme.DuoBorder
import ir.speakup.app.ui.theme.DuoGold
import ir.speakup.app.ui.theme.DuoGreen
import ir.speakup.app.ui.theme.DuoGreenDark
import ir.speakup.app.ui.theme.ltr

/**
 * نوار آمار بالای صفحه — زنجیره، امتیاز، سطح.
 *
 * جای کارت بزرگ «هدف روزانه» را گرفت. آن کارت ۱۴۰ پیکسل از بالای صفحه
 * را می‌گرفت و مسیر دروس را به پایینِ خط تا می‌راند؛ یعنی کاربر با باز
 * کردن اپ اول یک گزارش می‌دید، نه کاری که باید انجام دهد.
 *
 * اینجا همان سه عدد در یک نوار باریک‌اند: در یک نگاه خوانده می‌شوند و
 * جا را به مسیر می‌دهند. نوار پیشرفت هدف هم زیرشان، نازک و بی‌سروصدا.
 */
@Composable
fun StatsHeader(
    levelTitle: String,
    streakDays: Int,
    todayXp: Int,
    goalXp: Int,
    onPickLevel: () -> Unit,
    onStreak: () -> Unit,
    onXp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ratio = if (goalXp <= 0) 1f else (todayXp.toFloat() / goalXp).coerceIn(0f, 1f)
    val progress by animateFloatAsState(ratio, tween(700), label = "goalbar")

    Column(modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // سطح — خودش دکمه است، چون کاربر همان‌جا دنبال تعویضش می‌گردد
            Row(
                Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onPickLevel)
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(levelTitle, style = MaterialTheme.typography.titleMedium)
                Text("▾", style = MaterialTheme.typography.titleMedium)
            }

            // هر دو نشان قابل زدن‌اند و به صفحه‌ای می‌روند که همان عدد را
            // توضیح می‌دهد: آتش به زنجیره، ستاره به لیگ (که رتبه‌اش را
            // همین امتیاز می‌سازد). پیش‌تر فقط عدد بودند و زدنشان هیچ
            // نمی‌کرد — کاربر می‌زد چون به نظر دکمه می‌آمدند.
            Stat("🔥", streakDays.toPersianDigits(), "زنجیره", onStreak)
            Stat("⭐", todayXp.toPersianDigits(), "امتیاز امروز", onXp)
        }

        Spacer8()
        // نوار هدف روزانه — نازک، چون اطلاعات پس‌زمینه است نه کنش
        Box(
            Modifier.fillMaxWidth().height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(DuoBorder)
        ) {
            Box(
                Modifier.fillMaxWidth(progress).height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (progress >= 1f) DuoGreenDark else DuoGreen)
            )
        }
    }
}

@Composable
private fun Stat(icon: String, value: String, label: String, onClick: () -> Unit) {
    Row(
        // ناحیه لمس تا لبه نشان کشیده می‌شود، وگرنه هدفِ زدن به اندازه
        // خودِ اموجی کوچک می‌ماند و انگشت خطا می‌کند.
        Modifier.clip(RoundedCornerShape(12.dp))
            .clickable(onClickLabel = label, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(icon, style = MaterialTheme.typography.titleMedium)
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.ltr(),
            color = DuoGold.darkenForText(),
        )
    }
}

@Composable
private fun Spacer8() = Box(Modifier.height(8.dp))

/** طلایی روی سفید خوانا نیست؛ برای متن کمی تیره‌ترش می‌کنیم. */
private fun Color.darkenForText() = Color(red * 0.8f, green * 0.72f, blue * 0.2f, alpha)

/**
 * بنر بخش — نوار رنگی پهن بالای مسیر.
 *
 * کارش تقسیم مسیر بلند به تکه‌های قابل‌هضم است: کاربر به‌جای «۳۰ درس»،
 * «بخش ۱: آشنایی» را می‌بیند و پیشرفتش را نسبت به یک هدف نزدیک
 * می‌سنجد. همان دلیلی که واحدها در دولینگو بنر رنگی دارند.
 */
@Composable
fun UnitBanner(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    color: Color = DuoGreen,
    under: Color = DuoGreenDark,
) {
    Box(modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Box(
            Modifier.fillMaxWidth().height(76.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(under)
        )
        Box(
            Modifier.fillMaxWidth().height(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color)
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Column {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f),
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 1,
                )
            }
        }
    }
}
