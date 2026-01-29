package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant


class CommitmentRepositoryImpl(
    private val commitmentDao: CommitmentDao
): CommitmentRepository {
    override fun getCommitmentsForDay(dayStart: Instant, dayEnd: Instant): Flow<List<CommitmentEntity>> {
        return commitmentDao.getCommitmentsForDay(dayStart, dayEnd)
    }

    override suspend fun insertCommitment(commitmentEntity: CommitmentEntity) {
        commitmentDao.insert(commitmentEntity)
    }

    override suspend fun checkSchedulingConflictsBetweenCommitments(
        startDateTime: Instant,
        endDateTime: Instant,
        calendarId: Int
    ): Int {
        return commitmentDao.checkSchedulingConflictsBetweenCommitments(startDateTime, endDateTime, calendarId)
    }
}