package com.matheus.planningapp.data.commitment

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Dao
interface CommitmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commitmentEntity: CommitmentEntity): Long

    @Update
    suspend fun update(commitmentEntity: CommitmentEntity)

    @Delete
    suspend fun delete(commitmentEntity: CommitmentEntity)

    @Query("SELECT * FROM commitment WHERE id = :commitmentId")
    suspend fun getCommitment(commitmentId: Long): CommitmentEntity?

    @Query("""
        SELECT * FROM commitment
        WHERE startDateTime >= :dayStart AND startDateTime < :dayEnd AND calendar = :calendar
        ORDER BY startDateTime
    """)
    fun getCommitmentsForDay(
        dayStart: Instant,
        dayEnd: Instant,
        calendar: Long
    ): Flow<List<CommitmentEntity>>

    @Query("""
        SELECT COUNT(*) FROM commitment 
        WHERE calendar = :calendarId AND
        startDateTime < :endDateTime AND
        endDateTime > :startDateTime AND
        (:commitmentId IS NULL OR id != :commitmentId)
    """)
    suspend fun checkSchedulingConflictsBetweenCommitments(
        startDateTime: Instant,
        endDateTime: Instant,
        calendarId: Long,
        commitmentId: Long? = null
    ): Int

    @Query("""
        SELECT * FROM commitment
        WHERE startDateTime > :currentDateTime
    """)
    suspend fun getFutureCommitments(currentDateTime: Instant = Clock.System.now()): List<CommitmentEntity>
}
