package com.matheus.planningapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant


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
    val id: Int,
    val calendar: Int,
    val title: String,
    val description: String?,
    val startDateTime: Instant,
    val endDateTime: Instant,
    val allDay: Boolean,
    val priority: Priority,
    val createdAt: Instant,
    val updatedAt: Instant
)
