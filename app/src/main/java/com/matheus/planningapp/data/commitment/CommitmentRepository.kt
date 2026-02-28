package com.matheus.planningapp.data.commitment

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant


interface CommitmentRepository {
    suspend fun getCommitment(commitmentId: Long): CommitmentEntity?
    fun getCommitmentsForDay(dayStart: Instant, dayEnd: Instant, calendar: Long): Flow<List<CommitmentEntity>>
    suspend fun insertCommitment(commitmentEntity: CommitmentEntity): Long
    suspend fun updateCommitment(commitmentEntity: CommitmentEntity)
    suspend fun deleteCommitment(commitmentEntity: CommitmentEntity)
    suspend fun checkSchedulingConflictsBetweenCommitments(
        startDateTime: Instant,
        endDateTime: Instant,
        calendarId: Long,
        commitmentId: Long? = null
    ): Int
}