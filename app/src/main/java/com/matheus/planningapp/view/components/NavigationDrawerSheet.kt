package com.matheus.planningapp.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.unit.dp

@Composable
fun NavigationDrawerSheet(
    selectedScreen: String,
    onNavigateToCalendar: () -> Unit,
    onNavigateToCalendarsMenu: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    /*
    TODO: Read documentation if I really need to pass the method to all screen,
        or there is a form to get the methods to navigate automatically
    */
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
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 32.dp,
                    start = 16.dp
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Timeline",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )

                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                },
                onClick = {
                    onNavigateToCalendar()
                },
                selected = selectedScreen == "Home",
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
            )

            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Calendars",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )

                        Text(
                            text = "Calendars",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                },
                onClick = {
                    onNavigateToCalendarsMenu()
                },
                selected = selectedScreen == "Calendars",
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
            )

            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )

                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                },
                onClick = {
                    onNavigateToSettings()
                },
                selected = selectedScreen == "Settings",
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider()

            Text(
                text = "v 1.0.0",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
