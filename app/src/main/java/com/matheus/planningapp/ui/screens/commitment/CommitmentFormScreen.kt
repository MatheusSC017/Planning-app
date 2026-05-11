package com.matheus.planningapp.ui.screens.commitment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import com.matheus.planningapp.ui.screens.components.HandleEvents
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.DatabaseUiEvent
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormMode
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentScreen(
    onBackPressed: () -> Unit,
    commitmentFormMode: CommitmentFormMode,
) {
    val strings: StringsRepository = LocalStrings.current

    val commitmentFormViewModel: CommitmentFormViewModel =
        koinViewModel(
            parameters = { parametersOf(commitmentFormMode) },
        )
    val commitmentUiState by commitmentFormViewModel.commitmentUiState.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val localDate: LocalDate = commitmentUiState.startInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date

    HandleEvents(commitmentFormViewModel.events) { event ->
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
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (!commitmentUiState.isLoading) {
                        Text(
                            text =
                                strings.dateFormat.format(
                                    localDate.year,
                                    localDate.monthNumber,
                                    localDate.dayOfMonth,
                                ),
                            style =
                                TextStyle(
                                    fontSize = PageDesignSettings.mediumTitle,
                                    color = MaterialTheme.colorScheme.primary,
                                ),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onBackPressed,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.backMenuButton,
                            modifier = Modifier.size(PageDesignSettings.mediumIconSize),
                        )
                    }
                },
            )
        },
        content = { paddingValues ->

            when {
                commitmentUiState.isLoading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
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
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }

                else -> {
                    CommitmentForm(
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
                        commitmentUiState = commitmentUiState,
                        commitmentFormViewModel = commitmentFormViewModel,
                    )
                }
            }
        },
    )
}
