package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant


interface CommitmentRepository {
    fun getCommitment(dayStart: Instant, dayEnd: Instant): Flow<List<CommitmentEntity>>
    suspend fun insertCommitment(commitmentEntity: CommitmentEntity)
    suspend fun checkSchedulingConflictsBetweenCommitments(
        startDateTime: Instant,
        endDateTime: Instant,
        calendarId: Int
    ): Int
}