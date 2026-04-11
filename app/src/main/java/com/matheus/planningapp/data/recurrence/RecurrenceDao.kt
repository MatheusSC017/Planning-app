package com.matheus.planningapp.data.recurrence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurrenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurrenceEntity: RecurrenceEntity)

    @Update
    suspend fun update(recurrenceEntity: RecurrenceEntity)

    @Delete
    suspend fun delete(recurrenceEntity: RecurrenceEntity)

    @Query(
        """
        SELECT * FROM Recurrence 
        WHERE id = :recurrenceId
    """,
    )
    suspend fun getRecurrenceById(recurrenceId: Long): RecurrenceEntity?

    @Query(
        """
        SELECT 
            c.id AS commitmentId, r.id AS recurrenceId, c.title, c.description, c.startDateTime, 
            c.endDateTime, r.frequency, r.dayOfWeekList, r.dayOfMonth, r.interval
        FROM Recurrence r 
        JOIN Commitment c ON r.commitment = c.id 
        WHERE c.calendar = :calendarId
    """,
    )
    fun getRecurrenceByCalendar(calendarId: Long): Flow<List<CommitmentRecurrenceDataClass>>

    @Query("SELECT * FROM Recurrence WHERE commitment = :commitmentId")
    suspend fun getRecurrenceByCommitment(commitmentId: Long): RecurrenceEntity?
}
