package com.matheus.planningapp.data.commitment

import com.matheus.planningapp.util.enums.DayOfWeekEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface CommitmentRepository {
    suspend fun getCommitment(commitmentId: Long): CommitmentEntity?

    fun getCommitmentsForDay(
        dayStart: Instant,
        dayEnd: Instant,
        calendar: Long,
    ): Flow<List<CommitmentEntity>>

    fun searchCommitments(
        query: String,
        calendarId: Long,
    ): Flow<List<CommitmentEntity>>

    suspend fun insertCommitment(commitmentEntity: CommitmentEntity): Long

    suspend fun updateCommitment(commitmentEntity: CommitmentEntity)

    suspend fun deleteCommitment(commitmentEntity: CommitmentEntity)

    suspend fun checkSchedulingConflictsBetweenCommitments(
        startDateTime: Instant,
        endDateTime: Instant,
        calendarId: Long,
        commitmentId: Long? = null,
    ): Int

    suspend fun getFutureCommitments(): List<CommitmentEntity>

    fun getCommitmentByRecurrence(
        calendar: Long,
        today: Instant,
        dayOfWeek: DayOfWeekEnum,
        dayOfMonth: Int,
    ): Flow<List<CommitmentEntity>>
}
