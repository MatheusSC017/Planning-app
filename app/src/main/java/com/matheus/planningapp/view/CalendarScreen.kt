package com.matheus.planningapp.view

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matheus.planningapp.R
import com.matheus.planningapp.data.CalendarEntity
import com.matheus.planningapp.viewmodel.CalendarViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen (
    onNavigateToCommitment: (date: Instant, selectedCalendar: Int) -> Unit,
    calendarViewModel: CalendarViewModel = koinViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val calendarsEntities by calendarViewModel.calendars.collectAsStateWithLifecycle()
    var selectedCalendar by remember { mutableStateOf<CalendarEntity?>(null) }

    LaunchedEffect(calendarsEntities) {
        if (selectedCalendar == null && calendarsEntities.isNotEmpty()) {
            selectedCalendar = calendarsEntities.first()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "My Calendars",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
    ) {
        Scaffold (
            topBar = {
                PlanningTopAppBar(
                    modifier = Modifier,
                    calendarsEntities = calendarsEntities,
                    selectedCalendar = selectedCalendar,
                    onCalendarSelected = { selectedCalendar = it},
                    onMenuClick = { scope.launch { drawerState.open() }}
                )
            },
            content = { paddingValues ->
                CalendarContent(
                    modifier = Modifier
                        .padding(paddingValues),
                    selectedCalendar = selectedCalendar,
                    onNavigateToCommitment = onNavigateToCommitment,
                    calendarViewModel = calendarViewModel
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningTopAppBar(
    modifier: Modifier,
    calendarsEntities: List<CalendarEntity>,
    selectedCalendar: CalendarEntity?,
    onCalendarSelected: (CalendarEntity) -> Unit,
    onMenuClick: () -> Unit
) {
    var columnViewSelected by remember { mutableStateOf(true) }
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
                        .height(32.dp)
                        .width(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (columnViewSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background)
                        .clickable { columnViewSelected = true }
                )
                Icon(
                    painter = painterResource(id = R.drawable.grid_view),
                    contentDescription = "Grid view",
                    tint = if (!columnViewSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .height(32.dp)
                        .width(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!columnViewSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background)
                        .clickable { columnViewSelected = false }
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
                        fontSize = 24.sp
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
                    modifier = Modifier
                        .size(64.dp)
                )
            }
        }
    )
}

@Composable
fun CalendarContent(
    modifier: Modifier,
    selectedCalendar: CalendarEntity?,
    onNavigateToCommitment: (date: Instant, selectedCalendar: Int) -> Unit,
    calendarViewModel: CalendarViewModel
) {
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    var selectedDay by remember { mutableIntStateOf(LocalDate.now().dayOfMonth) }
    var selectedMonth by remember { mutableIntStateOf(LocalDate.now().monthValue) }
    var selectedYear by remember { mutableIntStateOf(LocalDate.now().year) }
    val tasks = mapOf(
        1 to "Task 1",
        7 to "Task 7",
        8 to "Task 8",
        13 to "Task 13",
        15 to "Task 15",
        18 to "Task 18",
        24 to "Task 24",
    )

    val zone = remember { ZoneId.systemDefault() }
    val date = remember(selectedYear, selectedMonth, selectedDay) {
        LocalDate.of(selectedYear, selectedMonth, selectedDay)
    }
    val startOfDay = remember(date) {
        date.atStartOfDay(zone).toInstant().toKotlinInstant()
    }
    val endOfDay = remember(date) {
        date.atTime(LocalTime.MAX).atZone(zone).toInstant().toKotlinInstant()
    }
    val commitments by calendarViewModel.getCommitmentsForDay(startOfDay, endOfDay).collectAsState(initial = emptyList())
    Log.d("Commitments", "CalendarContent: $commitments")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(
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
                        text = selectedYear.toString(),
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
                                .clickable { selectedYear += 1 }
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Down",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .size(24.dp)
                                .clickable { selectedYear -= 1 }
                        )
                    }

                }

                MonthGrid(
                    months = months,
                    selectedMonth = selectedMonth,
                    onMonthSelected = { selectedMonth = it }
                )

                Text(
                    text = months[selectedMonth - 1],
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
                    yearMonth = YearMonth.of(selectedYear, selectedMonth),
                    selectedDay = selectedDay,
                    onDateSelected = { selectedDay = it }
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

                    Row {
                        IconButton(
                            onClick = {
                                if (selectedCalendar != null) {
                                    onNavigateToCommitment(startOfDay, selectedCalendar.id)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add new Commitment",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(64.dp)
                            )
                        }

                        IconButton(
                            onClick = { /* Todo: Update calendar commitments */ }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Commitments",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(64.dp)
                            )
                        }
                    }
                }

            }

        }

        items(24) { index ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .padding(bottom = 8.dp)
                    .drawBehind {
                        drawLine(
                            color = Color.White,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .background(if (tasks.containsKey(index + 1)) MaterialTheme.colorScheme.onBackground else Color.Transparent)
            ) {
                val time = String.format("%2d:00", index + 1)
                Text(
                    text = time,
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = if (tasks.containsKey(index + 1)) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .width(96.dp)
                )
                if (tasks.containsKey(index + 1)) {
                    Text(
                        text =  tasks[index + 1] ?: "",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = if (tasks.containsKey(index + 1)) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.secondary
                        ),
                    )
                }
            }
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

                    if (cellIndex < firstDayOfWeek) {
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
