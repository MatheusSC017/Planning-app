package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    suspend fun getCalendars(): Flow<List<CalendarEntity>>
}