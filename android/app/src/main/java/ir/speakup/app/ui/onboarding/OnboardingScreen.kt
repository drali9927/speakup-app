package ir.speakup.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ir.speakup.app.ui.common.DuoButton

private data class Slide(val image: String, val title: String, val body: String)

/**
 * سه صفحه معرفی.
 *
 * ترتیب پیام‌ها عمدی است و از تحلیل رقیب آمده (سند ۰۱ بخش ۱.۱):
 * اعتمادسازی → رفع مانع اصلی (وقت و سختی) → شرطی‌سازی برای زنجیره مطالعه.
 * صفحه سوم کاربر را پیش از ورود برای قلاب نگهداشت آماده می‌کند.
 */
private val SLIDES = listOf(
    Slide(
        "a1_l01_w01_hello.webp",
        "انگلیسی، این بار تا آخر",
        "درس‌های کوتاه، تمرین‌های واقعی، و مسیری که رهایش نمی‌کنی.",
    ),
    Slide(
        "a1_l01_w10_student.webp",
        "روزی ده دقیقه کافی است",
        "هر درس از واژه تا گرامر و گفتار را می‌بری — بدون اینکه وقت زیادی بگیرد.",
    ),
    Slide(
        "a1_l09_w15_together.webp",
        "استمرار، راز کار است",
        "هر روز یک درس تمام کن و زنجیره‌ات را نگه دار. اگر یک روز جا ماندی، فریزش می‌کنیم.",
    ),
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pager = rememberPagerState { SLIDES.size }
    val scope = rememberCoroutineScope()
    val isLast = pager.currentPage == SLIDES.lastIndex

    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
    ) {
        Box(Modifier.fillMaxWidth().padding(8.dp)) {
            TextButton(onClick = onFinish, modifier = Modifier.align(Alignment.CenterEnd)) {
                Text("رد کردن", style = MaterialTheme.typography.bodyLarge)
            }
        }

        HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { page ->
            val s = SLIDES[page]
            Column(
                Modifier.fillMaxSize().padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AsyncImage(
                    model = ir.speakup.app.data.remote.ImageSource.resolve(LocalContext.current, s.image),
                    contentDescription = null,
                    modifier = Modifier.height(260.dp).aspectRatio(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.size(36.dp))
                Text(
                    s.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.size(14.dp))
                Text(
                    s.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(SLIDES.size) { i ->
                Box(
                    Modifier.padding(horizontal = 4.dp).size(if (i == pager.currentPage) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (i == pager.currentPage) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }

        DuoButton(
            text = if (isLast) "شروع کن" else "بعدی",
            onClick = {
                if (isLast) onFinish()
                else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
            },
            height = 56.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        )
        Spacer(Modifier.size(24.dp))
    }
}
