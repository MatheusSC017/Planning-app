package com.matheus.planningapp.data.commitment

import com.matheus.planningapp.util.enums.DayOfWeekEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant


class CommitmentRepositoryImpl (
    private val commitmentDao: CommitmentDao
): CommitmentRepository {
    override suspend fun getCommitment(commitmentId: Long): CommitmentEntity? {
        return commitmentDao.getCommitment(commitmentId)
    }

    override fun getCommitmentsForDay(dayStart: Instant, dayEnd: Instant, calendar: Long): Flow<List<CommitmentEntity>> {
        return commitmentDao.getCommitmentsForDay(dayStart, dayEnd, calendar)
    }

    override suspend fun insertCommitment(commitmentEntity: CommitmentEntity): Long {
        return commitmentDao.insert(commitmentEntity)
    }

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
        commitmentId: Long?
    ): Int {
        return commitmentDao.checkSchedulingConflictsBetweenCommitments(startDateTime, endDateTime, calendarId, commitmentId)
    }

    override suspend fun getFutureCommitments(): List<CommitmentEntity> {
        return commitmentDao.getFutureCommitments()
    }

    override fun getCommitmentByRecurrence(today: Instant, dayOfWeek: DayOfWeekEnum, dayOfMonth: Int): Flow<List<CommitmentEntity>> {
        return commitmentDao.getCommitmentByRecurrence(today, dayOfWeek, dayOfMonth)
    }
}