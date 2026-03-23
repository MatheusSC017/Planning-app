package com.matheus.planningapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matheus.planningapp.R
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.data.recurrence.CommitmentRecurrenceDataClass
import com.matheus.planningapp.data.recurrence.RecurrenceEntity
import com.matheus.planningapp.viewmodel.recurrence.RecurrenceViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceScreen(
    recurrenceViewModel: RecurrenceViewModel = koinViewModel(),
    onMenuClick: () -> Unit
) {
    val uiState by recurrenceViewModel.uiState.collectAsStateWithLifecycle()

    var selectedCalendar by remember { mutableStateOf<CalendarEntity?>(null) }

    LaunchedEffect(uiState.calendars) {
        if (selectedCalendar == null && uiState.calendars.isNotEmpty()) {
            selectedCalendar = uiState.calendars.first()
        }
    }

    val recurrences: List<CommitmentRecurrenceDataClass> by recurrenceViewModel
        .getRecurrencesByCalendar(calendarId = selectedCalendar?.id ?: 0)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recurrences",
                        style = TextStyle(
                            fontSize = 36.sp,
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
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            RecurrenceList(
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
                recurrences = recurrences
            )
        }
    )
}

@Composable
fun RecurrenceList(
    modifier: Modifier,
    recurrences: List<CommitmentRecurrenceDataClass>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            recurrences.forEach { recurrence ->
                item {
                    RecurrenceCard(recurrence)
                }
            }
        }
    }
}

@Composable
fun RecurrenceCard(
    recurrence: CommitmentRecurrenceDataClass
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val commitmentStartDateTime: LocalDateTime = recurrence.startDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val startTimeString = String.format(Locale.US, "%02d:%02d", commitmentStartDateTime.hour, commitmentStartDateTime.minute)
    val commitmentEndDateTime: LocalDateTime = recurrence.endDateTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTimeString = String.format(Locale.US, "%02d:%02d", commitmentEndDateTime.hour, commitmentEndDateTime.minute)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {

                Text(
                    text = recurrence.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$startTimeString — $endTimeString",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = .6f)
                )

                Text(
                    text = recurrence.frequency.label,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = .6f)
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
                            /* TODO: Create View Commitment/Recurrence */
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
                            /* TODO: Redirect to Edit commitment Screen */
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
                            /* TODO: Delete this commitment and/or Recurrence */
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