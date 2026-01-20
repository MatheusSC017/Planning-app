package com.matheus.planningapp.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CommitmentRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val commitmentDao: CommitmentDao
): CommitmentRepository {
    override suspend fun getCommitment(): Flow<List<CommitmentEntity>> {
        return withContext(dispatcher) {
            commitmentDao.getCommitments()
        }
    }
}