package com.matheus.planningapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface CommitmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commitmentEntity: CommitmentEntity)

    @Query("""
        SELECT * FROM commitment
        WHERE startDateTime >= :dayStart AND startDateTime < :dayEnd
        ORDER BY startDateTime
    """)
    fun getCommitmentsForDay(
        dayStart: Instant,
        dayEnd: Instant
    ): Flow<List<CommitmentEntity>>

    @Query("""
        SELECT COUNT(*) FROM commitment 
        WHERE calendar = :calendarId AND
        startDateTime < :endDateTime AND
        endDateTime > :startDateTime
    """)
    suspend fun checkSchedulingConflictsBetweenCommitments(
        startDateTime: Instant,
        endDateTime: Instant,
        calendarId: Int): Int
}
