package com.matheus.planningapp.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.DatabaseUiEvent
import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.FrequencyEnum
import com.matheus.planningapp.util.enums.PriorityEnum
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormMode
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormUiState
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormViewModel
import com.matheus.planningapp.viewmodel.commitment.RecurrenceFormUiState
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
    commitmentFormMode: CommitmentFormMode,
) {
    val strings: StringsRepository = LocalStrings.current

    val commitmentFormViewModel: CommitmentFormViewModel =
        koinViewModel(
            parameters = { parametersOf(commitmentFormMode) },
        )
    val commitmentUiState by commitmentFormViewModel.commitmentUiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val localDate: LocalDate = commitmentUiState.startInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date

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
                            snackBarHostState.showSnackbar(strings.savedMessage)
                        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentForm(
    modifier: Modifier,
    commitmentUiState: CommitmentFormUiState,
    commitmentFormViewModel: CommitmentFormViewModel,
) {
    val strings: StringsRepository = LocalStrings.current
    val recurrenceUiState by commitmentFormViewModel.recurrenceUiState.collectAsState()
    var expandedPriorityDropDown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(PageDesignSettings.extraLargePaddingValue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PageDesignSettings.extraLargePaddingValue),
    ) {
        item {
            TextField(
                value = commitmentUiState.title,
                onValueChange = { commitmentFormViewModel.onTitleChange(it) },
                label = {
                    Text(
                        text = strings.commitmentTitleField,
                        style =
                            TextStyle(
                                fontSize = PageDesignSettings.smallTitle,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                    )
                },
                textStyle =
                    TextStyle(
                        fontSize = PageDesignSettings.mediumText,
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(PageDesignSettings.smallComponentSize),
                singleLine = true,
            )
        }

        item {
            TextField(
                value = commitmentUiState.description,
                onValueChange = { commitmentFormViewModel.onDescriptionChange(it) },
                label = {
                    Text(
                        text = strings.commitmentDescriptionField,
                        style =
                            TextStyle(
                                fontSize = PageDesignSettings.smallTitle,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                    )
                },
                textStyle =
                    TextStyle(
                        fontSize = PageDesignSettings.mediumText,
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(PageDesignSettings.mediumComponentSize),
                singleLine = false,
            )
        }

        item {
            Text(
                text = strings.commitmentStartField,
                style =
                    TextStyle(
                        fontSize = PageDesignSettings.smallTitle,
                        color = MaterialTheme.colorScheme.primary,
                    ),
            )

            TimeStepperField(
                time = commitmentUiState.startInstant,
                onTimeChange = { commitmentFormViewModel.onStartInstantChange(it) },
            )
        }

        item {
            Text(
                text = strings.commitmentEndField,
                style =
                    TextStyle(
                        fontSize = PageDesignSettings.smallTitle,
                        color = MaterialTheme.colorScheme.primary,
                    ),
            )

            TimeStepperField(
                time = commitmentUiState.endInstant,
                isEndTime = true,
                onTimeChange = { commitmentFormViewModel.onEndInstantChange(it) },
            )
        }

        item {
            Text(
                text = strings.commitmentPriorityField,
                style =
                    TextStyle(
                        fontSize = PageDesignSettings.smallTitle,
                        color = MaterialTheme.colorScheme.primary,
                    ),
            )

            Spacer(modifier = Modifier.height(PageDesignSettings.extraLargePaddingValue))

            ExposedDropdownMenuBox(
                expanded = expandedPriorityDropDown,
                onExpandedChange = { expandedPriorityDropDown = !expandedPriorityDropDown },
            ) {
                TextField(
                    value = commitmentUiState.priorityEnum.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expandedPriorityDropDown)
                    },
                    modifier =
                        Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                    textStyle =
                        TextStyle(
                            fontSize = PageDesignSettings.mediumText,
                        ),
                    colors =
                        ExposedDropdownMenuDefaults.textFieldColors(
                            focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                            disabledIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                            focusedTextColor = MaterialTheme.colorScheme.secondary,
                            unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                            disabledTextColor = MaterialTheme.colorScheme.secondary,
                        ),
                )

                ExposedDropdownMenu(
                    expanded = expandedPriorityDropDown,
                    onDismissRequest = { expandedPriorityDropDown = false },
                    containerColor = MaterialTheme.colorScheme.background,
                    border = BorderStroke(PageDesignSettings.borderWidth, MaterialTheme.colorScheme.primary),
                ) {
                    PriorityEnum.entries.forEach { priority ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = priority.name,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            onClick = {
                                commitmentFormViewModel.onPriorityChange(priority)
                                expandedPriorityDropDown = false
                            },
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PageDesignSettings.mediumPaddingValue),
            ) {
                Switch(
                    checked = recurrenceUiState.isRecurrenceActive,
                    onCheckedChange = {
                        commitmentFormViewModel.onRecurrenceFormActiveChange(!recurrenceUiState.isRecurrenceActive)
                    },
                )

                Text(
                    text = strings.recurrenceIsRecurrenceActiveField,
                    style =
                        TextStyle(
                            fontSize = PageDesignSettings.smallTitle,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                )
            }
        }

        item {
            if (recurrenceUiState.isRecurrenceActive) {
                RecurrenceForm(
                    recurrenceUiState = recurrenceUiState,
                    commitmentFormViewModel = commitmentFormViewModel,
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    onClick = {
                        if (commitmentUiState.id == null) {
                            commitmentFormViewModel.insertCommitment()
                        } else {
                            commitmentFormViewModel.updateCommitment()
                        }
                    },
                ) {
                    Text(
                        text = strings.confirmButton,
                        style =
                            TextStyle(
                                fontSize = PageDesignSettings.smallTitle,
                            ),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceForm(
    recurrenceUiState: RecurrenceFormUiState,
    commitmentFormViewModel: CommitmentFormViewModel,
) {
    val strings: StringsRepository = LocalStrings.current

    val firstDayOfMonth = 1
    val lastDayOfMonth = 28
    val firstValueInterval = 1
    val lastValueInterval = 7
    var isExpandedFrequencyDropdown: Boolean by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PageDesignSettings.extraLargePaddingValue),
    ) {
        Text(
            text = strings.recurrenceFrequencyField,
            style =
                TextStyle(
                    fontSize = PageDesignSettings.smallTitle,
                    color = MaterialTheme.colorScheme.primary,
                ),
        )

        ExposedDropdownMenuBox(
            expanded = isExpandedFrequencyDropdown,
            onExpandedChange = { isExpandedFrequencyDropdown = !isExpandedFrequencyDropdown },
        ) {
            TextField(
                value = recurrenceUiState.frequencyEnum.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(isExpandedFrequencyDropdown)
                },
                modifier =
                    Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                textStyle =
                    TextStyle(
                        fontSize = PageDesignSettings.largeText,
                    ),
                colors =
                    ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                        disabledIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                        focusedTextColor = MaterialTheme.colorScheme.secondary,
                        unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                        disabledTextColor = MaterialTheme.colorScheme.secondary,
                    ),
            )

            ExposedDropdownMenu(
                expanded = isExpandedFrequencyDropdown,
                onDismissRequest = { isExpandedFrequencyDropdown = false },
                containerColor = MaterialTheme.colorScheme.background,
                border = BorderStroke(PageDesignSettings.borderWidth, MaterialTheme.colorScheme.primary),
            ) {
                FrequencyEnum.entries.forEach { frequencyEnum ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = frequencyEnum.label,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        onClick = {
                            commitmentFormViewModel.onFrequencyChange(frequencyEnum)
                            isExpandedFrequencyDropdown = false
                        },
                    )
                }
            }
        }

        if (recurrenceUiState.frequencyEnum == FrequencyEnum.WEEKLY) {
            Text(
                text = strings.recurrenceWeekDaysField,
                style =
                    TextStyle(
                        fontSize = PageDesignSettings.smallTitle,
                        color = MaterialTheme.colorScheme.primary,
                    ),
            )

            DaysOfWeek(
                daysOfWeekList = recurrenceUiState.daysOfWeekList,
                onSelection = { dayOfWeekList ->
                    commitmentFormViewModel.onDaysOfWeekChange(dayOfWeekList)
                },
            )
        }

        if (recurrenceUiState.frequencyEnum == FrequencyEnum.MONTHLY) {
            Text(
                text = strings.recurrenceDayOfMonthField,
                style =
                    TextStyle(
                        fontSize = PageDesignSettings.smallTitle,
                        color = MaterialTheme.colorScheme.primary,
                    ),
            )

            IntegerField(
                selectedValue = recurrenceUiState.dayOfMonth,
                onIntegerValueChange = { newDayOfMonth ->
                    commitmentFormViewModel.onDayOfMonthChange(newDayOfMonth)
                },
                minValue = firstDayOfMonth,
                maxValue = lastDayOfMonth,
            )
        }

        if (recurrenceUiState.frequencyEnum == FrequencyEnum.CUSTOMIZED) {
            Text(
                text = strings.recurrenceIntervalField,
                style =
                    TextStyle(
                        fontSize = PageDesignSettings.smallTitle,
                        color = MaterialTheme.colorScheme.primary,
                    ),
            )

            IntegerField(
                selectedValue = recurrenceUiState.interval,
                onIntegerValueChange = { newInterval ->
                    commitmentFormViewModel.onIntervalChange(newInterval)
                },
                minValue = firstValueInterval,
                maxValue = lastValueInterval,
            )
        }
    }
}

@Composable
fun DaysOfWeek(
    daysOfWeekList: List<DayOfWeekEnum>,
    onSelection: (List<DayOfWeekEnum>) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PageDesignSettings.smallPaddingValue),
    ) {
        DayOfWeekEnum.entries.forEach { dayOfWeekEnum ->
            val isSelected = daysOfWeekList.contains(dayOfWeekEnum)

            Button(
                onClick = {
                    val newSelectedDaysOfWeek = daysOfWeekList.toMutableList()
                    if (isSelected) {
                        newSelectedDaysOfWeek.remove(dayOfWeekEnum)
                    } else {
                        newSelectedDaysOfWeek.add(dayOfWeekEnum)
                    }
                    onSelection(newSelectedDaysOfWeek)
                },
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            },
                        contentColor =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            },
                    ),
            ) {
                Text(
                    text = dayOfWeekEnum.name.take(1),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun IntegerField(
    selectedValue: Int,
    onIntegerValueChange: (Int) -> Unit,
    minValue: Int,
    maxValue: Int,
) {
    val strings: StringsRepository = LocalStrings.current
    val delayInMilliseconds = 100L

    var increaseButtonPressed by remember { mutableStateOf(false) }
    var decreaseButtonPressed by remember { mutableStateOf(false) }

    LaunchedEffect(increaseButtonPressed, selectedValue) {
        while (increaseButtonPressed) {
            delay(delayInMilliseconds)
            if (selectedValue < maxValue) onIntegerValueChange(selectedValue + 1)
        }
    }

    LaunchedEffect(decreaseButtonPressed, selectedValue) {
        while (decreaseButtonPressed) {
            delay(delayInMilliseconds)
            if (selectedValue > minValue) onIntegerValueChange(selectedValue - 1)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = selectedValue.toString(),
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = strings.recurrenceValueField,
                    style =
                        TextStyle(
                            fontSize = PageDesignSettings.smallText,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                )
            },
            textStyle =
                TextStyle(
                    fontSize = PageDesignSettings.mediumText,
                    color = MaterialTheme.colorScheme.secondary,
                ),
            modifier =
                Modifier
                    .weight(1f)
                    .height(PageDesignSettings.smallComponentSize),
            singleLine = true,
        )

        Column {
            IconButton(
                onClick = {},
                modifier =
                    Modifier.pointerInteropFilter {
                        increaseButtonPressed =
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> true
                                else -> false
                            }
                        true
                    },
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = strings.increaseButton)
            }

            IconButton(
                onClick = {},
                modifier =
                    Modifier.pointerInteropFilter {
                        decreaseButtonPressed =
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> true
                                else -> false
                            }
                        true
                    },
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = strings.decreaseButton)
            }
        }
    }
}

@Composable
fun TimeStepperField(
    time: Instant,
    isEndTime: Boolean = false,
    onTimeChange: (Instant) -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current
    val delayInMilliseconds = 50L
    val timeStepMinutes = 30.minutes
    val minutesPerHour = 60
    val hoursPerDay = 24

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
            delay(delayInMilliseconds)
            if ((!isEndTime && (localDateTime.hour < hourLimit || localDateTime.minute == 0)) ||
                (isEndTime && localDateTime.dayOfMonth == currentDayOfMonth)
            ) {
                onTimeChange(selectedTime + timeStepMinutes)
                selectedTime += timeStepMinutes
            }
        }
    }

    LaunchedEffect(decreaseButtonPressed, selectedTime) {
        val localDateTime: LocalDateTime = selectedTime.toLocalDateTime(TimeZone.currentSystemDefault())

        while (decreaseButtonPressed) {
            delay(delayInMilliseconds)
            if (((localDateTime.hour * minutesPerHour) + localDateTime.minute > minuteLimit) ||
                (localDateTime.dayOfMonth > currentDayOfMonth)
            ) {
                onTimeChange(selectedTime - timeStepMinutes)
                selectedTime -= timeStepMinutes
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value =
                strings.hourFormat.format(
                    if (localDateTime.dayOfMonth == currentDayOfMonth) localDateTime.hour else hoursPerDay,
                    localDateTime.minute,
                ),
            onValueChange = {},
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
            singleLine = true,
            textStyle =
                TextStyle(
                    fontSize = PageDesignSettings.largeText,
                    color = MaterialTheme.colorScheme.secondary,
                ),
            modifier = Modifier.weight(1f),
        )

        Column {
            IconButton(
                onClick = {},
                modifier =
                    Modifier.pointerInteropFilter {
                        increaseButtonPressed =
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> true
                                else -> false
                            }
                        true
                    },
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = strings.increaseButton)
            }

            IconButton(
                onClick = {},
                modifier =
                    Modifier.pointerInteropFilter {
                        decreaseButtonPressed =
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> true
                                else -> false
                            }
                        true
                    },
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = strings.decreaseButton)
            }
        }
    }
}
