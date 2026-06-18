package com.matheus.planningapp.ui.screens.recurrence

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
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
import com.matheus.planningapp.data.recurrence.CommitmentRecurrenceDataClass
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.enums.FrequencyEnum
import com.matheus.planningapp.viewmodel.recurrence.RecurrenceViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlin.collections.forEach

@Composable
fun RecurrenceList(
    modifier: Modifier,
    onDeleteRecurrence: (recurrenceId: Long) -> Unit,
    recurrences: List<CommitmentRecurrenceDataClass>,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
) {
    var selectedRecurrence by remember { mutableStateOf<CommitmentRecurrenceDataClass?>(null) }
    var showRecurrenceViewDialog by remember { mutableStateOf(false) }

    RecurrenceViewDialog(
        recurrence = selectedRecurrence,
        showDialog = showRecurrenceViewDialog,
        onDismissRequest = { showRecurrenceViewDialog = false },
    )

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(PageDesignSettings.extraLargePaddingValue),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            recurrences.forEach { recurrence ->
                item {
                    RecurrenceCard(
                        recurrence = recurrence,
                        onViewRecurrence = { recurrence ->
                            selectedRecurrence = recurrence
                            showRecurrenceViewDialog = true
                        },
                        onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                        onDeleteRecurrence = onDeleteRecurrence
                    )
                }
            }
        }
    }
}

@Composable
fun RecurrenceCard(
    recurrence: CommitmentRecurrenceDataClass,
    onViewRecurrence: (CommitmentRecurrenceDataClass) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    onDeleteRecurrence: (recurrenceId: Long) -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current
    var menuExpanded by remember { mutableStateOf(false) }

    val commitmentStartDateTime: LocalDateTime = recurrence.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val startTimeString = String.format(Locale.US, strings.hourFormat, commitmentStartDateTime.hour, commitmentStartDateTime.minute)
    val commitmentEndDateTime: LocalDateTime = recurrence.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTimeString = String.format(Locale.US, strings.hourFormat, commitmentEndDateTime.hour, commitmentEndDateTime.minute)

    var recurrenceText = "${strings.recurrenceFrequencyField}: ${recurrence.frequency.label}"
    when (recurrence.frequency) {
        FrequencyEnum.CUSTOMIZED -> recurrenceText += " - ${strings.recurrenceIntervalField}: ${recurrence.interval}"
        FrequencyEnum.MONTHLY -> recurrenceText += " - ${strings.recurrenceDayOfMonthField}: ${recurrence.dayOfMonth}"
        FrequencyEnum.WEEKLY -> {
            recurrenceText += " - ${strings.recurrenceWeekDaysField}: ${
                recurrence.dayOfWeekList.joinToString(", ") { dayOfWeek -> dayOfWeek.label }
            }"
        }
        else -> recurrenceText
    }

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = Color.Transparent,
            ),
        shape = RoundedCornerShape(PageDesignSettings.largeIconClip),
        elevation = CardDefaults.cardElevation(PageDesignSettings.zeroPaddingValue),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(PageDesignSettings.zeroPaddingValue, PageDesignSettings.mediumPaddingValue)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                        ),
                    ),
                    shape = RoundedCornerShape(PageDesignSettings.largeIconClip),
                ).border(
                    BorderStroke(
                        PageDesignSettings.borderWidth,
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite,
                        ),
                    ),
                    shape = RoundedCornerShape(PageDesignSettings.largeIconClip),
                ),
    ) {
        Row(
            modifier = Modifier.padding(PageDesignSettings.largePaddingValue),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(end = PageDesignSettings.mediumPaddingValue)
                        .align(Alignment.CenterVertically),
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier
                            .size(PageDesignSettings.mediumIconSize)
                            .border(
                                BorderStroke(
                                    PageDesignSettings.borderWidth / 2,
                                    MaterialTheme.colorScheme.primary.copy(alpha = .5f),
                                ),
                                CircleShape,
                            ).clip(CircleShape)
                            .padding(PageDesignSettings.extraSmallPaddingValue),
                )
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(vertical = PageDesignSettings.mediumPaddingValue),
            ) {
                Text(
                    text = recurrence.title,
                    fontSize = PageDesignSettings.largeText,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                )

                Spacer(modifier = Modifier.height(PageDesignSettings.mediumPaddingValue))

                Text(
                    text = "$startTimeString — $endTimeString",
                    fontSize = PageDesignSettings.largeText,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                )

                Spacer(modifier = Modifier.height(PageDesignSettings.smallPaddingValue))

                Text(
                    text = recurrenceText,
                    fontSize = PageDesignSettings.smallText,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            Box {
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
                            onViewRecurrence(recurrence)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.view),
                                contentDescription = strings.viewButton,
                                tint = MaterialTheme.colorScheme.onSecondary,
                            )
                        },
                    )

                    DropdownMenuItem(
                        text = { Text(strings.updateButton, color = MaterialTheme.colorScheme.onSecondary) },
                        onClick = {
                            menuExpanded = false
                            onNavigateToUpdateCommitment(recurrence.commitmentId)
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
                            onDeleteRecurrence(recurrence.recurrenceId)
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
