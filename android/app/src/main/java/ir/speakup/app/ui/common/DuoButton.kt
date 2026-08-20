package ir.speakup.app.ui.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.speakup.app.ui.theme.DuoBlue
import ir.speakup.app.ui.theme.DuoBlueDark
import ir.speakup.app.ui.theme.DuoBorder
import ir.speakup.app.ui.theme.DuoDisabled
import ir.speakup.app.ui.theme.DuoGreen
import ir.speakup.app.ui.theme.DuoGreenDark
import ir.speakup.app.ui.theme.DuoInk
import ir.speakup.app.ui.theme.DuoMuted
import ir.speakup.app.ui.theme.DuoRed
import ir.speakup.app.ui.theme.DuoRedDark

/**
 * دکمه سه‌بعدی — امضای بصری این سبک.
 *
 * ساختارش عمداً ساده است: یک لایه تیره‌تر پشت، و روی آن لایه اصلی که
 * چند پیکسل بالاتر نشسته. با فشردن، لایه رویی پایین می‌آید و روی لایه
 * تیره می‌نشیند. همین حرکت کوچک است که به دکمه حس **جسم فیزیکی** می‌دهد.
 *
 * چرا مهم است: در اپ آموزشی، کاربر روزی ده‌ها بار همین یک دکمه را
 * می‌زند. اگر فشردنش حس خوبی بدهد، ادامه دادن آسان‌تر می‌شود. سایه محو
 * متریال این حس را نمی‌دهد چون هنگام فشار «فرو نمی‌رود».
 *
 * موج (ripple) عمداً حذف شده — با حرکت جابه‌جایی تداخل دارد و حس
 * پلاستیکی می‌دهد.
 */
@Composable
fun DuoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: DuoButtonStyle = DuoButtonStyle.Primary,
    enabled: Boolean = true,
    height: Dp = 52.dp,
) {
    DuoButtonBox(modifier, style, enabled, height, onClick) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
    }
}

/** همان دکمه، اما با محتوای دلخواه (آیکن + متن و مانند آن). */
@Composable
fun DuoButtonBox(
    modifier: Modifier = Modifier,
    style: DuoButtonStyle = DuoButtonStyle.Primary,
    enabled: Boolean = true,
    height: Dp = 52.dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val press = remember { MutableInteractionSource() }
    val pressed by press.collectIsPressedAsState()

    // وقتی غیرفعال است اصلاً برجسته نیست: چیزی که فشار داده نمی‌شود
    // نباید شبیه چیزی باشد که فشار داده می‌شود.
    val depth: Dp = if (!enabled) 0.dp else DEPTH
    val drop by animateDpAsState(if (pressed && enabled) depth else 0.dp, label = "duo-press")

    // دکمه خنثی، پرکنندهٔ سفیدِ ثابت داشت. در حالت شب یک دکمه سفیدِ
    // روشن وسط صفحه تیره می‌نشست و بیرون از تم دیده می‌شد. رنگش از
    // خودِ تم گرفته می‌شود؛ بقیه سبک‌ها روی زمینه رنگی‌اند و متن سفیدشان
    // در هر دو تم درست است.
    val base = style.colors(enabled)
    val c = if (enabled && style == DuoButtonStyle.Neutral) {
        base.copy(
            fill = MaterialTheme.colorScheme.surface,
            content = MaterialTheme.colorScheme.onSurfaceVariant,
            border = MaterialTheme.colorScheme.outlineVariant,
        )
    } else {
        base
    }

    Box(modifier.height(height + depth)) {
        // لایه زیرین — همان چیزی که هنگام فشردن دیده می‌شود
        Box(
            Modifier.fillMaxWidth().height(height + depth)
                .clip(SHAPE)
                .background(c.under)
        )
        Box(
            Modifier
                .offset(y = drop)
                .fillMaxWidth()
                .height(height)
                .clip(SHAPE)
                .background(c.fill)
                .then(if (c.border != null) Modifier.border(2.dp, c.border, SHAPE) else Modifier)
                .pressable(enabled, press, onClick),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalContentColor provides c.content) {
                Row(
                    Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) { content() }
            }
        }
    }
}

private fun Modifier.pressable(
    enabled: Boolean,
    interaction: MutableInteractionSource,
    onClick: () -> Unit,
) = clickable(
    interactionSource = interaction,
    indication = null,          // موج ندارد؛ خودِ جابه‌جایی بازخورد است
    enabled = enabled,
    onClick = onClick,
)

enum class DuoButtonStyle {
    /** کنش اصلی صفحه — همیشه فقط یکی در هر صفحه */
    Primary,

    /** کنش دوم: ادامه، اطلاعات، مسیر جایگزین */
    Secondary,

    /** کنش خنثی: انصراف، رد کردن، بستن */
    Neutral,

    /** کنش مخرب یا حالت اشتباه */
    Danger,
    ;

    fun colors(enabled: Boolean): DuoButtonColors = when {
        !enabled -> DuoButtonColors(DuoBorder, DuoBorder, DuoDisabled, null)
        this == Primary -> DuoButtonColors(DuoGreen, DuoGreenDark, Color.White, null)
        this == Secondary -> DuoButtonColors(DuoBlue, DuoBlueDark, Color.White, null)
        this == Danger -> DuoButtonColors(DuoRed, DuoRedDark, Color.White, null)
        else -> DuoButtonColors(Color.White, DuoBorder, DuoMuted, DuoBorder)
    }
}

data class DuoButtonColors(
    val fill: Color,
    val under: Color,
    val content: Color,
    val border: Color?,
)

/** همان ژرفا در همه‌جای اپ — اگر جایی فرق کند، چشم فوراً می‌گیردش. */
private val DEPTH = 4.dp
private val SHAPE = RoundedCornerShape(16.dp)

/** رنگ متن روی سطح‌های روشن، برای جاهایی که خارج از دکمه لازم است. */
val DuoOnSurface = DuoInk
