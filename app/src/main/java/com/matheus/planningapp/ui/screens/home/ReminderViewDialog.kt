package com.matheus.planningapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.matheus.planningapp.R
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.reminder.ReminderEntity
import com.matheus.planningapp.ui.screens.components.IntegerField
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderViewDialog(
    commitmentEntity: CommitmentEntity?,
    reminders: List<ReminderEntity>,
    showDialog: Boolean,
    onInsertReminderAction: (commitmentEntity: CommitmentEntity, minutesBeforeCommitment: Int) -> Unit,
    onUpdateReminderAction: (reminderEntity: ReminderEntity, startDateTime: Instant, minutesBeforeCommitment: Int) -> Unit,
    onDeleteReminderAction: (reminderEntity: ReminderEntity) -> Unit,
    onDismissRequest: () -> Unit,
) {
    if (commitmentEntity == null) return

    val strings: StringsRepository = LocalStrings.current

    var selectedReminder by remember { mutableStateOf<ReminderEntity?>(null) }
    var minutesBeforeCommitment by remember { mutableStateOf(1) }

    val commitmentStartDateTime: LocalDateTime = commitmentEntity.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val startTimeString = String.format(Locale.US, strings.hourFormat, commitmentStartDateTime.hour, commitmentStartDateTime.minute)
    val commitmentEndDateTime: LocalDateTime = commitmentEntity.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTimeString = String.format(Locale.US, strings.hourFormat, commitmentEndDateTime.hour, commitmentEndDateTime.minute)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                minutesBeforeCommitment = 1
                selectedReminder = null
                onDismissRequest()
            },
            title = {
                Column {
                    Text(
                        text = commitmentEntity.title,
                        fontSize = PageDesignSettings.largeText,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = PageDesignSettings.mediumPaddingValue),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = PageDesignSettings.smallPaddingValue),
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                            modifier =
                                Modifier
                                    .size(PageDesignSettings.smallIconSize)
                                    .padding(end = PageDesignSettings.mediumPaddingValue),
                        )

                        Text(
                            text =
                                String.format(
                                    Locale.US,
                                    strings.dateFormat,
                                    commitmentStartDateTime.year,
                                    commitmentStartDateTime.monthNumber,
                                    commitmentStartDateTime.dayOfMonth,
                                ),
                            fontSize = PageDesignSettings.mediumText,
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_nest_clock_farsight_analog_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                            modifier =
                                Modifier
                                    .size(PageDesignSettings.smallIconSize)
                                    .padding(end = PageDesignSettings.mediumPaddingValue),
                        )

                        Text(
                            text = "$startTimeString — $endTimeString",
                            fontSize = PageDesignSettings.mediumText,
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                ) {
                    // Section 1: Reminder Form
                    Spacer(modifier = Modifier.height(PageDesignSettings.mediumPaddingValue))

                    ReminderFormSection(
                        selectedReminder = selectedReminder,
                        minutesBeforeCommitment = minutesBeforeCommitment,
                        onMinutesChange = { minutesBeforeCommitment = it },
                        onCancelClick = { selectedReminder = null },
                        onSubmitClick = {
                            val currentReminder = selectedReminder

                            if (currentReminder == null) {
                                onInsertReminderAction(commitmentEntity, minutesBeforeCommitment)
                            } else {
                                onUpdateReminderAction(currentReminder, commitmentEntity.startDateTime, minutesBeforeCommitment)
                                selectedReminder = null
                            }
                            minutesBeforeCommitment = 1
                        },
                    )

                    if (reminders.isNotEmpty()) {
                        // Section 2: Reminder List
                        Text(
                            text = "${strings.reminderListTitle} (${reminders.size})",
                            style =
                                TextStyle(
                                    fontSize = PageDesignSettings.smallTitle,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                ),
                            modifier = Modifier.padding(bottom = PageDesignSettings.mediumPaddingValue),
                        )

                        ReminderListSection(
                            reminders = reminders,
                            selectedReminder = selectedReminder,
                            onEditReminder = { reminder ->
                                selectedReminder = reminder
                                minutesBeforeCommitment = reminder.minutesBeforeCommitment
                            },
                            onDeleteReminder = onDeleteReminderAction,
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(
                    onClick = {
                        minutesBeforeCommitment = 1
                        selectedReminder = null
                        onDismissRequest()
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
                ) {
                    Text(
                        text = strings.dismissButton,
                        fontSize = PageDesignSettings.largeText,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = PageDesignSettings.smallPaddingValue),
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.onBackground,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(PageDesignSettings.largeIconClip))
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(PageDesignSettings.largeIconClip),
                    ),
        )
    }
}

@Composable
fun ReminderFormSection(
    selectedReminder: ReminderEntity?,
    minutesBeforeCommitment: Int,
    onMinutesChange: (Int) -> Unit,
    onCancelClick: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    brush =
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            ),
                        ),
                    shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
                ).border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
                ).padding(PageDesignSettings.extraLargePaddingValue),
    ) {
        // Form Title
        Text(
            text = if (selectedReminder == null) strings.insertButton else strings.updateButton,
            style =
                TextStyle(
                    fontSize = PageDesignSettings.smallTitle,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                ),
            modifier = Modifier.padding(bottom = PageDesignSettings.mediumPaddingValue),
        )

        // Minutes Input Field
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = PageDesignSettings.extraLargePaddingValue),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = PageDesignSettings.smallPaddingValue),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_nest_clock_farsight_analog_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier =
                        Modifier
                            .size(PageDesignSettings.mediumIconSize)
                            .padding(end = PageDesignSettings.mediumPaddingValue),
                )

                Text(
                    text = strings.reminderField,
                    style =
                        TextStyle(
                            fontSize = PageDesignSettings.mediumText,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                )
            }

            IntegerField(
                selectedValue = minutesBeforeCommitment,
                onIntegerValueChange = onMinutesChange,
                minValue = 1,
                maxValue = 60,
            )
        }

        // Action Buttons
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = PageDesignSettings.mediumPaddingValue),
            horizontalArrangement = Arrangement.spacedBy(PageDesignSettings.mediumPaddingValue),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                enabled = selectedReminder != null,
                onClick = onCancelClick,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    ),
                modifier =
                    Modifier
                        .weight(1f)
                        .height(48.dp),
                shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
            ) {
                Text(
                    text = strings.cancelButton,
                    fontSize = PageDesignSettings.mediumText,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Button(
                onClick = onSubmitClick,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                modifier =
                    Modifier
                        .weight(1.2f)
                        .height(48.dp),
                shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp,
                    ),
            ) {
                Text(
                    text = if (selectedReminder == null) strings.insertButton else strings.updateButton,
                    fontSize = PageDesignSettings.mediumText,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun ReminderListSection(
    reminders: List<ReminderEntity>,
    selectedReminder: ReminderEntity?,
    onEditReminder: (ReminderEntity) -> Unit,
    onDeleteReminder: (ReminderEntity) -> Unit,
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(PageDesignSettings.mediumPaddingValue),
    ) {
        items(reminders) { reminder ->
            ReminderItemCard(
                reminder = reminder,
                isSelected = selectedReminder?.id == reminder.id,
                onEdit = { onEditReminder(reminder) },
                onDelete = { onDeleteReminder(reminder) },
            )
        }
    }
}

@Composable
fun ReminderItemCard(
    reminder: ReminderEntity,
    isSelected: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color =
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        },
                    shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
                ).clip(RoundedCornerShape(PageDesignSettings.mediumIconClip)),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                    },
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PageDesignSettings.mediumPaddingValue),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Reminder Info with Icon
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PageDesignSettings.mediumPaddingValue),
            ) {
                // Time Icon with background
                Box(
                    modifier =
                        Modifier
                            .size(PageDesignSettings.largeIconSize)
                            .background(
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                shape = CircleShape,
                            ).clip(CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_nest_clock_farsight_analog_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(PageDesignSettings.mediumIconSize),
                    )
                }

                // Reminder text info
                Text(
                    text = strings.reminderInfo.format(reminder.minutesBeforeCommitment),
                    style =
                        TextStyle(
                            fontSize = PageDesignSettings.smallText,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        ),
                    modifier = Modifier.weight(1f),
                )
            }

            // Action Buttons
            Row(
                modifier =
                    Modifier
                        .wrapContentWidth()
                        .padding(start = PageDesignSettings.smallPaddingValue),
                horizontalArrangement = Arrangement.spacedBy(PageDesignSettings.smallPaddingValue),
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier =
                        Modifier
                            .size(PageDesignSettings.mediumIconSize)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(PageDesignSettings.largeIconClip),
                            ).clip(RoundedCornerShape(PageDesignSettings.largeIconClip)),
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = strings.updateButton,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(PageDesignSettings.mediumIconSize),
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier =
                        Modifier
                            .size(PageDesignSettings.mediumIconSize)
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(PageDesignSettings.largeIconClip),
                            ).clip(RoundedCornerShape(PageDesignSettings.largeIconClip)),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.deleteButton,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(PageDesignSettings.mediumIconSize),
                    )
                }
            }
        }
    }
}
