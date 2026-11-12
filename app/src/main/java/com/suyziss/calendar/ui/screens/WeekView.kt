package com.suyziss.calendar.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    Column(modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            days.forEach { date ->
                val selected = date == selectedDate
                val today = date == LocalDate.now()
                val bg by animateColorAsState(
                    when {
                        selected -> MaterialTheme.colorScheme.primaryContainer
                        today -> MaterialTheme.colorScheme.secondaryContainer
                        else -> Color.Transparent
                    },
                    label = "week-date-background"
                )
                val verticalPadding by animateDpAsState(if (selected) 12.dp else 9.dp, label = "week-date-size")
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(bg)
                        .clickable { onDateClick(date) }
                        .padding(vertical = verticalPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selected || today) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        androidx.compose.animation.AnimatedContent(
            targetState = selectedDate,
            transitionSpec = {
                androidx.compose.animation.fadeIn() togetherWith androidx.compose.animation.fadeOut()
            },
            label = "week-events-date"
        ) { date ->
            val visibleEvents = events.filter { it.occursOn(date) }.sortedBy { it.startTime }
            LazyColumn(
                Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (visibleEvents.isEmpty()) {
                    item {
                        Text(
                            "На этот день событий нет",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 36.dp)
                        )
                    }
                } else {
                    items(visibleEvents, key = { it.id }) { event ->
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
}
