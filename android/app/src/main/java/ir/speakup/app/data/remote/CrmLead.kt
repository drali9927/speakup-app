package ir.speakup.app.data.remote

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * فرستادن شماره کاربر به CRM به‌عنوان سرنخ فروش.
 *
 * سه قاعده که این کار نباید هیچ‌کدامشان را بشکند:
 *
 * **۱. هرگز ورود کاربر را کند یا خراب نکند.** روی یک اسکوپ جدا اجرا
 * می‌شود و هر خطایی — قطعی شبکه، خطای سرور، تایم‌اوت — بی‌صدا بلعیده
 * می‌شود. کسی که می‌خواهد وارد اپ شود، نباید معطل CRM بماند.
 *
 * **۲. فقط هنگام ارسال واقعیِ کد، نه با هر ضربه کیبورد.** ثبت شماره در
 * حال تایپ، هم دیتابیس CRM را پر از شماره ناقص می‌کند و هم شماره کسانی
 * را می‌فرستد که اصلاً ادامه نداده‌اند.
 *
 * **۳. شماره در لاگ نمی‌رود.** حتی در لاگِ خطا فقط می‌گوییم نشد، نه
 * اینکه برای چه شماره‌ای.
 *
 * توجه حقوقی: متن «قوانین» که کاربر هنگام ورود می‌پذیرد باید استفاده
 * بازاریابی از شماره را پوشش بدهد، وگرنه این کار پشتوانه ندارد.
 */
@Singleton
class CrmLead @Inject constructor() {

    @Serializable
    private data class Question(val title: String, val answer: String)

    @Serializable
    private data class Payload(
        val phone: String,
        val utm_source: String,
        val utm_medium: String,
        val utm_campaign: String,
        val data: Data,
    ) {
        @Serializable
        data class Data(val questions: List<Question>)
    }

    /** شماره را به رقم لاتین و بدون جداکننده می‌برد */
    private fun normalize(raw: String): String = buildString {
        for (c in raw) when (c) {
            in '0'..'9' -> append(c)
            in '۰'..'۹' -> append('0' + (c - '۰'))
            in '٠'..'٩' -> append('0' + (c - '٠'))
        }
    }

    fun send(rawPhone: String) {
        val phone = normalize(rawPhone)
        if (phone.length < 10) return

        scope.launch {
            runCatching {
                val body = json.encodeToString(
                    Payload(
                        phone = phone,
                        utm_source = "app",
                        utm_medium = "speakupapp",
                        utm_campaign = "speakupapp",
                        data = Payload.Data(
                            listOf(Question("منبع", "اپ اندروید SpeakUp")),
                        ),
                    ),
                ).toByteArray()

                (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                    connectTimeout = 8_000
                    readTimeout = 8_000
                    outputStream.use { it.write(body) }
                    // خواندن کد پاسخ لازم است تا درخواست واقعاً فرستاده شود
                    val code = responseCode
                    disconnect()
                    code
                }
            }.onFailure {
                // شماره عمداً در لاگ نمی‌آید
                Log.w(TAG, "ارسال سرنخ به CRM انجام نشد: ${it.javaClass.simpleName}")
            }
        }
    }

    private companion object {
        const val TAG = "CrmLead"
        const val ENDPOINT = "https://apicrm.spkupacademy.com/api/porsline/phones/store"

        // اسکوپ جدا و نه viewModelScope: اگر کاربر بلافاصله وارد شود و
        // صفحه ورود از بین برود، درخواست نباید نیمه‌کاره لغو شود.
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val json = Json { encodeDefaults = true }
    }
}
