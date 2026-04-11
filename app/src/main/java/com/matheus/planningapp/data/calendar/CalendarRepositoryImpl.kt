package com.matheus.planningapp.data.calendar

import kotlinx.coroutines.flow.Flow

class CalendarRepositoryImpl(
    private val calendarDao: CalendarDao,
) : CalendarRepository {
    override suspend fun insertCalendar(calendarEntity: CalendarEntity): Long = calendarDao.insert(calendarEntity)

    override suspend fun updateCalendar(calendarEntity: CalendarEntity) {
        calendarDao.updateWithDateTime(calendarEntity)
    }

    override suspend fun deleteCalendar(calendarEntity: CalendarEntity) {
        calendarDao.delete(calendarEntity)
    }

    override fun getCalendars(): Flow<List<CalendarEntity>> = calendarDao.getCalendars()

    override suspend fun getCalendarById(calendarId: Long): CalendarEntity? = calendarDao.getCalendarById(calendarId)

    override suspend fun ensureDefaultCalendarExists() {
        if (calendarDao.countCalendars() == 0) {
            calendarDao.insert(
                CalendarEntity(
                    name = "Default",
                    isDefault = true,
                ),
            )
        }
    }

    override suspend fun setAllDefaultAsFalse() {
        calendarDao.setAllDefaultAsFalse()
    }
}
