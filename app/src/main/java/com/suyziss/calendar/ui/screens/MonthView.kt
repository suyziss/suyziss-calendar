package com.suyziss.calendar.ui.screens

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

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 4.dp),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(days) { date ->
                if (date == null) {
                    Spacer(modifier = Modifier.aspectRatio(1f))
                } else {
                    val dayEvents = events.filter { it.occursOn(date) }
                    val isSelected = date == selectedDate
                    val isToday = date == LocalDate.now()

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                    isToday -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { onDateClick(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                    isToday -> MaterialTheme.colorScheme.onSecondaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                            if (dayEvents.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    dayEvents.take(3).forEach { event ->
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
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
    }
}
