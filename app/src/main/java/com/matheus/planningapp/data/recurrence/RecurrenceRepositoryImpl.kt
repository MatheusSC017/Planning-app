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
        /* TODO: Use softdelete in the part to maintain a history of recurrency? */
        recurrenceDao.delete(recurrenceEntity)
    }

    override suspend fun getRecurrenceByCalendar(calendarId: Long): List<RecurrenceEntity> {
        /* TODO: Create page with all the active recurrence by Calendar */
        return recurrenceDao.getRecurrenceByCalendar(calendarId)
    }

    override suspend fun getRecurrenceByCommitment(commitmentId: Long): RecurrenceEntity? {
        return recurrenceDao.getRecurrenceByCommitment(commitmentId)
    }
}