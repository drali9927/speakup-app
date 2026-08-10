package ir.speakup.app.ui.common

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * شکل شش‌ضلعی نوک‌بالا — برای شماره درس، به‌جای مربع گرد ساده.
 *
 * رقیب از همین شکل برای گره‌های مسیر درس استفاده می‌کند و بلافاصله
 * حس «برنامه یادگیری با پیشرفت مرحله‌ای» را منتقل می‌کند؛ مربع ساده
 * این حس را نمی‌دهد. سند بازبینی بصری — رده «فهرست دروس».
 */
class HexagonShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val radius = min(size.width, size.height) / 2f
        val cx = size.width / 2f
        val cy = size.height / 2f
        val path = Path().apply {
            for (i in 0..5) {
                // زاویه شروع از بالا (-۹۰ درجه) تا نوک شش‌ضلعی رو به بالا باشد
                val angle = (PI / 180.0) * (60 * i - 90)
                val x = cx + radius * cos(angle).toFloat()
                val y = cy + radius * sin(angle).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        return Outline.Generic(path)
    }
}
