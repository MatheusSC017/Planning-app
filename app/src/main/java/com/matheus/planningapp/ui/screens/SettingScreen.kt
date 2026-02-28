package com.matheus.planningapp.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.matheus.planningapp.ui.screens.components.ConfirmationDialog
import com.matheus.planningapp.viewmodel.setting.EmailOptions
import com.matheus.planningapp.viewmodel.setting.SettingViewModel
import com.matheus.planningapp.viewmodel.setting.ViewOptions
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(
                        onClick = onMenuClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            SettingsForm(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(
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
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsForm(
    modifier: Modifier,
    settignsViewModel: SettingViewModel = koinViewModel()
) {
    var isExpandedViewDropdown: Boolean by remember { mutableStateOf(false) }
    var selectedViewOption: ViewOptions by rememberSaveable { mutableStateOf(ViewOptions.COLUMN) }
    var isExpandedEmailDropdown: Boolean by remember { mutableStateOf(false) }
    var selectedEmailOption: EmailOptions by rememberSaveable { mutableStateOf(EmailOptions.NO_SEND) }
    var activeNotifications: Boolean by rememberSaveable { mutableStateOf( false ) }

    val uiState by settignsViewModel.uiState.collectAsState()
    LaunchedEffect(uiState) {
        selectedViewOption = uiState.viewMode
        selectedEmailOption = uiState.emailOption
        activeNotifications = uiState.activeNotifications
    }

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {}

    ConfirmationDialog(
        item = listOf(selectedViewOption, selectedEmailOption),
        showDialog = showDialog,
        title = "Confirm the settings",
        message = "Are you sure you want to save the settings",
        onDismissRequest = { showDialog = false },
        onConfirm = {
            settignsViewModel.updateSettings(selectedViewOption, selectedEmailOption, activeNotifications)
            showDialog = false
            if (uiState.activeNotifications != activeNotifications) {
                if (activeNotifications) {
                    scheduleNotification(notificationPermissionLauncher, context)
                } else {
                    /* TODO: Delete the notification to future commitments */
                }
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = 16.dp,
                start = 16.dp,
                bottom = 16.dp,
                end = 16.dp
            )
    ) {
        Text(
            text = "Viewing mode",
            style = TextStyle(
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.padding(8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = isExpandedViewDropdown,
            onExpandedChange = { isExpandedViewDropdown = !isExpandedViewDropdown }
        ) {
            TextField(
                value = selectedViewOption.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(isExpandedViewDropdown)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 16.sp
                ),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                    disabledTextColor = MaterialTheme.colorScheme.secondary
                )
            )

            ExposedDropdownMenu(
                expanded = isExpandedViewDropdown,
                onDismissRequest = { isExpandedViewDropdown = false },
                containerColor = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                ViewOptions.entries.forEach { viewOption ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = viewOption.label,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        onClick = {
                            selectedViewOption = viewOption
                            isExpandedViewDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "E-mail configuration",
            style = TextStyle(
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.padding(8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = isExpandedEmailDropdown,
            onExpandedChange = { isExpandedEmailDropdown = !isExpandedEmailDropdown }
        ) {
            TextField(
                value = selectedEmailOption.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(isExpandedEmailDropdown)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 16.sp
                ),
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSecondary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                    disabledTextColor = MaterialTheme.colorScheme.secondary
                )
            )

            ExposedDropdownMenu(
                expanded = isExpandedEmailDropdown,
                onDismissRequest = { isExpandedEmailDropdown = false },
                containerColor = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                EmailOptions.entries.forEach { emailOption ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = emailOption.label,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        onClick = {
                            selectedEmailOption = emailOption
                            isExpandedEmailDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enable notifications",
            style = TextStyle(
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.padding(8.dp)
        )

        Switch(
            checked = activeNotifications,
            onCheckedChange = {
                activeNotifications = it
            },
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                uncheckedBorderColor = MaterialTheme.colorScheme.secondary,
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                checkedBorderColor = MaterialTheme.colorScheme.primary
                ),
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {
                    showDialog = true
                }
            ) {
                Text(
                    text = "Save",
                    style = TextStyle(
                        fontSize = 24.sp
                    )
                )
            }
        }
    }
}

fun scheduleNotification(notificationPermissionLauncher: ActivityResultLauncher<String>, context: Context) {
    if (Build.VERSION.SDK_INT >= 33) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
            return
        }
    }

    if (Build.VERSION.SDK_INT >= 31) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExactAlarms()) {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            )
            return
        }
    }

    /* TODO: Create the notification to future commitments */
}
