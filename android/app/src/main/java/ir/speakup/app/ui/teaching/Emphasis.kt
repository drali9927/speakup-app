package ir.speakup.app.ui.teaching

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * پررنگ کردن بخش‌های میان `**…**` در متن کارت‌های گرامر.
 *
 * متن کارت‌ها در کاربرگ نوشته می‌شود و آنجا تأکید با ستاره علامت
 * می‌خورد. تا امروز اپ آن را عیناً نشان می‌داد و کاربر روی صفحه
 * «**حال کامل**» می‌دید — یعنی همان نکته‌ای که قرار بود برجسته شود،
 * شلوغ‌تر از بقیه متن به چشم می‌آمد.
 *
 * عمداً یک تجزیه‌گر مارک‌داون کامل نیست: فقط همین یک نشانه، چون تنها
 * چیزی است که در محتوا به کار می‌رود.
 */
fun emphasise(raw: String): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (true) {
        val open = raw.indexOf(MARK, i)
        if (open < 0) {
            append(raw.substring(i)); return@buildAnnotatedString
        }
        val close = raw.indexOf(MARK, open + MARK.length)
        if (close < 0) {
            append(raw.substring(i)); return@buildAnnotatedString
        }
        append(raw.substring(i, open))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(raw.substring(open + MARK.length, close))
        }
        i = close + MARK.length
    }
}

private const val MARK = "**"
