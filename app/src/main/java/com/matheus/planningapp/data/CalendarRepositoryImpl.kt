package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class CalendarRepositoryImpl(
    private val calendarDao: CalendarDao
): CalendarRepository {
    override suspend fun insertCalendar(calendarEntity: CalendarEntity) {
        calendarDao.insert(calendarEntity)
    }

    override fun getCalendars(): Flow<List<CalendarEntity>> {
        return calendarDao.getCalendars()
    }

    override suspend fun ensureDefaultCalendarExists() {
        if (calendarDao.countCalendars() == 0) {
            calendarDao.insert(
                CalendarEntity(
                    name = "Default",
                    isDefault = true,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            )
        }
    }

}