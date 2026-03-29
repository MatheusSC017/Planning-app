package com.matheus.planningapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.ui.text.TextStyle
import com.matheus.planningapp.ui.screens.components.ConfirmationDialog
import com.matheus.planningapp.ui.theme.LocalStrings
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.StringsRepository
import com.matheus.planningapp.util.enums.NotificationEnum
import com.matheus.planningapp.viewmodel.setting.SettingUiState
import com.matheus.planningapp.viewmodel.setting.SettingViewModel
import com.matheus.planningapp.util.enums.ViewEnum
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onMenuClick: () -> Unit
) {
    val strings: StringsRepository = LocalStrings.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.settingsMenuButton,
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
                            contentDescription = strings.menuButton,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(PageDesignSettings.largeIconSize)
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
    val strings: StringsRepository = LocalStrings.current

    var isExpandedViewDropdown: Boolean by remember { mutableStateOf(false) }
    var selectedViewOption: ViewEnum by rememberSaveable { mutableStateOf(ViewEnum.COLUMN) }

    var isExpandedNotificationDropdown: Boolean by remember { mutableStateOf(false) }
    var selectedNotificationOption: NotificationEnum by rememberSaveable { mutableStateOf(NotificationEnum.NO_SEND) }

    val uiState by settignsViewModel.uiState.collectAsState()
    LaunchedEffect(uiState) {
        selectedViewOption = uiState.viewMode
        selectedNotificationOption = uiState.notificationOption
    }

    var showDialog by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {}

    ConfirmationDialog(
        item = listOf(selectedViewOption, selectedNotificationOption),
        showDialog = showDialog,
        title = strings.dialogUpdateSettingTitle,
        message = strings.dialogUpdateSettingMessage,
        onDismissRequest = { showDialog = false },
        onConfirm = {
            val settingUiState = SettingUiState(
                viewMode = selectedViewOption,
                notificationOption = selectedNotificationOption
            )
            settignsViewModel.updateSettings(settingUiState, notificationPermissionLauncher)
            showDialog = false
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(PageDesignSettings.extraLargePaddingValue)
    ) {
        Text(
            text = strings.settingViewModeField,
            style = TextStyle(
                fontSize = PageDesignSettings.smallTitle,
                color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.padding(PageDesignSettings.mediumPaddingValue)
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
                    fontSize = PageDesignSettings.mediumText
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
                border = BorderStroke(PageDesignSettings.borderWidth, MaterialTheme.colorScheme.primary)
            ) {
                ViewEnum.entries.forEach { viewOption ->
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

        Spacer(modifier = Modifier.height(PageDesignSettings.extraLargePaddingValue))

        Text(
            text = strings.settingNotificationField,
            style = TextStyle(
                fontSize = PageDesignSettings.smallTitle,
                color = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.padding(PageDesignSettings.mediumPaddingValue)
        )

        ExposedDropdownMenuBox(
            expanded = isExpandedNotificationDropdown,
            onExpandedChange = { isExpandedNotificationDropdown = !isExpandedNotificationDropdown }
        ) {
            TextField(
                value = selectedNotificationOption.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(isExpandedNotificationDropdown)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = PageDesignSettings.mediumText
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
                expanded = isExpandedNotificationDropdown,
                onDismissRequest = { isExpandedNotificationDropdown = false },
                containerColor = MaterialTheme.colorScheme.background,
                border = BorderStroke(PageDesignSettings.borderWidth, MaterialTheme.colorScheme.primary)
            ) {
                NotificationEnum.entries.forEach { notificationOption ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = notificationOption.label,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        onClick = {
                            selectedNotificationOption = notificationOption
                            isExpandedNotificationDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PageDesignSettings.extraLargePaddingValue),
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
                    text = strings.confirmButton,
                    style = TextStyle(
                        fontSize = PageDesignSettings.smallTitle
                    )
                )
            }
        }
    }
}
