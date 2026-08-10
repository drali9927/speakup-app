package ir.speakup.app.ui.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.app.Activity
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.data.remote.RemotePlan
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.common.DuoButton
import ir.speakup.app.ui.common.DuoButtonBox
import ir.speakup.app.ui.common.DuoButtonStyle

private val Green = Color(0xFF2E7D32)

private data class Perk(val icon: ImageVector, val label: String)

private val PERKS = listOf(
    Perk(Icons.Default.School, "همه درس‌ها"),
    Perk(Icons.Default.Book, "کتاب‌های داستان"),
    Perk(Icons.Default.CloudDownload, "استفاده آفلاین"),
    Perk(Icons.Default.Verified, "بدون محدودیت تمرین"),
)

@Composable
fun PaywallScreen(
    onClose: () -> Unit,
    onPurchased: () -> Unit,
    vm: PaywallViewModel = hiltViewModel(),
) {
    val s by vm.state.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity

    // فعال شدن اشتراک را سرور تأیید کرده، نه اپ
    LaunchedEffect(s.purchased) { if (s.purchased) onPurchased() }

    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    ) {
        Box(Modifier.fillMaxWidth().padding(8.dp)) {
            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.Close, "بستن")
            }
        }

        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        ) {
            Text(
                "یادگیری بی‌وقفه",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.size(6.dp))
            Text(
                "با اشتراک، همه‌چیز باز می‌شود:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.size(18.dp))
            PERKS.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { p ->
                        Surface(
                            Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(p.icon, null, Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary)
                                Text(p.label, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
                Spacer(Modifier.size(12.dp))
            }

            Spacer(Modifier.size(10.dp))
            s.plans.forEachIndexed { i, plan ->
                PlanCard(plan, selected = i == s.selected) { vm.select(i) }
                Spacer(Modifier.size(10.dp))
            }

            Spacer(Modifier.size(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Verified, null, Modifier.size(18.dp), tint = Green)
                Spacer(Modifier.size(6.dp))
                Text("۷ روز ضمانت بازگشت وجه", style = MaterialTheme.typography.bodyMedium, color = Green)
            }
            Spacer(Modifier.size(10.dp))
            Text(
                "۱۰٪ مالیات بر ارزش افزوده به قیمت اضافه می‌شود.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.size(20.dp))
        }

        // پیام خطا بالای دکمه می‌نشیند، جایی که چشم کاربر بعد از زدن دکمه است
        s.message?.let {
            Text(
                it,
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }

        DuoButtonBox(
            onClick = { activity?.let(vm::purchase) },
            enabled = !s.busy && activity != null && s.plans.isNotEmpty(),
            height = 56.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        ) {
            if (s.busy) {
                CircularProgressIndicator(
                    Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text("ادامه و پرداخت", style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.size(20.dp))
    }
}

@Composable
private fun PlanCard(plan: RemotePlan, selected: Boolean, onSelect: () -> Unit) {
    val border = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onSelect)
            .border(if (selected) 2.dp else 1.dp, border, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        else MaterialTheme.colorScheme.surface,
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(22.dp).background(
                    if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    RoundedCornerShape(11.dp),
                ).border(2.dp, border, RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Icon(Icons.Default.Check, null, Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(plan.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                // قیمت ماهانه از روی مدت محاسبه می‌شود تا سرور مجبور نباشد
                // یک عدد مشتق‌شده را هم بفرستد و همگام نگهش دارد. رند شده
                // به هزار تومان، چون «۱۳۱,۶۶۶ تومان» شبیه قیمت نیست.
                val sub = if (plan.days >= 60) {
                    val perMonth = (plan.priceRial / 10 * 30 / plan.days + 500) / 1000 * 1000
                    "ماهانه ${toman(perMonth)}"
                } else plan.note

                // برچسب و زیرنویس زیر عنوان می‌نشینند و نه کنارش: وقتی کنار
                // عنوان بودند عرض ستون را می‌بلعیدند و روی قیمت می‌افتادند.
                // هرکدام هم خط خودش را دارد، چون کنار هم «ماهانه …» بریده می‌شد.
                sub?.let {
                    Spacer(Modifier.size(2.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
                plan.badge?.let {
                    Spacer(Modifier.size(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondary,
                    ) {
                        Text(
                            it,
                            Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondary,
                            maxLines = 1,
                        )
                    }
                }
            }

            Spacer(Modifier.size(8.dp))
            Text(
                toman(plan.priceRial / 10),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

/** جداکننده هزارگان با ارقام فارسی — «۷۹۰,۰۰۰ تومان» */
private fun toman(v: Long): String {
    val grouped = v.toString().reversed().chunked(3).joinToString(",").reversed()
    return grouped.map { if (it.isDigit()) it.digitToInt().toPersianDigits() else it.toString() }
        .joinToString("") + " تومان"
}
