package com.suyziss.calendar.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EventRepository(private val eventDao: EventDao) {
    fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    fun getEventsForDay(date: LocalDate): Flow<List<Event>> {
        val start = date.atStartOfDay()
        val end = date.plusDays(1).atStartOfDay()
        return eventDao.getEventsBetween(start, end)
    }

    fun getEventsForMonth(year: Int, month: Int): Flow<List<Event>> {
        val start = LocalDate.of(year, month, 1).atStartOfDay()
        val end = start.plusMonths(1)
        return eventDao.getEventsBetween(start, end)
    }

    fun getEventsForWeek(startOfWeek: LocalDate): Flow<List<Event>> {
        val start = startOfWeek.atStartOfDay()
        val end = startOfWeek.plusDays(7).atStartOfDay()
        return eventDao.getEventsBetween(start, end)
    }

    suspend fun insert(event: Event): Long = eventDao.insert(event)
    suspend fun update(event: Event) = eventDao.update(event)
    suspend fun delete(event: Event) = eventDao.delete(event)
    suspend fun deleteById(id: Long) = eventDao.deleteById(id)
    suspend fun getEventById(id: Long) = eventDao.getEventById(id)
}
