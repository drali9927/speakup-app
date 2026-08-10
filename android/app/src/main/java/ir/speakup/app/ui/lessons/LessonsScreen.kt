package ir.speakup.app.ui.lessons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.data.local.LessonEntity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ir.speakup.app.ui.common.StatsHeader
import ir.speakup.app.ui.common.UnitBanner
import ir.speakup.app.ui.theme.ltr
import ir.speakup.app.domain.toPersianDigits

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(
    onOpenReview: () -> Unit,
    onOpenLesson: (LessonEntity) -> Unit,
    onOpenStreak: () -> Unit,
    onOpenLeague: () -> Unit,
    vm: LessonsViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var showLevels by remember { mutableStateOf(false) }

    // انتخابگر سطح — همان الگوی رقیب: عنوان سطح خودش دکمه است، چون
    // کاربر اول از همه همان‌جا دنبالش می‌گردد.
    if (showLevels) {
        AlertDialog(
            onDismissRequest = { showLevels = false },
            title = { Text("انتخاب سطح") },
            text = {
                Column {
                    state.availableLevels.forEach { lvl ->
                        val selected = lvl.code == state.levelCode
                        Surface(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                .clickable { vm.selectLevel(lvl.code); showLevels = false },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface,
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(
                                    lvl.titleFa,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    lvl.titleEn,
                                    style = MaterialTheme.typography.bodyMedium.ltr(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLevels = false }) { Text("بستن") }
            },
        )
    }

    Scaffold(
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            when {
                state.loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> Text(
                    "خطا در بارگذاری محتوا:\n${state.error}",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                else -> LazyColumn(Modifier.fillMaxSize()) {
                    item {
                        StatsHeader(
                            levelTitle = state.levelTitle,
                            streakDays = state.streakDays,
                            todayXp = state.todayXp,
                            goalXp = state.goalXp,
                            onPickLevel = { showLevels = true },
                            onStreak = onOpenStreak,
                            onXp = onOpenLeague,
                        )
                    }
                    item {
                        UnitBanner(
                            title = state.levelTitle,
                            subtitle = "${state.completedLessonIds.size.toPersianDigits()} از " +
                                "${state.lessons.size.toPersianDigits()} درس انجام شده",
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        )
                    }
                    // نخستین درسی که هنوز تمام نشده = درس جاری
                    val currentId = state.lessons.firstOrNull {
                        it.id !in state.completedLessonIds
                    }?.id

                    // دروس مستقیم آیتم‌های LazyColumn‌اند و نه یک آیتمِ
                    // دربرگیرنده. پیش‌تر هر سی درس داخل یک item ساخته
                    // می‌شدند و بازیافت آیتم‌ها عملاً از کار می‌افتاد.
                    itemsIndexed(state.lessons, key = { _, l -> l.id }) { i, lesson ->
                        // ایستگاه مرور هر پنج درس، سرِ راه کاربر
                        if (i > 0 && i % 5 == 0) {
                            ReviewStation(dueCount = state.dueCount, onClick = onOpenReview)
                        }
                        LessonRow(
                            lesson = lesson,
                            state = when {
                                lesson.id in state.completedLessonIds -> NodeState.DONE
                                lesson.id == currentId -> NodeState.CURRENT
                                else -> NodeState.OPEN
                            },
                            progress = state.lessonProgress[lesson.id] ?: 0f,
                            onClick = { onOpenLesson(lesson) },
                        )
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

internal fun parseColor(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color.Gray)
