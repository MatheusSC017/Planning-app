package com.matheus.planningapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.matheus.planningapp.ui.screens.components.ConfirmationDialog
import com.matheus.planningapp.ui.screens.components.stardardBackground
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusModeScreen(onExitFocus: () -> Unit) {
    val strings: StringsRepository = LocalStrings.current
    BackHandler(enabled = true) {}

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.focusModeMenuButton,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                // Navigation actions are removed to enforce focus mode (drawer access disabled)
                actions = {}
            )
        },
        content = { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .stardardBackground(),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    onClick = {  onExitFocus() },
                ) {
                    Text(
                        text = strings.stopFocusModeButton,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        },
    )
}
