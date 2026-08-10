package ir.speakup.app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection

/**
 * متن انگلیسی داخل چیدمان راست‌به‌چپ.
 *
 * بدون این، الگوریتم دوجهته یونیکد نشانه‌های پایانی را جابه‌جا می‌کند:
 * «He is a manager.» به شکل «‎.manager / He is a» رندر می‌شود.
 * هر جا محتوای انگلیسی نمایش می‌دهیم — عنوان درس، واژه، جمله مثال،
 * IPA، متن تمرین — باید از این استفاده شود.
 */
fun TextStyle.ltr(): TextStyle = copy(
    textDirection = TextDirection.Ltr,
    textAlign = TextAlign.Start,
)

/** متن فارسی — جهت صریح راست‌به‌چپ برای مواردی که با محتوای لاتین مخلوط است. */
fun TextStyle.rtl(): TextStyle = copy(
    textDirection = TextDirection.Rtl,
    textAlign = TextAlign.Start,
)

/**
 * جهت را از روی خود متن تشخیص می‌دهد.
 *
 * صورت سوال تمرین‌ها هم فارسی است ("او یک پزشک است.") هم انگلیسی
 * ("Nice to meet you."). گذاشتن جهت ثابت، یکی از این دو را خراب می‌کند:
 * انگلیسی داخل RTL به شکل "‎.Nice to meet you" درمی‌آید.
 *
 * قاعده: نخستین نویسه جهت‌دار قوی، جهت کل متن را تعیین می‌کند —
 * همان قاعده‌ای که استاندارد دوجهته یونیکد برای پاراگراف به‌کار می‌برد.
 */
fun TextStyle.autoDir(text: String): TextStyle =
    if (startsRtl(text)) rtl() else ltr()

private fun startsRtl(text: String): Boolean {
    for (ch in text) {
        when {
            ch in '֐'..'ࣿ' -> return true    // عربی، فارسی، عبری
            ch in 'יִ'..'﷿' -> return true    // صورت‌های نمایشی عربی
            ch in 'ﹰ'..'﻿' -> return true
            ch.isLetter() -> return false              // نخستین حرف لاتین
        }
    }
    return false
}
