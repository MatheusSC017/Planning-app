package com.matheus.planningapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommitmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commitmentEntity: CommitmentEntity)

    @Query("SELECT * FROM Commitment")
    fun getCommitments(): Flow<List<CommitmentEntity>>
}
