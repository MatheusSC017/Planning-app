package com.matheus.planningapp.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CalendarRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val calendarDao: CalendarDao
): CalendarRepository {
    override suspend fun getCalendars(): Flow<List<CalendarEntity>> {
        return withContext(dispatcher) {
            calendarDao.getCalendars()
        }
    }
}