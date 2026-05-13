package com.matheus.planningapp.ui.screens.category

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.matheus.planningapp.data.category.CategoryEntity
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository

@Composable
fun CategoryListItem(
    category: CategoryEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(PageDesignSettings.mediumPaddingValue),
        border =
            BorderStroke(
                width = PageDesignSettings.borderWidth,
                color = MaterialTheme.colorScheme.primary,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(PageDesignSettings.mediumPaddingValue),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(end = PageDesignSettings.mediumPaddingValue),
                verticalArrangement = Arrangement.spacedBy(PageDesignSettings.smallPaddingValue),
            ) {
                Text(
                    text = category.name,
                    style =
                        TextStyle(
                            fontSize = PageDesignSettings.smallTitle,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (!category.description.isNullOrBlank()) {
                    Text(
                        text = category.description,
                        style =
                            TextStyle(
                                fontSize = PageDesignSettings.smallText,
                                color = MaterialTheme.colorScheme.secondary,
                            ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(PageDesignSettings.smallPaddingValue),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onEdit,
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = strings.editButton,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                IconButton(
                    onClick = onDelete,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = strings.deleteButton,
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}
