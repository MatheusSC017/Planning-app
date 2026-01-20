package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow

interface CommitmentRepository {
    suspend fun getCommitment(): Flow<List<CommitmentEntity>>
}