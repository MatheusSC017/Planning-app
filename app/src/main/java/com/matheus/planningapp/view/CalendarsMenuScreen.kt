package com.matheus.planningapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.matheus.planningapp.data.CalendarEntity
import com.matheus.planningapp.ui.theme.DatabaseUiEvent
import com.matheus.planningapp.view.components.ConfirmationDialog
import com.matheus.planningapp.viewmodel.CalendarViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarsMenuScreen(
    onBackPressed: () -> Unit,
    calendarViewModel: CalendarViewModel = koinViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            calendarViewModel.events.collect { event ->
                when (event) {
                    is DatabaseUiEvent.ShowError -> {
                        scope.launch {
                            snackBarHostState.showSnackbar(event.message)
                        }
                    }

                    DatabaseUiEvent.Saved -> {
                        snackBarHostState.showSnackbar("Saved")
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendars",
                        style = TextStyle(
                            fontSize = 36.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = onBackPressed
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            CalendarsMenuContent(
                modifier = Modifier.padding(paddingValues),
                calendarViewModel = calendarViewModel
            )
        }
    )

}

@Composable
fun CalendarsMenuContent(
    modifier: Modifier,
    calendarViewModel: CalendarViewModel
) {
    var selectedCalendarToDelete by remember { mutableStateOf<CalendarEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val calendarEntities by calendarViewModel.calendars.collectAsStateWithLifecycle()

    var selectedCalendar by remember { mutableStateOf<CalendarEntity?>(null) }
    var calendarName by remember { mutableStateOf("") }
    var calendarIsDefault by remember { mutableStateOf(false) }

    ConfirmationDialog(
        item = selectedCalendarToDelete,
        showDialog = showDialog,
        title = "Delete calendar",
        message = "Are you sure you want to delete this calendar?",
        onConfirm = { calendar: CalendarEntity ->
            calendarViewModel.deleteCalendar(calendar)
        },
        onDismiss = {
            showDialog = false
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Column {
            TextField(
                value = calendarName,
                onValueChange = { calendarName = it },
                label = {
                    Text(
                        text = "Calendar name",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = calendarIsDefault,
                    onCheckedChange = { calendarIsDefault = it }
                )
                Text(
                    text = "Set as default",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        val calendar = selectedCalendar
                        
                        if (calendar != null) {
                            calendar.name = calendarName
                            calendar.isDefault = calendarIsDefault
                            calendar.updatedAt = Clock.System.now()
                            calendarViewModel.updateCalendar(calendar)
                        } else {
                            calendarViewModel.insertCalendar(
                                CalendarEntity(
                                    name = calendarName,
                                    isDefault = calendarIsDefault
                                )
                            )
                        }
                        selectedCalendar = null
                        calendarName = ""
                        calendarIsDefault = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = {
                        selectedCalendar = null
                        calendarName = ""
                        calendarIsDefault = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Cancel",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            calendarEntities.forEach { calendar ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.onPrimary),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = calendar.name,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(16.dp)
                        )

                        Row(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            if (calendar.isDefault) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Default",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            } else {
                                IconButton(
                                    onClick = {
                                        calendar.isDefault = true
                                        calendar.updatedAt = Clock.System.now()
                                        calendarViewModel.updateCalendar(calendar)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FavoriteBorder,
                                        contentDescription = "Set as default",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    selectedCalendar = calendar
                                    calendarName = calendar.name
                                    calendarIsDefault = calendar.isDefault
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    selectedCalendarToDelete = calendar
                                    showDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
