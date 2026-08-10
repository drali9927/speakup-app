package ir.speakup.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.speakup.app.domain.AuthService
import ir.speakup.app.domain.toPersianDigits
import ir.speakup.app.ui.theme.ltr
import ir.speakup.app.ui.common.DuoButton
import ir.speakup.app.ui.common.DuoButtonBox
import ir.speakup.app.ui.common.DuoButtonStyle
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.height

@Composable
fun AuthScreen(onDone: () -> Unit, vm: AuthViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(s.done) { if (s.done) onDone() }

    Column(
        Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.size(20.dp))

        // تصویر خوش‌آمد.
        //
        // صفحه ورود پیش‌تر یک عنوان و یک کادر خالی بود با دو سوم صفحه
        // فضای سفید — یعنی اولین چیزی که کاربر از اپ می‌دید، خالی بودن
        // بود. تصویر از خودِ محتوای درس اول می‌آید (همراه APK است، پس
        // پیش از ورود هم بدون اینترنت نشان داده می‌شود).
        Box(
            Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = ir.speakup.app.data.remote.ImageSource.resolve(
                    LocalContext.current, "a1_l01_w20_meet.webp",
                ),
                contentDescription = null,
                modifier = Modifier.size(200.dp).clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(Modifier.size(20.dp))

        when (s.step) {
            AuthStep.PHONE -> PhoneStep(s, vm)
            AuthStep.CODE -> CodeStep(s, vm)
        }

        // سه وعده اصلی محصول، پیش از اینکه کاربر شماره‌اش را بدهد.
        //
        // درخواست شماره موبایل بالاترین نقطه ریزش است؛ اگر کاربر تا اینجا
        // ندانسته چه چیزی می‌گیرد، دلیلی برای دادن شماره ندارد.
        if (s.step == AuthStep.PHONE) {
            Spacer(Modifier.size(28.dp))
            Promise("📚", "۲۴۰۰ واژه", "از مقدماتی تا متوسط، با تصویر و صدا")
            Promise("🗣", "تمرین تولیدی", "بیشترِ تمرین‌ها تایپ و گفتار است، نه تست")
            Promise("🔁", "مرور خودکار", "واژه‌هایی که اشتباه می‌زنی خودشان برمی‌گردند")
        }

        s.error?.let {
            Spacer(Modifier.size(12.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.weight(1f))

        DuoButtonBox(
            onClick = { if (s.step == AuthStep.PHONE) vm.requestCode() else vm.verify() },
            enabled = !s.busy && if (s.step == AuthStep.PHONE) s.phoneValid else s.codeComplete,
            height = 56.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (s.busy) {
                CircularProgressIndicator(
                    Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    if (s.step == AuthStep.PHONE) "دریافت کد ورود" else "تأیید و ورود",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        Spacer(Modifier.size(16.dp))
        Text(
            "ورود شما به معنی پذیرش قوانین است.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.size(24.dp))
    }
}

@Composable
private fun PhoneStep(s: AuthUiState, vm: AuthViewModel) {
    Text("شماره موبایلت را وارد کن", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.size(10.dp))
    Text(
        "یک کد ۵ رقمی برایت می‌فرستیم تا وارد شوی و پیشرفتت ذخیره بماند.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.size(28.dp))

    OutlinedTextField(
        value = s.phone,
        onValueChange = vm::onPhoneChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !s.busy,
        placeholder = { Text("۰۹۱۲۳۴۵۶۷۸۹", style = MaterialTheme.typography.titleMedium) },
        // متن شماره لاتین است ولی برای کاربر فارسی چپ‌چین درست‌تر خوانده می‌شود
        textStyle = MaterialTheme.typography.titleMedium.ltr(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        shape = RoundedCornerShape(12.dp),
        isError = s.phone.isNotEmpty() && !s.phoneValid,
        supportingText = {
            if (s.phone.isNotEmpty() && !s.phoneValid) {
                Text("شماره باید با ۰۹ شروع شود و ۱۱ رقم باشد")
            }
        },
    )
}

@Composable
private fun CodeStep(s: AuthUiState, vm: AuthViewModel) {
    Text("کد ورود را وارد کن", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.size(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("کد به شماره", style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            AuthService.formatPhone(s.phone),
            style = MaterialTheme.typography.bodyLarge.ltr(),
            fontWeight = FontWeight.SemiBold,
        )
        Text("فرستاده شد", style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Spacer(Modifier.size(28.dp))
    OutlinedTextField(
        value = s.code,
        onValueChange = vm::onCodeChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !s.busy,
        placeholder = { Text("- - - - -", style = MaterialTheme.typography.titleMedium) },
        textStyle = MaterialTheme.typography.headlineMedium.ltr(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        shape = RoundedCornerShape(12.dp),
    )

    Spacer(Modifier.size(12.dp))
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = vm::changePhone) { Text("تغییر شماره") }
        Box(Modifier.weight(1f))
        if (s.canResend) {
            TextButton(onClick = vm::requestCode) { Text("ارسال دوباره کد") }
        } else {
            Text(
                "ارسال دوباره تا ${s.secondsLeft.toPersianDigits()} ثانیه",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** یک سطر وعده در صفحه ورود — آیکن، عنوان کوتاه، یک خط توضیح. */
@Composable
private fun Promise(icon: String, title: String, body: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(icon, style = MaterialTheme.typography.headlineSmall)
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
