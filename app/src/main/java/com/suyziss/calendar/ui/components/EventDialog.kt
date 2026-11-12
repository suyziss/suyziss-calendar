package com.suyziss.calendar.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.suyziss.calendar.data.Event
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun EventDialog(
    event: Event? = null,
    initialDate: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, start: LocalDateTime, end: LocalDateTime) -> Unit
) {
    val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

    var title by remember(event?.id, initialDate) { mutableStateOf(event?.title.orEmpty()) }
    var description by remember(event?.id, initialDate) { mutableStateOf(event?.description.orEmpty()) }
    var startDate by remember(event?.id, initialDate) {
        mutableStateOf((event?.startTime?.toLocalDate() ?: initialDate).format(dateFormat))
    }
    var startTime by remember(event?.id, initialDate) {
        mutableStateOf((event?.startTime?.toLocalTime() ?: LocalTime.of(9, 0)).format(timeFormat))
    }
    var endDate by remember(event?.id, initialDate) {
        mutableStateOf((event?.endTime?.toLocalDate() ?: initialDate).format(dateFormat))
    }
    var endTime by remember(event?.id, initialDate) {
        mutableStateOf((event?.endTime?.toLocalTime() ?: LocalTime.of(10, 0)).format(timeFormat))
    }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                if (event == null) "Новое событие" else "Изменить событие",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                Modifier.heightIn(max = 560.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; validationError = null },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(16.dp)
                )

                Text("Начало", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it; validationError = null },
                    label = { Text("Дата · ГГГГ-ММ-ДД") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it; validationError = null },
                    label = { Text("Время · ЧЧ:ММ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text("Окончание", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it; validationError = null },
                    label = { Text("Дата · ГГГГ-ММ-ДД") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it; validationError = null },
                    label = { Text("Время · ЧЧ:ММ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                AnimatedVisibility(
                    visible = validationError != null,
                    enter = fadeIn() + expandVertically()
                ) {
                    Text(
                        validationError.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    "Можно задать событие на несколько дней. Время окончания должно быть позже начала.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    val parsed = runCatching {
                        val start = LocalDateTime.of(LocalDate.parse(startDate.trim(), dateFormat), LocalTime.parse(startTime.trim(), timeFormat))
                        val end = LocalDateTime.of(LocalDate.parse(endDate.trim(), dateFormat), LocalTime.parse(endTime.trim(), timeFormat))
                        start to end
                    }.getOrElse {
                        validationError = "Проверь дату и время. Формат: 2026-11-13 и 09:30."
                        return@FilledTonalButton
                    }

                    when {
                        title.isBlank() -> validationError = "Укажи название события."
                        !parsed.second.isAfter(parsed.first) -> validationError = "Окончание должно быть позже начала."
                        else -> onSave(title.trim(), description.trim(), parsed.first, parsed.second)
                    }
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
