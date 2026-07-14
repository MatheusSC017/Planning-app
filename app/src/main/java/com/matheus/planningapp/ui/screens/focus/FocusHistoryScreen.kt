package com.matheus.planningapp.ui.screens.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.matheus.planningapp.R
import com.matheus.planningapp.data.focus.FocusSessionEntity
import com.matheus.planningapp.ui.screens.components.stardardBackground
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.viewmodel.focus.FocusHistoryViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel

object FocusHistoryScreen {
    const val route = "focusHistory"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusHistoryScreen(
    onBackPressed: () -> Unit,
    viewModel: FocusHistoryViewModel = koinViewModel()
) {
    val strings = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.focusHistoryTitle) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.backMenuButton
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        bottomBar = {
            PaginationControls(
                currentPage = uiState.currentPage,
                totalPages = uiState.totalPages,
                onPrevious = viewModel::onPreviousPage,
                onNext = viewModel::onNextPage,
                isNextEnabled = !uiState.isLastPage,
                isPreviousEnabled = uiState.currentPage > 0,
                strings = strings
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .stardardBackground()
        ) {
            if (uiState.sessions.isEmpty() && !uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = strings.noValuesFound, style = MaterialTheme.typography.bodyLarge)
                }
            } else if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.sessions) { session ->
                        FocusSessionItem(session = session, strings = strings)
                    }
                }
            }
        }
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isNextEnabled: Boolean,
    isPreviousEnabled: Boolean,
    strings: StringsRepository
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPrevious,
            enabled = isPreviousEnabled
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
        }

        Text(
            text = "${currentPage + 1} / $totalPages",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onNext,
            enabled = isNextEnabled
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
fun FocusSessionItem(session: FocusSessionEntity, strings: StringsRepository) {
    val dateTime = Instant.fromEpochMilliseconds(session.startTime)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    
    val formattedDate = "%02d/%02d/%04d %02d:%02d".format(
        dateTime.dayOfMonth,
        dateTime.monthNumber,
        dateTime.year,
        dateTime.hour,
        dateTime.minute
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_history),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = if (session.completed) strings.focusHistoryCompleted else strings.focusHistoryIncomplete,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (session.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            Text(
                text = strings.focusHistoryDuration.format(session.durationSeconds / 60),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
