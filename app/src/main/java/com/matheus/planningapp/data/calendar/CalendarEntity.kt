package com.matheus.planningapp.data.calendar

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


@Entity(
    tableName = "Calendar",
    indices = [Index(value = ["name"], unique = true)]
)
data class CalendarEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var isDefault: Boolean,
    val createdAt: Instant = Clock.System.now(),
    var updatedAt: Instant = Clock.System.now()
)
