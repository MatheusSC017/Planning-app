package com.matheus.planningapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.matheus.planningapp.data.calendar.CalendarDao
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.commitment.CommitmentDao
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.local.converters.DateTimeConverters
import com.matheus.planningapp.data.local.converters.DayOfWeekFrequency
import com.matheus.planningapp.data.local.converters.FrequencyConverters
import com.matheus.planningapp.data.local.converters.PriorityConverters
import com.matheus.planningapp.data.recurrence.RecurrenceDao
import com.matheus.planningapp.data.recurrence.RecurrenceEntity

@Database(
    entities = [CalendarEntity::class, CommitmentEntity::class, RecurrenceEntity::class],
    version = 11,
    exportSchema = false,
)
@TypeConverters(
    DateTimeConverters::class,
    PriorityConverters::class,
    FrequencyConverters::class,
    DayOfWeekFrequency::class,
)
abstract class CalendarDatabase : RoomDatabase() {
    abstract fun calendarDao(): CalendarDao

    abstract fun commitmentDao(): CommitmentDao

    abstract fun recurrenceDao(): RecurrenceDao
}
