package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow

class CommitmentRepositoryImpl(
    private val commitmentDao: CommitmentDao
): CommitmentRepository {
    override fun getCommitment(): Flow<List<CommitmentEntity>> {
        return commitmentDao.getCommitments()
    }
}