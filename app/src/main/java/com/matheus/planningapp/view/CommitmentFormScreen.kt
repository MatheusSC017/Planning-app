package com.matheus.planningapp.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matheus.planningapp.data.CommitmentEntity
import com.matheus.planningapp.data.Priority
import com.matheus.planningapp.helper.parseTime
import com.matheus.planningapp.helper.snapToHalfHour
import com.matheus.planningapp.helper.step30
import com.matheus.planningapp.helper.sumInstantWithLocalTime
import com.matheus.planningapp.viewmodel.CalendarViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentScreen(
    onBackPressed: () -> Unit,
    selectedCalendar: Int,
    instant: Instant
) {
    val localDate: LocalDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "%02d/%02d/%04d".format(localDate.dayOfMonth, localDate.monthNumber, localDate.year),
                        style = TextStyle(
                            fontSize = 36.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                actions = {
                    IconButton (
                        onClick = onBackPressed
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(36.dp)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            CommitmentForm(
                modifier = Modifier
                    .padding(paddingValues),
                onBackPressed = onBackPressed,
                selectedCalendar = selectedCalendar,
                instant = instant
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentForm(
    modifier: Modifier,
    onBackPressed: () -> Unit,
    selectedCalendar: Int,
    instant: Instant,
    calendarViewModel: CalendarViewModel = koinViewModel()
) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStartTime by remember { mutableStateOf(LocalTime(hour = 0, minute = 0)) }
    var selectedEndTime by remember { mutableStateOf(LocalTime(hour = 0, minute = 30)) }
    var selectedPriority by remember { mutableStateOf(Priority.LOW) }

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
                value = title,
                onValueChange = { title = it },
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
                modifier = Modifier.fillMaxWidth().height(64.dp),
                singleLine = true
            )
        }

        item {
            TextField(
                value = description,
                onValueChange = { description = it},
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
                modifier = Modifier.fillMaxWidth().height(128.dp),
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
                time = selectedStartTime,
                onTimeChange = { selectedStartTime = it }
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
                time = selectedEndTime,
                onTimeChange = { selectedEndTime = it }
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
                    value = selectedPriority.name,
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
                    Priority.entries.forEach { priority ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = priority.name,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            onClick = {
                                selectedPriority = priority
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
                        /* Validate start time must be lesser than end time*/

                        val commitmentEntity = CommitmentEntity(
                            calendar = selectedCalendar,
                            title = title,
                            description = description,
                            startDateTime = sumInstantWithLocalTime(instant, selectedStartTime),
                            endDateTime =  sumInstantWithLocalTime(instant, selectedEndTime),
                            allDay = false,
                            priority = selectedPriority,
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )

                        calendarViewModel.insertCommitment(commitmentEntity)
                        onBackPressed()
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
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = "%02d:%02d".format(time.hour, time.minute),
            onValueChange = { input ->
                parseTime(input)?.let {
                    onTimeChange(snapToHalfHour(it))
                }
            },
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
                onClick = {
                    onTimeChange(time.step30(+1))
                }
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
            }

            IconButton(
                onClick = {
                    onTimeChange(time.step30(-1))
                }
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
            }
        }
    }
}

