package com.matheus.planningapp.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.matheus.planningapp.util.enums.PriorityEnum
import com.matheus.planningapp.viewmodel.commitment.DatabaseUiEvent
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormMode
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormUiState
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentScreen(
    onBackPressed: () -> Unit,
    commitmentFormMode: CommitmentFormMode
) {
    val commitmentFormViewModel: CommitmentFormViewModel = koinViewModel(
        parameters = { parametersOf(commitmentFormMode) }
    )
    val uiState by commitmentFormViewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val localDate: LocalDate = uiState.startInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            commitmentFormViewModel.events.collect { event ->
                when (event) {
                    is DatabaseUiEvent.ShowError -> {
                        scope.launch {
                            snackBarHostState.showSnackbar(event.message)
                        }
                    }

                    DatabaseUiEvent.Saved -> {
                        onBackPressed()
                        scope.launch {
                            snackBarHostState.showSnackbar("Saved")
                        }
                    }
                }
            }
        }
    }

    Scaffold (
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (!uiState.isLoading) {
                        Text(
                            text = "%04d-%02d-%02d".format(
                                localDate.year,
                                localDate.monthNumber,
                                localDate.dayOfMonth
                            ),
                            style = TextStyle(
                                fontSize = 36.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                },
                actions = {
                    IconButton (
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

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
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
                                    end = Offset.Infinite
                                )
                            )
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                else -> {
                    CommitmentForm(
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
                        uiState = uiState,
                        commitmentFormViewModel = commitmentFormViewModel
                    )
                }
            }

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentForm(
    modifier: Modifier,
    uiState: CommitmentFormUiState,
    commitmentFormViewModel: CommitmentFormViewModel
) {
    var expandedPriorityDropDown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        item {
            TextField(
                value = uiState.title,
                onValueChange = { commitmentFormViewModel.onTitleChange(it) },
                label = {
                    Text(
                        text = "Title",
                        style = TextStyle(
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                singleLine = true
            )

        }

        item {
            TextField(
                value = uiState.description,
                onValueChange = { commitmentFormViewModel.onDescriptionChange(it) },
                label = {
                    Text(
                        text = "Description",
                        style = TextStyle(
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp),
                singleLine = false
            )
        }

        item {
            Text(
                text = "Start time",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            TimeStepperField(
                time = uiState.startInstant,
                onTimeChange = { commitmentFormViewModel.onStartInstantChange(it) }
            )
        }

        item {
            Text(
                text = "End time",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            TimeStepperField(
                time = uiState.endInstant,
                isEndTime = true,
                onTimeChange = { commitmentFormViewModel.onEndInstantChange(it) }
            )
        }

        item {
            Text(
                text = "Priority",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            ExposedDropdownMenuBox(
                expanded = expandedPriorityDropDown,
                onExpandedChange = { expandedPriorityDropDown = !expandedPriorityDropDown }
            ) {
                TextField(
                    value = uiState.priorityEnum.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expandedPriorityDropDown)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    ),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.secondary,
                        unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                        disabledTextColor = MaterialTheme.colorScheme.secondary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expandedPriorityDropDown,
                    onDismissRequest = { expandedPriorityDropDown = false },
                    containerColor = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                ) {
                    PriorityEnum.entries.forEach { priority ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = priority.name,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            onClick = {
                                commitmentFormViewModel.onPriorityChange(priority)
                                expandedPriorityDropDown = false
                            }
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        if (uiState.id == null) {
                            commitmentFormViewModel.insertCommitment()

                        } else {
                            commitmentFormViewModel.updateCommitment()
                        }

                    }
                ) {
                    Text(
                        text = "Save",
                        style = TextStyle(
                            fontSize = 24.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TimeStepperField(
    time: Instant,
    isEndTime: Boolean = false,
    onTimeChange: (Instant) -> Unit
) {
    var selectedTime by remember { mutableStateOf(time) }
    val localDateTime: LocalDateTime = selectedTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val currentDayOfMonth: Int = remember { localDateTime.dayOfMonth }
    val hourLimit: Int = if (isEndTime) 24 else 23
    val minuteLimit: Int = if (isEndTime) 30 else 0

    var increaseButtonPressed by remember { mutableStateOf(false) }
    var decreaseButtonPressed by remember { mutableStateOf(false) }

    LaunchedEffect(increaseButtonPressed, selectedTime) {
        val localDateTime: LocalDateTime = selectedTime.toLocalDateTime(TimeZone.currentSystemDefault())

        while (increaseButtonPressed) {
            if ((!isEndTime && (localDateTime.hour < hourLimit || localDateTime.minute == 0))
                || (isEndTime && localDateTime.dayOfMonth == currentDayOfMonth)) {
                onTimeChange(selectedTime + 30.minutes)
                selectedTime += 30.minutes
            }
            delay(150)
        }
    }

    LaunchedEffect(decreaseButtonPressed, selectedTime) {
        val localDateTime: LocalDateTime = selectedTime.toLocalDateTime(TimeZone.currentSystemDefault())

        while (decreaseButtonPressed) {
            if (((localDateTime.hour * 60) + localDateTime.minute > minuteLimit) || (localDateTime.dayOfMonth > currentDayOfMonth)) {
                onTimeChange(selectedTime - 30.minutes)
                selectedTime -= 30.minutes
            }
            delay(150)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = "%02d:%02d".format(
                if (localDateTime.dayOfMonth == currentDayOfMonth) localDateTime.hour else 24,
                localDateTime.minute),
            onValueChange = {},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.weight(1f)
        )

        Column {
            IconButton(
                onClick = {},
                modifier = Modifier.pointerInteropFilter {
                    increaseButtonPressed = when (it.action) {
                        MotionEvent.ACTION_DOWN -> true
                        else -> false
                    }
                    true
                }
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
            }

            IconButton(
                onClick = {},
                modifier = Modifier.pointerInteropFilter {
                    decreaseButtonPressed = when (it.action) {
                        MotionEvent.ACTION_DOWN -> true
                        else -> false
                    }
                    true
                }
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
            }
        }
    }
}

