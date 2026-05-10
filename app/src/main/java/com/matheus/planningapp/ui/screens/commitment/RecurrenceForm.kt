package com.matheus.planningapp.ui.screens.commitment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.matheus.planningapp.ui.screens.components.IntegerField
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.enums.DayOfWeekEnum
import com.matheus.planningapp.util.enums.FrequencyEnum
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormViewModel
import com.matheus.planningapp.viewmodel.commitment.RecurrenceFormUiState

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
