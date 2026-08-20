package ir.speakup.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.data.prefs.AppPreferences
import ir.speakup.app.domain.AuthService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthStep { PHONE, CODE }

data class AuthUiState(
    val step: AuthStep = AuthStep.PHONE,
    val phone: String = "",
    val code: String = "",
    val busy: Boolean = false,
    val error: String? = null,
    val secondsLeft: Int = 0,
    val done: Boolean = false,
) {
    val phoneValid: Boolean get() = AuthService.isValidPhone(phone)
    val codeComplete: Boolean get() = code.filter(Char::isDigit).length == AuthService.CODE_LENGTH
    val canResend: Boolean get() = secondsLeft <= 0
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AuthService,
    private val crm: ir.speakup.app.data.remote.CrmLead,
    private val prefs: AppPreferences,
    @dagger.hilt.android.qualifiers.ApplicationContext private val appContext: android.content.Context,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onPhoneChange(v: String) {
        // فقط رقم نگه می‌داریم؛ کاربر ممکن است فاصله یا خط تیره تایپ کند
        _state.value = _state.value.copy(phone = v.filter(Char::isDigit).take(11), error = null)
    }

    fun onCodeChange(v: String) {
        _state.value = _state.value.copy(
            code = v.filter(Char::isDigit).take(AuthService.CODE_LENGTH),
            error = null,
        )
    }

    fun requestCode() {
        val s = _state.value
        if (s.busy || !s.phoneValid) return
        _state.value = s.copy(busy = true, error = null)
        // سرنخ فروش، همین‌جا و نه با هر ضربه کیبورد: این نخستین لحظه‌ای
        // است که کاربر شماره‌اش را عمداً ثبت می‌کند.
        crm.send(s.phone)
        viewModelScope.launch {
            auth.requestCode(s.phone)
                .onSuccess { skipCode ->
                    // حساب تست: سرور کد نفرستاده، پس صفحه کد را هم رد
                    // می‌کنیم و مستقیم وارد می‌شویم.
                    if (skipCode) {
                        _state.value = _state.value.copy(code = TEST_CODE, busy = false)
                        verify()
                        return@onSuccess
                    }
                    _state.value = _state.value.copy(step = AuthStep.CODE, busy = false, code = "")
                    startResendTimer()
                }
                .onFailure {
                    _state.value = _state.value.copy(busy = false, error = it.message ?: "ارسال کد ناموفق بود")
                }
        }
    }

    fun verify() {
        val s = _state.value
        if (s.busy || !s.codeComplete) return
        _state.value = s.copy(busy = true, error = null)
        viewModelScope.launch {
            auth.verifyCode(s.phone, s.code)
                .onSuccess { session ->
                    prefs.saveSession(session.phone, session.token, session.referralCode)
                    // بلافاصله پس از ورود: پیشرفت قبلی کاربر از سرور برمی‌گردد
                    ir.speakup.app.data.remote.SyncWorker.syncNow(appContext)
                    _state.value = _state.value.copy(busy = false, done = true)
                }
                .onFailure {
                    _state.value = _state.value.copy(busy = false, error = it.message ?: "کد تأیید نادرست است")
                }
        }
    }

    fun changePhone() {
        _state.value = _state.value.copy(step = AuthStep.PHONE, code = "", error = null, secondsLeft = 0)
    }

    private fun startResendTimer() {
        viewModelScope.launch {
            for (t in AuthService.RESEND_SECONDS downTo 0) {
                _state.value = _state.value.copy(secondsLeft = t)
                delay(1000)
            }
        }
    }
}

/** کد ساختگی برای حساب تست — سرور نادیده‌اش می‌گیرد و فقط شماره را می‌بیند. */
private const val TEST_CODE = "00000"
