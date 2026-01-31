package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant


interface CommitmentRepository {
    fun getCommitmentsForDay(dayStart: Instant, dayEnd: Instant, calendar: Int): Flow<List<CommitmentEntity>>
    suspend fun insertCommitment(commitmentEntity: CommitmentEntity)
    suspend fun checkSchedulingConflictsBetweenCommitments(
        startDateTime: Instant,
        endDateTime: Instant,
        calendarId: Int
    ): Int
}