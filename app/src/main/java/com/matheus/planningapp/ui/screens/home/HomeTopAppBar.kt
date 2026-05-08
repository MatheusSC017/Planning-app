package com.matheus.planningapp.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.matheus.planningapp.R
import com.matheus.planningapp.data.calendar.CalendarEntity
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningTopAppBar(
    modifier: Modifier,
    calendarsEntities: List<CalendarEntity>,
    selectedCalendar: CalendarEntity?,
    columnViewSelected: Boolean,
    onViewSelected: (Boolean) -> Unit,
    onCalendarSelected: (CalendarEntity) -> Unit,
    onMenuClick: () -> Unit,
) {
    var isExpandedCalendarDropDown by remember { mutableStateOf(false) }
    val strings: StringsRepository = LocalStrings.current

    TopAppBar(
        modifier = modifier,
        title = {},
        navigationIcon = {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.columns_view),
                    contentDescription = strings.columnView,
                    tint = if (columnViewSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier
                            .size(PageDesignSettings.largeIconSize)
                            .clip(RoundedCornerShape(PageDesignSettings.mediumIconClip))
                            .background(
                                if (columnViewSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                            ).padding(PageDesignSettings.extraSmallPaddingValue)
                            .clickable {
                                onViewSelected(true)
                            },
                )

                Spacer(
                    modifier = Modifier.width(PageDesignSettings.extraLargePaddingValue),
                )

                Icon(
                    painter = painterResource(id = R.drawable.grid_view),
                    contentDescription = strings.gridView,
                    tint = if (!columnViewSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier
                            .size(PageDesignSettings.largeIconSize)
                            .clip(RoundedCornerShape(PageDesignSettings.mediumIconClip))
                            .background(
                                if (!columnViewSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                            ).padding(PageDesignSettings.extraSmallPaddingValue)
                            .clickable {
                                onViewSelected(false)
                            },
                )
            }
        },
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
                onClick = { onMenuClick() },
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = strings.menuButton,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(PageDesignSettings.largeIconSize),
                )
            }
        },
    )
}
