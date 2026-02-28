package com.matheus.planningapp.data.calendar

import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    suspend fun insertCalendar(calendarEntity: CalendarEntity): Long
    suspend fun updateCalendar(calendarEntity: CalendarEntity)
    suspend fun deleteCalendar(calendarEntity: CalendarEntity)
    fun getCalendars(): Flow<List<CalendarEntity>>
    suspend fun getCalendarById(calendarId: Long): CalendarEntity?
    suspend fun ensureDefaultCalendarExists()
    suspend fun setAllDefaultAsFalse()
}