package com.matheus.planningapp.ui.screens.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.viewmodel.category.CategoryFormUiState
import com.matheus.planningapp.viewmodel.category.CategoryViewModel

@Composable
fun CategoryForm(
    categoryViewModel: CategoryViewModel,
    categoryFormUiState: CategoryFormUiState,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onShowSnackbar: (String) -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current
    var nameError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PageDesignSettings.largePaddingValue),
    ) {
        item {
            Spacer(modifier = Modifier.height(PageDesignSettings.largePaddingValue))
        }

        item {
            Text(
                text = if (categoryFormUiState.id != null) strings.updateCategoryTitle else strings.createCategoryTitle,
                style =
                    TextStyle(
                        fontSize = PageDesignSettings.mediumTitle,
                        color = MaterialTheme.colorScheme.primary,
                    ),
            )
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(PageDesignSettings.mediumPaddingValue),
            ) {
                TextField(
                    value = categoryFormUiState.name,
                    onValueChange = {
                        categoryViewModel.onNameChange(it)
                        if (it.isNotEmpty()) {
                            nameError = null
                        }
                    },
                    label = {
                        Text(
                            text = strings.categoryNameField,
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
                    isError = nameError != null,
                )

                if (nameError != null) {
                    Text(
                        text = nameError!!,
                        style =
                            TextStyle(
                                fontSize = PageDesignSettings.smallText,
                                color = MaterialTheme.colorScheme.error,
                            ),
                    )
                }
            }
        }

        item {
            TextField(
                value = categoryFormUiState.description,
                onValueChange = {
                    categoryViewModel.onDescriptionChange(it)
                },
                label = {
                    Text(
                        text = strings.categoryDescriptionField,
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
                        .height(PageDesignSettings.largeComponentSize),
                maxLines = 4,
            )
        }

        item {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(PageDesignSettings.smallComponentSize),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = onCancel,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                        ),
                ) {
                    Text(strings.cancelButton)
                }

                Spacer(modifier = Modifier.width(PageDesignSettings.mediumPaddingValue))

                Button(
                    onClick = {
                        if (categoryFormUiState.name.isBlank()) {
                            nameError = strings.categoryEmptyNameError
                            onShowSnackbar(strings.categoryEmptyNameError)
                        } else {
                            onSave()
                        }
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                ) {
                    Text(
                        if (categoryFormUiState.id != null) strings.updateButton else strings.insertButton,
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(PageDesignSettings.largePaddingValue))
        }
    }
}
