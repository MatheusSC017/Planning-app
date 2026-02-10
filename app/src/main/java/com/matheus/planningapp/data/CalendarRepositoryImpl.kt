package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class CalendarRepositoryImpl(
    private val calendarDao: CalendarDao
): CalendarRepository {
    override suspend fun insertCalendar(calendarEntity: CalendarEntity): Long {
        return calendarDao.insert(calendarEntity)
    }

    override suspend fun updateCalendar(calendarEntity: CalendarEntity) {
        calendarDao.updateWithDateTime(calendarEntity)
    }

    override suspend fun deleteCalendar(calendarEntity: CalendarEntity) {
        calendarDao.delete(calendarEntity)
    }

    override fun getCalendars(): Flow<List<CalendarEntity>> {
        return calendarDao.getCalendars()
    }

    override suspend fun getCalendarById(calendarId: Int): CalendarEntity? {
        return calendarDao.getCalendarById(calendarId)
    }

    override suspend fun ensureDefaultCalendarExists() {
        if (calendarDao.countCalendars() == 0) {
            calendarDao.insert(
                CalendarEntity(
                    name = "Default",
                    isDefault = true
                )
            )
        }
    }

    override suspend fun setAllDefaultAsFalse() {
        calendarDao.setAllDefaultAsFalse()
    }

}