package ir.speakup.app.ui.placement

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.personalize.PersonalizeViewModel
import ir.speakup.app.ui.theme.ltr

private val Green = Color(0xFF58CC02)
private val Red = Color(0xFFD9483B)

/**
 * آزمون تعیین سطح.
 *
 * سه چیز که عمداً این‌طور است:
 *
 * **۱. همیشه قابل رد کردن.** آزمون اجباری در نخستین دقیقه استفاده، خودش
 * یک دیوار است. کسی که حوصله ندارد باید بتواند رد کند و شروع کند.
 *
 * **۲. پاسخ درست بعد از هر پرسش نشان داده می‌شود، با توضیح فارسی.**
 * آزمونی که فقط نمره می‌دهد، وقتِ تلف‌شده است؛ این‌طور کاربر حتی اگر
 * سطحش پایین باشد چیزی یاد گرفته و حس بدی از اپ نمی‌گیرد.
 *
 * **۳. نوار پیشرفت طول واقعی را نشان نمی‌دهد**، چون آزمون ممکن است زود
 * تمام شود. نشان دادن «۳ از ۱۲» به کسی که در پرسش چهارم تمام می‌کند،
 * دروغ نیست ولی بی‌خود نگرانش می‌کند.
 */
@Composable
fun PlacementScreen(
    onDone: () -> Unit,
    vm: PlacementViewModel = hiltViewModel(),
) {
    val s by vm.state.collectAsStateWithLifecycle()
    val scheme = MaterialTheme.colorScheme

    if (s.loading) {
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        return
    }

    if (s.finished) {
        Result(
            level = s.suggested,
            goalXp = s.goalXp,
            motive = s.motive,
            onStart = { vm.accept(onDone) },
        )
        return
    }

    // اگر فایل پرسش‌ها نیامده باشد، آزمون رد می‌شود — ولی داخل
    // LaunchedEffect و نه در خودِ composition. فراخوانی مستقیم، با هر
    // ترکیب دوباره تکرار می‌شد و می‌توانست چند بار پشت‌سرهم ناوبری کند.
    val q = s.current
    if (q == null) {
        LaunchedEffect(Unit) { vm.skip(onDone) }
        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        return
    }

    // بدون این دو، عنوان زیر نوار وضعیت می‌رود و دکمه «ادامه» پشت
    // نوار ناوبری گم می‌شود — روی همین گوشی دیده شد.
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "بیایید ببینیم از کجا شروع کنی",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            TextButton(onClick = { vm.skip(onDone) }) { Text("رد کردن") }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "چند سؤال کوتاه. هر جا سخت شد، همان‌جا تمام می‌شود.",
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { (s.index + 1f) / s.total },
            modifier = Modifier.fillMaxWidth().height(8.dp),
        )

        Spacer(Modifier.height(28.dp))
        Text(
            q.text,
            style = MaterialTheme.typography.headlineSmall.ltr(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))
        q.options.forEachIndexed { i, opt ->
            val picked = s.picked
            val isAnswer = i == q.answer
            val bg = when {
                picked == null -> scheme.surface
                isAnswer -> Green.copy(alpha = 0.14f)
                i == picked -> Red.copy(alpha = 0.12f)
                else -> scheme.surface
            }
            val border = when {
                picked == null -> scheme.outlineVariant
                isAnswer -> Green
                i == picked -> Red
                else -> scheme.outlineVariant
            }
            Surface(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .border(2.dp, border, RoundedCornerShape(14.dp))
                    .clickable(enabled = picked == null) { vm.pick(i) },
                shape = RoundedCornerShape(14.dp),
                color = bg,
            ) {
                Text(
                    opt,
                    style = MaterialTheme.typography.titleMedium.ltr(),
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        // توضیح فقط بعد از پاسخ. پیش از آن، خودش جواب را لو می‌دهد.
        if (s.picked != null && q.explanationFa.isNotBlank()) {
            Surface(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = scheme.surfaceVariant,
            ) {
                Text(
                    q.explanationFa,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp),
                )
            }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = { vm.next() },
            enabled = s.picked != null,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
        ) { Text("ادامه", style = MaterialTheme.typography.titleMedium) }
    }
}

@Composable
private fun Result(
    level: String?,
    goalXp: Int,
    motive: String?,
    onStart: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val titleFa = LEVEL_FA[level] ?: "مقدماتی ۱"

    // برآورد هفته‌ها: هر سطح حدود ۷ ساعت، و هدف روزانه دقیقه‌های روز را
    // تعیین می‌کند. عدد گردشده است و عمداً «حدود» نوشته می‌شود — وعده
    // دقیق دادن درباره یادگیری زبان، وعده‌ای است که نمی‌شود پایش ایستاد.
    val minutesPerDay = when {
        goalXp <= 20 -> 5
        goalXp <= 50 -> 10
        else -> 20
    }
    val weeks = ((LEVELS_LEFT[level] ?: 4) * 7 * 60 / minutesPerDay / 7).coerceAtLeast(4)

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(96.dp).background(Green.copy(alpha = 0.14f), RoundedCornerShape(48.dp)),
            contentAlignment = Alignment.Center,
        ) { Text("🎯", style = MaterialTheme.typography.displaySmall) }

        Spacer(Modifier.height(20.dp))
        Text("مسیر تو", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))
        Text(
            titleFa,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Green,
        )

        Spacer(Modifier.height(22.dp))
        // جمله‌ای که همه‌چیز را به هم وصل می‌کند: سطح، وقتی که خودش
        // گفته، و دلیلی که خودش گفته. این همان «برای من ساخته شده» است.
        Text(
            "با روزی ${minutesPerDay.toPersianDigits()} دقیقه، تا حدود " +
                "${weeks.toPersianDigits()} هفته دیگر می‌توانی " +
                PersonalizeViewModel.motiveLine(motive) + ".",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(10.dp))
        Text(
            "هر وقت خواستی می‌توانی از بالای صفحه دروس سطح را عوض کنی.",
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
        ) { Text("بزن بریم", style = MaterialTheme.typography.titleMedium) }
    }
}

/** چند سطح تا پایان محتوا مانده — برای برآورد هفته‌ها */
private val LEVELS_LEFT = mapOf("A1" to 4, "A2" to 3, "B1" to 2, "B2" to 1)

private val LEVEL_FA = mapOf(
    "A1" to "مقدماتی ۱",
    "A2" to "مقدماتی ۲",
    "B1" to "متوسط ۱",
    "B2" to "متوسط ۲",
)
