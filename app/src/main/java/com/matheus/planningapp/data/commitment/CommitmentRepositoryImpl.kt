package com.matheus.planningapp.data.commitment

import com.matheus.planningapp.util.enums.DayOfWeekEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CommitmentRepositoryImpl(
    private val commitmentDao: CommitmentDao,
) : CommitmentRepository {
    override suspend fun getCommitment(commitmentId: Long): CommitmentEntity? = commitmentDao.getCommitment(commitmentId)

    override fun getCommitmentsForDay(
        dayStart: Instant,
        dayEnd: Instant,
        calendar: Long,
    ): Flow<List<CommitmentEntity>> = commitmentDao.getCommitmentsForDay(dayStart, dayEnd, calendar)

    override fun searchCommitments(
        query: String,
        calendarId: Long,
    ): Flow<List<CommitmentEntity>> = commitmentDao.searchCommitments(query, calendarId)

    override suspend fun insertCommitment(commitmentEntity: CommitmentEntity): Long = commitmentDao.insert(commitmentEntity)

    override suspend fun updateCommitment(commitmentEntity: CommitmentEntity) {
        commitmentDao.update(commitmentEntity)
    }

    override suspend fun deleteCommitment(commitmentEntity: CommitmentEntity) {
        commitmentDao.delete(commitmentEntity)
    }

    override suspend fun checkSchedulingConflictsBetweenCommitments(
        startDateTime: Instant,
        endDateTime: Instant,
        calendarId: Long,
        commitmentId: Long?,
    ): Int {
        val localDateTime: LocalDateTime = startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
        return commitmentDao.checkSchedulingConflictsBetweenCommitments(
            startDateTime,
            endDateTime,
            calendarId,
            commitmentId,
            DayOfWeekEnum.valueOf(localDateTime.dayOfWeek.name),
            localDateTime.dayOfMonth,
        )
    }

    override suspend fun getFutureCommitments(): List<CommitmentEntity> = commitmentDao.getFutureCommitments()

    override fun getCommitmentByRecurrence(
        calendar: Long,
        today: Instant,
        dayOfWeek: DayOfWeekEnum,
        dayOfMonth: Int,
    ): Flow<List<CommitmentEntity>> = commitmentDao.getCommitmentByRecurrence(calendar, today, dayOfWeek, dayOfMonth)
}
