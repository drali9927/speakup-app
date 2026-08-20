package ir.speakup.app.ui.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RuleFolder
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import ir.speakup.app.data.local.ActivityEntity
import ir.speakup.app.data.local.LessonEntity
import ir.speakup.app.data.model.ActivityType
import ir.speakup.app.ui.common.StreakBadge
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.theme.ltr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    onBack: () -> Unit,
    onOpenActivity: (ActivityEntity) -> Unit,
    vm: LessonViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val lesson = state.lesson

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // «درس ۳» و نه «Lesson 3»: این یک برچسب است و نه
                    // محتوای درس. جمله انگلیسی زیرش می‌ماند، چون آن
                    // خودش همان چیزی است که قرار است یاد گرفته شود.
                    Column {
                        Text(
                            "درس ${(lesson?.number ?: 0).toPersianDigits()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        if (lesson != null) {
                            Text(lesson.titleEn, style = MaterialTheme.typography.bodyMedium.ltr())
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                actions = { StreakBadge(modifier = Modifier.padding(end = 12.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            if (state.loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // موضوع گرامر درس، بالای فهرست بخش‌ها.
                    //
                    // در فهرست دروس جایش را به «ماموریت درس» داد، چون
                    // آن‌جا کاربر دارد انتخاب می‌کند و نتیجه برایش مهم
                    // است. این‌جا وارد شده و سؤالش عوض شده: «قرار است چه
                    // ساختاری یاد بگیرم؟» — و همین‌جا باید جوابش باشد.
                    if (lesson != null && lesson.grammarTopicFa.isNotBlank()) {
                        item {
                            Surface(
                                Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            ) {
                                Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Text(
                                        "در این درس یاد می‌گیری",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        lesson.grammarTopicFa,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                    items(state.sections, key = { it.id }) { card ->
                        SectionCardView(card, state.artFiles) { act -> onOpenActivity(act) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCardView(
    card: SectionCard,
    artFiles: List<String>,
    onOpenActivity: (ActivityEntity) -> Unit,
) {
    val next = card.nextActivity
    val ctx = LocalContext.current
    // تصویر زمینه هر کارت با گامِ متفاوت بر اساس نوع بخش، تا شش کارت یک
    // درس تصویر یکسان نگیرند. فهرست از محتوا می‌آید نه از assets: بعد از
    // انتقال تصاویر به سرور، فقط درس اول در assets ماند و بقیه دروس
    // کارت‌های بی‌تصویر می‌گرفتند.
    val artFile = remember(artFiles, card.type) {
        artFiles.getOrNull((card.type.ordinal * 3 + 1).mod(artFiles.size.coerceAtLeast(1)))
    }
    val surface = MaterialTheme.colorScheme.surface

    // کارت با مرز ضخیم و لبه زیرین تیره، نه سایه محو.
    //
    // سایه ملایم متریال روی پس‌زمینه سفید تقریباً دیده نمی‌شد و کارت‌ها
    // مثل تکه‌های متن روی صفحه شناور بودند. مرز دو پیکسلی و لبه زیرین،
    // هرکدام را به یک شیء مستقل و قابل زدن تبدیل می‌کند.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .clickable(enabled = next != null) { next?.let(onOpenActivity) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            if (artFile != null) {
                AsyncImage(
                    model = ir.speakup.app.data.remote.ImageSource.resolve(ctx, artFile),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    // برش از بالا تا چهره‌ها بریده نشوند
                    alignment = Alignment.TopCenter,
                )
                // محو تصویر به رنگ زمینه سمت شروع متن — کارت خوانا می‌ماند، تصویر هم دیده می‌شود
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.horizontalGradient(
                            0f to Color.Transparent,
                            0.62f to surface.copy(alpha = 0.55f),
                            1f to surface,
                        )
                    )
                )
            }

            Column(Modifier.fillMaxSize().padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // فارسی بالا، انگلیسی زیرش.
                    //
                    // پیش‌تر برعکس بود: «Vocabulary» درشت و بولد، و
                    // «واژگان» ریز و کم‌رنگ زیرش. در محصولی که تمام
                    // مخاطبش فارسی‌زبان است، این وارونه است — نام بخش
                    // یک برچسب ناوبری است و باید به زبان کاربر خوانده
                    // شود، نه به زبانی که هنوز یاد نگرفته.
                    //
                    // انگلیسی حذف نشد چون خودش ارزش دارد: کاربر همان
                    // واژه‌ها را در اپ‌های دیگر و در آزمون‌ها می‌بیند.
                    Column(Modifier.weight(1f)) {
                        Text(
                            card.type.titleFa,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            card.type.titleEn,
                            style = MaterialTheme.typography.bodyMedium.ltr(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (card.isComplete) {
                        Box(
                            Modifier.size(28.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                    Text(
                        "${card.estimatedMinutes}´",
                        style = MaterialTheme.typography.bodyMedium.ltr(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }

                if (card.activities.isNotEmpty()) {
                    // نوار قطعه‌قطعه، به‌جای حلقه‌های آیکن‌دار.
                    //
                    // حلقه‌ها ابهام داشتند: خواندنشان به رمزگشاییِ آیکن و
                    // تشخیصِ «حلقه دارد یا ندارد» نیاز داشت، و در بخش
                    // نه‌گامی هم جا نمی‌شدند و به شمارنده «+۴» می‌رسیدند.
                    // نوار، پیشرفت را بی‌واسطه نشان می‌دهد، هر تعداد گام را
                    // جا می‌دهد، و چیزی برای رمزگشایی ندارد.
                    val steps = card.steps
                    Row(
                        Modifier.fillMaxWidth().padding(top = 12.dp, end = 40.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        steps.forEach { st ->
                            Box(
                                Modifier.weight(1f).height(7.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    // سه پله‌ی روشن: سبزِ پر (انجام‌شده)،
                                    // سبزِ کم‌رنگ (همین حالا اینجایی)، خاکستری
                                    // (نرسیده). رنگ اصلی اپ خودش سبز است، پس
                                    // «جاری» را با پررنگی جدا نمی‌شود کرد و
                                    // باید با شفافیت جدا شود.
                                    .background(
                                        when {
                                            st.done -> ir.speakup.app.ui.theme.DuoGreen
                                            st.current ->
                                                ir.speakup.app.ui.theme.DuoGreen.copy(alpha = 0.38f)
                                            else -> MaterialTheme.colorScheme.outlineVariant
                                        }
                                    )
                            )
                        }
                    }

                    // شمارش، و مهم‌تر از آن **نامِ** کاری که بعد می‌آید —
                    // با آیکن همان گام، تا «جفت‌یابی» فقط یک واژه نماند.
                    val total = card.stepCount
                    val next = steps.firstOrNull { it.current }?.type
                    Row(
                        Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (next != null && !card.isComplete) {
                            Icon(
                                iconFor(next),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Text(
                            when {
                                card.isComplete -> "انجام شد"
                                next == null -> "${total.toPersianDigits()} گام"
                                card.completedCount == 0 ->
                                    "${total.toPersianDigits()} گام · شروع: ${labelFor(next)}"
                                else ->
                                    "گام ${(card.completedCount + 1).toPersianDigits()} از " +
                                        "${total.toPersianDigits()} · ${labelFor(next)}"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

private const val MAX_BADGES = 5

private fun iconFor(type: ActivityType): ImageVector = when (type) {
    ActivityType.TEACHING -> Icons.AutoMirrored.Filled.MenuBook
    ActivityType.FLASHCARD -> Icons.Default.Style
    ActivityType.MULTIPLE_CHOICE -> Icons.Default.RuleFolder
    ActivityType.MATCHING -> Icons.AutoMirrored.Filled.CompareArrows
    ActivityType.DIALOGUE -> Icons.AutoMirrored.Filled.Chat
    ActivityType.WORD_MATCH -> Icons.Default.Extension
    ActivityType.STORY -> Icons.AutoMirrored.Filled.MenuBook
    ActivityType.FILL_BLANK -> Icons.Default.Edit
    ActivityType.FREE_TEXT -> Icons.AutoMirrored.Filled.Send
    ActivityType.REORDER -> Icons.Default.SwapVert
    ActivityType.TRANSLATE_TO_EN -> Icons.Default.Translate
    ActivityType.SPEAKING -> Icons.Default.Mic
    ActivityType.LISTENING -> Icons.Default.Headphones
}

/**
 * نشان دایره‌ای کوچک نوع فعالیت — آیکون به‌جای متن.
 * چگالی اطلاعات کارت را بالا می‌برد بدون اینکه شلوغ شود؛ الگوی رقیب.
 */
/**
 * نام فارسیِ نوع فعالیت.
 *
 * آیکن تنها کافی نبود: کاربر می‌دید کارتی هست اما نمی‌دانست «جفت‌یابی»
 * یعنی چه کاری. نام، غافلگیریِ ورود به فعالیت را برمی‌دارد.
 */
private fun labelFor(type: ActivityType): String = when (type) {
    ActivityType.TEACHING -> "آموزش"
    ActivityType.FLASHCARD -> "کارت واژه"
    ActivityType.WORD_MATCH -> "جفت‌یابی"
    ActivityType.MULTIPLE_CHOICE -> "چندگزینه‌ای"
    ActivityType.MATCHING -> "تطبیق"
    ActivityType.DIALOGUE -> "مکالمه"
    ActivityType.STORY -> "داستانک"
    ActivityType.FILL_BLANK -> "جای خالی"
    ActivityType.FREE_TEXT -> "پاسخ کوتاه"
    ActivityType.REORDER -> "مرتب‌سازی"
    ActivityType.TRANSLATE_TO_EN -> "ترجمه"
    ActivityType.SPEAKING -> "گفتار"
    ActivityType.LISTENING -> "شنیدار"
}
