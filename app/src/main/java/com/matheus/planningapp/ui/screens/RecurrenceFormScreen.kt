package com.matheus.planningapp.ui.screens

import android.view.MotionEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.FrequencyEnum
import com.matheus.planningapp.util.enums.ViewEnum
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceFormScren (
    onBackPressed: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(
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
            RecurrenceForm(
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
                    )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceForm (
    modifier: Modifier
) {
    var isExpandedFrequencyDropdown: Boolean by remember { mutableStateOf(false) }
    var selectedFrequencyEnum: FrequencyEnum by rememberSaveable { mutableStateOf(FrequencyEnum.DAILY) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Frequency",
            style = TextStyle(
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.padding(8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = isExpandedFrequencyDropdown,
            onExpandedChange = { isExpandedFrequencyDropdown = !isExpandedFrequencyDropdown }
        ) {
            TextField(
                value = selectedFrequencyEnum.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(isExpandedFrequencyDropdown)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 16.sp
                ),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                    disabledTextColor = MaterialTheme.colorScheme.secondary
                )
            )

            ExposedDropdownMenu(
                expanded = isExpandedFrequencyDropdown,
                onDismissRequest = { isExpandedFrequencyDropdown = false },
                containerColor = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                FrequencyEnum.entries.forEach { frequencyEnum ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = frequencyEnum.label,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        onClick = {
                            selectedFrequencyEnum = frequencyEnum
                            isExpandedFrequencyDropdown = false
                        }
                    )
                }
            }
        }

        if (selectedFrequencyEnum == FrequencyEnum.WEEKLY) {
            Text(
                text = "Days of Week",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(8.dp)
            )

            DaysOfWeek(
                onSelection = {}
            )
        }

        if (selectedFrequencyEnum == FrequencyEnum.MONTHLY) {
            Text(
                text = "Days of Month",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(8.dp)
            )

            IntegerField(
                onIntegerValueChange = {},
                minValue = 1,
                maxValue = 28
            )
        }

        if (selectedFrequencyEnum == FrequencyEnum.CUSTOMIZED) {
            Text(
                text = "Interval",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(8.dp)
            )

            IntegerField(
                onIntegerValueChange = {},
                minValue = 1,
                maxValue = 7
            )
        }

        Spacer(modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {}
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

@Composable
fun DaysOfWeek(
    onSelection: (List<DayOfWeekEnum>) -> Unit
) {
    val selectedDaysOfWeek = remember { mutableStateListOf<DayOfWeekEnum>() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        DayOfWeekEnum.entries.forEach { dayOfWeekEnum ->
            val isSelected = selectedDaysOfWeek.contains(dayOfWeekEnum)

            Button(
                onClick = {
                    if (isSelected) selectedDaysOfWeek.remove(dayOfWeekEnum)
                    else selectedDaysOfWeek.add(dayOfWeekEnum)

                },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer,
                    contentColor =
                        if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = dayOfWeekEnum.name.take(1),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun IntegerField(
    onIntegerValueChange: (Int) -> Unit,
    minValue: Int,
    maxValue: Int
) {
    var selectedValue by remember { mutableIntStateOf(minValue) }
    var increaseButtonPressed by remember { mutableStateOf(false) }
    var decreaseButtonPressed by remember { mutableStateOf(false) }

    LaunchedEffect(increaseButtonPressed, selectedValue) {
        while (increaseButtonPressed) {
            if (selectedValue < maxValue) selectedValue += 1
            delay(150)
        }
    }

    LaunchedEffect(decreaseButtonPressed, selectedValue) {
        while (decreaseButtonPressed) {
            if (selectedValue > minValue) selectedValue -= 1
            delay(150)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = selectedValue.toString(),
            onValueChange = { newValue ->
                onIntegerValueChange(newValue.toInt())
            },
            readOnly = true,
            label = {
                Text(
                    text = "Value",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            },
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier
                .weight(1f)
                .height(64.dp),
            singleLine = true
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
