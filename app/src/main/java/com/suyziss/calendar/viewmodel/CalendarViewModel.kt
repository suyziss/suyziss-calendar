package com.suyziss.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.suyziss.calendar.data.Event
import com.suyziss.calendar.data.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

enum class CalendarViewMode {
    MONTH, WEEK, DAY
}

class CalendarViewModel(private val repository: EventRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _viewMode = MutableStateFlow(CalendarViewMode.MONTH)
    val viewMode: StateFlow<CalendarViewMode> = _viewMode.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    val events: StateFlow<List<Event>> = repository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setViewMode(mode: CalendarViewMode) {
        _viewMode.value = mode
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _currentMonth.value = YearMonth.from(date)
    }

    fun goToToday() {
        val today = LocalDate.now()
        _selectedDate.value = today
        _currentMonth.value = YearMonth.from(today)
    }

    fun previousPeriod() {
        when (_viewMode.value) {
            CalendarViewMode.MONTH -> {
                _currentMonth.value = _currentMonth.value.minusMonths(1)
                _selectedDate.value = _currentMonth.value.atDay(1)
            }
            CalendarViewMode.WEEK -> {
                _selectedDate.value = _selectedDate.value.minusWeeks(1)
            }
            CalendarViewMode.DAY -> {
                _selectedDate.value = _selectedDate.value.minusDays(1)
            }
        }
    }

    fun nextPeriod() {
        when (_viewMode.value) {
            CalendarViewMode.MONTH -> {
                _currentMonth.value = _currentMonth.value.plusMonths(1)
                _selectedDate.value = _currentMonth.value.atDay(1)
            }
            CalendarViewMode.WEEK -> {
                _selectedDate.value = _selectedDate.value.plusWeeks(1)
            }
            CalendarViewMode.DAY -> {
                _selectedDate.value = _selectedDate.value.plusDays(1)
            }
        }
    }

    fun getStartOfWeek(date: LocalDate = _selectedDate.value): LocalDate {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    fun addEvent(title: String, description: String, start: LocalDateTime, end: LocalDateTime, color: Long = 0xFF6750A4) {
        viewModelScope.launch {
            repository.insert(
                Event(
                    title = title,
                    description = description,
                    startTime = start,
                    endTime = end,
                    color = color
                )
            )
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            repository.update(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repository.delete(event)
        }
    }

    fun eventsForDate(date: LocalDate): List<Event> {
        return events.value.filter {
            it.startTime.toLocalDate() == date
        }
    }
}

class CalendarViewModelFactory(private val repository: EventRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
