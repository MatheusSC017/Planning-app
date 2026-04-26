package com.matheus.planningapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.matheus.planningapp.R
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.reminder.ReminderEntity
import com.matheus.planningapp.ui.screens.components.ConfirmationDialog
import com.matheus.planningapp.ui.screens.components.IntegerField
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.DatabaseUiEvent
import com.matheus.planningapp.util.enums.NotificationEnum
import com.matheus.planningapp.util.enums.PriorityEnum
import com.matheus.planningapp.util.enums.ViewEnum
import com.matheus.planningapp.util.indexToTimeString
import com.matheus.planningapp.util.notification.TaskNotificationScheduler
import com.matheus.planningapp.util.timeToIndex
import com.matheus.planningapp.viewmodel.home.HomeUiState
import com.matheus.planningapp.viewmodel.home.HomeViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddCommitment: (date: Instant, selectedCalendar: Long) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    homeViewModel: HomeViewModel = koinViewModel(),
    onMenuClick: () -> Unit,
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    var selectedCalendar by remember { mutableStateOf<CalendarEntity?>(null) }
    var columnViewSelected by remember(uiState.viewMode) { mutableStateOf(uiState.viewMode == ViewEnum.COLUMN) }

    LaunchedEffect(uiState.calendars) {
        if (selectedCalendar == null && uiState.calendars.isNotEmpty()) {
            selectedCalendar = uiState.calendars.first()
        }
    }

    val strings: StringsRepository = LocalStrings.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            homeViewModel.events.collect { event ->
                when (event) {
                    is DatabaseUiEvent.ShowError -> {
                        scope.launch {
                            snackBarHostState.showSnackbar(event.message)
                        }
                    }

                    DatabaseUiEvent.Saved -> {
                        scope.launch {
                            snackBarHostState.showSnackbar(strings.savedMessage)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            PlanningTopAppBar(
                modifier = Modifier,
                calendarsEntities = uiState.calendars,
                selectedCalendar = selectedCalendar,
                onCalendarSelected = { selectedCalendar = it },
                columnViewSelected = columnViewSelected,
                onViewSelected = { columnViewSelected = it },
                onMenuClick = onMenuClick,
            )
        },
        content = { paddingValues ->
            CalendarContent(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f),
                                    MaterialTheme.colorScheme.background,
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite,
                            ),
                        ),
                selectedCalendar = selectedCalendar,
                columnViewSelected = columnViewSelected,
                onNavigateToAddCommitment = onNavigateToAddCommitment,
                onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                homeViewModel = homeViewModel,
                uiState = uiState,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningTopAppBar(
    modifier: Modifier,
    calendarsEntities: List<CalendarEntity>,
    selectedCalendar: CalendarEntity?,
    columnViewSelected: Boolean,
    onViewSelected: (Boolean) -> Unit,
    onCalendarSelected: (CalendarEntity) -> Unit,
    onMenuClick: () -> Unit,
) {
    var isExpandedCalendarDropDown by remember { mutableStateOf(false) }
    val strings: StringsRepository = LocalStrings.current

    TopAppBar(
        modifier = modifier,
        title = {},
        navigationIcon = {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.columns_view),
                    contentDescription = strings.columnView,
                    tint = if (columnViewSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier
                            .size(PageDesignSettings.largeIconSize)
                            .clip(RoundedCornerShape(PageDesignSettings.mediumIconClip))
                            .background(
                                if (columnViewSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                            ).padding(PageDesignSettings.extraSmallPaddingValue)
                            .clickable {
                                onViewSelected(true)
                            },
                )

                Spacer(
                    modifier = Modifier.width(PageDesignSettings.extraLargePaddingValue),
                )

                Icon(
                    painter = painterResource(id = R.drawable.grid_view),
                    contentDescription = strings.gridView,
                    tint = if (!columnViewSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier
                            .size(PageDesignSettings.largeIconSize)
                            .clip(RoundedCornerShape(PageDesignSettings.mediumIconClip))
                            .background(
                                if (!columnViewSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                            ).padding(PageDesignSettings.extraSmallPaddingValue)
                            .clickable {
                                onViewSelected(false)
                            },
                )
            }
        },
        actions = {
            ExposedDropdownMenuBox(
                expanded = isExpandedCalendarDropDown,
                onExpandedChange = { isExpandedCalendarDropDown = !isExpandedCalendarDropDown },
            ) {
                TextField(
                    value = selectedCalendar?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(isExpandedCalendarDropDown)
                    },
                    modifier =
                        Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .width(PageDesignSettings.largeComponentSize),
                    textStyle =
                        TextStyle(
                            fontSize = PageDesignSettings.mediumText,
                        ),
                    colors =
                        ExposedDropdownMenuDefaults.textFieldColors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.secondary,
                            unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                            disabledTextColor = MaterialTheme.colorScheme.secondary,
                        ),
                )

                ExposedDropdownMenu(
                    expanded = isExpandedCalendarDropDown,
                    onDismissRequest = { isExpandedCalendarDropDown = false },
                    containerColor = MaterialTheme.colorScheme.background,
                    border = BorderStroke(PageDesignSettings.borderWidth, MaterialTheme.colorScheme.primary),
                ) {
                    calendarsEntities.forEach { calendarEntity ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = calendarEntity.name,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            onClick = {
                                onCalendarSelected(calendarEntity)
                                isExpandedCalendarDropDown = false
                            },
                        )
                    }
                }
            }

            IconButton(
                onClick = { onMenuClick() },
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = strings.menuButton,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(PageDesignSettings.largeIconSize),
                )
            }
        },
    )
}

@Composable
fun CalendarContent(
    modifier: Modifier,
    selectedCalendar: CalendarEntity?,
    columnViewSelected: Boolean,
    onNavigateToAddCommitment: (date: Instant, selectedCalendar: Long) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    homeViewModel: HomeViewModel,
    uiState: HomeUiState,
    taskNotificationScheduler: TaskNotificationScheduler = koinInject(),
) {
    val strings: StringsRepository = LocalStrings.current
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
                                    isSearchFormActive = false
                                    commitmentSearchTerm = ""
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
                            onValueChange = { commitmentSearchTerm = it },
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
                                        onClick = { commitmentSearchTerm = "" },
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
            )
        } else {
            if (columnViewSelected) {
                timelineColumn(
                    strings,
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
                )
            }
        }
    }
}

fun LazyListScope.searchCommitmentsList(
    commitments: List<CommitmentEntity>,
    onReminderAction: (commitment: CommitmentEntity) -> Unit,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit,
) {
    items(commitments) { commitment ->

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
                48
            } else {
                timeToIndex(commitmentEndDateTime.time)
            }
        finalIndexCommitments.add(commitmentEndIndex)

        for (i in commitmentStartIndex until commitmentEndIndex) {
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
                                    val colspan =
                                        if (commitmentEndIndex - index + 1 >
                                            numberOfColumns
                                        ) {
                                            numberOfColumns - (index % numberOfColumns)
                                        } else {
                                            commitmentEndIndex - index
                                        }
                                    TimelineGridItem(
                                        startTime = indexToTimeString(index),
                                        commitmentEntity = commitments[indexCommitment],
                                        cellWidth = cellWidth,
                                        colspan = colspan,
                                        continuesInNextCell =
                                            if (index + colspan <=
                                                timelineItems.size - 1
                                            ) {
                                                timelineItems[index + colspan] == indexCommitment
                                            } else {
                                                false
                                            },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentViewDialog(
    commitmentEntity: CommitmentEntity?,
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
) {
    if (commitmentEntity == null) return

    val strings: StringsRepository = LocalStrings.current

    val statusColor =
        when (commitmentEntity.priorityEnum) {
            PriorityEnum.LOW -> Color.Green.copy(alpha = .6f)
            PriorityEnum.MEDIUM -> Color.Yellow.copy(alpha = .6f)
            PriorityEnum.HIGH -> Color.Red.copy(alpha = .6f)
        }

    val commitmentStartDateTime: LocalDateTime = commitmentEntity.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val startTimeString = String.format(Locale.US, strings.hourFormat, commitmentStartDateTime.hour, commitmentStartDateTime.minute)
    val commitmentEndDateTime: LocalDateTime = commitmentEntity.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTimeString = String.format(Locale.US, strings.hourFormat, commitmentEndDateTime.hour, commitmentEndDateTime.minute)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(PageDesignSettings.extraLargePaddingValue),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(PageDesignSettings.extraSmallIconSize)
                                    .clip(CircleShape)
                                    .background(statusColor),
                        )

                        Spacer(modifier = Modifier.width(PageDesignSettings.largePaddingValue))

                        Text(
                            text = commitmentEntity.title,
                            fontSize = PageDesignSettings.largeText,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondary,
                        )
                    }

                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
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
                Column {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(PageDesignSettings.mediumIconClip),
                                ).padding(PageDesignSettings.mediumPaddingValue),
                    ) {
                        Text(
                            text = commitmentEntity.description ?: "",
                            fontSize = PageDesignSettings.smallText,
                            color = MaterialTheme.colorScheme.onSecondary,
                        )
                    }

                    Spacer(modifier = Modifier.height(PageDesignSettings.extraLargePaddingValue))

                    HorizontalDivider()
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(
                    onClick = onDismissRequest,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = strings.dismissButton,
                        fontSize = PageDesignSettings.largeText,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderViewDialog(
    commitmentEntity: CommitmentEntity?,
    reminders: List<ReminderEntity>,
    showDialog: Boolean,
    onInsertReminderAction: (commitmentEntity: CommitmentEntity, minutesBeforeCommitment: Int) -> Unit,
    onUpdateReminderAction: (reminderEntity: ReminderEntity, startDateTime: Instant, minutesBeforeCommitment: Int) -> Unit,
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

                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
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
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(PageDesignSettings.largeIconClip),
                            ).padding(PageDesignSettings.mediumPaddingValue),
                ) {
                    Text(
                        text = strings.reminderField,
                        style =
                            TextStyle(
                                fontSize = PageDesignSettings.smallTitle,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                    )

                    IntegerField(
                        selectedValue = minutesBeforeCommitment,
                        onIntegerValueChange = { newValue ->
                            minutesBeforeCommitment = newValue
                        },
                        minValue = 1,
                        maxValue = 60,
                    )

                    if (selectedReminder != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Button(
                                onClick = {
                                    selectedReminder = null
                                },
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.secondary,
                                    ),
                            ) {
                                Text(
                                    text = strings.cancelButton,
                                    fontSize = PageDesignSettings.largeText,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.padding(top = PageDesignSettings.mediumPaddingValue),
                    ) {
                        items(reminders) { reminder ->
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = PageDesignSettings.mediumPaddingValue)
                                        .background(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(PageDesignSettings.smallIconClip),
                                        ).border(
                                            BorderStroke(
                                                PageDesignSettings.borderWidth,
                                                MaterialTheme.colorScheme.secondary,
                                            ),
                                            shape = RoundedCornerShape(PageDesignSettings.smallIconClip),
                                        ).padding(PageDesignSettings.smallPaddingValue),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = strings.reminderInfo.format(reminder.minutesBeforeCommitment),
                                    style =
                                        TextStyle(
                                            fontSize = PageDesignSettings.mediumText,
                                            color = MaterialTheme.colorScheme.secondary,
                                        ),
                                )

                                IconButton(
                                    onClick = {
                                        selectedReminder = reminder
                                        minutesBeforeCommitment = reminder.minutesBeforeCommitment
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = strings.updateButton,
                                        tint = MaterialTheme.colorScheme.secondary,
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        // TODO: Delete reminder action
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = strings.deleteButton,
                                        tint = MaterialTheme.colorScheme.secondary,
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentReminder = selectedReminder

                        if (currentReminder == null) {
                            onInsertReminderAction(commitmentEntity, minutesBeforeCommitment)
                        } else {
                            onUpdateReminderAction(currentReminder, commitmentEntity.startDateTime, minutesBeforeCommitment)
                        }
                        minutesBeforeCommitment = 1
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                    modifier = Modifier.width(PageDesignSettings.mediumComponentSize),
                ) {
                    Text(
                        text = if (selectedReminder == null) strings.insertButton else strings.updateButton,
                        fontSize = PageDesignSettings.largeText,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        minutesBeforeCommitment = 1
                        onDismissRequest()
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.secondary,
                        ),
                    modifier = Modifier.width(PageDesignSettings.mediumComponentSize),
                ) {
                    Text(
                        text = strings.dismissButton,
                        fontSize = PageDesignSettings.largeText,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.onBackground,
        )
    }
}
