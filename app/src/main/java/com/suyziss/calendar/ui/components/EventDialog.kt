package com.suyziss.calendar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suyziss.calendar.data.Event
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun EventDialog(
    event: Event? = null,
    initialDate: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, start: LocalDateTime, end: LocalDateTime) -> Unit
) {
    var title by remember(event?.id) { mutableStateOf(event?.title.orEmpty()) }
    var description by remember(event?.id) { mutableStateOf(event?.description.orEmpty()) }
    var startHour by remember(event?.id) { mutableIntStateOf(event?.startTime?.hour ?: 9) }
    var startMinute by remember(event?.id) { mutableIntStateOf(event?.startTime?.minute ?: 0) }
    var endHour by remember(event?.id) { mutableIntStateOf(event?.endTime?.hour ?: 10) }
    var endMinute by remember(event?.id) { mutableIntStateOf(event?.endTime?.minute ?: 0) }
    var endNextDay by remember(event?.id) {
        mutableStateOf(
            event?.let { it.endTime.toLocalDate().isAfter(it.startTime.toLocalDate()) } ?: false
        )
    }

    val date = event?.startTime?.toLocalDate() ?: initialDate
    val start = LocalDateTime.of(date, LocalTime.of(startHour, startMinute))
    val sameDayEnd = LocalDateTime.of(date, LocalTime.of(endHour, endMinute))
    val resolvedEnd = when {
        endNextDay || !sameDayEnd.isAfter(start) -> sameDayEnd.plusDays(1)
        else -> sameDayEnd
    }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val endsNextDay = resolvedEnd.toLocalDate().isAfter(date)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = if (event == null) "Новое событие" else "Редактировать событие",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "Дата: $date",
                    style = MaterialTheme.typography.labelLarge
                )
                Text("Начало: ${start.format(timeFormatter)}")
                Text("Час начала")
                Slider(
                    value = startHour.toFloat(),
                    onValueChange = { startHour = it.toInt() },
                    valueRange = 0f..23f,
                    steps = 22
                )
                Text("Минуты начала: ${startMinute.toString().padStart(2, '0')}")
                Slider(
                    value = startMinute.toFloat(),
                    onValueChange = { startMinute = (it.toInt() / 5) * 5 },
                    valueRange = 0f..55f,
                    steps = 10
                )

                Text("Окончание: ${resolvedEnd.format(timeFormatter)}")
                Text("Час окончания")
                Slider(
                    value = endHour.toFloat(),
                    onValueChange = { endHour = it.toInt() },
                    valueRange = 0f..23f,
                    steps = 22
                )
                Text("Минуты окончания: ${endMinute.toString().padStart(2, '0')}")
                Slider(
                    value = endMinute.toFloat(),
                    onValueChange = { endMinute = (it.toInt() / 5) * 5 },
                    valueRange = 0f..55f,
                    steps = 10
                )
                if (endsNextDay) {
                    Text(
                        text = "Окончание — на следующий день",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title.trim(), description.trim(), start, resolvedEnd)
                    }
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
