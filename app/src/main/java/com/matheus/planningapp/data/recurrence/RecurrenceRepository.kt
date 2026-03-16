package com.matheus.planningapp.data.recurrence

interface RecurrenceRepository {
    suspend fun insert(recurrenceEntity: RecurrenceEntity)
    suspend fun update(recurrenceEntity: RecurrenceEntity)
    suspend fun delete(recurrenceEntity: RecurrenceEntity)
    suspend fun getRecurrenceByCalendar(calendarId: Long): List<RecurrenceEntity>
    suspend fun getRecurrenceByCommitment(commitmentId: Long): RecurrenceEntity?
}