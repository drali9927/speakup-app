package ir.speakup.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import dagger.hilt.android.AndroidEntryPoint
import ir.speakup.app.ui.AppNav
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.ui.theme.SpeakUpTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // حالت نمایش از تنظیمات کاربر، با پیش‌فرض «مثل سیستم»
            val prefs = remember { ir.speakup.app.data.prefs.AppPreferences(applicationContext) }
            val mode by prefs.themeMode.collectAsStateWithLifecycle(initialValue = "SYSTEM")
            val dark = when (mode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            SpeakUpTheme(darkTheme = dark) {
                // کل رابط کاربری راست‌به‌چپ است
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AppNav()
                }
            }
        }
    }
}
