package com.suyziss.calendar.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
            } else {
                "${start.format(shortDateFormatter)} – ${end.format(shortDateFormatter)} ${end.year}"
            }
        }
        CalendarViewMode.DAY -> selectedDate.format(dayFormatter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.previousPeriod() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Предыдущий период")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.goToToday() }) {
                        Icon(Icons.Default.Today, contentDescription = "Сегодня")
                    }
                    IconButton(onClick = { viewModel.nextPeriod() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Следующий период")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить событие")
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 3.dp
            ) {
                NavigationBarItem(
                    selected = viewMode == CalendarViewMode.MONTH,
                    onClick = { viewModel.setViewMode(CalendarViewMode.MONTH) },
                    icon = { Text("📅", style = MaterialTheme.typography.titleMedium) },
                    label = { Text("Месяц") }
                )
                NavigationBarItem(
                    selected = viewMode == CalendarViewMode.WEEK,
                    onClick = { viewModel.setViewMode(CalendarViewMode.WEEK) },
                    icon = { Text("🗓️", style = MaterialTheme.typography.titleMedium) },
                    label = { Text("Неделя") }
                )
                NavigationBarItem(
                    selected = viewMode == CalendarViewMode.DAY,
                    onClick = { viewModel.setViewMode(CalendarViewMode.DAY) },
                    icon = { Text("📋", style = MaterialTheme.typography.titleMedium) },
                    label = { Text("День") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AnimatedContent(
                targetState = viewMode,
                transitionSpec = {
                    fadeIn() + slideInHorizontally() togetherWith fadeOut() + slideOutHorizontally()
                },
                label = "calendar-view-mode"
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
                        onEventClick = { },
                        onEditEvent = { editingEvent = it },
                        onDeleteEvent = { showDeleteConfirm = it }
                    )
                    CalendarViewMode.DAY -> DayView(
                        date = selectedDate,
                        events = events,
                        onEventClick = { },
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
                viewModel.updateEvent(
                    event.copy(
                        title = eventTitle,
                        description = description,
                        startTime = start,
                        endTime = end
                    )
                )
                editingEvent = null
            }
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
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Отмена")
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }
}
