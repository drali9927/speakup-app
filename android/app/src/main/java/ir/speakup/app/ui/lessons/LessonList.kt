package ir.speakup.app.ui.lessons

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.speakup.app.data.local.LessonEntity
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.common.HexagonShape
import ir.speakup.app.ui.theme.DuoGreen
import ir.speakup.app.ui.theme.ltr

/** وضعیت یک درس در فهرست */
enum class NodeState { DONE, CURRENT, OPEN, LOCKED }

/**
 * ایستگاه مرور بین دروس.
 *
 * شکلش عمداً با ردیف درس فرق دارد تا کاربر آن را با درس اشتباه نگیرد.
 *
 * چرا سرِ راه است و نه در تب لایتنر: مرورِ فاصله‌دار اثرش بر یادسپاری از
 * خودِ درس بیشتر است، اما تا وقتی کاربر باید خودش سراغش برود، عملاً
 * انجام نمی‌شود.
 */
@Composable
fun ReviewStation(dueCount: Int, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val active = dueCount > 0
    Surface(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (active) scheme.secondaryContainer else scheme.surfaceVariant,
    ) {
        Row(
            Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("🔁", style = MaterialTheme.typography.headlineSmall)
            Column(Modifier.weight(1f)) {
                Text(
                    "ایستگاه مرور",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    if (active) "${dueCount.toPersianDigits()} واژه آماده مرور است"
                    else "فعلاً واژه‌ای برای مرور نداری",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * ردیف یک درس.
 *
 * چرا فهرست، و نه مسیر پیچ‌درپیچ:
 *
 * **گره اینجا هم‌اندازه گره دولینگو نیست.** در دولینگو هر گره دو سه دقیقه
 * و یک مهارت است، پس مسیرِ مارپیچ یعنی «قدم‌های کوچکِ پشت‌سرهم». گره ما
 * یک درس چهارده‌دقیقه‌ای با شش بخش و هفده فعالیت است. مسیر، ریزدانگی‌ای
 * را وعده می‌دهد که محتوا ندارد.
 *
 * **معماری خودِ محصول فهرستی است.** هر درس شش بخش دارد — واژگان، مکالمه،
 * گرامر، گفتار، شنیدار، مرور — و صفحه درس هم همین حالا فهرست عمودی کارت
 * است. مسیر تنها جای دولینگویی در محصولی بود که بقیه‌اش فهرست است.
 *
 * **ریتم بصری از رنگ می‌آید، نه از جابه‌جایی افقی.** شش‌ضلعی رنگی همان
 * کاری را می‌کند که موج سینوسی می‌خواست بکند — یکنواختی فهرست را
 * می‌شکند — بی‌آنکه ترتیب خواندن را مبهم کند.
 *
 * آنچه از مسیر نگه داشته شد چیزهایی است که کارشان را می‌کردند: برجستگی
 * سه‌بعدی که ردیف را «قابل زدن» نشان می‌دهد، نشان پیشرفت درس نیمه‌کاره،
 * قفل و تیک، نبضِ درس جاری، و ایستگاه مرور هر پنج درس.
 */
@Composable
fun LessonRow(
    lesson: LessonEntity,
    state: NodeState,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val locked = state == NodeState.LOCKED
    val current = state == NodeState.CURRENT

    val hex = when (state) {
        NodeState.DONE -> DuoGreen
        NodeState.CURRENT -> scheme.primary
        // رنگ از پالت اپ می‌آید و نه از colorHex محتوا: آن رنگ‌ها برای
        // پالت بنفشِ قبلی انتخاب شده بودند و کنار سبزِ تازه گل‌آلود
        // دیده می‌شوند.
        NodeState.OPEN -> NODE_COLORS[lesson.number.mod(NODE_COLORS.size)]
        NodeState.LOCKED -> scheme.surfaceVariant
    }

    // فقط درس جاری نبض می‌زند. اگر همه می‌زدند، هیچ‌کدام توجه نمی‌گرفت.
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (current) 1.05f else 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulseScale",
    )

    Surface(
        modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable(enabled = !locked, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        // درس جاری زمینه رنگی می‌گیرد تا از بالای فهرست پیدا باشد
        color = if (current) scheme.primaryContainer.copy(alpha = 0.45f) else scheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            if (current) 2.dp else 1.dp,
            if (current) scheme.primary else scheme.outlineVariant,
        ),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // شش‌ضلعی شماره درس — همان برجستگی گره‌های مسیر، در اندازه ردیف
            Box(Modifier.size(56.dp).scale(pulse), contentAlignment = Alignment.TopCenter) {
                Box(
                    Modifier.size(52.dp).padding(top = HEX_DEPTH)
                        .clip(HexagonShape()).background(hex.darken())
                )
                Box(
                    Modifier.size(52.dp).clip(HexagonShape()).background(hex),
                    contentAlignment = Alignment.Center,
                ) {
                    when (state) {
                        NodeState.DONE ->
                            Icon(Icons.Default.Check, null, Modifier.size(26.dp), tint = Color.White)
                        NodeState.LOCKED ->
                            Icon(
                                Icons.Default.Lock, "قفل", Modifier.size(20.dp),
                                tint = scheme.onSurfaceVariant,
                            )
                        else -> Text(
                            lesson.number.toPersianDigits(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }

            // نشان‌ها بالای عنوان‌اند و نه کنارش.
            //
            // وقتی «رایگان» ستون کناری بود، عرض را از عنوان می‌گرفت و
            // «A matter of perspective.» به «A matter of» بریده می‌شد —
            // یعنی همان چیزی که قرار بود کاربر پیش از ورود بداند.
            Column(Modifier.weight(1f)) {
                val badge: Pair<String, Color>? = when {
                    current -> "از اینجا ادامه بده" to scheme.primary
                    state == NodeState.DONE -> "انجام شد" to DuoGreen
                    lesson.isFree && !locked -> "رایگان" to DuoGreen
                    else -> null
                }
                badge?.let { (label, tint) ->
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = tint,
                    )
                    Spacer(Modifier.height(2.dp))
                }
                // عنوان انگلیسیِ معنادار، نه فقط شماره — کاربر پیش از ورود
                // می‌داند چه یاد می‌گیرد و همین نرخ شروع را بالا می‌برد.
                Text(
                    lesson.titleEn,
                    style = MaterialTheme.typography.titleMedium.ltr(),
                    fontWeight = FontWeight.SemiBold,
                    color = if (locked) scheme.onSurfaceVariant else scheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                // اینجا «ماموریت درس» می‌آید و نه موضوع گرامر.
                //
                // کاربر در این فهرست دارد تصمیم می‌گیرد سراغ کدام درس
                // برود؛ «از کارهای روزمره‌ات بگو» به آن تصمیم جواب
                // می‌دهد، «حال ساده: I/you/we/they» نه. موضوع گرامر
                // داخل خود درس، بالای فهرست بخش‌ها نشان داده می‌شود —
                // جایی که کاربر وارد شده و می‌خواهد بداند چه می‌آموزد.
                //
                // موضوع و زمان دو Text جدا هستند و نه یک رشته.
                //
                // در یک رشته، الگوریتم دوسویه جداکننده «·» را کنارِ عدد
                // می‌نشاند و «۱۵ دقیقه» روی صفحه «۱۵۰ دقیقه» خوانده می‌شد.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        lesson.themeFa,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Text(
                        // «،» و نه «·» — صفر فارسی خودش یک نقطه است و
                        // نقطه کنار عدد، جزئی از همان عدد دیده می‌شود.
                        "،",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                    )
                    Text(
                        "${lesson.estimatedMinutes.toPersianDigits()} دقیقه",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }

                // نوار پیشرفت فقط برای درس نیمه‌کاره.
                //
                // در مسیر، این کار را حلقه دور گره می‌کرد. در ردیف، نوار
                // خطی خواناتر است چون درصد را کنارش جا می‌دهد.
                if (progress > 0f && state != NodeState.DONE) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LinearProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier.weight(1f).height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = scheme.primary,
                            trackColor = scheme.surfaceVariant,
                        )
                        Text(
                            "${(progress * 100).toInt().toPersianDigits()}٪",
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

        }
    }
}

/** نسخه تیره‌ترِ همان رنگ — برای لایه زیرینِ شش‌ضلعی. */
private fun Color.darken(factor: Float = 0.78f) =
    Color(red * factor, green * factor, blue * factor, alpha)

private val HEX_DEPTH = 4.dp

/**
 * چرخه رنگ شش‌ضلعی‌ها.
 *
 * قرمز عمداً نیست: در این اپ قرمز فقط یعنی «اشتباه» و اگر شماره درس هم
 * قرمز باشد، آن معنی رقیق می‌شود.
 */
private val NODE_COLORS = listOf(
    ir.speakup.app.ui.theme.DuoGreen,
    ir.speakup.app.ui.theme.DuoBlue,
    ir.speakup.app.ui.theme.DuoPurple,
    ir.speakup.app.ui.theme.DuoGold,
)
