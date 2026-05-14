package com.matheus.planningapp.ui.screens.commitment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.matheus.planningapp.ui.screens.components.TimeStepperField
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.enums.PriorityEnum
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormUiState
import com.matheus.planningapp.viewmodel.commitment.CommitmentFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentForm(
    modifier: Modifier,
    onBackPressed: () -> Unit,
    commitmentUiState: CommitmentFormUiState,
    commitmentFormViewModel: CommitmentFormViewModel,
) {
    val strings: StringsRepository = LocalStrings.current
    val categories by commitmentFormViewModel.categories.collectAsState()
    val recurrenceUiState by commitmentFormViewModel.recurrenceUiState.collectAsState()
    var expandedPriorityDropDown by remember { mutableStateOf(false) }
    var expandedCategoryDropDown by remember { mutableStateOf(false) }

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
            Text(
                text = strings.categoryManagementTitle,
                style =
                    TextStyle(
                        fontSize = PageDesignSettings.smallTitle,
                        color = MaterialTheme.colorScheme.primary,
                    ),
            )

            Spacer(modifier = Modifier.height(PageDesignSettings.extraLargePaddingValue))

            ExposedDropdownMenuBox(
                expanded = expandedCategoryDropDown,
                onExpandedChange = { expandedCategoryDropDown = !expandedCategoryDropDown },
            ) {
                TextField(
                    value = commitmentUiState.category?.name ?: strings.noCategorySelected,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expandedCategoryDropDown)
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
                    expanded = expandedCategoryDropDown,
                    onDismissRequest = { expandedCategoryDropDown = false },
                    containerColor = MaterialTheme.colorScheme.background,
                    border = BorderStroke(PageDesignSettings.borderWidth, MaterialTheme.colorScheme.primary),
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = strings.noCategorySelected,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        },
                        onClick = {
                            commitmentFormViewModel.onCategoryChange(null)
                            expandedCategoryDropDown = false
                        },
                    )

                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category.name,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            onClick = {
                                commitmentFormViewModel.onCategoryChange(category)
                                expandedCategoryDropDown = false
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
                        onBackPressed()
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
