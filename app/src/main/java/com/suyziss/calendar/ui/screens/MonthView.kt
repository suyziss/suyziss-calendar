package com.suyziss.calendar.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.suyziss.calendar.data.Event
import com.suyziss.calendar.data.occursOn
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthView(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    events: List<Event>,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val startOffset = yearMonth.atDay(1).dayOfWeek.value - 1
    val days = buildList<LocalDate?> {
        repeat(startOffset) { add(null) }
        for (day in 1..daysInMonth) add(yearMonth.atDay(day))
    }
    val weekDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    Column(modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { day ->
                Text(
                    day,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
            contentPadding = PaddingValues(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(days) { date ->
                if (date == null) {
                    Spacer(Modifier.aspectRatio(0.82f))
                } else {
                    val dayEvents = events.filter { it.occursOn(date) }
                    val selected = date == selectedDate
                    val today = date == LocalDate.now()
                    val targetColor = when {
                        selected -> MaterialTheme.colorScheme.primaryContainer
                        today -> MaterialTheme.colorScheme.secondaryContainer
                        else -> Color.Transparent
                    }
                    val cellColor by animateColorAsState(targetColor, label = "calendar-day-selection")
                    val textColor by animateColorAsState(
                        when {
                            selected -> MaterialTheme.colorScheme.onPrimaryContainer
                            today -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        label = "calendar-day-text"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.82f)
                            .animateContentSize()
                            .clip(RoundedCornerShape(18.dp))
                            .background(cellColor)
                            .clickable { onDateClick(date) }
                            .padding(vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (selected || today) FontWeight.Bold else FontWeight.Normal,
                            color = textColor
                        )
                        Spacer(Modifier.height(5.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            dayEvents.take(3).forEach { event ->
                                Box(
                                    Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color(event.color))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
