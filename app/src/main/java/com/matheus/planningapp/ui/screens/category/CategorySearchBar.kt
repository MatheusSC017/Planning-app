package com.matheus.planningapp.ui.screens.category

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository

@Composable
fun CategorySearchBar(
    searchQuery: String?,
    onSearchQueryChange: (String) -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current

    TextField(
        value = searchQuery ?: "",
        onValueChange = onSearchQueryChange,
        label = {
            Text(
                text = strings.searchCategoryField,
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
        modifier = Modifier.fillMaxWidth().height(PageDesignSettings.smallComponentSize),
        singleLine = true,
    )
}
