package com.matheus.planningapp.ui.screens.focus

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matheus.planningapp.R
import com.matheus.planningapp.ui.screens.components.stardardBackground
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.viewmodel.focus.FocusModeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusModeScreen(
    onExitFocus: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: FocusModeViewModel = koinViewModel()
) {
    val strings: StringsRepository = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()

    BackHandler(enabled = true) {
        viewModel.onExit(onExitFocus)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.focusModeMenuButton,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onExit(onExitFocus) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.backMenuButton
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_history),
                            contentDescription = strings.focusHistoryTitle
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
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
                            progress = { if (uiState.isRunning || uiState.isPaused) uiState.progress else 1f },
                            modifier = Modifier.size(280.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 8.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round,
                        )
                        Text(
                            text = uiState.formattedTime,
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    if (!uiState.isRunning && !uiState.isPaused) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TimeAdjustmentColumn(
                                value = uiState.hoursInput,
                                label = "H",
                                onIncrease = { viewModel.onHoursChange(uiState.hoursInput + 1) },
                                onDecrease = { viewModel.onHoursChange(uiState.hoursInput - 1) }
                            )
                            TimeAdjustmentColumn(
                                value = uiState.minutesInput,
                                label = "M",
                                onIncrease = { viewModel.onMinutesChange(uiState.minutesInput + 1) },
                                onDecrease = { viewModel.onMinutesChange(uiState.minutesInput - 1) }
                            )
                            TimeAdjustmentColumn(
                                value = uiState.secondsInput,
                                label = "S",
                                onIncrease = { viewModel.onSecondsChange(uiState.secondsInput + 1) },
                                onDecrease = { viewModel.onSecondsChange(uiState.secondsInput - 1) }
                            )
                        }
                        Spacer(modifier = Modifier.height(48.dp))
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isRunning || uiState.isPaused) {
                            if (uiState.isRunning) {
                                FilledTonalIconButton(
                                    onClick = viewModel::pauseTimer,
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_pause),
                                        contentDescription = strings.pauseFocusModeButton,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            } else {
                                FilledTonalIconButton(
                                    onClick = viewModel::startTimer,
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = strings.startFocusModeButton,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            FilledTonalIconButton(
                                onClick = viewModel::stopTimer,
                                modifier = Modifier.size(72.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_stop),
                                    contentDescription = strings.stopFocusModeButton,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        } else {
                            FilledTonalIconButton(
                                onClick = viewModel::startTimer,
                                modifier = Modifier.size(72.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = strings.startFocusModeButton,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun TimeAdjustmentColumn(
    value: Int,
    label: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(onClick = onIncrease) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%02d".format(value),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        IconButton(onClick = onDecrease) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
