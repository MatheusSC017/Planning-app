package com.matheus.planningapp.data.commitment

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.matheus.planningapp.util.enums.DayOfWeekEnum
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
        SELECT COUNT(c.id) FROM commitment c
        LEFT JOIN Recurrence r ON c.id = r.commitment
        WHERE (:commitmentId IS NULL OR c.id != :commitmentId) AND 
        c.calendar = :calendarId AND
        (
            (
                c.startDateTime < :endDateTime AND
                c.endDateTime > :startDateTime
            ) OR
            (
                r.id IS NOT NULL  AND
                c.startDateTime <= :startDateTime AND
                (
                    r.frequency = 'DAILY' OR
                    (r.frequency = 'WEEKLY' AND r.dayOfWeekList LIKE '%' || :dayOfWeek || '%') OR
                    (r.frequency = 'MONTHLY' AND r.dayOfMonth = :dayOfMonth) OR
                    (
                        r.frequency = 'CUSTOMIZED' AND
                        CAST((
                            julianday(:startDateTime / 1000, 'unixepoch') -
                            julianday(c.startDateTime / 1000, 'unixepoch')
                        ) AS INTEGER) % r.interval = 0
                    )
                ) AND 
                time(c.startDateTime / 1000, 'unixepoch') < time(:endDateTime / 1000, 'unixepoch') AND
                time(c.endDateTime / 1000, 'unixepoch') > time(:startDateTime / 1000, 'unixepoch')
            )
        )
    """)
    suspend fun checkSchedulingConflictsBetweenCommitments(
        startDateTime: Instant,
        endDateTime: Instant,
        calendarId: Long,
        commitmentId: Long? = null,
        dayOfWeek: DayOfWeekEnum,
        dayOfMonth: Int
    ): Int

    @Query("""
        SELECT * FROM commitment
        WHERE startDateTime > :currentDateTime
    """)
    suspend fun getFutureCommitments(currentDateTime: Instant = Clock.System.now()): List<CommitmentEntity>

    /* TODO: Check Customized query */
    @Query("""
        SELECT c.* FROM Commitment c 
        JOIN Recurrence r ON c.id = r.commitment 
        WHERE 
        c.calendar = :calendarId AND
        c.startDateTime <= :today AND 
        (
            r.frequency = 'DAILY' OR 
            (r.frequency = 'WEEKLY' AND r.dayOfWeekList LIKE '%' || :dayOfWeek || '%') OR 
            (r.frequency = 'MONTHLY' AND r.dayOfMonth = :dayOfMonth) OR 
            (
                r.frequency = 'CUSTOMIZED' AND
                CAST((
                    julianday(:today / 1000, 'unixepoch') -
                    julianday(c.startDateTime / 1000, 'unixepoch')
                ) AS INTEGER) % r.interval = 0
            )
        )
    """)
    fun getCommitmentByRecurrence(
        calendarId: Long,
        today: Instant,
        dayOfWeek: DayOfWeekEnum,
        dayOfMonth: Int
    ): Flow<List<CommitmentEntity>>
}
