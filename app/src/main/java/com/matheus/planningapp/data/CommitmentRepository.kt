package com.matheus.planningapp.data

import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface CommitmentRepository {
    fun getCommitment(dayStart: Instant, dayEnd: Instant): Flow<List<CommitmentEntity>>
}