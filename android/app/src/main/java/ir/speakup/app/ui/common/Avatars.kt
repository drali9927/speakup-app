package ir.speakup.app.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import ir.speakup.app.R

/**
 * چهره‌های پروفایل.
 *
 * هشت چهره‌ی ثابت که خودمان ساخته‌ایم، نه آپلود عکس کاربر. سه دلیل:
 * بارگذاری عکس یعنی نگهداری فایل کاربر روی سرور و مسئولیت حقوقی‌اش؛
 * یعنی نیاز به بازبینی محتوا؛ و در عمل بیشتر کاربران هیچ‌وقت عکس
 * نمی‌گذارند و پروفایل خالی می‌ماند. انتخاب از چند گزینه آماده، همان
 * حس شخصی‌سازی را با کسری از هزینه می‌دهد.
 *
 * شناسه‌ها از ۱ شروع می‌شوند و **هرگز نباید جابه‌جا شوند**: عدد ذخیره
 * می‌شود، پس تغییر ترتیب یعنی عوض شدن چهره‌ی همه‌ی کاربران قدیمی.
 */
object Avatars {
    data class Face(val id: Int, val res: Int, val label: String)

    val all = listOf(
        Face(1, R.drawable.avatar_1, "آوا"),
        Face(2, R.drawable.avatar_2, "کاوه"),
        Face(3, R.drawable.avatar_3, "نازنین"),
        Face(4, R.drawable.avatar_4, "سهیل"),
        Face(5, R.drawable.avatar_5, "پریسا"),
        Face(6, R.drawable.avatar_6, "بهرام"),
        Face(7, R.drawable.avatar_7, "ترانه"),
        Face(8, R.drawable.avatar_8, "آرش"),
    )

    /**
     * چهره‌ای که باید نشان داده شود.
     *
     * اگر کاربر انتخاب نکرده باشد (شناسه صفر)، از روی کلید ثابتی مثل
     * شماره‌اش یکی برداشته می‌شود. مهم این است که **ثابت** بماند: اگر
     * تصادفی انتخاب می‌کردیم، کاربر هر بار باز کردن اپ چهره‌ای دیگر
     * می‌دید و این خودش شبیه باگ است.
     */
    fun resolve(selectedId: Int, seed: String?): Face {
        all.firstOrNull { it.id == selectedId }?.let { return it }
        val h = (seed ?: "").hashCode()
        return all[((h % all.size) + all.size) % all.size]
    }
}

@Composable
fun AvatarImage(face: Avatars.Face, size: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(face.res),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .semantics { contentDescription = "چهره ${face.label}" },
    )
}

/**
 * انتخابگر چهره — دو ردیف چهارتایی.
 *
 * چهره‌ی انتخاب‌شده حلقه‌ی رنگی می‌گیرد و نه فقط تیک گوشه: در شبکه‌ای
 * از تصاویر رنگی، تیک کوچک گم می‌شود.
 */
@Composable
fun AvatarPicker(
    current: Int,
    onPick: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("چهره‌ات را انتخاب کن") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Avatars.all.chunked(4).forEach { row ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        row.forEach { face ->
                            val selected = face.id == current
                            Column(
                                Modifier
                                    .weight(1f)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable { onPick(face.id) }
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Box(
                                    Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(scheme.surfaceVariant)
                                        .border(
                                            width = if (selected) 3.dp else 0.dp,
                                            color = if (selected) scheme.primary else scheme.surfaceVariant,
                                            shape = CircleShape,
                                        )
                                        .padding(if (selected) 3.dp else 0.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    AvatarImage(face, size = if (selected) 58 else 64)
                                }
                                Text(
                                    face.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) scheme.primary else scheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("بستن") } },
    )
}
