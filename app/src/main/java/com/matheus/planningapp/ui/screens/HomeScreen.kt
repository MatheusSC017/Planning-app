package com.matheus.planningapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matheus.planningapp.R
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.commitment.CommitmentEntity
import com.matheus.planningapp.data.local.converters.Priority
import com.matheus.planningapp.util.indexToTimeString
import com.matheus.planningapp.util.timeToIndex
import com.matheus.planningapp.ui.screens.components.ConfirmationDialog
import com.matheus.planningapp.viewmodel.home.HomeViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen (
    onNavigateToAddCommitment: (date: Instant, selectedCalendar: Int) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Int) -> Unit,
    homeViewModel: HomeViewModel = koinViewModel(),
    onMenuClick: () -> Unit
) {
    val calendarsEntities by homeViewModel.calendars.collectAsStateWithLifecycle()
    var selectedCalendar by remember { mutableStateOf<CalendarEntity?>(null) }
    var columnViewSelected by remember { mutableStateOf(true) }

    LaunchedEffect(calendarsEntities) {
        if (selectedCalendar == null && calendarsEntities.isNotEmpty()) {
            selectedCalendar = calendarsEntities.first()
        }
    }

    Scaffold (
        topBar = {
            PlanningTopAppBar(
                modifier = Modifier,
                calendarsEntities = calendarsEntities,
                selectedCalendar = selectedCalendar,
                onCalendarSelected = { selectedCalendar = it},
                columnViewSelected = columnViewSelected,
                onViewSelected = { columnViewSelected = it },
                onMenuClick = onMenuClick
            )
        },
        content = { paddingValues ->
            CalendarContent(
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
                selectedCalendar = selectedCalendar,
                columnViewSelected = columnViewSelected,
                onNavigateToAddCommitment = onNavigateToAddCommitment,
                onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                homeViewModel = homeViewModel
            )
        }
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
    onMenuClick: () -> Unit
) {

    var expandedCalendarDropDown by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = {},
        navigationIcon = {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.columns_view),
                    contentDescription = "Column view",
                    tint = if (columnViewSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (columnViewSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background)
                        .clickable {
                            onViewSelected(true)
                        }
                )
                Spacer(
                    modifier = Modifier.width(16.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.grid_view),
                    contentDescription = "Grid view",
                    tint = if (!columnViewSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!columnViewSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background)
                        .clickable {
                            onViewSelected(false)
                        }
                )
            }
        },
        actions = {
            ExposedDropdownMenuBox(
                expanded = expandedCalendarDropDown,
                onExpandedChange = { expandedCalendarDropDown = !expandedCalendarDropDown }
            ) {
                TextField(
                    value = selectedCalendar?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expandedCalendarDropDown)
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
                    expanded = expandedCalendarDropDown,
                    onDismissRequest = { expandedCalendarDropDown = false },
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
                                expandedCalendarDropDown = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = { onMenuClick() }
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
fun CalendarContent(
    modifier: Modifier,
    selectedCalendar: CalendarEntity?,
    columnViewSelected: Boolean,
    onNavigateToAddCommitment: (date: Instant, selectedCalendar: Int) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Int) -> Unit,
    homeViewModel: HomeViewModel
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val selectedDate = uiState.selectedDate
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val zone = remember { ZoneId.systemDefault() }
    val startOfDay = remember(selectedDate) {
        selectedDate.atStartOfDay(zone).toInstant().toKotlinInstant()
    }
    val endOfDay = remember(selectedDate) {
        selectedDate.atTime(LocalTime.MAX).atZone(zone).toInstant().toKotlinInstant()
    }
    val commitments by homeViewModel.getCommitmentsForDay(startOfDay, endOfDay, selectedCalendar?.id ?: 0).collectAsState(initial = emptyList())

    var selectedCommitment by remember { mutableStateOf<CommitmentEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCommitmentViewDialog by remember { mutableStateOf(false) }

    CommitmentViewDialog(
        commitmentEntity = selectedCommitment,
        showDialog = showCommitmentViewDialog,
        onDismissRequest = { showCommitmentViewDialog = false }
    )

    ConfirmationDialog(
        item = selectedCommitment,
        showDialog = showDeleteDialog,
        title = "Delete commitment",
        message = "Are you sure you want to delete this commitment?",
        onConfirm = { commitmentEntity: CommitmentEntity ->
            homeViewModel.deleteCommitment(commitmentEntity)
        },
        onDismissRequest = {
            showDeleteDialog = false
        }
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = 16.dp,
                start = 16.dp,
                bottom = 16.dp,
                end = 16.dp
            )
    ) {
        item {
            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = selectedDate.year.toString(),
                        style = TextStyle(
                            fontSize = 48.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Up",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .size(24.dp)
                                .clickable { homeViewModel.incrementYear() }
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Down",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .size(24.dp)
                                .clickable { homeViewModel.decrementYear() }
                        )
                    }

                }

                MonthGrid(
                    months = months,
                    selectedMonth = selectedDate.monthValue,
                    onMonthSelected = { homeViewModel.onSelectedDate(month = it) }
                )

                Text(
                    text = months[selectedDate.monthValue - 1],
                    style = TextStyle(
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            bottom = 16.dp
                        )
                )

                DaysOnlyCalendar(
                    yearMonth = YearMonth.of(selectedDate.year, selectedDate.monthValue),
                    selectedDay = selectedDate.dayOfMonth,
                    onDateSelected = { homeViewModel.onSelectedDate(day = it) }
                )

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Timeline",
                        style = TextStyle(
                            fontSize = 48.sp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                bottom = 16.dp
                            )
                    )

                    IconButton(
                        onClick = {
                            onNavigateToAddCommitment(startOfDay, selectedCalendar!!.id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add new Commitment",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                }
            }
        }

        if (columnViewSelected) {
            timelineColumn(
                commitments,
                onViewCommitment = { commitment ->
                    selectedCommitment = commitment
                    showCommitmentViewDialog = true
                },
                onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                onDeleteCommitment = { commitment ->
                    selectedCommitment = commitment
                    showDeleteDialog = true
                }
            )
        } else {
            timelineGrid(
                commitments,
                onViewCommitment = { commitment ->
                    selectedCommitment = commitment
                    showCommitmentViewDialog = true
                },
                onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                onDeleteCommitment = { commitment ->
                    selectedCommitment = commitment
                    showDeleteDialog = true
                }
            )
        }
    }
}

@Composable
fun MonthGrid(
    months: List<String>,
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit
) {
    Column {
        months
            .chunked(6)
            .take(2)
            .forEachIndexed { rowIndex, row ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEachIndexed { columnIndex, month ->
                        val index = rowIndex * 6 + columnIndex

                        Text(
                            text = month.take(3),
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { onMonthSelected(index + 1) },
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = if (index == selectedMonth - 1) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                                color = MaterialTheme.colorScheme.secondary
                            )
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
    onDateSelected: (Int) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7
    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach {
                Text(
                    text = it,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Transparent)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

        val totalCells = firstDayOfWeek + daysInMonth
        val numberOfRows = (totalCells / 7) + if (totalCells % 7 > 0) 1 else 0

        repeat(numberOfRows) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { column ->
                    val cellIndex = row * 7 + column

                    if (cellIndex < firstDayOfWeek || cellIndex >= firstDayOfWeek + daysInMonth) {
                        Box(modifier = Modifier.size(40.dp))
                    } else {
                        val day =  cellIndex - firstDayOfWeek + 1

                        Text(
                            text = day.toString(),
                            style = TextStyle(
                                color = if (day == selectedDay) MaterialTheme.colorScheme.onSecondary else
                                    MaterialTheme.colorScheme.secondary,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onDateSelected(day) }
                                .background(
                                    if (day == selectedDay) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                    CircleShape
                                )
                                .wrapContentSize(Alignment.Center)
                        )
                    }

                }
            }
        }

    }
}

fun LazyListScope.timelineGrid(
    commitments: List<CommitmentEntity>,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Int) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit
) {
    val timelineItems = List(48) { -1 }.toMutableList()
    val finalIndexCommitments: MutableList<Int> = emptyList<Int>().toMutableList()
    commitments.forEachIndexed { index, commitment ->
        val commitmentStartDateTime = commitment.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
        val commitmentEndDateTime = commitment.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
        val commitmentStartIndex: Int = timeToIndex(commitmentStartDateTime.time)
        val commitmentEndIndex: Int = if (commitmentEndDateTime.dayOfMonth != commitmentStartDateTime.dayOfMonth) 48
        else timeToIndex(commitmentEndDateTime.time)
        finalIndexCommitments.add(commitmentEndIndex)

        for (i in commitmentStartIndex until commitmentEndIndex) {
            timelineItems[i] = index
        }
    }

    item {
        timelineItems
            .withIndex()
            .chunked(4)
            .forEach { indexsRow ->
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val cellWidth = maxWidth / 4

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
                                    onViewCommitment = {},
                                    onNavigateToUpdateCommitment = {},
                                    onDeleteCommitment = {}
                                )
                            } else {
                                // Start a new block if this is the first column in the row (index % 4 == 0)
                                // or if the current commitment is different from the previous one.
                                if ((index % 4 == 0) || timelineItems[index - 1] != indexCommitment) {
                                    val commitmentEndIndex = finalIndexCommitments[indexCommitment]
                                    val colspan = if (commitmentEndIndex - index + 1 > 4) 4 - (index % 4) else commitmentEndIndex - index
                                    TimelineGridItem(
                                        startTime = indexToTimeString(index),
                                        commitmentEntity = commitments[indexCommitment],
                                        cellWidth = cellWidth,
                                        colspan = colspan,
                                        continuesInNextCell = if (index + colspan <= 47) timelineItems[index + colspan] == indexCommitment else false,
                                        continuesFromPreviousCell = if (index > 0) timelineItems[index - 1] == indexCommitment else false,
                                        onViewCommitment = onViewCommitment,
                                        onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                                        onDeleteCommitment = onDeleteCommitment
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
fun RowScope.TimelineGridItem(
    startTime: String,
    commitmentEntity: CommitmentEntity?,
    cellWidth: Dp,
    colspan: Int,
    continuesInNextCell: Boolean,
    continuesFromPreviousCell: Boolean,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Int) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit
){
    var menuExpanded by remember { mutableStateOf(false) }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(cellWidth * colspan)
            .height(cellWidth)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 8.dp,
                    bottom = 8.dp,
                    end = if (continuesInNextCell) 0.dp else 8.dp,
                    start = if (continuesFromPreviousCell) 0.dp else 8.dp
                )
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    ),
                    shape = RoundedCornerShape(
                        topEnd = if (continuesInNextCell) 0.dp else 12.dp,
                        bottomEnd = if (continuesInNextCell) 0.dp else 12.dp,
                        topStart = if (continuesFromPreviousCell) 0.dp else 12.dp,
                        bottomStart = if (continuesFromPreviousCell) 0.dp else 12.dp,
                    )
                )
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var titleOfCell = ""
                if (!continuesFromPreviousCell) titleOfCell += startTime
                if (commitmentEntity != null && !continuesInNextCell && (continuesFromPreviousCell || colspan > 1)) {
                    val commitmentEndDateTime = commitmentEntity.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
                    titleOfCell += String.format(
                        Locale.US,
                        " ~ %02d:%02d",
                        commitmentEndDateTime.hour,
                        commitmentEndDateTime.minute
                    )
                }
                Text(
                    text = titleOfCell,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )

                if (commitmentEntity != null) {
                    val statusColor = when(commitmentEntity.priority) {
                        Priority.LOW -> Color.Green.copy(alpha = .6f)
                        Priority.MEDIUM -> Color.Yellow.copy(alpha = .6f)
                        Priority.HIGH -> Color.Red.copy(alpha = .6f)
                    }

                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                }
            }

            if (commitmentEntity != null) {
                Box (
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        painterResource(R.drawable.outline_more_horiz_24),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { menuExpanded = true },
                        tint = MaterialTheme.colorScheme.secondary.copy(.6f)
                    )

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "View",
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onViewCommitment(commitmentEntity)
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
                            text = {
                                Text(
                                    "Edit",
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onNavigateToUpdateCommitment(commitmentEntity.id)
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
                                onDeleteCommitment(commitmentEntity)
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
}

fun LazyListScope.timelineColumn(
    commitments: List<CommitmentEntity>,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Int) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit
) {
    var commitmentsLastIndex = 0

    if (commitments.isEmpty()) {
        items(48) { index ->
            TimelineRow(
                startTime = indexToTimeString(index),
                commitment = null,
                onViewCommitment = {},
                onNavigateToUpdateCommitment = {},
                onDeleteCommitment = {}
            )
        }
    } else {
        val timesList = List(48) { it }
        commitments.forEach { commitment ->
            val commitmentStartDateTime = commitment.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
            val commitmentEndDateTime = commitment.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
            val commitmentStartTime: String = String.format(Locale.US, "%02d:%02d", commitmentStartDateTime.hour, commitmentStartDateTime.minute)
            val commitmentStartIndex: Int = timeToIndex(commitmentStartDateTime.time)

            if (commitmentsLastIndex < commitmentStartIndex) {
                items(timesList.subList(commitmentsLastIndex, commitmentStartIndex)) { index ->
                    TimelineRow(
                        startTime = indexToTimeString(index),
                        commitment = null,
                        onViewCommitment = {},
                        onNavigateToUpdateCommitment = {},
                        onDeleteCommitment = {}
                    )
                }
            }

            commitmentsLastIndex = if (commitmentEndDateTime.dayOfMonth != commitmentStartDateTime.dayOfMonth) 48
            else timeToIndex(commitmentEndDateTime.time)


            item {
                TimelineRow(
                    startTime = commitmentStartTime,
                    commitment = commitment,
                    onViewCommitment = onViewCommitment,
                    onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                    onDeleteCommitment = onDeleteCommitment
                )
            }
        }

        if (commitmentsLastIndex < 48) {
            items(timesList.subList(commitmentsLastIndex, 48)) { index ->
                TimelineRow(
                    startTime = indexToTimeString(index),
                    commitment = null,
                    onViewCommitment = {},
                    onNavigateToUpdateCommitment = {},
                    onDeleteCommitment = {}
                )

            }
        }
    }
}

@Composable
fun TimelineRow(
    startTime: String,
    commitment: CommitmentEntity?,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Int) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(end = 16.dp, start = 16.dp, bottom = 16.dp)
            .height(IntrinsicSize.Min)
            .heightIn(min = 100.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(60.dp)
        ) {
            Text(
                text = startTime,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = .6f)
            )

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = .6f))
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (commitment != null) {
            CommitmentCard(
                commitmentEntity = commitment,
                onViewCommitment = onViewCommitment,
                onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                onDeleteCommitment = onDeleteCommitment
            )
        }
    }
}

@Composable
fun CommitmentCard(
    commitmentEntity: CommitmentEntity,
    onViewCommitment: (commitment: CommitmentEntity) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Int) -> Unit,
    onDeleteCommitment: (commitment: CommitmentEntity) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val commitmentStartDateTime: LocalDateTime = commitmentEntity.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val startTimeString = String.format(Locale.US, "%02d:%02d", commitmentStartDateTime.hour, commitmentStartDateTime.minute)
    val commitmentEndDateTime: LocalDateTime = commitmentEntity.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTimeString = String.format(Locale.US, "%02d:%02d", commitmentEndDateTime.hour, commitmentEndDateTime.minute)

    val statusColor = when(commitmentEntity.priority) {
        Priority.LOW -> Color.Green.copy(alpha = .6f)
        Priority.MEDIUM -> Color.Yellow.copy(alpha = .6f)
        Priority.HIGH -> Color.Red.copy(alpha = .6f)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(12.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {

                Text(
                    text = commitmentEntity.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$startTimeString — $endTimeString",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f)
                )
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        painterResource(R.drawable.outline_more_horiz_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary.copy(.6f)
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
                            onViewCommitment(commitmentEntity)
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
                            onNavigateToUpdateCommitment(commitmentEntity.id)
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
                            onDeleteCommitment(commitmentEntity)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Edit commitment",
                                tint = MaterialTheme.colorScheme.error)
                        }
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
    onDismissRequest: () -> Unit
) {
    if (commitmentEntity == null) return

    val statusColor = when(commitmentEntity.priority) {
        Priority.LOW -> Color.Green.copy(alpha = .6f)
        Priority.MEDIUM -> Color.Yellow.copy(alpha = .6f)
        Priority.HIGH -> Color.Red.copy(alpha = .6f)
    }

    val commitmentStartDateTime: LocalDateTime = commitmentEntity.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val startTimeString = String.format(Locale.US, "%02d:%02d", commitmentStartDateTime.hour, commitmentStartDateTime.minute)
    val commitmentEndDateTime: LocalDateTime = commitmentEntity.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTimeString = String.format(Locale.US, "%02d:%02d", commitmentEndDateTime.hour, commitmentEndDateTime.minute)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Column {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = commitmentEntity.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = .6f),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )

                        Text(
                            text = String.format(Locale.US, "%04d-%02d-%02d", commitmentStartDateTime.year, commitmentStartDateTime.monthNumber, commitmentStartDateTime.dayOfMonth),
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
                            text = commitmentEntity.description ?: "",
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
