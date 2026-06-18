package com.matheus.planningapp.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.ui.screens.components.HandleEvents
import com.matheus.planningapp.ui.screens.components.stardardBackground
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.DatabaseUiEvent
import com.matheus.planningapp.util.enums.ViewEnum
import com.matheus.planningapp.viewmodel.home.HomeViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddCommitment: (date: Instant, selectedCalendar: Long) -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
    homeViewModel: HomeViewModel = koinViewModel(),
    onMenuClick: () -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    var selectedCalendar by remember { mutableStateOf<CalendarEntity?>(null) }
    var columnViewSelected by remember(uiState.viewMode) { mutableStateOf(uiState.viewMode == ViewEnum.COLUMN) }

    LaunchedEffect(uiState.calendars) {
        if (selectedCalendar == null && uiState.calendars.isNotEmpty()) {
            selectedCalendar = uiState.calendars.first()
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    HandleEvents(homeViewModel.events) { event ->
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
            HomeContent(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .stardardBackground(),
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
