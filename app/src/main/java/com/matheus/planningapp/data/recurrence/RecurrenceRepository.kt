package com.matheus.planningapp.data.recurrence

import kotlinx.coroutines.flow.Flow

interface RecurrenceRepository {
    suspend fun insert(recurrenceEntity: RecurrenceEntity)
    suspend fun update(recurrenceEntity: RecurrenceEntity)
    suspend fun delete(recurrenceEntity: RecurrenceEntity)
    suspend fun getRecurrenceById(recurrenceId: Long): RecurrenceEntity?
    fun getRecurrenceByCalendar(calendarId: Long): Flow<List<CommitmentRecurrenceDataClass>>
    suspend fun getRecurrenceByCommitment(commitmentId: Long): RecurrenceEntity?
}