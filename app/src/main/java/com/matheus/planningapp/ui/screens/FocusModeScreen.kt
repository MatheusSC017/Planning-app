package com.matheus.planningapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matheus.planningapp.ui.screens.components.stardardBackground
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.viewmodel.focus.FocusModeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusModeScreen(
    onExitFocus: () -> Unit,
    viewModel: FocusModeViewModel = koinViewModel()
) {
    val strings: StringsRepository = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()

    BackHandler(enabled = true) {
        onExitFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.focusModeMenuButton,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                actions = {}
            )
        },
        content = { paddingValues ->
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .stardardBackground(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { uiState.progress },
                            modifier = Modifier.size(250.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 12.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round,
                        )
                        Text(
                            text = uiState.formattedTime,
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (!uiState.isRunning) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${uiState.hoursInput} H",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Column {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowUp,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { viewModel.onHoursChange(uiState.hoursInput + 1) },
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { viewModel.onHoursChange(uiState.hoursInput - 1) },
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Text(
                                text =  "${uiState.minutesInput} m",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Column {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowUp,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { viewModel.onMinutesChange(uiState.minutesInput + 1) },
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { viewModel.onMinutesChange(uiState.minutesInput - 1) },
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Text(
                                text =  "${uiState.secondsInput} s",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Column {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowUp,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { viewModel.onSecondsChange(uiState.secondsInput + 1) },
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { viewModel.onSecondsChange(uiState.secondsInput - 1) },
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    Button(
                        onClick = {
                            if (uiState.isRunning) {
                                viewModel.stopTimer()
                            } else {
                                viewModel.startTimer()
                            }
                        },
                    ) {
                        Text(
                            text = if (uiState.isRunning) strings.stopFocusModeButton else strings.startFocusModeButton,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    if (uiState.isRunning) {
                        Button(onClick = viewModel::pauseTimer) {
                            Text(
                                text = strings.pauseFocusModeButton,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onExitFocus()
                            },
                        ) {
                            Text(
                                text = strings.backMenuButton,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }
        },
    )
}
