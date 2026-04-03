package com.matheus.planningapp.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.ui.screens.components.ConfirmationDialog
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.DatabaseUiEvent
import com.matheus.planningapp.viewmodel.calendar.CalendarViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = koinViewModel(),
    onMenuClick: () -> Unit
) {
    val strings: StringsRepository = LocalStrings.current

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
                        snackBarHostState.showSnackbar(strings.savedMessage)
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
                        text = strings.calendarsMenuButton,
                        style = TextStyle(
                            fontSize = PageDesignSettings.mediumTitle,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = onMenuClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = strings.menuButton,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(PageDesignSettings.largeIconSize)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            CalendarsMenuContent(
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
    val strings: StringsRepository = LocalStrings.current

    var selectedCalendarToDelete by remember { mutableStateOf<CalendarEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val calendarEntities by calendarViewModel.calendars.collectAsStateWithLifecycle()

    var selectedCalendar by remember { mutableStateOf<CalendarEntity?>(null) }
    var calendarName by remember { mutableStateOf("") }
    var calendarIsDefault by remember { mutableStateOf(false) }

    ConfirmationDialog(
        item = selectedCalendarToDelete,
        showDialog = showDialog,
        title = strings.dialogDeleteCalendarTitle,
        message = strings.dialogDeleteCalendarMessage,
        onConfirm = { calendar: CalendarEntity ->
            calendarViewModel.deleteCalendar(calendar)
        },
        onDismissRequest = {
            showDialog = false
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(PageDesignSettings.extraLargePaddingValue)
    ) {

        Column {
            TextField(
                value = calendarName,
                onValueChange = { calendarName = it },
                label = {
                    Text(
                        text = strings.calendarNameField,
                        style = TextStyle(
                            fontSize = PageDesignSettings.mediumText,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    )
                },
                textStyle = TextStyle(
                    fontSize = PageDesignSettings.mediumText,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PageDesignSettings.smallComponentSize)
                    .padding(bottom = PageDesignSettings.mediumPaddingValue),
                singleLine = true
            )

            Row(
                modifier = Modifier.padding(bottom = PageDesignSettings.mediumPaddingValue),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = calendarIsDefault,
                    onCheckedChange = { calendarIsDefault = it }
                )
                Text(
                    text = strings.calendarIsDefaultField,
                    style = TextStyle(
                        fontSize = PageDesignSettings.mediumText,
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
                            val updatedCalendar = calendar.copy(
                                name = calendarName,
                                isDefault = calendarIsDefault,
                                updatedAt = Clock.System.now()
                            )
                            calendarViewModel.updateCalendar(updatedCalendar)
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
                        contentDescription = strings.confirmButton,
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
                        contentDescription = strings.cancelButton,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = PageDesignSettings.extraLargePaddingValue)
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
                                fontSize = PageDesignSettings.mediumText,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(PageDesignSettings.extraLargePaddingValue)
                        )

                        Row(
                            modifier = Modifier.padding(PageDesignSettings.extraLargePaddingValue)
                        ) {
                            if (calendar.isDefault) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = strings.calendarIsDefaultField,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(PageDesignSettings.largePaddingValue)
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
                                        contentDescription = strings.calendarIsDefaultField,
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
                                    contentDescription = strings.updateButton,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = PageDesignSettings.extraLargePaddingValue)
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
                                    contentDescription = strings.deleteButton,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = PageDesignSettings.extraLargePaddingValue)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
