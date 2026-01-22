package com.matheus.planningapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CalendarEntity::class, CommitmentEntity::class],
    version = 3
)
@TypeConverters(DateTimeConverters::class, PriorityConverters::class)
abstract class CalendarDatabase: RoomDatabase() {
    abstract fun calendarDao(): CalendarDao
    abstract fun commitmentDao(): CommitmentDao
}
