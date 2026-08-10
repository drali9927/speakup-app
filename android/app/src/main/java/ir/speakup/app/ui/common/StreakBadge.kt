package ir.speakup.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.speakup.app.domain.StreakRepository
import ir.speakup.app.domain.toPersianDigits
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * نشان زنجیره در نوار بالا — همیشه در دید، نه پنهان در یک تب.
 *
 * رقیب شعله و نشان اعلان را دائم در نوار بالا نگه می‌دارد؛ این خودش
 * یک محرک بازگشت روزانه است. مخفی کردن آن پشت تب «زنجیره» باعث می‌شود
 * کاربر یادش برود چند روز است پشت‌سر‌هم درس خوانده — سند بازبینی بصری.
 */
@HiltViewModel
class StreakBadgeViewModel @Inject constructor(
    repo: StreakRepository,
) : ViewModel() {
    val length: StateFlow<Int> = flow {
        emit(repo.state().currentLength)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}

@Composable
fun StreakBadge(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    vm: StreakBadgeViewModel = hiltViewModel(),
) {
    val length by vm.length.collectAsStateWithLifecycle()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("🔥", fontSize = 16.sp)
        Text(
            length.toPersianDigits(),
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
        )
    }
}
