package com.matheus.planningapp.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import com.matheus.planningapp.R
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.enums.PriorityEnum
import com.matheus.planningapp.util.indexToTimeString
import com.matheus.planningapp.util.timeToIndex
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

fun LazyListScope.timelineGrid(
    commitments: List<CommitmentEntity>,
    onReminderAction: (commitment: CommitmentEntity) -> Unit,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit,
) {
    val numberOfColumns = 4
    val timelineItems = List(48) { -1 }.toMutableList()
    val finalIndexCommitments: MutableList<Int> = emptyList<Int>().toMutableList()
    commitments.forEachIndexed { index, commitment ->
        val commitmentStartDateTime = commitment.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
        val commitmentEndDateTime = commitment.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
        val commitmentStartIndex: Int = timeToIndex(commitmentStartDateTime.time)
        val commitmentEndIndex: Int =
            if (commitmentEndDateTime.dayOfMonth != commitmentStartDateTime.dayOfMonth) {
                47
            } else {
                timeToIndex(commitmentEndDateTime.time) - 1
            }
        finalIndexCommitments.add(commitmentEndIndex)

        for (i in commitmentStartIndex..commitmentEndIndex) {
            timelineItems[i] = index
        }
    }

    item {
        timelineItems
            .withIndex()
            .chunked(numberOfColumns)
            .forEach { indexsRow ->
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val cellWidth = maxWidth / numberOfColumns

                    Row(modifier = Modifier.fillMaxWidth()) {
                        indexsRow.forEach { indexedHour ->
                            val index = indexedHour.index
                            val indexCommitment = indexedHour.value

                            if (indexCommitment == -1) {
                                TimelineGridItem(
                                    startTime = indexToTimeString(index),
                                    commitmentEntity = null,
                                    cellWidth = cellWidth,
                                    colspan = 1,
                                    continuesInNextCell = false,
                                    continuesFromPreviousCell = false,
                                    onReminderAction = {},
                                    onViewCommitment = {},
                                    onNavigateToUpdateCommitment = {},
                                    onDeleteCommitment = {},
                                )
                            } else {
                                // Start a new block if this is the first column in the row (index % 4 == 0)
                                // or if the current commitment is different from the previous one.
                                if ((index % numberOfColumns == 0) || timelineItems[index - 1] != indexCommitment) {
                                    val commitmentEndIndex = finalIndexCommitments[indexCommitment]
                                    val indexEndOfRow = index + numberOfColumns - (index % numberOfColumns) - 1
                                    val colspan =
                                        if (indexEndOfRow > commitmentEndIndex) {
                                            commitmentEndIndex - index + 1
                                        } else {
                                            indexEndOfRow - index + 1
                                        }
                                    TimelineGridItem(
                                        startTime = indexToTimeString(index),
                                        commitmentEntity = commitments[indexCommitment],
                                        cellWidth = cellWidth,
                                        colspan = colspan,
                                        continuesInNextCell = index + colspan - 1 < commitmentEndIndex,
                                        continuesFromPreviousCell = if (index > 0) timelineItems[index - 1] == indexCommitment else false,
                                        onReminderAction = onReminderAction,
                                        onViewCommitment = onViewCommitment,
                                        onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                                        onDeleteCommitment = onDeleteCommitment,
                                    )
                                }
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun TimelineGridItem(
    startTime: String,
    commitmentEntity: CommitmentEntity?,
    cellWidth: Dp,
    colspan: Int,
    continuesInNextCell: Boolean,
    continuesFromPreviousCell: Boolean,
    onReminderAction: (commitment: CommitmentEntity) -> Unit,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current
    var menuExpanded by remember { mutableStateOf(false) }

    val endBoxPadding = if (continuesInNextCell) PageDesignSettings.zeroPaddingValue else PageDesignSettings.largePaddingValue
    val startBoxPadding = if (continuesFromPreviousCell) PageDesignSettings.zeroPaddingValue else PageDesignSettings.largePaddingValue

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .width(cellWidth * colspan)
                .height(cellWidth),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = PageDesignSettings.mediumPaddingValue,
                        bottom = PageDesignSettings.mediumPaddingValue,
                        end = min(endBoxPadding, PageDesignSettings.mediumPaddingValue),
                        start = min(startBoxPadding, PageDesignSettings.mediumPaddingValue),
                    ).background(
                        brush =
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.secondaryContainer,
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite,
                            ),
                        shape =
                            RoundedCornerShape(
                                topEnd = endBoxPadding,
                                bottomEnd = endBoxPadding,
                                topStart = startBoxPadding,
                                bottomStart = startBoxPadding,
                            ),
                    ).border(
                        BorderStroke(
                            PageDesignSettings.borderWidth / 2,
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite,
                            ),
                        ),
                        shape =
                            RoundedCornerShape(
                                topEnd = endBoxPadding,
                                bottomEnd = endBoxPadding,
                                topStart = startBoxPadding,
                                bottomStart = startBoxPadding,
                            ),
                    ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var titleOfCell = ""
                if (!continuesFromPreviousCell) titleOfCell += startTime
                if (commitmentEntity != null && !continuesInNextCell && (continuesFromPreviousCell || colspan > 1)) {
                    val commitmentEndDateTime = commitmentEntity.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
                    titleOfCell +=
                        String.format(
                            Locale.US,
                            " ~ ${strings.hourFormat}",
                            commitmentEndDateTime.hour,
                            commitmentEndDateTime.minute,
                        )
                }

                Text(
                    text = titleOfCell,
                    fontSize = PageDesignSettings.mediumText,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                )

                if (commitmentEntity != null) {
                    val statusColor =
                        when (commitmentEntity.priorityEnum) {
                            PriorityEnum.LOW -> Color.Green.copy(alpha = .6f)
                            PriorityEnum.MEDIUM -> Color.Yellow.copy(alpha = .6f)
                            PriorityEnum.HIGH -> Color.Red.copy(alpha = .6f)
                        }

                    Box(
                        modifier =
                            Modifier
                                .size(PageDesignSettings.smallIconSize)
                                .clip(CircleShape)
                                .background(statusColor),
                    )
                }
            }

            if (commitmentEntity != null) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd),
                ) {
                    Icon(
                        painterResource(R.drawable.outline_more_horiz_24),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .padding(end = PageDesignSettings.mediumPaddingValue)
                                .clickable { menuExpanded = true },
                        tint = MaterialTheme.colorScheme.secondary.copy(.6f),
                    )

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.onBackground),
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    strings.viewButton,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                )
                            },
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
                                text = {
                                    Text(
                                        strings.reminderButton,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                    )
                                },
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
                            text = {
                                Text(
                                    strings.updateButton,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                )
                            },
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
}
