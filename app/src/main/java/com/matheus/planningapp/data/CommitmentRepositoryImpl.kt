package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant


class CommitmentRepositoryImpl(
    private val commitmentDao: CommitmentDao
): CommitmentRepository {
    override suspend fun getCommitment(commitmentId: Int): CommitmentEntity? {
        return commitmentDao.getCommitment(commitmentId)
    }

    override fun getCommitmentsForDay(dayStart: Instant, dayEnd: Instant, calendar: Int): Flow<List<CommitmentEntity>> {
        return commitmentDao.getCommitmentsForDay(dayStart, dayEnd, calendar)
    }

    override suspend fun insertCommitment(commitmentEntity: CommitmentEntity) {
        commitmentDao.insert(commitmentEntity)
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
        calendarId: Int,
        commitmentId: Int?
    ): Int {
        return commitmentDao.checkSchedulingConflictsBetweenCommitments(startDateTime, endDateTime, calendarId, commitmentId)
    }
}