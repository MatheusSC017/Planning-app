package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    suspend fun insertCalendar(calendarEntity: CalendarEntity)
    fun getCalendars(): Flow<List<CalendarEntity>>
    suspend fun ensureDefaultCalendarExists()
    suspend fun setAllDefaultAsFalse()
}