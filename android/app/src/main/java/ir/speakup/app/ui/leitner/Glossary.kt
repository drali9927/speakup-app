package ir.speakup.app.ui.leitner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.speakup.app.data.local.DictionaryEntryEntity
import ir.speakup.app.ui.theme.ltr

/**
 * واژه‌نامه — جست‌وجو در همان تب لایتنر.
 *
 * چرا تب تازه نساختیم: نوار پایین از قبل پنج تب دارد و ششمی آن را
 * شلوغ می‌کند. واژه‌نامه و جعبه لایتنر هر دو «جای واژه‌ها»یند و کنار
 * هم بودنشان طبیعی است — همان کاری که رقیب هم می‌کند.
 *
 * داده‌اش از قبل روی دستگاه بود: ۲٬۰۸۸ واژه با تلفظ، معنی و مثال.
 * تنها چیزی که کم بود، راهی برای دیدنشان.
 */
@Composable
fun GlossarySearch(
    query: String,
    results: List<DictionaryEntryEntity>,
    onQueryChange: (String) -> Unit,
    onPick: (DictionaryEntryEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme

    Column(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            placeholder = { Text("جست‌وجوی واژه — فارسی یا انگلیسی") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        )

        if (query.isBlank()) return@Column

        if (results.isEmpty()) {
            Spacer(Modifier.size(12.dp))
            Text(
                "واژه‌ای پیدا نشد",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            )
            return@Column
        }

        Spacer(Modifier.size(8.dp))
        // سقف ارتفاع، وگرنه فهرست نتایج کل صفحه را می‌گیرد و جعبه
        // لایتنر که زیرش است دیگر دیده نمی‌شود.
        LazyColumn(
            Modifier.fillMaxWidth().heightIn(max = 360.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(results, key = { it.id }) { e ->
                Surface(
                    Modifier.fillMaxWidth().clickable { onPick(e) },
                    shape = RoundedCornerShape(12.dp),
                    color = scheme.surfaceVariant,
                ) {
                    Row(
                        Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                e.word,
                                style = MaterialTheme.typography.titleMedium.ltr(),
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                e.translationFa,
                                style = MaterialTheme.typography.bodyMedium,
                                color = scheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        e.ipaUk?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall.ltr(),
                                color = scheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * جزئیات یک واژه از واژه‌نامه.
 *
 * همان اطلاعاتی که کارت واژه در درس نشان می‌دهد، به‌علاوه دکمه افزودن
 * به جعبه لایتنر — چون کاربری که واژه‌ای را جست‌وجو می‌کند، احتمالاً
 * می‌خواهد یادش بگیرد و نه فقط یک بار ببیندش.
 */
@Composable
fun WordSheet(
    entry: DictionaryEntryEntity,
    inBox: Boolean,
    speechStatus: ir.speakup.app.domain.SpeechService.Status,
    speakingId: String?,
    onSpeak: (String, String) -> Unit,
    onUnavailable: () -> Unit,
    onAdd: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ir.speakup.app.ui.common.SpeakButton(
                    text = entry.word, id = "gl-${entry.id}",
                    status = speechStatus, speakingId = speakingId,
                    onSpeak = onSpeak, onUnavailable = onUnavailable,
                )
                Text(entry.word, style = MaterialTheme.typography.headlineSmall.ltr(), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    entry.pos.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium.ltr(), color = scheme.onSurfaceVariant)
                    }
                    entry.ipaUk?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium.ltr(), color = scheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.size(10.dp))
                Text(entry.translationFa, style = MaterialTheme.typography.titleMedium)
                entry.exampleEn?.let {
                    Spacer(Modifier.size(14.dp))
                    Text(it, style = MaterialTheme.typography.bodyLarge.ltr())
                }
                entry.exampleFa?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = scheme.onSurfaceVariant)
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onAdd, enabled = !inBox) {
                Text(if (inBox) "در جعبه هست" else "افزودن به لایتنر")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { Text("بستن") }
        },
    )
}
