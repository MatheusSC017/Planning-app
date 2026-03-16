package com.matheus.planningapp.data.recurrence

class RecurrenceRepositoryImpl (
    private val recurrenceDao: RecurrenceDao
): RecurrenceRepository {

    override suspend fun insert(recurrenceEntity: RecurrenceEntity) {
        recurrenceDao.insert(recurrenceEntity)
    }

    override suspend fun update(recurrenceEntity: RecurrenceEntity) {
        recurrenceDao.update(recurrenceEntity)
    }

    override suspend fun delete(recurrenceEntity: RecurrenceEntity) {
        recurrenceDao.delete(recurrenceEntity)
    }

    override suspend fun getRecurrenceByCalendar(calendarId: Long): List<RecurrenceEntity> {
        return recurrenceDao.getRecurrenceByCalendar(calendarId)
    }

    override suspend fun getRecurrenceByCommitment(commitmentId: Long): RecurrenceEntity? {
        return recurrenceDao.getRecurrenceByCommitment(commitmentId)
    }
}