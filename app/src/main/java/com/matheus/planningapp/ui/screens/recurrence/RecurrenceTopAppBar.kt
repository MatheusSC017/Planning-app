package com.matheus.planningapp.ui.screens.recurrence

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceTopAppBar(
    calendarsEntities: List<CalendarEntity>,
    selectedCalendar: CalendarEntity?,
    onCalendarSelected: (CalendarEntity) -> Unit,
    onMenuClick: () -> Unit,
) {
    var isExpandedCalendarDropDown by remember { mutableStateOf(false) }

    TopAppBar(
        title = {},
        actions = {
            ExposedDropdownMenuBox(
                expanded = isExpandedCalendarDropDown,
                onExpandedChange = { isExpandedCalendarDropDown = !isExpandedCalendarDropDown },
            ) {
                TextField(
                    value = selectedCalendar?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(isExpandedCalendarDropDown)
                    },
                    modifier =
                        Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .width(PageDesignSettings.largeComponentSize),
                    textStyle =
                        TextStyle(
                            fontSize = PageDesignSettings.mediumText,
                        ),
                    colors =
                        ExposedDropdownMenuDefaults.textFieldColors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.secondary,
                            unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                            disabledTextColor = MaterialTheme.colorScheme.secondary,
                        ),
                )

                ExposedDropdownMenu(
                    expanded = isExpandedCalendarDropDown,
                    onDismissRequest = { isExpandedCalendarDropDown = false },
                    containerColor = MaterialTheme.colorScheme.background,
                    border = BorderStroke(PageDesignSettings.borderWidth, MaterialTheme.colorScheme.primary),
                ) {
                    calendarsEntities.forEach { calendarEntity ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = calendarEntity.name,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            onClick = {
                                onCalendarSelected(calendarEntity)
                                isExpandedCalendarDropDown = false
                            },
                        )
                    }
                }
            }

            IconButton(
                onClick = onMenuClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = LocalStrings.current.menuButton,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(PageDesignSettings.largeIconSize),
                )
            }
        },
    )
}
