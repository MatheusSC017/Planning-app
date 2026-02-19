package com.matheus.planningapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.matheus.planningapp.data.calendar.CalendarDao
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.commitment.CommitmentDao
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.local.converters.DateTimeConverters
import com.matheus.planningapp.data.local.converters.PriorityConverters

@Database(
    entities = [CalendarEntity::class, CommitmentEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class, PriorityConverters::class)
abstract class CalendarDatabase: RoomDatabase() {
    abstract fun calendarDao(): CalendarDao
    abstract fun commitmentDao(): CommitmentDao
}
