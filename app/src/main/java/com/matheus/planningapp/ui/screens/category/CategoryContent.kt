package com.matheus.planningapp.ui.screens.category

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.matheus.planningapp.data.category.CategoryEntity
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository

@Composable
fun CategoryContent(
    modifier: Modifier,
    onShowSnackbar: (String) -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current
    var categories by remember {
        mutableStateOf<List<CategoryEntity>>(
            listOf(
                CategoryEntity(name = "Category 1", description = "Description 1"),
                CategoryEntity(name = "Category 2", description = "Description 2"),
                CategoryEntity(name = "Category 3", description = "Description 3"),
            ),
        )
    }
    var searchQuery by remember { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(PageDesignSettings.largePaddingValue),
        verticalArrangement = Arrangement.spacedBy(PageDesignSettings.largePaddingValue),
    ) {
        CategorySearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
        )

        if (showForm) {
            CategoryForm(
                modifier = Modifier.fillMaxWidth(),
                selectedCategory = selectedCategory,
                onSave = { _ ->
                    // Will be implemented in ViewModel
                    onShowSnackbar(strings.savedMessage)
                    showForm = false
                    selectedCategory = null
                    searchQuery = ""
                },
                onCancel = {
                    showForm = false
                    selectedCategory = null
                },
                onShowSnackbar = onShowSnackbar,
            )
        } else {
            if (categories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = strings.noCategoriesFound,
                        style =
                            TextStyle(
                                fontSize = PageDesignSettings.smallTitle,
                                color = MaterialTheme.colorScheme.secondary,
                            ),
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(PageDesignSettings.mediumPaddingValue),
                ) {
                    items(categories) { category ->
                        CategoryListItem(
                            category = category,
                            onEdit = {
                                selectedCategory = category
                                showForm = true
                            },
                            onDelete = {
                                // Will be implemented in ViewModel
                                onShowSnackbar(strings.categoryDeletedMessage)
                            },
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick = {
                        selectedCategory = null
                        showForm = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = strings.insertButton,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(PageDesignSettings.largeIconSize),
                    )
                }
            }
        }
    }
}
