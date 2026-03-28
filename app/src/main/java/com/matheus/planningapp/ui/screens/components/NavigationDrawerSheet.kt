package com.matheus.planningapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.matheus.planningapp.BuildConfig
import com.matheus.planningapp.ui.theme.PageDesignSettings

@Composable
fun NavigationDrawerSheet(
    onNavigateToHomeScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToSettingsScreen: () -> Unit,
    onNavigateToRecurrenceScreen: () -> Unit
) {
    /* TODO: Create component to menu buttons */
    ModalDrawerSheet {
        Column(
            modifier = Modifier.background(
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
        ) {
            Text(
                text = "Planning your life",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(
                    top = PageDesignSettings.extraLargePaddingValue,
                    end = PageDesignSettings.extraLargePaddingValue,
                    bottom = PageDesignSettings.extraLargePaddingValue * 2,
                    start = PageDesignSettings.extraLargePaddingValue
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(bottom = PageDesignSettings.extraLargePaddingValue)
            )

            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PageDesignSettings.extraLargePaddingValue),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Timeline",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(PageDesignSettings.mediumIconSize)
                        )

                        Spacer(modifier = Modifier.width(PageDesignSettings.mediumPaddingValue))

                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                },
                onClick = onNavigateToHomeScreen,
                selected = false,
                modifier = Modifier.padding(
                    vertical = PageDesignSettings.smallPaddingValue,
                    horizontal = PageDesignSettings.extraLargePaddingValue
                )
            )

            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PageDesignSettings.extraLargePaddingValue),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Calendars",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(PageDesignSettings.mediumIconSize)
                        )

                        Spacer(modifier = Modifier.width(PageDesignSettings.mediumPaddingValue))

                        Text(
                            text = "Calendars",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                },
                onClick = onNavigateToCalendarScreen,
                selected = false,
                modifier = Modifier.padding(
                    vertical = PageDesignSettings.smallPaddingValue,
                    horizontal = PageDesignSettings.extraLargePaddingValue
                )
            )

            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PageDesignSettings.extraLargePaddingValue),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recurrences",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(PageDesignSettings.mediumIconSize)
                        )

                        Spacer(modifier = Modifier.width(PageDesignSettings.mediumPaddingValue))

                        Text(
                            text = "Recurrences",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                },
                onClick = onNavigateToRecurrenceScreen,
                selected = false,
                modifier = Modifier.padding(
                    vertical = PageDesignSettings.smallPaddingValue,
                    horizontal = PageDesignSettings.extraLargePaddingValue
                )
            )

            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PageDesignSettings.extraLargePaddingValue),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(PageDesignSettings.mediumIconSize)
                        )

                        Spacer(modifier = Modifier.width(PageDesignSettings.mediumPaddingValue))

                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                },
                onClick = onNavigateToSettingsScreen,
                selected = false,
                modifier = Modifier.padding(
                    vertical = PageDesignSettings.smallPaddingValue,
                    horizontal = PageDesignSettings.extraLargePaddingValue
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider()

            Text(
                text = "Version ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(PageDesignSettings.extraLargePaddingValue)
            )
        }
    }
}
