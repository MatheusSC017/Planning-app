package com.matheus.planningapp.ui.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.ui.screens.components.ConfirmationDialog
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.enums.NotificationEnum
import com.matheus.planningapp.util.notification.TaskNotificationScheduler
import com.matheus.planningapp.viewmodel.home.HomeUiState
import com.matheus.planningapp.viewmodel.home.HomeViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import org.koin.compose.koinInject
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeContent(
    modifier: Modifier,
    selectedCalendar: CalendarEntity?,
    columnViewSelected: Boolean,
    onNavigateToAddCommitment: (date: Instant, selectedCalendar: Long) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    onNavigateToFocusMode: (commitmentId: Long) -> Unit,
    homeViewModel: HomeViewModel,
    uiState: HomeUiState,
    taskNotificationScheduler: TaskNotificationScheduler = koinInject(),
) {
    val strings: StringsRepository = LocalStrings.current
    val dateFormatter = DateTimeFormatter.ofPattern(strings.dateFormat)
    val selectedDate = uiState.selectedDate

    val zone = remember { ZoneId.systemDefault() }
    val startOfDay = remember(selectedDate) { selectedDate.atStartOfDay(zone).toInstant().toKotlinInstant() }
    val endOfDay =
        remember(selectedDate) {
            selectedDate
                .atTime(LocalTime.MAX)
                .atZone(zone)
                .toInstant()
                .toKotlinInstant()
        }
    val commitments by homeViewModel
        .getCommitmentsForDay(startOfDay, endOfDay, selectedCalendar?.id ?: 0)
        .collectAsState(initial = emptyList())

    var showSelectDateForm by remember { mutableStateOf(false) }
    var isSearchFormActive by remember { mutableStateOf(false) }
    var commitmentSearchTerm by remember { mutableStateOf("") }
    val searchCommitments by homeViewModel
        .searchCommitments(
            commitmentSearchTerm,
            selectedCalendar?.id ?: 0,
        ).collectAsState(initial = emptyList())

    var selectedCommitment by remember { mutableStateOf<CommitmentEntity?>(null) }
    var showCommitmentViewDialog by remember { mutableStateOf(false) }
    var showReminderViewDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val reminders by homeViewModel
        .getRemindersByCommitment(selectedCommitment?.id ?: 0L)
        .collectAsState(initial = emptyList())

    val notificationPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {}
    val scheduleExactAlarmLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {}

    CommitmentViewDialog(
        commitmentEntity = selectedCommitment,
        showDialog = showCommitmentViewDialog,
        onDismissRequest = { showCommitmentViewDialog = false },
    )

    ReminderViewDialog(
        commitmentEntity = selectedCommitment,
        reminders = reminders,
        showDialog = showReminderViewDialog,
        onInsertReminderAction = { commitmentEntity, minutesBeforeCommitment ->
            homeViewModel.insertReminder(
                commitmentEntity,
                minutesBeforeCommitment,
                notificationPermissionLauncher,
                scheduleExactAlarmLauncher,
            )
        },
        onUpdateReminderAction = { reminderEntity, startDateTime, minutesBeforeCommitment ->
            homeViewModel.updateReminder(
                reminderEntity,
                startDateTime,
                minutesBeforeCommitment,
                notificationPermissionLauncher,
                scheduleExactAlarmLauncher,
            )
        },
        onDeleteReminderAction = { reminderEntity ->
            homeViewModel.deleteReminder(reminderEntity, notificationPermissionLauncher, scheduleExactAlarmLauncher)
        },
        onDismissRequest = { showReminderViewDialog = false },
    )

    ConfirmationDialog(
        item = selectedCommitment,
        showDialog = showDeleteDialog,
        title = strings.dialogDeleteCommitmentTitle,
        message = strings.dialogDeleteCommitmentMessage,
        onConfirm = { commitmentEntity: CommitmentEntity ->
            homeViewModel.deleteCommitment(commitmentEntity)
            if (uiState.notificationOption != NotificationEnum.NO_SEND) {
                taskNotificationScheduler.cancelTaskNotification(commitmentEntity.id)
            }
        },
        onDismissRequest = {
            showDeleteDialog = false
        },
    )

    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(PageDesignSettings.extraLargePaddingValue),
    ) {
        item {
            Column {

                if (showSelectDateForm) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = PageDesignSettings.extraLargePaddingValue),
                    ) {
                        Text(
                            text = selectedDate.year.toString(),
                            style =
                                TextStyle(
                                    fontSize = PageDesignSettings.largeTitle,
                                    color = MaterialTheme.colorScheme.primary,
                                ),
                        )
                        Column(
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = strings.increaseButton,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(PageDesignSettings.smallIconClip))
                                        .size(PageDesignSettings.smallIconSize)
                                        .clickable { homeViewModel.incrementYear() },
                            )

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = strings.decreaseButton,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(PageDesignSettings.smallIconClip))
                                        .size(PageDesignSettings.smallIconSize)
                                        .clickable { homeViewModel.decrementYear() },
                            )
                        }


                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = strings.increaseButton,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .size(PageDesignSettings.mediumIconSize)
                                .clickable { showSelectDateForm = false },
                        )
                    }

                    MonthGrid(
                        selectedMonth = selectedDate.monthValue,
                        onMonthSelected = { homeViewModel.onSelectedDate(month = it) },
                    )

                    Text(
                        text = strings.monthNames[selectedDate.monthValue - 1],
                        style =
                            TextStyle(
                                fontSize = PageDesignSettings.largeTitle,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                        modifier = Modifier.padding(vertical = PageDesignSettings.extraLargePaddingValue),
                    )

                    DaysOnlyCalendar(
                        yearMonth = YearMonth.of(selectedDate.year, selectedDate.monthValue),
                        selectedDay = selectedDate.dayOfMonth,
                        onDateSelected = { homeViewModel.onSelectedDate(day = it) },
                    )
                } else {
                    Row() {
                        Text(
                            text = dateFormatter.format(selectedDate),
                            style =
                                TextStyle(
                                    fontSize = PageDesignSettings.largeTitle,
                                    color = MaterialTheme.colorScheme.primary,
                                ),
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = strings.increaseButton,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .size(PageDesignSettings.mediumIconSize)
                                .clickable { showSelectDateForm = true },
                        )
                    }

                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = strings.timeline,
                        style =
                            TextStyle(
                                fontSize = PageDesignSettings.largeTitle,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                        modifier = Modifier.padding(vertical = PageDesignSettings.extraLargePaddingValue),
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (!isSearchFormActive) {
                        IconButton(
                            onClick = {
                                isSearchFormActive = !isSearchFormActive
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = strings.searchCommitmentField,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(PageDesignSettings.largeIconSize),
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            onNavigateToAddCommitment(startOfDay, selectedCalendar!!.id)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = strings.insertButton,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(PageDesignSettings.largeIconSize),
                        )
                    }
                }

                if (isSearchFormActive) {
                    SearchForm(
                        strings = strings,
                        commitmentSearchTerm = commitmentSearchTerm,
                        setIsSearchFormActive = { isSearchFormActive = it },
                        setCommitmentSearchTerm = { commitmentSearchTerm = it },
                    )
                }
            }
        }

        if (isSearchFormActive && commitmentSearchTerm.isNotEmpty()) {
            searchCommitmentsList(
                searchCommitments,
                onReminderAction = { commitment ->
                    selectedCommitment = commitment
                    showReminderViewDialog = true
                },
                onViewCommitment = { commitment ->
                    selectedCommitment = commitment
                    showCommitmentViewDialog = true
                },
                onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                onDeleteCommitment = { commitment ->
                    selectedCommitment = commitment
                    showDeleteDialog = true
                },
                onNavigateToFocusMode = onNavigateToFocusMode,
            )
        } else {
            if (columnViewSelected) {
                timelineColumn(
                    strings,
                    commitments,
                    onMove = { commitment, instant ->
                        homeViewModel.moveCommitment(commitment, instant)
                    },
                    onReminderAction = { commitment ->
                        selectedCommitment = commitment
                        showReminderViewDialog = true
                    },
                    onViewCommitment = { commitment ->
                        selectedCommitment = commitment
                        showCommitmentViewDialog = true
                    },
                    onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                    onDeleteCommitment = { commitment ->
                        selectedCommitment = commitment
                        showDeleteDialog = true
                    },
                    onNavigateToFocusMode = onNavigateToFocusMode,
                )
            } else {
                timelineGrid(
                    commitments,
                    onReminderAction = { commitment ->
                        selectedCommitment = commitment
                        showReminderViewDialog = true
                    },
                    onViewCommitment = { commitment ->
                        selectedCommitment = commitment
                        showCommitmentViewDialog = true
                    },
                    onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                    onDeleteCommitment = { commitment ->
                        selectedCommitment = commitment
                        showDeleteDialog = true
                    },
                    onNavigateToFocusMode = onNavigateToFocusMode,
                )
            }
        }
    }
}

@Composable
fun MonthGrid(
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit,
) {
    val numberOfColumns = 6
    val numberOfRows = 2
    val numberLettersAbbrev = 3

    Column {
        LocalStrings.current.monthNames
            .chunked(numberOfColumns)
            .take(numberOfRows)
            .forEachIndexed { rowIndex, row ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    row.forEachIndexed { columnIndex, month ->
                        val index = rowIndex * numberOfColumns + columnIndex

                        Text(
                            text = month.take(numberLettersAbbrev),
                            modifier =
                                Modifier
                                    .padding(PageDesignSettings.extraLargePaddingValue)
                                    .clickable { onMonthSelected(index + 1) },
                            style =
                                TextStyle(
                                    fontSize = PageDesignSettings.mediumText,
                                    fontWeight = if (index == selectedMonth - 1) FontWeight.W900 else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.secondary,
                                ),
                        )
                    }
                }
            }
    }
}

@Composable
fun DaysOnlyCalendar(
    yearMonth: YearMonth,
    selectedDay: Int,
    onDateSelected: (Int) -> Unit,
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val numberDayOfWeek = 7
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % numberDayOfWeek

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            LocalStrings.current.weekDaysAbbrev.forEach {
                Text(
                    text = it.toString(),
                    style =
                        TextStyle(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                            fontSize = PageDesignSettings.mediumText,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                    modifier =
                        Modifier
                            .size(PageDesignSettings.largeIconSize)
                            .background(Color.Transparent)
                            .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }
        }

        val totalCells = firstDayOfWeek + daysInMonth
        val numberOfRows = (totalCells / numberDayOfWeek) + if (totalCells % numberDayOfWeek > 0) 1 else 0

        repeat(numberOfRows) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                repeat(numberDayOfWeek) { column ->
                    val cellIndex = row * numberDayOfWeek + column

                    if (cellIndex < firstDayOfWeek || cellIndex >= firstDayOfWeek + daysInMonth) {
                        Box(modifier = Modifier.size(PageDesignSettings.largeIconSize))
                    } else {
                        val day = cellIndex - firstDayOfWeek + 1

                        Text(
                            text = day.toString(),
                            style =
                                TextStyle(
                                    color =
                                        if (day == selectedDay) {
                                            MaterialTheme.colorScheme.onSecondary
                                        } else {
                                            MaterialTheme.colorScheme.secondary
                                        },
                                    fontSize = PageDesignSettings.mediumText,
                                    textAlign = TextAlign.Center,
                                ),
                            modifier =
                                Modifier
                                    .size(PageDesignSettings.largeIconSize)
                                    .clickable { onDateSelected(day) }
                                    .background(
                                        if (day == selectedDay) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                        CircleShape,
                                    ).wrapContentSize(Alignment.Center),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchForm(
    strings: StringsRepository,
    commitmentSearchTerm: String,
    setIsSearchFormActive: (Boolean) -> Unit,
    setCommitmentSearchTerm: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = PageDesignSettings.mediumPaddingValue,
                    vertical = PageDesignSettings.extraLargePaddingValue,
                ).background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(PageDesignSettings.largeIconClip),
                ).border(
                    width = PageDesignSettings.borderWidth,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(PageDesignSettings.largeIconClip),
                ).padding(PageDesignSettings.mediumPaddingValue),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = PageDesignSettings.mediumPaddingValue),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = strings.searchCommitmentField,
                style =
                    TextStyle(
                        fontSize = PageDesignSettings.smallTitle,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    ),
                modifier = Modifier.padding(start = PageDesignSettings.mediumPaddingValue),
            )

            IconButton(
                onClick = {
                    setIsSearchFormActive(false)
                    setCommitmentSearchTerm("")
                },
                modifier = Modifier.size(PageDesignSettings.largeIconSize),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = strings.searchCommitmentField,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(PageDesignSettings.largeIconSize),
                )
            }
        }

        TextField(
            value = commitmentSearchTerm,
            onValueChange = { setCommitmentSearchTerm(it) },
            placeholder = {
                Text(
                    text = strings.commitmentTitleField,
                    style =
                        TextStyle(
                            fontSize = PageDesignSettings.mediumText,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        ),
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier.size(PageDesignSettings.smallIconSize),
                )
            },
            trailingIcon = {
                if (commitmentSearchTerm.isNotEmpty()) {
                    IconButton(
                        onClick = { setCommitmentSearchTerm("") },
                        modifier = Modifier.size(PageDesignSettings.mediumIconSize),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(PageDesignSettings.mediumIconSize),
                        )
                    }
                }
            },
            textStyle =
                TextStyle(
                    fontSize = PageDesignSettings.mediumText,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium,
                ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
        )
    }
}
