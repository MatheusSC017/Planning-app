package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun getCalendars(): Flow<List<CalendarEntity>>
    suspend fun ensureDefaultCalendarExists()
}