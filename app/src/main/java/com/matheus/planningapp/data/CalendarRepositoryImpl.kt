package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow
import java.time.Instant

class CalendarRepositoryImpl(
    private val calendarDao: CalendarDao
): CalendarRepository {

    override fun getCalendars(): Flow<List<CalendarEntity>> {
        return calendarDao.getCalendars()
    }

    override suspend fun ensureDefaultCalendarExists() {
        if (calendarDao.countCalendars() == 0) {
            calendarDao.insert(
                CalendarEntity(
                    id = 0,
                    name = "Default",
                    isDefault = true,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
            )
        }
    }

}