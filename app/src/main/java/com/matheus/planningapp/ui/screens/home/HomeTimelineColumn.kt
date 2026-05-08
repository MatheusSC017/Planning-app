package com.matheus.planningapp.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.matheus.planningapp.R
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.enums.PriorityEnum
import com.matheus.planningapp.util.indexToTimeString
import com.matheus.planningapp.util.timeToIndex
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

fun LazyListScope.timelineColumn(
    strings: StringsRepository,
    commitments: List<CommitmentEntity>,
    onReminderAction: (commitment: CommitmentEntity) -> Unit,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit,
) {
    var commitmentsLastIndex = 0
    val timeLineItems = 48

    if (commitments.isEmpty()) {
        items(timeLineItems) { index ->
            TimelineRow(
                startTime = indexToTimeString(index),
                commitment = null,
                onReminderAction = {},
                onViewCommitment = {},
                onNavigateToUpdateCommitment = {},
                onDeleteCommitment = {},
            )
        }
    } else {
        val timesList = List(timeLineItems) { it }
        val sortedCommitments = commitments.sortedBy { it.startDateTime }
        sortedCommitments.forEach { commitment ->
            val commitmentStartDateTime = commitment.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
            val commitmentEndDateTime = commitment.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
            val commitmentStartTime: String =
                String.format(
                    Locale.US,
                    strings.hourFormat,
                    commitmentStartDateTime.hour,
                    commitmentStartDateTime.minute,
                )
            val commitmentStartIndex: Int = timeToIndex(commitmentStartDateTime.time)

            if (commitmentsLastIndex < commitmentStartIndex) {
                items(timesList.subList(commitmentsLastIndex, commitmentStartIndex)) { index ->
                    TimelineRow(
                        startTime = indexToTimeString(index),
                        commitment = null,
                        onReminderAction = {},
                        onViewCommitment = {},
                        onNavigateToUpdateCommitment = {},
                        onDeleteCommitment = {},
                    )
                }
            }

            commitmentsLastIndex =
                if (commitmentEndDateTime.dayOfMonth != commitmentStartDateTime.dayOfMonth) {
                    48
                } else {
                    timeToIndex(commitmentEndDateTime.time)
                }

            item {
                TimelineRow(
                    startTime = commitmentStartTime,
                    commitment = commitment,
                    onReminderAction = onReminderAction,
                    onViewCommitment = onViewCommitment,
                    onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                    onDeleteCommitment = onDeleteCommitment,
                )
            }
        }

        if (commitmentsLastIndex < timeLineItems) {
            items(timesList.subList(commitmentsLastIndex, timeLineItems)) { index ->
                TimelineRow(
                    startTime = indexToTimeString(index),
                    commitment = null,
                    onReminderAction = {},
                    onViewCommitment = {},
                    onNavigateToUpdateCommitment = {},
                    onDeleteCommitment = {},
                )
            }
        }
    }
}

@Composable
fun TimelineRow(
    startTime: String,
    commitment: CommitmentEntity?,
    onReminderAction: (commitment: CommitmentEntity) -> Unit,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .padding(
                    end = PageDesignSettings.extraLargePaddingValue,
                    start = PageDesignSettings.extraLargePaddingValue,
                    bottom = PageDesignSettings.extraLargePaddingValue,
                ).height(IntrinsicSize.Min)
                .heightIn(min = PageDesignSettings.mediumComponentSize),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(PageDesignSettings.smallComponentSize),
        ) {
            Text(
                text = startTime,
                fontSize = PageDesignSettings.mediumText,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = .6f),
            )

            Box(
                modifier =
                    Modifier
                        .width(PageDesignSettings.extraSmallPaddingValue)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = .6f)),
            )
        }

        Spacer(modifier = Modifier.width(PageDesignSettings.largePaddingValue))

        if (commitment != null) {
            CommitmentCard(
                commitmentEntity = commitment,
                onReminderAction = onReminderAction,
                onViewCommitment = onViewCommitment,
                onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                onDeleteCommitment = onDeleteCommitment,
            )
        }
    }
}

@Composable
fun CommitmentCard(
    commitmentEntity: CommitmentEntity,
    onReminderAction: (commitment: CommitmentEntity) -> Unit,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current
    var menuExpanded by remember { mutableStateOf(false) }

    val commitmentStartDateTime: LocalDateTime = commitmentEntity.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val startTimeString = String.format(Locale.US, strings.hourFormat, commitmentStartDateTime.hour, commitmentStartDateTime.minute)
    val commitmentEndDateTime: LocalDateTime = commitmentEntity.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTimeString = String.format(Locale.US, strings.hourFormat, commitmentEndDateTime.hour, commitmentEndDateTime.minute)

    val statusColor =
        when (commitmentEntity.priorityEnum) {
            PriorityEnum.LOW -> Color.Green.copy(alpha = .6f)
            PriorityEnum.MEDIUM -> Color.Yellow.copy(alpha = .6f)
            PriorityEnum.HIGH -> Color.Red.copy(alpha = .6f)
        }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
        elevation = CardDefaults.cardElevation(PageDesignSettings.mediumIconClip / 2),
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(
                        PageDesignSettings.borderWidth,
                        Brush.linearGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite,
                        ),
                    ),
                    shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
                ),
    ) {
        Row(
            modifier = Modifier.padding(end = PageDesignSettings.largePaddingValue),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .width(PageDesignSettings.extraSmallIconSize)
                        .clip(CircleShape)
                        .background(statusColor),
            )

            Spacer(modifier = Modifier.width(PageDesignSettings.largePaddingValue))

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(vertical = PageDesignSettings.extraLargePaddingValue),
            ) {
                Text(
                    text = commitmentEntity.title,
                    fontSize = PageDesignSettings.mediumText,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                )

                Spacer(modifier = Modifier.height(PageDesignSettings.smallPaddingValue))

                Text(
                    text = "$startTimeString — $endTimeString",
                    fontSize = PageDesignSettings.mediumText,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = .6f),
                )
            }

            Box(
                modifier = Modifier.align(Alignment.Top),
            ) {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        painterResource(R.drawable.outline_more_horiz_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary.copy(.6f),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground),
                ) {
                    DropdownMenuItem(
                        text = { Text(strings.viewButton, color = MaterialTheme.colorScheme.onSecondary) },
                        onClick = {
                            menuExpanded = false
                            onViewCommitment(commitmentEntity)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.view),
                                contentDescription = strings.viewButton,
                                tint = MaterialTheme.colorScheme.onSecondary,
                            )
                        },
                    )

                    if (commitmentEntity.startDateTime > Clock.System.now()) {
                        DropdownMenuItem(
                            text = { Text(strings.reminderButton, color = MaterialTheme.colorScheme.onSecondary) },
                            onClick = {
                                menuExpanded = false
                                onReminderAction(commitmentEntity)
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.outline_notifications_24),
                                    contentDescription = strings.reminderButton,
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                )
                            },
                        )
                    }

                    DropdownMenuItem(
                        text = { Text(strings.updateButton, color = MaterialTheme.colorScheme.onSecondary) },
                        onClick = {
                            menuExpanded = false
                            onNavigateToUpdateCommitment(commitmentEntity.id)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = strings.updateButton,
                                tint = MaterialTheme.colorScheme.onSecondary,
                            )
                        },
                    )

                    HorizontalDivider()

                    DropdownMenuItem(
                        text = { Text(strings.deleteButton, color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            menuExpanded = false
                            onDeleteCommitment(commitmentEntity)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.deleteButton,
                                tint = MaterialTheme.colorScheme.error,
                            )
                        },
                    )
                }
            }
        }
    }
}
