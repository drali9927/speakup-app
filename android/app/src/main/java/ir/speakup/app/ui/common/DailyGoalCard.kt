package ir.speakup.app.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.speakup.app.domain.toPersianDigits

/**
 * هدف روزانه — حلقه پیشرفت، سطح، و زنجیره در یک کارت.
 *
 * چرا هر سه کنار هم: تحقیق روی دولینگو نشان می‌دهد وقتی این سازوکارها
 * در صفحه‌های جدا پخش می‌شوند، کاربر آن‌ها را «چند قابلیت بی‌ربط»
 * می‌بیند. کنار هم بودنشان، همان چیزی است که یک سیستم می‌سازد.
 *
 * حلقه عمداً بزرگ‌ترین عنصر کارت است: چیزی که کاربر باید هر روز ببیند
 * «چقدر تا هدف امروز مانده» است، نه مجموع امتیاز کل.
 */
@Composable
fun DailyGoalCard(
    todayXp: Int,
    goalXp: Int,
    level: Int,
    streakDays: Int,
    modifier: Modifier = Modifier,
) {
    val target = if (goalXp <= 0) 1f else (todayXp.toFloat() / goalXp).coerceIn(0f, 1f)
    val progress by animateFloatAsState(target, tween(700), label = "goal")
    val done = todayXp >= goalXp

    Surface(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GoalRing(progress = progress, done = done, todayXp = todayXp, goalXp = goalXp)

            Column(Modifier.weight(1f)) {
                Text(
                    if (done) "هدف امروز را زدی" else "هدف امروز",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    if (done) "هر امتیاز از این به بعد اضافه است."
                    else "${(goalXp - todayXp).toPersianDigits()} امتیاز تا هدف",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.size(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip("سطح ${level.toPersianDigits()}")
                    if (streakDays > 0) Chip("🔥 ${streakDays.toPersianDigits()} روز")
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface) {
        Text(
            text,
            Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun GoalRing(progress: Float, done: Boolean, todayXp: Int, goalXp: Int) {
    val track = MaterialTheme.colorScheme.outlineVariant
    val fill = if (done) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary

    Box(Modifier.size(86.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(86.dp)) {
            val stroke = 9.dp.toPx()
            val inset = stroke / 2
            val arc = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = track, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = arc, style = Stroke(width = stroke),
            )
            drawArc(
                color = fill, startAngle = -90f, sweepAngle = 360f * progress, useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = arc, style = Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                todayXp.toPersianDigits(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = fill,
            )
            Text(
                "از ${goalXp.toPersianDigits()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
