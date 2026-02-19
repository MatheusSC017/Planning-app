package com.matheus.planningapp.data.calendar

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

@Dao
interface CalendarDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(calendarEntity: CalendarEntity): Long

    @Update
    suspend fun update(calendarEntity: CalendarEntity)

    suspend fun updateWithDateTime(calendarEntity: CalendarEntity) {
        calendarEntity.updatedAt = Clock.System.now()
        update(calendarEntity)
    }

    @Delete
    suspend fun delete(calendarEntity: CalendarEntity)

    @Query("SELECT * FROM Calendar ORDER BY isDefault DESC")
    fun getCalendars(): Flow<List<CalendarEntity>>

    @Query("SELECT * FROM Calendar WHERE id = :calendarId")
    suspend fun getCalendarById(calendarId: Int): CalendarEntity?

    @Query("SELECT COUNT(*) FROM Calendar")
    suspend fun countCalendars(): Int

    @Query("UPDATE Calendar SET isDefault = 0 WHERE isDefault = 1")
    suspend fun setAllDefaultAsFalse()
}
