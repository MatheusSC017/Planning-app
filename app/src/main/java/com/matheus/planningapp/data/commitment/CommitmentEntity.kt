package com.matheus.planningapp.data.commitment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.local.enums.Priority
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


@Entity(
    tableName = "Commitment",
    foreignKeys = [
        ForeignKey(
            entity = CalendarEntity::class,
            parentColumns = ["id"],
            childColumns = ["calendar"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("calendar"),
        Index("startDateTime"),
        Index("endDateTime")
    ]
)
data class CommitmentEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val calendar: Long,
    val title: String,
    val description: String?,
    val startDateTime: Instant,
    val endDateTime: Instant,
    val priority: Priority,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
