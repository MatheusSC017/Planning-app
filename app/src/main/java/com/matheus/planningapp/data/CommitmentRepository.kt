package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow

interface CommitmentRepository {
    fun getCommitment(): Flow<List<CommitmentEntity>>
}