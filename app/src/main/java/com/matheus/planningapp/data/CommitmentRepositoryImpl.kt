package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow
import java.time.Instant

class CommitmentRepositoryImpl(
    private val commitmentDao: CommitmentDao
): CommitmentRepository {
    override fun getCommitment(dayStart: Instant, dayEnd: Instant): Flow<List<CommitmentEntity>> {
        return commitmentDao.getCommitmentsForDay(dayStart, dayEnd)
    }
}