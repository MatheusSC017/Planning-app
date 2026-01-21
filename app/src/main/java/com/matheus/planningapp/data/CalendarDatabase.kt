package com.matheus.planningapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CalendarEntity::class, CommitmentEntity::class],
    version = 2
)
abstract class CalendarDatabase: RoomDatabase() {
    abstract fun calendarDao(): CalendarDao
    abstract fun commitmentDao(): CommitmentDao
}
