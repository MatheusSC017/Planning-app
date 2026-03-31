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
import androidx.compose.ui.graphics.vector.ImageVector
import com.matheus.planningapp.BuildConfig
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.StringsRepository

@Composable
fun NavigationDrawerSheet(
    onNavigateToHomeScreen: () -> Unit,
    onNavigateToCalendarScreen: () -> Unit,
    onNavigateToSettingsScreen: () -> Unit,
    onNavigateToRecurrenceScreen: () -> Unit
) {
    val strings: StringsRepository = LocalStrings.current

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
                text = strings.projectName,
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

            MenuNavigationDrawerItem(
                title = strings.homeMenuButton,
                icon = Icons.Default.Home,
                onNavigate = onNavigateToHomeScreen
            )

            MenuNavigationDrawerItem(
                title = strings.calendarsMenuButton,
                icon = Icons.Default.DateRange,
                onNavigate = onNavigateToCalendarScreen
            )

            MenuNavigationDrawerItem(
                title = strings.recurrencesMenuButton,
                icon = Icons.Default.Refresh,
                onNavigate = onNavigateToRecurrenceScreen
            )

            MenuNavigationDrawerItem(
                title = strings.settingsMenuButton,
                icon = Icons.Default.Info,
                onNavigate = onNavigateToSettingsScreen
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

@Composable
fun MenuNavigationDrawerItem(
    title: String,
    icon: ImageVector,
    onNavigate: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PageDesignSettings.extraLargePaddingValue),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(PageDesignSettings.mediumIconSize)
                )

                Spacer(modifier = Modifier.width(PageDesignSettings.mediumPaddingValue))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        },
        onClick = onNavigate,
        selected = false,
        modifier = Modifier.padding(
            vertical = PageDesignSettings.smallPaddingValue,
            horizontal = PageDesignSettings.extraLargePaddingValue
        )
    )
}
