package ir.speakup.app.ui.lessons

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.speakup.app.data.local.LessonEntity
import ir.speakup.app.domain.toPersianDigits

private val Green = Color(0xFF58CC02)

/**
 * «برنامه امروز» — سه کار مشخص، در یک جا.
 *
 * چرا لازم شد: کاربری که اپ را باز می‌کند با فهرست سی درس روبه‌رو
 * می‌شد و باید خودش تصمیم می‌گرفت از کجا شروع کند. نشان «از اینجا ادامه
 * بده» و ایستگاه مرور هرکدام جدا بودند و هیچ‌جا نمی‌گفت «امروز این‌قدر
 * کار داری». تصمیم گرفتن، خودش اصطکاک است — و در اپی که قرار است روزی
 * ده دقیقه استفاده شود، همان اصطکاک فاصله بین باز کردن و بستن است.
 *
 * سه قاعده:
 *
 * **۱. هر سطر یک کنش است، نه یک گزارش.** زدن روی هرکدام مستقیم همان کار
 * را باز می‌کند.
 *
 * **۲. کارِ تمام‌شده حذف نمی‌شود، تیک می‌خورد.** فهرستی که کوتاه‌تر
 * می‌شود، حس پیشرفت نمی‌دهد؛ فهرستی که تیک می‌خورد می‌دهد.
 *
 * **۳. وقتی همه‌چیز تمام شد، کارت جای تشویق می‌شود و نه یادآوریِ خالی.**
 * کاربری که کارش را کرده نباید باز هم فهرست کار ببیند.
 */
@Composable
fun TodayPlan(
    lesson: LessonEntity?,
    lessonsDone: Int,
    lessonsTotal: Int,
    dueCount: Int,
    todayXp: Int,
    goalXp: Int,
    onOpenLesson: () -> Unit,
    onOpenReview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val goalDone = todayXp >= goalXp
    val lessonDone = lesson == null
    val reviewDone = dueCount == 0
    val allDone = goalDone && reviewDone

    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = scheme.surface,
        border = androidx.compose.foundation.BorderStroke(2.dp, scheme.outlineVariant),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (allDone) "کار امروزت تمام شد" else "برنامه امروز",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "${todayXp.toPersianDigits()} از ${goalXp.toPersianDigits()} امتیاز",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                )
            }

            // شمارش دروس این‌جا آمد چون بنر سبزِ بالای فهرست برداشته شد.
            //
            // آن بنر نام سطح را تکرار می‌کرد — که در سرصفحه هست — و
            // شمارش را نشان می‌داد. سه بلوکِ پشت‌سرهم که تقریباً یک چیز
            // می‌گفتند، و تنها یکی‌شان قابل زدن بود.
            Spacer(Modifier.height(2.dp))
            Text(
                "${lessonsDone.toPersianDigits()} از ${lessonsTotal.toPersianDigits()} درس این سطح انجام شده",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { if (goalXp <= 0) 1f else (todayXp.toFloat() / goalXp).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Green,
                trackColor = scheme.surfaceVariant,
            )

            if (allDone) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "هر تمرین بیشتری از این‌جا به بعد، سود خالص است.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                )
                return@Column
            }

            Spacer(Modifier.height(6.dp))

            if (lesson != null) {
                PlanRow(
                    icon = "📘",
                    title = lesson.themeFa.ifBlank { lesson.titleEn },
                    subtitle = "درس ${lesson.number.toPersianDigits()} · " +
                        "${lesson.estimatedMinutes.toPersianDigits()} دقیقه",
                    done = false,
                    onClick = onOpenLesson,
                )
            } else {
                PlanRow(
                    icon = "📘",
                    title = "همه درس‌های این سطح تمام شد",
                    subtitle = "سطح بعدی را از بالای صفحه انتخاب کن",
                    done = true,
                    onClick = {},
                )
            }

            PlanRow(
                icon = "🔁",
                title = if (reviewDone) "مروری برای امروز نمانده"
                else "${dueCount.toPersianDigits()} واژه آماده مرور",
                subtitle = if (reviewDone) "واژه‌های اشتباه خودشان برمی‌گردند"
                else "چند دقیقه، و از یادت نمی‌رود",
                done = reviewDone,
                onClick = { if (!reviewDone) onOpenReview() },
            )
        }
    }
}

@Composable
private fun PlanRow(
    icon: String,
    title: String,
    subtitle: String,
    done: Boolean,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !done, onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (done) Green.copy(alpha = 0.15f) else scheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(if (done) "✓" else icon, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (done) FontWeight.Normal else FontWeight.SemiBold,
                color = if (done) scheme.onSurfaceVariant else scheme.onSurface,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }
    }
}
