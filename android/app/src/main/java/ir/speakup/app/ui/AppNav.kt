package ir.speakup.app.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.ui.auth.AuthScreen
import ir.speakup.app.ui.onboarding.OnboardingScreen
import ir.speakup.app.ui.paywall.PaywallScreen
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ir.speakup.app.ui.leitner.LeitnerScreen
import ir.speakup.app.ui.leitner.LeitnerViewModel
import ir.speakup.app.ui.lesson.LessonScreen
import ir.speakup.app.ui.lesson.LessonViewModel
import ir.speakup.app.ui.lessons.LessonsScreen
import ir.speakup.app.ui.player.PlayerScreen
import ir.speakup.app.ui.streak.StreakScreen
import ir.speakup.app.ui.streak.StreakViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.EmojiEvents

object Routes {
    const val ONBOARDING = "onboarding"
    const val AUTH = "auth"
    const val PAYWALL = "paywall"
    const val LESSONS = "lessons"
    const val LEITNER = "leitner"
    const val LEAGUE = "league"
    const val STREAK = "streak"
    const val PROFILE = "profile"
    const val LESSON = "lesson/{lessonId}"
    const val PLAYER = "player/{activityId}"

    fun lesson(id: String) = "lesson/$id"
    fun player(id: String) = "player/$id"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

/**
 * رفتن به یک تب — چه از نوار پایین و چه از میان‌برهای داخل صفحه.
 *
 * ناوبری ساده نسخه دومی از همان صفحه روی پشته می‌گذاشت و دکمه بازگشت
 * به جای درست برنمی‌گشت؛ چون مقصد شروعِ گراف همیشه «دروس» نیست و بسته
 * به اینکه کاربر وارد شده یا نه فرق می‌کند.
 */
private fun NavHostController.switchTab(route: String) = navigate(route) {
    popUpTo(graph.findStartDestination().id) { saveState = true }
    launchSingleTop = true
    restoreState = true
}

private val TABS = listOf(
    Tab(Routes.LESSONS, "دروس", Icons.Default.School),
    Tab(Routes.LEITNER, "لایتنر", Icons.Default.Style),
    Tab(Routes.LEAGUE, "لیگ", Icons.Default.EmojiEvents),
    Tab(Routes.STREAK, "زنجیره", Icons.Default.LocalFireDepartment),
    Tab(Routes.PROFILE, "پروفایل", Icons.Default.Person),
)

@Composable
fun AppNav(nav: NavHostController = rememberNavController()) {
    val ctx = LocalContext.current
    val prefs = remember { AppPreferences(ctx.applicationContext) }
    val scope = rememberCoroutineScope()

    // مقصد اولیه از وضعیت ذخیره‌شده: معرفی → ورود → اپ
    val onboarded by prefs.onboarded.collectAsStateWithLifecycle(initialValue = null)
    // وضعیت اشتراک از سرور می‌آید (`/v1/me`) و با زمان سرور سنجیده می‌شود.
    // پیش‌تر یک بولین محلی بود که خود اپ روشنش می‌کرد — یعنی دستکاری فایل
    // تنظیمات برای گرفتن اشتراک رایگان کافی بود.
    val session: SessionViewModel = hiltViewModel()
    val isSubscribed by session.isSubscribed.collectAsStateWithLifecycle()
    val token by prefs.token.collectAsStateWithLifecycle(initialValue = null)
    var start by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(onboarded, token) {
        if (start == null && onboarded != null) {
            start = when {
                onboarded == false -> Routes.ONBOARDING
                token == null -> Routes.AUTH
                else -> Routes.LESSONS
            }
        }
    }
    if (start == null) return

    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    // نوار تب فقط در صفحات سطح اول؛ داخل درس و تمرین تمام‌صفحه است
    val showTabs = route in TABS.map { it.route }

    Scaffold(
        bottomBar = {
            if (showTabs) {
                // نوار تب با خط جداکننده ضخیم بالا و بدون سایه.
                //
                // نوار شناور متریال از صفحه جدا نبود؛ این خط دو پیکسلی
                // مرز محتوا و ناوبری را قطعی می‌کند — همان کاری که سبک
                // دولینگو با مرزهای ضخیمش می‌کند.
                Column {
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                    ) {
                        TABS.forEach { tab ->
                            val selected = entry?.destination?.hierarchy?.any { it.route == tab.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = { nav.switchTab(tab.route) },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                        }
                    }
                }
            }
        }
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(if (showTabs) inner else PaddingValues())) {
            NavHost(
                navController = nav,
                startDestination = start!!,
                // انیمیشن گذار — بدون آن، تعویض مقصد در همان فریم رخ می‌دهد و اگر
                // کاربر سریع ضربه بزند، رویداد up روی محتوای صفحهٔ جدید همان
                // مختصات دوباره hit-test می‌شود؛ یعنی یک ضربه می‌تواند از فهرست
                // دروس مستقیم به داخل یک تمرین ببرد و صفحه درس رد شود.
                enterTransition = { fadeIn(tween(180)) + slideInHorizontally(tween(180)) { it / 12 } },
                exitTransition = { fadeOut(tween(180)) },
                popEnterTransition = { fadeIn(tween(180)) },
                popExitTransition = { fadeOut(tween(180)) + slideOutHorizontally(tween(180)) { it / 12 } },
            ) {

                composable(Routes.ONBOARDING) {
                    OnboardingScreen(onFinish = {
                        scope.launch { prefs.setOnboarded() }
                        nav.navigate(Routes.AUTH) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                    })
                }

                composable(Routes.AUTH) {
                    AuthScreen(onDone = {
                        nav.navigate(Routes.LESSONS) { popUpTo(Routes.AUTH) { inclusive = true } }
                    })
                }

                composable(Routes.PAYWALL) {
                    PaywallScreen(
                        onClose = { nav.popBackStack() },
                        // فقط پس از تأیید رسید توسط سرور صدا زده می‌شود
                        onPurchased = { nav.popBackStack() },
                    )
                }

                composable(Routes.LESSONS) {
                    LessonsScreen(
                        onOpenReview = { nav.switchTab(Routes.LEITNER) },
                        onOpenLesson = { lesson ->
                            // درس قفل → پی‌وال. ورودی طبیعی خرید همین‌جاست:
                            // کاربر دقیقاً وقتی می‌بیند چه چیزی را از دست می‌دهد.
                            if (lesson.isFree || isSubscribed) nav.navigate(Routes.lesson(lesson.id))
                            else nav.navigate(Routes.PAYWALL)
                        },
                        // نشان‌های نوار آمار هم مثل تب رفتار می‌کنند و نه
                        // مثل مقصد تازه، وگرنه نسخه دومی از همان صفحه روی
                        // پشته می‌نشیند — همان اشتباهی که برای لایتنر رخ داد.
                        onOpenStreak = { nav.switchTab(Routes.STREAK) },
                        onOpenLeague = { nav.switchTab(Routes.LEAGUE) },
                    )
                }

                composable(Routes.PROFILE) { ir.speakup.app.ui.profile.ProfileScreen() }

                composable(Routes.LEITNER) { e ->
                    val vm: LeitnerViewModel = hiltViewModel(e)
                    // شمارنده‌ها باید بعد از هر تمرین تازه شوند
                    LifecycleResumeEffect(Unit) { vm.refresh(); onPauseOrDispose { } }
                    LeitnerScreen(vm)
                }

                composable(Routes.LEAGUE) { ir.speakup.app.ui.league.LeagueScreen() }
                composable(Routes.STREAK) { e ->
                    val vm: StreakViewModel = hiltViewModel(e)
                    LifecycleResumeEffect(Unit) { vm.refresh(); onPauseOrDispose { } }
                    StreakScreen(vm)
                }

                composable(
                    Routes.LESSON,
                    arguments = listOf(navArgument("lessonId") { type = NavType.StringType }),
                ) { e ->
                    val vm: LessonViewModel = hiltViewModel(e)
                    LifecycleResumeEffect(Unit) { vm.reload(); onPauseOrDispose { } }
                    LessonScreen(
                        onBack = { nav.popBackStack() },
                        onOpenActivity = { nav.navigate(Routes.player(it.id)) },
                        vm = vm,
                    )
                }

                composable(
                    Routes.PLAYER,
                    arguments = listOf(navArgument("activityId") { type = NavType.StringType }),
                ) {
                    PlayerScreen(
                        onClose = { nav.popBackStack() },
                        // تمرین بعدی **جایگزین** تمرین فعلی می‌شود و رویش
                        // نمی‌نشیند؛ وگرنه بعد از شش تمرین، دکمه بازگشت
                        // باید شش بار زده شود تا کاربر به فهرست درس برسد.
                        onNext = { next ->
                            nav.navigate(Routes.player(next)) {
                                popUpTo(Routes.PLAYER) { inclusive = true }
                            }
                        },
                    )
                }
            }
        }
    }
}
