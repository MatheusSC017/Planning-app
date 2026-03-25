package com.matheus.planningapp.data.recurrence

import kotlinx.coroutines.flow.Flow

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

    override suspend fun getRecurrenceById(recurrenceId: Long): RecurrenceEntity? {
        return recurrenceDao.getRecurrenceById(recurrenceId)
    }

    override fun getRecurrenceByCalendar(calendarId: Long): Flow<List<CommitmentRecurrenceDataClass>> {
        return recurrenceDao.getRecurrenceByCalendar(calendarId)
    }

    override suspend fun getRecurrenceByCommitment(commitmentId: Long): RecurrenceEntity? {
        return recurrenceDao.getRecurrenceByCommitment(commitmentId)
    }
}