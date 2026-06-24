package com.matheus.planningapp.ui.screens.recurrence

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matheus.planningapp.ui.screens.components.stardardBackground
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.viewmodel.recurrence.RecurrenceViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecurrenceScreen(
    recurrenceViewModel: RecurrenceViewModel = koinViewModel(),
    onMenuClick: () -> Unit,
    onNavigateToUpdateCommitment: (commitmentId: Long) -> Unit,
) {
    val uiState by recurrenceViewModel.uiState.collectAsStateWithLifecycle()
    val recurrences by recurrenceViewModel.filteredRecurrences.collectAsStateWithLifecycle()
    val selectedCalendar by recurrenceViewModel.selectedCalendar.collectAsStateWithLifecycle()

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
            Box(
                modifier =  Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .stardardBackground(),
                contentAlignment = Alignment.Center,
            ) {
                if (recurrences.isEmpty())
                    EmptyListRecurrences(LocalStrings.current.noValuesFound)
                else
                    RecurrenceList(
                        onDeleteRecurrence = { recurrenceId ->
                            recurrenceViewModel.deleteRecurrence(recurrenceId)
                        },
                        recurrences = recurrences,
                        onNavigateToUpdateCommitment = onNavigateToUpdateCommitment,
                    )
            }

        },
    )
}


@Composable
fun EmptyListRecurrences(
    emptyText: String,
) {
    Text(
        text = emptyText,
        style =
            TextStyle(
                fontSize = PageDesignSettings.smallTitle,
                color = MaterialTheme.colorScheme.secondary,
            ),
    )
}
