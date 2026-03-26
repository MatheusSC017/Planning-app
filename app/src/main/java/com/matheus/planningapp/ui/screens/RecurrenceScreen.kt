package com.matheus.planningapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matheus.planningapp.R
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.recurrence.CommitmentRecurrenceDataClass
import com.matheus.planningapp.util.enums.FrequencyEnum
import com.matheus.planningapp.viewmodel.recurrence.RecurrenceViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceScreen(
    recurrenceViewModel: RecurrenceViewModel = koinViewModel(),
    onMenuClick: () -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit
) {
    val uiState by recurrenceViewModel.uiState.collectAsStateWithLifecycle()

    var selectedCalendar by remember { mutableStateOf<CalendarEntity?>(null) }

    LaunchedEffect(uiState.calendars) {
        if (selectedCalendar == null && uiState.calendars.isNotEmpty()) {
            selectedCalendar = uiState.calendars.first()
        }
    }

    val recurrences: List<CommitmentRecurrenceDataClass> by recurrenceViewModel
        .getRecurrencesByCalendar(calendarId = selectedCalendar?.id ?: 0)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            RecurrenceTopAppBar(
                calendarsEntities = uiState.calendars,
                selectedCalendar = selectedCalendar,
                onCalendarSelected = { selectedCalendar = it },
                onMenuClick = onMenuClick
            )
        },
        content = { paddingValues ->
            RecurrenceList(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f),
                                MaterialTheme.colorScheme.background,
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    ),
                recurrenceViewModel = recurrenceViewModel,
                recurrences = recurrences,
                onNavigateToUpdateCommitment = onNavigateToUpdateCommitment
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceTopAppBar(
    calendarsEntities: List<CalendarEntity>,
    selectedCalendar: CalendarEntity?,
    onCalendarSelected: (CalendarEntity) -> Unit,
    onMenuClick: () -> Unit
) {
    var isExpandedCalendarDropDown by remember { mutableStateOf(false) }

    TopAppBar(
        title = {},
        actions = {
            ExposedDropdownMenuBox(
                expanded = isExpandedCalendarDropDown,
                onExpandedChange = { isExpandedCalendarDropDown = !isExpandedCalendarDropDown }
            ) {
                TextField(
                    value = selectedCalendar?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(isExpandedCalendarDropDown)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .width(200.dp),
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    ),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.secondary,
                        unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                        disabledTextColor = MaterialTheme.colorScheme.secondary
                    )
                )

                ExposedDropdownMenu(
                    expanded = isExpandedCalendarDropDown,
                    onDismissRequest = { isExpandedCalendarDropDown = false },
                    containerColor = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                ) {
                    calendarsEntities.forEach { calendarEntity ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = calendarEntity.name,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            onClick = {
                                onCalendarSelected(calendarEntity)
                                isExpandedCalendarDropDown = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = onMenuClick
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    )
}

@Composable
fun RecurrenceList(
    modifier: Modifier,
    recurrenceViewModel: RecurrenceViewModel,
    recurrences: List<CommitmentRecurrenceDataClass>,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit
) {
    var selectedRecurrence by remember { mutableStateOf<CommitmentRecurrenceDataClass?>(null) }
    var showRecurrenceViewDialog by remember { mutableStateOf(false) }

    RecurrenceViewDialog(
        recurrence = selectedRecurrence,
        showDialog = showRecurrenceViewDialog,
        onDismissRequest = { showRecurrenceViewDialog = false }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
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
                        onDeleteRecurrence = { recurrenceId ->
                            recurrenceViewModel.deleteRecurrence(recurrenceId)
                        }
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
    onDeleteRecurrence: (recurrenceId: Long) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val commitmentStartDateTime: LocalDateTime = recurrence.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val startTimeString = String.format(Locale.US, "%02d:%02d", commitmentStartDateTime.hour, commitmentStartDateTime.minute)
    val commitmentEndDateTime: LocalDateTime = recurrence.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTimeString = String.format(Locale.US, "%02d:%02d", commitmentEndDateTime.hour, commitmentEndDateTime.minute)

    var recurrenceText = "Frequency: ${recurrence.frequency.label}"
    when (recurrence.frequency) {
        FrequencyEnum.CUSTOMIZED -> recurrenceText += " - Interval: ${recurrence.interval}"
        FrequencyEnum.MONTHLY -> recurrenceText += " - Day of month: ${recurrence.dayOfMonth}"
        FrequencyEnum.WEEKLY -> {
            recurrenceText += " - Week days: ${
                recurrence.dayOfWeekList.joinToString(", ") { dayOfWeek -> dayOfWeek.label }
            }"
        }
        else -> recurrenceText
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
                        ),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(36.dp)
                        .border(
                            BorderStroke(.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = .5f)),
                            CircleShape
                        )
                        .clip(CircleShape)
                        .padding(2.dp)

                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {

                Text(
                    text = recurrence.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$startTimeString — $endTimeString",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = recurrenceText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        painterResource(R.drawable.outline_more_horiz_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary.copy(.6f)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                ) {
                    DropdownMenuItem(
                        text = { Text("View", color = MaterialTheme.colorScheme.onSecondary) },
                        onClick = {
                            menuExpanded = false
                            onViewRecurrence(recurrence)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.view),
                                contentDescription = "View commitment",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Edit", color = MaterialTheme.colorScheme.onSecondary) },
                        onClick = {
                            menuExpanded = false
                            onNavigateToUpdateCommitment(recurrence.commitmentId)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit commitment",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    )

                    HorizontalDivider()

                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            menuExpanded = false
                            onDeleteRecurrence(recurrence.recurrenceId)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Edit commitment",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceViewDialog(
    recurrence: CommitmentRecurrenceDataClass?,
    showDialog: Boolean,
    onDismissRequest: () -> Unit
) {
    if (recurrence == null) return

    val commitmentStartDateTime: LocalDateTime = recurrence.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val startTimeString = String.format(Locale.US, "%02d:%02d", commitmentStartDateTime.hour, commitmentStartDateTime.minute)
    val commitmentEndDateTime: LocalDateTime = recurrence.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTimeString = String.format(Locale.US, "%02d:%02d", commitmentEndDateTime.hour, commitmentEndDateTime.minute)

    var recurrenceText = "Frequency: ${recurrence.frequency.label}"
    when (recurrence.frequency) {
        FrequencyEnum.CUSTOMIZED -> recurrenceText += " - Interval: ${recurrence.interval}"
        FrequencyEnum.MONTHLY -> recurrenceText += " - Day of month: ${recurrence.dayOfMonth}"
        FrequencyEnum.WEEKLY -> {
            recurrenceText += " - Week days: ${
                recurrence.dayOfWeekList.joinToString(", ") { dayOfWeek -> dayOfWeek.label }
            }"
        }
        else -> recurrenceText
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Column {
                    Text(
                        text = recurrence.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )

                        Text(
                            text = recurrenceText,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_nest_clock_farsight_analog_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )

                        Text(
                            text = "$startTimeString — $endTimeString",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f)
                        )
                    }
                }
            },
            text = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = recurrence.description ?: "",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondary,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider()
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Dismiss",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.onBackground
        )
    }
}
