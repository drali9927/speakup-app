package ir.speakup.app.ui.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * قوانین و حریم خصوصی.
 *
 * متن داخل خود اپ است و از شبکه نمی‌آید. دلیلش این است که کاربر دقیقاً
 * وقتی این را می‌خواند که هنوز وارد نشده و ممکن است اینترنتش هم ضعیف
 * باشد؛ صفحه‌ای که آن لحظه خالی بماند، بدتر از نبودنش است.
 *
 * نسخه کامل و قابل انتشار روی وب: docs/قوانین-و-حریم-خصوصی.md — هر تغییری
 * باید در هر دو اعمال شود.
 */
@Composable
fun TermsScreen(onBack: () -> Unit) {
    // بدون این، دکمه برگشت گوشی از کل اپ خارج می‌شد — چون این صفحه روی
    // صفحه پیش از خودش نشسته و مقصد جداگانه‌ای در گراف ناوبری نیست.
    BackHandler { onBack() }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = onBack) { Text("بستن") }
        }
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Text(
                "قوانین و حریم خصوصی",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "آخرین به‌روزرسانی: ۲۹ مرداد ۱۴۰۵",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            TERMS.forEach { block ->
                if (block.startsWith("## ")) {
                    Spacer(Modifier.height(18.dp))
                    Text(
                        block.removePrefix("## "),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                } else {
                    Text(block, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

private val TERMS = listOf(
    "این متن می‌گوید با اطلاعاتت چه می‌کنیم.",

    "## چه چیزی از تو می‌گیریم",
    "شماره موبایل — برای ورود و اینکه پیشرفتت روی حساب خودت بماند. تنها چیزی است که از تو می‌پرسیم.",
    "پیشرفت یادگیری — درس‌هایی که تمام کرده‌ای، واژه‌های جعبه لایتنر، و پاسخ‌های متنی‌ات به تمرین‌ها. اگر گوشی عوض کنی، از صفر شروع نمی‌کنی.",
    "شناسه نصب — یک عدد تصادفی که به شماره یا نام تو وصل نیست. با آن می‌شماریم چند نفر یک صفحه را رها کرده‌اند تا همان‌جا را درست کنیم.",
    "رسید خرید — اگر اشتراک بخری، کد رسید کافه‌بازار نزد ما می‌ماند تا اشتراکت فعال شود.",

    "## چه چیزی نمی‌گیریم",
    "نام، ایمیل، کد ملی، تاریخ تولد و نشانی. دفترچه مخاطبان، پیامک‌ها، عکس‌ها و فایل‌های گوشی. موقعیت مکانی.",
    "هیچ اطلاعات بانکی. پرداخت تماماً داخل کافه‌بازار انجام می‌شود؛ شماره کارت و رمزت هرگز وارد اپ ما نمی‌شود.",

    "## میکروفون",
    "صدایت را ذخیره نمی‌کنیم و روی سرور خودمان نمی‌فرستیم. فقط متنی که از گفتارت تشخیص داده می‌شود ثبت می‌شود تا معلوم شود پاسخ درست بوده یا نه.",
    "خودِ تشخیص گفتار را سرویس اندروید گوشی‌ات انجام می‌دهد، نه ما. اپ اول حالت آفلاین را امتحان می‌کند؛ اگر گوشی‌ات بسته آفلاین انگلیسی نداشته باشد، سرویس اندروید ممکن است صدا را به سرور سازنده‌اش (معمولاً گوگل) بفرستد. این از دست ما خارج است. اگر نمی‌خواهی، اجازه میکروفون را نده — بقیه اپ کار می‌کند.",

    "## تماس ما با تو",
    "با ورود به اپ، شماره‌ات نزد ما ثبت می‌شود و به ما اجازه می‌دهی درباره دوره‌ها، تخفیف‌ها و خدمات SpeakUp پیامک بفرستیم یا تماس بگیریم.",
    "شماره‌ات به شخص یا شرکت دیگری فروخته یا اجاره داده نمی‌شود. هر وقت خواستی می‌توانی بگویی دیگر پیامی نفرستیم. یادآورهای خودِ اپ جداست و از تنظیمات خاموش می‌شود.",

    "## با اطلاعات چه نمی‌کنیم",
    "نمی‌فروشیم. به شرکت تبلیغاتی نمی‌دهیم. هیچ ابزار ردیابی یا تبلیغاتی شرکت دیگری داخل اپ نگذاشته‌ایم — آمار استفاده را خودمان روی سرور خودمان نگه می‌داریم. ارتباط اپ با سرور رمزگذاری‌شده است.",

    "## منابع و حقوق محتوا",
    "درس‌ها، تمرین‌ها، جمله‌های نمونه و واژه‌نامهٔ این اپ را خودِ تیم SpeakUp نوشته است. از هیچ کتاب، سایت یا اپ دیگری رونویسی نشده.",
    "تصویرهای کارت‌های واژه و شخصیت‌های پروفایل را خودمان و با ابزار هوش مصنوعی، از روی همین متن‌ها ساخته‌ایم.",
    "قلم فارسی اپ «وزیرمتن» ساختهٔ صابر راستی‌کردار است و با پروانهٔ SIL Open Font License 1.1 منتشر شده.",
    "اپ از کتابخانه‌های متن‌باز اندروید (AndroidX، Retrofit، OkHttp، Coil و Hilt) استفاده می‌کند که همگی با پروانهٔ Apache 2.0 منتشر شده‌اند.",

    "## حذف حساب",
    "هر وقت بخواهی می‌توانی حذف حساب و اطلاعاتت را درخواست کنی. شماره و پیشرفتت پاک می‌شود. این کار برگشت‌پذیر نیست.",
    "برای درخواست حذف، از همان شماره‌ای که با آن وارد شده‌ای به info@spkupacademy.com ایمیل بزن.",
    "اشتراک خریداری‌شده تابع قوانین کافه‌بازار است و بازگشت وجه از همان‌جا پیگیری می‌شود.",

    "## تماس با ما",
    "برای هر پرسشی دربارهٔ اطلاعاتت، حذف حساب، یا خودِ اپ: info@spkupacademy.com",
)
