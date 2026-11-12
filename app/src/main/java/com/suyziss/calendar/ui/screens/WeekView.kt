package com.suyziss.calendar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suyziss.calendar.data.Event
import com.suyziss.calendar.data.occursOn
import com.suyziss.calendar.ui.components.EventCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekView(
    startOfWeek: LocalDate,
    selectedDate: LocalDate,
    events: List<Event>,
    onDateClick: (LocalDate) -> Unit,
    onEventClick: (Event) -> Unit,
    onEditEvent: (Event) -> Unit,
    onDeleteEvent: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    val dayEvents = events.filter { it.occursOn(selectedDate) }.sortedBy { it.startTime }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { date ->
                val isSelected = date == selectedDate
                val isToday = date == LocalDate.now()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                isToday -> MaterialTheme.colorScheme.secondaryContainer
                                else -> Color.Transparent
                            }
                        )
                        .clickable { onDateClick(date) }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                            isToday -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (dayEvents.isEmpty()) {
                item {
                    Text(
                        text = "Нет событий на этот день",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }
            } else {
                items(dayEvents, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        onClick = { onEventClick(event) },
                        onEdit = { onEditEvent(event) },
                        onDelete = { onDeleteEvent(event) }
                    )
                }
            }
        }
    }
}
