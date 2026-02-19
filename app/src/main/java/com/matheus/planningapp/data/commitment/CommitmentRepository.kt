package com.matheus.planningapp.data.commitment

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant


interface CommitmentRepository {
    suspend fun getCommitment(commitmentId: Int): CommitmentEntity?
    fun getCommitmentsForDay(dayStart: Instant, dayEnd: Instant, calendar: Int): Flow<List<CommitmentEntity>>
    suspend fun insertCommitment(commitmentEntity: CommitmentEntity)
    suspend fun updateCommitment(commitmentEntity: CommitmentEntity)
    suspend fun deleteCommitment(commitmentEntity: CommitmentEntity)
    suspend fun checkSchedulingConflictsBetweenCommitments(
        startDateTime: Instant,
        endDateTime: Instant,
        calendarId: Int,
        commitmentId: Int? = null
    ): Int
}