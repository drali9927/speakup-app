package ir.speakup.app.ui.vocab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.domain.SpeechService
import ir.speakup.app.domain.toPersianDigits
import kotlinx.coroutines.launch

private val Green = Color(0xFF00897B)

/**
 * نمای واژگان — فهرست کامل واژه‌های فعالیت، هر لحظه در دسترس.
 *
 * بدون این، کاربر داخل یک دسته کارت خطی حبس می‌شود و نمی‌داند کجای کار است.
 * کارت بعدی عمداً از لبه پیداست تا سوایپ‌پذیر بودن خودش را نشان دهد.
 */
@Composable
fun WordListSheet(
    items: List<ActivityItemEntity>,
    startIndex: Int,
    inLeitner: Set<String>,
    speechStatus: SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onUnavailable: () -> Unit,
    onAddToLeitner: (ActivityItemEntity) -> Unit,
    onClose: () -> Unit,
) {
    if (items.isEmpty()) return
    val pager = rememberPagerState(initialPage = startIndex.coerceIn(0, items.lastIndex)) { items.size }
    val scope = rememberCoroutineScope()

    // تصویر کارت‌های بعدی از سرور می‌آید؛ جلوتر می‌گیریمشان تا سوایپ
    // روی شبکه کند هم بدون مکث بماند.
    val ctx = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(pager.currentPage, items) {
        ir.speakup.app.data.remote.ImagePrefetcher.prefetch(
            ctx,
            items.drop(pager.currentPage + 1).map { it.imageFile },
        )
    }

    Column(
        Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // نوار بالا
        Box(Modifier.fillMaxWidth().padding(8.dp)) {
            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.Close, "بستن")
            }
            Text(
                "نمای واژگان",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        // راهنمای سوایپ
        Box(
            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "برای واژه‌های بعدی، صفحه را به چپ یا راست بکش.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        HorizontalPager(
            state = pager,
            modifier = Modifier.weight(1f),
            // لبه کارت بعدی پیدا باشد تا سوایپ‌پذیری خودش را نشان دهد
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 12.dp,
        ) { page ->
            Card(
                Modifier.fillMaxSize().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                WordDetailCard(
                    item = items[page],
                    speechStatus = speechStatus,
                    speakingId = speakingId,
                    onSpeak = onSpeak,
                    onUnavailable = onUnavailable,
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                )
            }
        }

        // شمارنده و افزودن به لایتنر
        val current = items[pager.currentPage]
        val added = current.targetWord?.lowercase() in inLeitner
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { scope.launch { pager.animateScrollToPage((pager.currentPage - 1).coerceAtLeast(0)) } },
                    enabled = pager.currentPage > 0,
                ) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "قبلی") }

                Text(
                    "${(pager.currentPage + 1).toPersianDigits()} / ${items.size.toPersianDigits()}",
                    style = MaterialTheme.typography.bodyLarge,
                )

                IconButton(
                    onClick = { scope.launch { pager.animateScrollToPage((pager.currentPage + 1).coerceAtMost(items.lastIndex)) } },
                    enabled = pager.currentPage < items.lastIndex,
                ) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "بعدی") }
            }

            Box(Modifier.weight(1f))

            Button(
                onClick = { onAddToLeitner(current) },
                enabled = !added,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    disabledContainerColor = Green.copy(alpha = 0.35f),
                ),
                modifier = Modifier.heightIn(min = 46.dp),
            ) {
                Icon(
                    if (added) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    if (added) "در لایتنر" else "افزودن به لایتنر",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }
    }
}
