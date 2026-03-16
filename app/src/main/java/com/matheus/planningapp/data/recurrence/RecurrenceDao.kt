package com.matheus.planningapp.data.recurrence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecurrenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurrenceEntity: RecurrenceEntity)

    @Update
    suspend fun update(recurrenceEntity: RecurrenceEntity)

    @Delete
    suspend fun delete(recurrenceEntity: RecurrenceEntity)

    @Query("SELECT r.* FROM Recurrence r " +
            "JOIN Commitment c ON r.commitment = c.id " +
            "WHERE c.calendar = :calendarId")
    suspend fun getRecurrenceByCalendar(calendarId: Long): List<RecurrenceEntity>

    @Query("SELECT * FROM Recurrence WHERE commitment = :commitmentId")
    suspend fun getRecurrenceByCommitment(commitmentId: Long): RecurrenceEntity?
}