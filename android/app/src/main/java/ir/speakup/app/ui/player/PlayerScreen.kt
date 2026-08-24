package ir.speakup.app.ui.player

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.data.local.ActivityItemEntity
import ir.speakup.app.data.local.optionKey
import ir.speakup.app.data.model.ActivityType
import ir.speakup.app.domain.RecognitionState
import ir.speakup.app.domain.SpeechMatcher
import ir.speakup.app.domain.SpeechService
import ir.speakup.app.ui.common.MicPermissionPrompt
import ir.speakup.app.ui.common.DuoButton
import ir.speakup.app.ui.common.DuoButtonStyle
import ir.speakup.app.ui.common.SpeakButton
import ir.speakup.app.ui.common.SpeechRecognizerUnavailableDialog
import ir.speakup.app.ui.common.SpeechUnavailableDialog
import ir.speakup.app.ui.common.hasRecordAudioPermission
import ir.speakup.app.ui.conversation.ConversationScreen
import ir.speakup.app.ui.match.WordMatchScreen
import ir.speakup.app.ui.teaching.TeachingScreen
import ir.speakup.app.ui.vocab.WordListSheet
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.theme.autoDir
import ir.speakup.app.ui.theme.ltr
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.border
import ir.speakup.app.ui.story.StoryScreen

private val Green = Color(0xFF2E7D32)
private val Red = Color(0xFFC62828)

@Composable
fun PlayerScreen(
    onClose: () -> Unit,
    onNext: (String, Int, Int) -> Unit,
    vm: PlayerViewModel = hiltViewModel(),
) {
    val s by vm.state.collectAsStateWithLifecycle()
    val speechStatus by vm.speech.status.collectAsStateWithLifecycle()
    val speakingId by vm.speech.speakingId.collectAsStateWithLifecycle()
    var showSpeechHelp by remember { mutableStateOf(false) }
    // راهنما با ضربه باز می‌شود و با هر آیتم تازه بسته. عمداً خودکار نشان
    // داده نمی‌شود: کاربر اول باید تلاش کند، وگرنه راهنما جای فکر کردن
    // را می‌گیرد. دکمه‌اش پیش‌تر ساخته شده بود ولی به هیچ‌جا وصل نبود.
    var showHint by remember(s.current?.id) { mutableStateOf(false) }

    // ---------- گفتار: مجوز میکروفون ----------
    val ctx = LocalContext.current
    var micGranted by remember { mutableStateOf(hasRecordAudioPermission(ctx)) }
    var primingSkipped by remember { mutableStateOf(false) }
    var showRecognizerUnavailable by remember { mutableStateOf(false) }
    val recognizerAvailable = remember { vm.recognizer.isAvailable() }
    val micLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        micGranted = granted
        primingSkipped = true   // نتیجه هرچه بود، دیگر صفحه توضیح دوباره نشان داده نمی‌شود
    }
    // آیا الان باید صفحه توضیح پیش از دیالوگ سیستم را نشان دهیم؟
    val showMicPriming = s.type == ActivityType.SPEAKING && !s.loading && !micGranted && !primingSkipped

    /**
     * تشخیص گفتار روی این آیتم شکست خورد و راه دیگری نمانده.
     *
     * چرا لازم شد: دکمه پایین صفحه تا رسیدن رونوشت **غیرفعال** است. اگر
     * موتور همیشه خطا بدهد، کاربر هیچ راهی جز رها کردن کل درس ندارد — و
     * روی گوشی واقعی دقیقاً همین دیده شد: «مشکل شبکه» و صفحهٔ قفل.
     *
     * موتور آنلاین گوگل در ایران معمولاً در دسترس نیست و بستهٔ آفلاین
     * انگلیسی هم روی بیشتر گوشی‌ها نصب نیست؛ یعنی این بن‌بست حالت نادر
     * نیست، حالت رایج است. با این پرچم، تمرین به شکل «بشنو و بنویس»
     * برمی‌گردد که از قبل ساخته شده بود و کاربر می‌تواند ادامه دهد.
     *
     * با هر آیتم تازه صفر می‌شود تا یک شکست، بقیهٔ درس را تایپی نکند.
     */
    var micFailed by remember(s.current?.id) { mutableStateOf(false) }

    // آیا کاربر واقعاً از میکروفون استفاده می‌کند (مجوز هست، موتور نصب است، و هنوز شکست نخورده)
    val usingMic = s.type == ActivityType.SPEAKING && micGranted && recognizerAvailable && !micFailed
    // تا وقتی رونوشت گفتار نرسیده، دکمه پایین صفحه کاری برای انجام دادن ندارد
    val awaitingMicResult = usingMic && s.verdict == null

    if (showSpeechHelp) {
        SpeechUnavailableDialog(
            status = speechStatus,
            onDismiss = { showSpeechHelp = false },
            onRetry = { vm.speech.retry() },
        )
    }
    if (showRecognizerUnavailable) {
        SpeechRecognizerUnavailableDialog(onDismiss = { showRecognizerUnavailable = false })
    }

    if (s.showWordList) {
        WordListSheet(
            items = s.items,
            startIndex = s.index,
            inLeitner = s.inLeitner,
            speechStatus = speechStatus,
            speakingId = speakingId,
            onSpeak = { t, id -> vm.speech.speak(t, id) },
            onUnavailable = { showSpeechHelp = true },
            onAddToLeitner = vm::addToLeitner,
            onClose = { vm.speech.stop(); vm.closeWordList() },
        )
        return
    }

    // بازی جفت‌یابی صفحه خودش را دارد
    if (s.type == ActivityType.WORD_MATCH && !s.finished && s.items.isNotEmpty()) {
        LaunchedEffect(Unit) { vm.startMatchIfNeeded() }
        WordMatchScreen(
            tiles = s.matchTiles,
            selectedId = s.matchSelected,
            wrongPair = s.matchWrongPair,
            roundIndex = s.matchRound,
            roundCount = s.matchRoundCount,
            onTap = vm::onMatchTap,
            onClose = { vm.speech.stop(); onClose() },
        )
        return
    }

    // مکالمه صفحه گفتگو دارد، نه تمرین
    if (s.type == ActivityType.DIALOGUE && !s.finished && s.items.isNotEmpty()) {
        ConversationScreen(
            items = s.items,
            playingIndex = s.dialogueLine,
            isPlaying = s.dialoguePlaying,
            showTranslation = s.showTranslation,
            onPlayToggle = vm::toggleDialogue,
            onPrev = vm::dialoguePrev,
            onNext = vm::dialogueNext,
            onLineTap = vm::playLine,
            onToggleTranslation = vm::toggleTranslation,
            onFinish = vm::finishDialogue,
            onClose = { vm.speech.stop(); onClose() },
        )
        return
    }

    // داستانک هم صفحه خودش را دارد — خواندنی و پیوسته، نه تمرین.
    // ساز و کار پخش همان مکالمه است (بند به بند، با هایلایت)، پس همان
    // متدهای ViewModel استفاده می‌شوند و منطق تازه‌ای اضافه نمی‌شود.
    if (s.type == ActivityType.STORY && !s.finished && s.items.isNotEmpty()) {
        StoryScreen(
            title = s.title,
            items = s.items,
            playingIndex = s.dialogueLine ?: -1,
            isPlaying = s.dialoguePlaying,
            showTranslation = s.showTranslation,
            onPlayToggle = vm::toggleDialogue,
            onLineTap = vm::playLine,
            onToggleTranslation = vm::toggleTranslation,
            onFinish = vm::finishDialogue,
            onClose = { vm.speech.stop(); onClose() },
        )
        return
    }

    // بخش آموزش صفحه خودش را دارد — پیوسته و بدون «بعدی» تکراری
    if (s.type == ActivityType.TEACHING && !s.finished && s.items.isNotEmpty()) {
        TeachingScreen(
            items = s.items,
            isSpeaking = s.narrating,
            speechReady = speechStatus == SpeechService.Status.Ready,
            rate = s.narrationRate,
            onPlayToggle = vm::toggleNarration,
            onRateChange = vm::cycleNarrationRate,
            onFinish = { vm.speech.stop(); vm.finishTeaching() },
            onClose = { vm.speech.stop(); onClose() },
            onCheckAnswered = vm::onCheckAnswered,
        )
        return
    }

    // تمرینِ تک‌سوالی وسط بخش، صفحه پایانِ تمام‌صفحه نمی‌گیرد.
    //
    // بخش گرامر نُه فعالیت دارد که **هر نُه تا تک‌آیتمی‌اند**. با صفحه پایان
    // برای هرکدام، کاربر در یک بخش دودقیقه‌ای هفت بار «آفرین! ۱ از ۱ پاسخ
    // درست» تمام‌صفحه می‌دید. جشنی که پشت سر هم تکرار شود دیگر جشن نیست؛
    // فرسایش است. بازخوردِ درست/غلط را همان‌جا زیر سوال دیده و همین کافی
    // است — جشن برای پایان بخش می‌ماند.
    val skipFinish = s.finished && s.nextInSection != null && s.items.size <= 2
    // آمار بخش با هر پرش جمع می‌شود. فقط فعالیت‌های نمره‌دار شمرده می‌شوند:
    // کارت واژه و آموزش سوالی نمی‌پرسند و «۰ از ۱۰» برایشان بی‌معناست.
    val tallyCorrect = s.sectionCorrect + if (s.type.isScored) s.correctCount else 0
    val tallyTotal = s.sectionTotal + if (s.type.isScored) s.items.size else 0
    LaunchedEffect(skipFinish) {
        if (skipFinish) s.nextInSection?.let {
            vm.speech.stop()
            onNext(it, tallyCorrect, tallyTotal)
        }
    }

    if (s.finished && !skipFinish) {
        FinishedView(
            // در پایان بخش، آمارِ **کل بخش** — نه فقط آخرین تمرین.
            // بدون این، بخش گرامر که هفت سوال دارد در پایان «۱ از ۱ پاسخ
            // درست» می‌گفت، چون آخرین تمرینش تک‌آیتمی است.
            correct = if (s.nextInSection == null) tallyCorrect else s.correctCount,
            total = if (s.nextInSection == null) tallyTotal else s.items.size,
            scored = if (s.nextInSection == null) tallyTotal > 0 else s.type.isScored,
            earnedXp = s.earnedXp,
            goalReached = s.goalReached,
            streakNote = s.streakNote,
            step = s.stepInSection,
            steps = s.stepsInSection,
            nextInSection = s.nextInSection,
            onNext = {
                s.nextInSection?.let { vm.speech.stop(); onNext(it, tallyCorrect, tallyTotal) }
            },
            onClose = onClose,
        )
        return
    }

    val hint = s.current?.hintFa

    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding()) {
        // نوار بالا: بستن + پیشرفت + راهنما
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, "بستن") }
            // نوار پیشرفت ضخیم و گرد، نه خط نازک متریال.
            //
            // این نوار تنها چیزی است که در تمرین می‌گوید «چقدر مانده» و
            // خط دو پیکسلی آن را تقریباً نامرئی می‌کرد. ضخیم بودنش
            // همچنین حس پیشروی می‌دهد: هر پاسخ درست، تکه‌ای پر می‌شود.
            Box(
                Modifier.weight(1f).padding(horizontal = 8.dp).height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(
                    Modifier.fillMaxWidth(s.progress.coerceIn(0f, 1f)).height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            if (s.type == ActivityType.FLASHCARD) {
                WordListChip(onClick = { vm.speech.stop(); vm.openWordList() })
            } else if (!hint.isNullOrBlank()) {
                IconButton(onClick = { showHint = !showHint }) {
                    Icon(
                        Icons.Default.Lightbulb,
                        if (showHint) "بستن راهنما" else "راهنما",
                        tint = if (showHint) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        if (showHint && !hint.isNullOrBlank()) {
            Surface(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            ) {
                Text(
                    hint,
                    Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        val item = s.current
        if (s.loading || item == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { Text("در حال بارگذاری…") }
            return@Column
        }

        if (showMicPriming) {
            // صفحه توضیح جای همه‌چیز دیگر را می‌گیرد — دکمه‌های خودش دارد،
            // نه چیپ راهنما و نه دکمه ثابت پایین لازم است
            MicPermissionPrompt(
                onAllow = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                onSkip = { primingSkipped = true },
                modifier = Modifier.weight(1f),
            )
            return@Column
        }

        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (s.type) {
                ActivityType.FLASHCARD -> FlashcardPager(
                    items = s.items,
                    index = s.index,
                    reveal = s.reveal,
                    speechStatus = speechStatus,
                    speakingId = speakingId,
                    onSpeak = { t, id -> vm.speech.speak(t, id) },
                    onUnavailable = { showSpeechHelp = true },
                    onPageChanged = { vm.speech.stop(); vm.goTo(it) },
                    checkOptions = s.wordCheckOptions,
                    checkPicked = s.wordCheckPicked,
                    addedToLeitner = s.addedToLeitner,
                    onPickCheck = vm::pickWordCheck,
                )
                ActivityType.TEACHING -> Teaching(item)
                ActivityType.MULTIPLE_CHOICE, ActivityType.MATCHING -> Choice(s, vm)
                ActivityType.REORDER -> Reorder(s, vm)
                ActivityType.SPEAKING -> Speaking(
                    onRecognitionFailed = { micFailed = true },
                    s = s, vm = vm, item = item, usingMic = usingMic,
                    speechStatus = speechStatus, speakingId = speakingId,
                    onSpeak = { t, id -> vm.speech.speak(t, id) },
                    onSpeechUnavailable = { showSpeechHelp = true },
                    onRecognizerUnavailable = { showRecognizerUnavailable = true },
                )
                // دیکته صفحه خودش را دارد: صفحه متن‌آزاد صورت سوال را
                // نشان می‌دهد و برای این تمرین یعنی لو رفتن پاسخ.
                ActivityType.LISTENING -> Dictation(
                    s = s, vm = vm, item = item,
                    speechStatus = speechStatus, speakingId = speakingId,
                    onSpeak = { t, id -> vm.speech.speak(t, id) },
                    onSpeechUnavailable = { showSpeechHelp = true },
                )
                // مرور واژگان: بیشتر کاشی حروف، هر سومی چندگزینه‌ای.
                //
                // کاشی‌چینی برای هر واژه، پشت سر هم، خسته‌کننده می‌شود و
                // کاربر جلسه را نیمه رها می‌کند. اما تبدیل همه‌شان به تست
                // هم تمرین را از تولیدی به تشخیصی می‌برد — یعنی همان چیزی
                // که کل محصول رویش بنا شده.
                //
                // نسبت دو به یک: ریتم عوض می‌شود، و دو سوم تمرین‌ها
                // همچنان تولید واقعی واژه‌اند.
                ActivityType.TRANSLATE_TO_EN -> when {
                    s.useKeyboard -> TextEntry(s, vm)
                    s.index % 3 == 2 && quickOptions(s).size >= 2 -> QuickChoice(s, vm)
                    else -> WordBuild(s, vm)
                }
                else -> TextEntry(s, vm)
            }

            s.verdict?.let { v ->
                Spacer(Modifier.size(20.dp))
                Feedback(v.correct, v.userAnswer, v.expected, s.addedToLeitner)
            }
        }

        // دکمه اصلی عرض‌کامل — همیشه در جای ثابت.
        //
        // بعد از پاسخ، رنگش با نتیجه عوض می‌شود: سبز برای درست، قرمز برای
        // اشتباه. کاربر نتیجه را از رنگِ همان دکمه‌ای که دستش رویش است
        // می‌فهمد، بدون اینکه چشمش را به بالای صفحه ببرد.
        DuoButton(
            text = when {
                s.type == ActivityType.FLASHCARD || s.type == ActivityType.TEACHING -> "بعدی"
                s.verdict != null -> if (s.isLast) "پایان" else "بعدی"
                awaitingMicResult -> "با میکروفون صحبت کن"
                else -> "تأیید"
            },
            onClick = {
                when {
                    s.type == ActivityType.FLASHCARD -> vm.revealNext()
                    s.type == ActivityType.TEACHING -> vm.advance()
                    s.verdict != null -> vm.advance()
                    awaitingMicResult -> { /* منتظر نتیجه میکروفون — این دکمه کاری ندارد */ }
                    else -> vm.submit()
                }
            },
            style = when {
                s.verdict?.correct == true -> DuoButtonStyle.Primary
                s.verdict != null -> DuoButtonStyle.Danger
                else -> DuoButtonStyle.Primary
            },
            // در گام آزمون واژه، «بعدی» تا پاسخ ندادن کاری ندارد
            enabled = !awaitingMicResult &&
                !(s.type == ActivityType.FLASHCARD && s.reveal == RevealStep.CHECK && s.wordCheckPicked == null),
            height = 56.dp,
            modifier = Modifier.fillMaxWidth().padding(20.dp),
        )
    }
}

/** چیپ «نمای واژگان» — همیشه در دسترس داخل کارت واژه */
@Composable
private fun WordListChip(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(Icons.Default.ViewCarousel, null, Modifier.size(17.dp))
            Text("نمای واژگان", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * کارت‌های واژه با سوایپ.
 * حرکت بین واژه‌ها فقط با دکمه، خشک و کند حس می‌شود — سوایپ همان چیزی است
 * که تجربه رقیب را روان‌تر می‌کند.
 */
@Composable
private fun FlashcardPager(
    items: List<ActivityItemEntity>,
    index: Int,
    reveal: RevealStep,
    speechStatus: SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onUnavailable: () -> Unit,
    onPageChanged: (Int) -> Unit,
    checkOptions: List<String>,
    checkPicked: String?,
    addedToLeitner: Boolean,
    onPickCheck: (String) -> Unit,
) {
    val pager = rememberPagerState(initialPage = index) { items.size }

    // سوایپ کاربر → به‌روزرسانی وضعیت
    LaunchedEffect(pager.settledPage) {
        if (pager.settledPage != index) onPageChanged(pager.settledPage)
    }
    // دکمه «بعدی» → حرکت انیمیشنی pager
    LaunchedEffect(index) {
        if (pager.currentPage != index) pager.animateScrollToPage(index)
    }

    // تصویر کارت‌های بعدی از سرور می‌آید؛ همین حالا که کاربر مشغول این
    // کارت است بگیریمشان، وگرنه بعد از «بعدی» چند ثانیه چرخنده می‌بیند.
    val ctx = LocalContext.current
    LaunchedEffect(index, items) {
        ir.speakup.app.data.remote.ImagePrefetcher.prefetch(
            ctx,
            items.drop(index + 1).map { it.imageFile },
        )
    }

    HorizontalPager(state = pager, modifier = Modifier.fillMaxWidth()) { page ->
        // بدون fillMaxWidth، ستون محتوا را می‌پیچد و در چیدمان RTL به راست می‌چسبد
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val step = if (page == index) reveal else RevealStep.WORD
            if (step == RevealStep.CHECK) {
                WordCheck(
                    item = items[page],
                    options = checkOptions,
                    picked = checkPicked,
                    addedToLeitner = addedToLeitner,
                    onPick = onPickCheck,
                )
            } else {
                Flashcard(items[page], step, speechStatus, speakingId, onSpeak, onUnavailable)
            }
        }
    }
}

// ---------- کارت واژه: افشای تدریجی ----------

@Composable
private fun Flashcard(
    item: ActivityItemEntity,
    reveal: RevealStep,
    speechStatus: SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onUnavailable: () -> Unit,
) {
    Spacer(Modifier.size(4.dp))
    Text(
        "واژه جدید",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.size(8.dp))

    WordImage(item.imageFile)

    Spacer(Modifier.size(16.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SpeakButton(
            text = item.prompt, id = item.id,
            status = speechStatus, speakingId = speakingId,
            onSpeak = onSpeak, onUnavailable = onUnavailable,
        )
        Text(item.prompt, style = MaterialTheme.typography.displaySmall.ltr(), fontWeight = FontWeight.Bold)
    }
    item.promptFa?.let {
        Text(it, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    item.hintFa?.let {
        Text(it, style = MaterialTheme.typography.bodyMedium.ltr(), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    if (reveal >= RevealStep.EXAMPLE) {
        Spacer(Modifier.size(20.dp))
        item.exampleEn?.let { ex ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SpeakButton(
                    text = ex, id = item.id + "-ex", size = 36,
                    status = speechStatus, speakingId = speakingId,
                    onSpeak = onSpeak, onUnavailable = onUnavailable,
                )
                Text(ex, style = MaterialTheme.typography.titleMedium.ltr(), textAlign = TextAlign.Center)
            }
        }
    }
    if (reveal == RevealStep.TRANSLATION) {
        item.exampleFa?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ---------- کارت آموزش گرامر ----------

@Composable
private fun Teaching(item: ActivityItemEntity) {
    Spacer(Modifier.size(24.dp))
    Text(item.prompt, style = MaterialTheme.typography.headlineMedium.autoDir(item.prompt), fontWeight = FontWeight.Bold)
    Spacer(Modifier.size(16.dp))
    item.promptFa?.let {
        Text(it, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Start)
    }
    item.correctAnswer?.let { examples ->
        Spacer(Modifier.size(20.dp))
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Text(
                examples,
                Modifier.padding(16.dp).fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium.ltr(),
            )
        }
    }
}

// ---------- چندگزینه‌ای و تطبیق ----------

@Composable
private fun Choice(s: PlayerUiState, vm: PlayerViewModel) {
    val item = s.current ?: return
    Spacer(Modifier.size(32.dp))
    Text(item.prompt, style = MaterialTheme.typography.headlineMedium.autoDir(item.prompt), textAlign = TextAlign.Center)
    Spacer(Modifier.size(28.dp))

    item.optionsList().forEach { option ->
        val locked = s.verdict != null
        val isCorrect = locked && AnswerNormalizeEquals(option, item.correctAnswer)
        val isPicked = locked && AnswerNormalizeEquals(option, s.verdict?.userAnswer)
        val bg = when {
            isCorrect -> Green.copy(alpha = 0.15f)
            isPicked -> Red.copy(alpha = 0.15f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
        Surface(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                .clickable(enabled = !locked) { vm.submit(option) },
            shape = RoundedCornerShape(12.dp),
            color = bg,
        ) {
            Text(
                option,
                Modifier.padding(16.dp).fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium.autoDir(option),
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun AnswerNormalizeEquals(a: String?, b: String?): Boolean =
    a != null && b != null &&
        ir.speakup.app.domain.AnswerChecker.normalize(a) == ir.speakup.app.domain.AnswerChecker.normalize(b)

// ---------- مرتب‌سازی ----------

@Composable
private fun Reorder(s: PlayerUiState, vm: PlayerViewModel) {
    Spacer(Modifier.size(32.dp))
    Text("جمله را مرتب کن", style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.size(20.dp))

    // ناحیه چیده‌شده
    Surface(
        Modifier.fillMaxWidth().heightIn(min = 70.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            s.arranged.forEachIndexed { i, piece ->
                Chip(piece, enabled = s.verdict == null) { vm.unpick(i) }
            }
        }
    }

    Spacer(Modifier.size(24.dp))
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        s.pool.forEach { piece ->
            Chip(piece, enabled = s.verdict == null) { vm.pick(piece) }
        }
    }
}

@Composable
private fun Chip(text: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Text(
            text,
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.titleMedium.ltr(),
        )
    }
}

// ---------- گفتار ----------

/**
 * تمرین گفتار — دو حالت:
 *   ۱. میکروفون فعال: ضبط با SpeechRecognizer، بازخورد کلمه‌به‌کلمه
 *   ۲. حالت شنیدنی-فقط: وقتی مجوز رد شده یا موتور تشخیص گفتار نیست —
 *      دقیقاً مثل بقیه تمرین‌های نوشتاری، هرگز بن‌بست نمی‌شود.
 */
@Composable
private fun Speaking(
    onRecognitionFailed: () -> Unit,
    s: PlayerUiState,
    vm: PlayerViewModel,
    item: ActivityItemEntity,
    usingMic: Boolean,
    speechStatus: SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onSpeechUnavailable: () -> Unit,
    onRecognizerUnavailable: () -> Unit,
) {
    if (usingMic) {
        SpeakingMic(item, s, vm, speechStatus, speakingId, onSpeak, onSpeechUnavailable, onRecognitionFailed)
    } else {
        SpeakingFallback(
            item, s, vm, speechStatus, speakingId, onSpeak, onSpeechUnavailable,
            recognizerUnavailable = !vm.recognizer.isAvailable(),
            onRecognizerUnavailableClick = onRecognizerUnavailable,
        )
    }
}

@Composable
private fun SpeakingMic(
    item: ActivityItemEntity,
    s: PlayerUiState,
    vm: PlayerViewModel,
    speechStatus: SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onSpeechUnavailable: () -> Unit,
    onRecognitionFailed: () -> Unit,
) {
    val recState by vm.recognizer.state.collectAsStateWithLifecycle()
    val locked = s.verdict != null

    // نتیجه رسید → همان تصحیح استاندارد (recordAnswer + لایتنر خودکار) اجرا شود
    LaunchedEffect(recState, s.index) {
        val r = recState
        if (r is RecognitionState.Result && !locked) vm.submitSpeech(r.transcript)
    }

    /**
     * دو شکست پیاپی → تمرین به «بشنو و بنویس» برمی‌گردد.
     *
     * یک شکست ممکن است واقعاً گذرا باشد (کاربر چیزی نگفت)، پس بلافاصله
     * حالت را عوض نمی‌کنیم. ولی بار دوم یعنی موتور روی این گوشی کار
     * نمی‌کند و اصرار بیشتر فقط کاربر را در صفحهٔ قفل نگه می‌دارد.
     */
    var failures by remember(s.index) { mutableIntStateOf(0) }
    LaunchedEffect(recState, s.index) {
        if (recState is RecognitionState.Error && !locked) {
            failures += 1
            if (failures >= 2) onRecognitionFailed()
        }
    }

    Spacer(Modifier.size(20.dp))
    Text(
        "این جمله را با صدای بلند بخوان:",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.size(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SpeakButton(
            text = item.prompt, id = item.id, status = speechStatus, speakingId = speakingId,
            onSpeak = onSpeak, onUnavailable = onSpeechUnavailable,
        )
        Text(item.prompt, style = MaterialTheme.typography.headlineSmall.ltr(), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
    item.hintFa?.let {
        Spacer(Modifier.size(6.dp))
        Text(it, style = MaterialTheme.typography.bodyMedium.ltr(), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Spacer(Modifier.size(32.dp))

    val listening = recState is RecognitionState.Listening
    Box(
        modifier = Modifier
            .size(84.dp)
            .clip(CircleShape)
            .background(if (listening) Red.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer)
            .clickable(enabled = !locked) {
                if (listening) vm.recognizer.stopListening() else vm.recognizer.startListening()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Default.Mic, contentDescription = "ضبط صدا",
            modifier = Modifier.size(38.dp),
            tint = if (listening) Red else MaterialTheme.colorScheme.primary,
        )
    }
    Spacer(Modifier.size(10.dp))
    if (!locked) {
        Text(
            if (listening) "در حال شنیدن… وقتی تمام شد دوباره لمس کن"
            else "برای شروع، لمس کن و جمله را بگو",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }

    val err = recState as? RecognitionState.Error
    if (err != null && !locked) {
        Spacer(Modifier.size(8.dp))
        Text(err.messageFa, color = Red, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }

    if (s.speechWords.isNotEmpty()) {
        Spacer(Modifier.size(24.dp))
        WordFeedbackRow(s.speechWords)
    }
}

/** بازخورد سبز/قرمز روی کلمه‌های جمله هدف — سبز یعنی شنیده شد، قرمز یعنی جا افتاد یا اشتباه بود */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun WordFeedbackRow(words: List<SpeechMatcher.WordResult>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        words.forEach { w ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (w.correct) Green.copy(alpha = 0.15f) else Red.copy(alpha = 0.15f),
            ) {
                Text(
                    w.word,
                    Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium.ltr(),
                    color = if (w.correct) Green else Red,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SpeakingFallback(
    item: ActivityItemEntity,
    s: PlayerUiState,
    vm: PlayerViewModel,
    speechStatus: SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onSpeechUnavailable: () -> Unit,
    recognizerUnavailable: Boolean,
    onRecognizerUnavailableClick: () -> Unit,
) {
    Spacer(Modifier.size(20.dp))
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(enabled = recognizerUnavailable, onClick = onRecognizerUnavailableClick),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Default.Mic, null, modifier = Modifier.size(18.dp))
            Text(
                if (recognizerUnavailable) "میکروفون در دسترس نیست — فعلاً بشنو و بنویس (برای راهنما لمس کن)"
                else "میکروفون خاموش است — فعلاً بشنو و بنویس",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
    Spacer(Modifier.size(16.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SpeakButton(
            text = item.prompt, id = item.id, status = speechStatus, speakingId = speakingId,
            onSpeak = onSpeak, onUnavailable = onSpeechUnavailable,
        )
        Text("به تلفظ گوش کن", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Spacer(Modifier.size(8.dp))
    TextEntry(s, vm)
}

/** مرور واژگان: معنی فارسی بالا، کاشی حروف پایین */
@Composable
private fun WordBuild(s: PlayerUiState, vm: PlayerViewModel) {
    val item = s.current ?: return
    Spacer(Modifier.size(24.dp))
    Text(
        "این واژه را بساز",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.size(10.dp))
    Text(
        item.prompt,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.size(30.dp))
    LetterTiles(
        slots = s.letterSlots,
        pool = s.letterPool,
        enabled = s.verdict == null,
        onPick = vm::pickLetter,
        onUnpick = vm::unpickLetter,
    )
    Spacer(Modifier.size(16.dp))
    // برای کسی که تایپ برایش سریع‌تر است — همان الگوی دولینگو
    Text(
        "با صفحه‌کلید می‌نویسم",
        Modifier.clickable { vm.toggleKeyboard() }.padding(8.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

// ---------- ورودی متنی ----------

@Composable
private fun TextEntry(s: PlayerUiState, vm: PlayerViewModel) {
    val item = s.current ?: return
    Spacer(Modifier.size(32.dp))
    Text(item.prompt, style = MaterialTheme.typography.headlineMedium.autoDir(item.prompt), textAlign = TextAlign.Center)
    Spacer(Modifier.size(28.dp))
    OutlinedTextField(
        value = s.input,
        onValueChange = vm::onInputChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = s.verdict == null,
        singleLine = true,
        textStyle = MaterialTheme.typography.titleMedium.ltr(),
        placeholder = { Text("پاسخت را بنویس…", style = MaterialTheme.typography.bodyLarge) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        shape = RoundedCornerShape(12.dp),
    )
}

// ---------- بازخورد ----------

/**
 * الگوی بازخورد استاندارد محصول: پاسخ نادرست خط‌خورده قرمز، پاسخ درست سبز.
 * بدون پیام سرزنش‌آمیز و بدون افت امتیاز — سند ۰۳ / F-03.
 */
@Composable
private fun Feedback(correct: Boolean, userAnswer: String, expected: String, addedToLeitner: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (!correct) {
            Text(
                userAnswer,
                style = MaterialTheme.typography.titleMedium.ltr(),
                color = Red,
                textDecoration = TextDecoration.LineThrough,
            )
        }
        Text(
            expected,
            style = MaterialTheme.typography.titleLarge.ltr(),
            color = Green,
            fontWeight = FontWeight.Bold,
        )
        if (addedToLeitner) {
            Spacer(Modifier.size(12.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Text(
                    "به جعبه لایتنر اضافه شد",
                    Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

// ---------- پایان فعالیت ----------

@Composable
private fun FinishedView(
    correct: Int,
    total: Int,
    scored: Boolean,
    earnedXp: Int,
    goalReached: Boolean,
    streakNote: String?,
    step: Int,
    steps: Int,
    nextInSection: String?,
    onNext: () -> Unit,
    onClose: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // پیام باید با نتیجه بخواند. «آفرین!» گفتن به کسی که همه را غلط زده،
        // اعتبار بازخوردهای بعدی را هم از بین می‌برد — کاربر می‌فهمد که اپ
        // فقط تعارف می‌کند. سه حالت جدا، هر کدام با لحن خودش.
        val ratio = if (scored && total > 0) correct.toFloat() / total else 1f
        val strong = ratio >= 0.8f
        val weak = ratio < 0.5f
        val accent = when {
            strong -> Green
            weak -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.primary
        }

        Spacer(Modifier.weight(1f))
        Box(
            Modifier.size(96.dp).clip(RoundedCornerShape(48.dp)).background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (weak) "↻" else "✓",
                style = MaterialTheme.typography.displaySmall,
                color = accent,
            )
        }

        Spacer(Modifier.size(16.dp))
        Text(
            when {
                strong -> "آفرین!"
                weak -> "این بخش را دوباره ببین"
                else -> "خوب بود"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        // کارت آموزشی سوالی ندارد؛ نمایش «۰ از ۱ پاسخ درست» هم غلط است هم دلسردکننده
        if (scored && total > 0) {
            Spacer(Modifier.size(8.dp))
            Text(
                "${correct.toPersianDigits()} از ${total.toPersianDigits()} پاسخ درست",
                style = MaterialTheme.typography.bodyLarge,
            )
            if (weak) {
                Spacer(Modifier.size(10.dp))
                // واژه‌های غلط خودکار وارد لایتنر شده‌اند؛ گفتنش هم دلگرمی
                // است و هم تمایز اصلی محصول را به کاربر نشان می‌دهد
                Text(
                    "واژه‌هایی که اشتباه زدی به لایتنر رفتند و دوباره برمی‌گردند.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // امتیاز همان‌جایی دیده می‌شود که کاربر تلاشش را تمام کرده. اگر
        // فقط در صفحه اصلی جمع می‌شد، رابطه «کار → پاداش» گم می‌شد.
        if (earnedXp > 0) {
            Spacer(Modifier.size(20.dp))
            // جعبه امتیاز با مرز ضخیم و رنگ طلایی — همان زبان «جایزه».
            //
            // نسخه قبلی یک برچسب کم‌رنگ بود که کنار بقیه متن گم می‌شد؛
            // در حالی که این تنها چیز روی صفحه است که باید حس پاداش بدهد.
            Row(
                Modifier
                    .border(2.dp, ir.speakup.app.ui.theme.DuoGold, RoundedCornerShape(14.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("⭐", style = MaterialTheme.typography.titleLarge)
                Text(
                    "+${earnedXp.toPersianDigits()} امتیاز",
                    style = MaterialTheme.typography.titleLarge,
                    color = ir.speakup.app.ui.theme.DuoGoldDark,
                )
            }
        }
        // خبر زنجیره — فریز تازه یا ترمیم. سپری که دیده نشود، انگار نیست.
        streakNote?.let {
            Spacer(Modifier.size(12.dp))
            Text(
                it,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = ir.speakup.app.ui.theme.DuoGoldDark,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
        if (goalReached) {
            Spacer(Modifier.size(12.dp))
            Text(
                "🎯 هدف امروزت را زدی!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Green,
            )
        }

        // پیشرفت در بخش — «۳ از ۴». بدون این، کاربر نمی‌داند تا پایان
        // این بخش چقدر مانده و هر «ادامه» یک تعهد نامعلوم به نظر می‌رسد.
        if (steps > 1) {
            Spacer(Modifier.size(16.dp))
            Text(
                if (nextInSection == null) "این بخش تمام شد"
                else "${step.toPersianDigits()} از ${steps.toPersianDigits()} فعالیتِ این بخش",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.weight(1f))
        // دکمه اصلی همیشه پایین صفحه — همان جای همیشگی در کل اپ
        DuoButton(
            text = if (nextInSection != null) "بعدی" else "ادامه",
            onClick = if (nextInSection != null) onNext else onClose,
            height = 56.dp,
            modifier = Modifier.fillMaxWidth(),
        )

        // خروجِ آبرومندانه.
        //
        // تا امروز پایان هر تمرین کاربر را به فهرست درس برمی‌گرداند و او
        // باید دوباره روی کارت بخش می‌زد — هفده رفت‌وبرگشت در یک درس.
        // حالا تمرین‌ها زنجیر می‌شوند، اما رفتن هم باید یک ضربه باشد و نه
        // فرار از صفحه؛ کاربری که راهِ رفتن دارد، راحت‌تر برمی‌گردد.
        if (nextInSection != null) {
            Spacer(Modifier.size(8.dp))
            DuoButton(
                text = "فعلاً کافی است",
                onClick = onClose,
                style = DuoButtonStyle.Neutral,
                height = 52.dp,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.size(4.dp))
    }
}

/**
 * گزینه‌های چندگزینه‌ای برای مرور واژگان.
 *
 * حواس‌پرت‌ها از پاسخ‌های درستِ همین فعالیت ساخته می‌شوند: واژه‌هایی که
 * کاربر همین حالا دیده. گزینه بی‌ربط، تست را بی‌معنا می‌کند چون بدون
 * دانستن واژه هم می‌شود حذفش کرد.
 */
private fun quickOptions(s: PlayerUiState): List<String> {
    val answer = s.current?.correctAnswer?.trim().orEmpty()
    if (answer.isEmpty()) return emptyList()
    // یکتایی و مقایسه با پاسخ، هر دو روی همان شکل دیداری: با distinct
    // خام، «Book» و «book» دو گزینه جدا می‌شدند در حالی که پاسخ درست فقط
    // یکی‌شان را کنار می‌گذاشت.
    //
    // چرخش از جایگاه آیتم می‌آید: نسخه پیشین همیشه سه واژه **اول** فعالیت
    // را برمی‌داشت و الفبایی می‌چید، یعنی در کل مرور همان سه حواس‌پرت‌کن
    // تکرار می‌شد و جای پاسخ هم از حرف اولش قابل حدس بود.
    val seen = mutableSetOf(answer.optionKey())
    val others = ArrayList<String>(3)
    for (step in 1 until s.items.size) {
        val cand = s.items[(s.index + step) % s.items.size].correctAnswer?.trim().orEmpty()
        if (cand.isEmpty() || !seen.add(cand.optionKey())) continue
        others += cand
        if (others.size == 3) break
    }
    if (others.isEmpty()) return emptyList()
    // ترتیب معین است (تابعِ جایگاه آیتم) و نه تصادفی: با هر بازترسیم صفحه
    // گزینه‌ها نباید جابه‌جا شوند، وگرنه کاربر روی گزینه اشتباه می‌زند.
    return ArrayList(others).apply {
        add(answerSlot(answer, s.index, size + 1), answer)
    }
}

/** مرور واژه به‌شکل تست — همان بازخورد و همان مسیر ثبت پاسخ. */
@Composable
private fun QuickChoice(s: PlayerUiState, vm: PlayerViewModel) {
    val item = s.current ?: return
    Spacer(Modifier.size(32.dp))
    Text(
        item.prompt,
        style = MaterialTheme.typography.headlineMedium.autoDir(item.prompt),
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.size(24.dp))
    quickOptions(s).forEach { opt ->
        val picked = s.input.equals(opt, ignoreCase = true)
        Surface(
            Modifier.fillMaxWidth().padding(vertical = 5.dp)
                .clickable(enabled = s.verdict == null) { vm.onInputChange(opt) },
            shape = RoundedCornerShape(14.dp),
            color = if (picked) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                if (picked) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant,
            ),
        ) {
            Text(
                opt,
                Modifier.padding(horizontal = 16.dp, vertical = 14.dp).fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium.ltr(),
                textAlign = TextAlign.Center,
            )
        }
    }
}
