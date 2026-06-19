package com.matheus.planningapp.ui.screens.recurrence

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matheus.planningapp.ui.screens.components.stardardBackground
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.viewmodel.recurrence.RecurrenceViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceScreen(
    recurrenceViewModel: RecurrenceViewModel = koinViewModel(),
    onMenuClick: () -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
) {
    val uiState by recurrenceViewModel.uiState.collectAsStateWithLifecycle()
    val recurrences by recurrenceViewModel.filteredRecurrences.collectAsStateWithLifecycle()
    val strings: StringsRepository = LocalStrings.current

    val selectedCalendar by recurrenceViewModel.selectedCalendar.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.calendars) {
        recurrenceViewModel.initializeDefaultCalendar(uiState.calendars)
    }

    Scaffold(
        topBar = {
            RecurrenceTopAppBar(
                calendarsEntities = uiState.calendars,
                selectedCalendar = selectedCalendar,
                onCalendarSelected = { calendar ->
                    recurrenceViewModel.onCalendarSelected(calendar)
                },
                onMenuClick = onMenuClick,
            )
        },
        content = { paddingValues ->

            if (recurrences.isEmpty())
                EmptyListRecurrences(
                    paddingValues = paddingValues,
                    strings = strings,
                )
            else
                RecurrenceList(
                    modifier =
                        Modifier
                            .padding(paddingValues)
                            .stardardBackground(),
                    onDeleteRecurrence = { recurrenceId ->
                        recurrenceViewModel.deleteRecurrence(recurrenceId)
                    },
                    recurrences = recurrences,
                    onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                )
        },
    )
}


@Composable
fun EmptyListRecurrences(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    strings: StringsRepository
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues).stardardBackground(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = strings.noValuesFound,
            style =
                TextStyle(
                    fontSize = PageDesignSettings.smallTitle,
                    color = MaterialTheme.colorScheme.secondary,
                ),
        )
    }
}
