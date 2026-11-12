package com.suyziss.calendar.ui.components

import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    event: Event? = null,
    initialDate: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, start: LocalDateTime, end: LocalDateTime) -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var startHour by remember { mutableIntStateOf(event?.startTime?.hour ?: 9) }
    var startMinute by remember { mutableIntStateOf(event?.startTime?.minute ?: 0) }
    var endHour by remember { mutableIntStateOf(event?.endTime?.hour ?: 10) }
    var endMinute by remember { mutableIntStateOf(event?.endTime?.minute ?: 0) }

    val isEdit = event != null

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = if (isEdit) "Редактировать событие" else "Новое событие",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Text(
                    text = "Время: ${String.format("%02d:%02d", startHour, startMinute)} – ${String.format("%02d:%02d", endHour, endMinute)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                // Simple time pickers via sliders for demo (in real app use TimePicker)
                Text("Начало (час)", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = startHour.toFloat(),
                    onValueChange = { startHour = it.toInt() },
                    valueRange = 0f..23f,
                    steps = 22
                )
                Text("Конец (час)", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = endHour.toFloat(),
                    onValueChange = { endHour = it.toInt() },
                    valueRange = 0f..23f,
                    steps = 22
                )
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val date = event?.startTime?.toLocalDate() ?: initialDate
                        val start = LocalDateTime.of(date, LocalTime.of(startHour, startMinute))
                        val end = LocalDateTime.of(date, LocalTime.of(endHour.coerceAtLeast(startHour), endMinute))
                        onSave(title, description, start, end)
                    }
                },
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
