package com.suyziss.calendar.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val color: Long = 0xFF6750A4
)

/**
 * Returns true when the event overlaps any part of the given calendar day.
 * The end time is exclusive, matching normal calendar interval semantics.
 */
fun Event.occursOn(date: LocalDate): Boolean {
    val dayStart = date.atStartOfDay()
    val nextDayStart = date.plusDays(1).atStartOfDay()
    return startTime < nextDayStart && endTime > dayStart
}
