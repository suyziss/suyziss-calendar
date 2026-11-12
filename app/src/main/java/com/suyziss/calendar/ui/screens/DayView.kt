package com.suyziss.calendar.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suyziss.calendar.data.Event
import com.suyziss.calendar.data.occursOn
import com.suyziss.calendar.ui.components.EventCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DayView(
    date: LocalDate,
    events: List<Event>,
    onEventClick: (Event) -> Unit,
    onEditEvent: (Event) -> Unit,
    onDeleteEvent: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("ru"))
    val dayEvents = events.filter { it.occursOn(date) }.sortedBy { it.startTime }

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(
            date.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 14.dp)
        )
        AnimatedVisibility(
            visible = dayEvents.isEmpty(),
            enter = fadeIn() + slideInVertically { it / 5 }
        ) {
            Text(
                "Свободный день",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 28.dp)
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
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
