package com.matheus.planningapp.data.focus

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.matheus.planningapp.data.commitment.CommitmentEntity

@Entity(
    tableName = "focus_sessions",
    foreignKeys = [
        ForeignKey(
            entity = CommitmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["commitmentId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("commitmentId")]
)
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val durationSeconds: Int,
    val completed: Boolean,
    val commitmentId: Long? = null
)
