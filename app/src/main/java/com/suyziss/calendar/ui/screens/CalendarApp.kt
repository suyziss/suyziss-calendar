package com.suyziss.calendar.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suyziss.calendar.data.Event
import com.suyziss.calendar.ui.components.EventDialog
import com.suyziss.calendar.viewmodel.CalendarViewMode
import com.suyziss.calendar.viewmodel.CalendarViewModel
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarApp(viewModel: CalendarViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val events by viewModel.events.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<Event?>(null) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<Event?>(null) }

    val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))
    val dayFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
    val shortDateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))

    val title = when (viewMode) {
        CalendarViewMode.MONTH -> currentMonth.format(monthFormatter).replaceFirstChar { it.uppercase() }
        CalendarViewMode.WEEK -> {
            val start = viewModel.getStartOfWeek(selectedDate)
            val end = start.plusDays(6)
            if (start.month == end.month && start.year == end.year) {
                "${start.dayOfMonth}–${end.dayOfMonth} ${end.month.getDisplayName(TextStyle.SHORT, Locale("ru"))} ${end.year}"
            } else "${start.format(shortDateFormatter)} – ${end.format(shortDateFormatter)} ${end.year}"
        }
        CalendarViewMode.DAY -> selectedDate.format(dayFormatter).replaceFirstChar { it.uppercase() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text("Suyziss Calendar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        AnimatedContent(
                            targetState = title,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "calendar-heading"
                        ) { value ->
                            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = viewModel::previousPeriod) {
                        Icon(Icons.Outlined.ChevronLeft, contentDescription = "Предыдущий период")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::goToToday) {
                        Icon(Icons.Outlined.Today, contentDescription = "Сегодня")
                    }
                    IconButton(onClick = viewModel::nextPeriod) {
                        Icon(Icons.Outlined.ChevronRight, contentDescription = "Следующий период")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null) },
                text = { Text("Событие") },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surfaceContainer, tonalElevation = 2.dp) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = viewMode == CalendarViewMode.MONTH,
                        onClick = { viewModel.setViewMode(CalendarViewMode.MONTH) },
                        icon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
                        label = { Text("Месяц") }
                    )
                    NavigationBarItem(
                        selected = viewMode == CalendarViewMode.WEEK,
                        onClick = { viewModel.setViewMode(CalendarViewMode.WEEK) },
                        icon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null) },
                        label = { Text("Неделя") }
                    )
                    NavigationBarItem(
                        selected = viewMode == CalendarViewMode.DAY,
                        onClick = { viewModel.setViewMode(CalendarViewMode.DAY) },
                        icon = { Icon(Icons.Outlined.Today, contentDescription = null) },
                        label = { Text("День") }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
        ) {
            AnimatedVisibility(
                visible = viewMode != CalendarViewMode.MONTH,
                enter = fadeIn() + slideInVertically { -it / 3 },
                exit = fadeOut()
            ) {
                Text(
                    text = "Выберите дату",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 4.dp)
                )
            }
            AnimatedContent(
                targetState = viewMode,
                transitionSpec = {
                    fadeIn() + slideInVertically { it / 12 } togetherWith fadeOut()
                },
                label = "calendar-main-content"
            ) { mode ->
                when (mode) {
                    CalendarViewMode.MONTH -> MonthView(
                        yearMonth = currentMonth,
                        selectedDate = selectedDate,
                        events = events,
                        onDateClick = viewModel::selectDate
                    )
                    CalendarViewMode.WEEK -> WeekView(
                        startOfWeek = viewModel.getStartOfWeek(selectedDate),
                        selectedDate = selectedDate,
                        events = events,
                        onDateClick = viewModel::selectDate,
                        onEventClick = { selectedEvent = it },
                        onEditEvent = { editingEvent = it },
                        onDeleteEvent = { showDeleteConfirm = it }
                    )
                    CalendarViewMode.DAY -> DayView(
                        date = selectedDate,
                        events = events,
                        onEventClick = { selectedEvent = it },
                        onEditEvent = { editingEvent = it },
                        onDeleteEvent = { showDeleteConfirm = it }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        EventDialog(
            initialDate = selectedDate,
            onDismiss = { showAddDialog = false },
            onSave = { eventTitle, description, start, end ->
                viewModel.addEvent(eventTitle, description, start, end)
                showAddDialog = false
            }
        )
    }

    editingEvent?.let { event ->
        EventDialog(
            event = event,
            onDismiss = { editingEvent = null },
            onSave = { eventTitle, description, start, end ->
                viewModel.updateEvent(event.copy(title = eventTitle, description = description, startTime = start, endTime = end))
                editingEvent = null
            }
        )
    }

    selectedEvent?.let { event ->
        AlertDialog(
            onDismissRequest = { selectedEvent = null },
            title = { Text(event.title, style = MaterialTheme.typography.headlineSmall) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${event.startTime.format(DateTimeFormatter.ofPattern("d MMMM, HH:mm", Locale("ru")))} – ${event.endTime.format(DateTimeFormatter.ofPattern("d MMMM, HH:mm", Locale("ru")))}")
                    if (event.description.isNotBlank()) Text(event.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { editingEvent = event; selectedEvent = null }) { Text("Изменить") }
            },
            dismissButton = {
                TextButton(onClick = { selectedEvent = null }) { Text("Закрыть") }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    showDeleteConfirm?.let { event ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Удалить событие?") },
            text = { Text("«${event.title}» будет удалено безвозвратно.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEvent(event)
                        showDeleteConfirm = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Удалить") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = null }) { Text("Отмена") } },
            shape = RoundedCornerShape(28.dp)
        )
    }
}
