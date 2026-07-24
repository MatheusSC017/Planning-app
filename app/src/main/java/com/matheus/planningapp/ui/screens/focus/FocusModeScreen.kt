package com.matheus.planningapp.ui.screens.focus

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matheus.planningapp.R
import com.matheus.planningapp.ui.screens.components.stardardBackground
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.PermissionManager
import com.matheus.planningapp.viewmodel.focus.FocusModeViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusModeScreen(
    commitmentId: Long? = null,
    onExitFocus: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: FocusModeViewModel = koinViewModel()
) {
    val strings: StringsRepository = LocalStrings.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionManager: PermissionManager = koinInject()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val dndPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (permissionManager.isNotificationPolicyAccessGranted()) {
            viewModel.toggleDeepFocus(true)
        } else {
            viewModel.toggleDeepFocus(false)
        }
    }

    val usageStatsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (permissionManager.hasUsageStatsPermission()) {
            viewModel.toggleAppTracking(true)
        } else {
            viewModel.toggleAppTracking(false)
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!permissionManager.hasNotificationPermission()) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(commitmentId) {
        viewModel.setCommitmentId(commitmentId)
    }

    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    if (window != null) {
        val windowInsetsController = WindowCompat.getInsetsController(window, view)
        DisposableEffect(key1 = uiState.isRunning) {
            if (uiState.isRunning) {
                windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
                windowInsetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
            }
            onDispose {
                windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
            }
        }
    }

    BackHandler(enabled = true) {
        viewModel.onExit(onExitFocus)
    }

    Scaffold(
        topBar = {
            if (!uiState.isRunning) {
                TopAppBar(
                    title = {
                        Text(
                            text = strings.focusModeMenuButton,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onExit(onExitFocus) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.backMenuButton
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToHistory) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_history),
                                contentDescription = strings.focusHistoryTitle
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = if (uiState.isRunning) PageDesignSettings.zeroPaddingValue else paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                    )
                    .stardardBackground(),
                contentAlignment = Alignment.Center,
            ) {

                if (uiState.isRunning) {
                    Column {
                        BreathingAnimation()
                        Spacer(modifier = Modifier.height(PageDesignSettings.extraLargeIconSize + PageDesignSettings.largeSpacer))
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { if (uiState.isRunning || uiState.isPaused) uiState.progress else 1f },
                            modifier = Modifier.size(PageDesignSettings.largeComponentSize),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = PageDesignSettings.smallIconClip,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round,
                        )
                        Text(
                            text = uiState.formattedTime,
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(PageDesignSettings.largeSpacer))

                    if (!uiState.isRunning && !uiState.isPaused) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(PageDesignSettings.extraLargePaddingValue),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TimeAdjustmentColumn(
                                value = uiState.hoursInput,
                                label = "H",
                                onIncrease = { viewModel.onHoursChange(uiState.hoursInput + 1) },
                                onDecrease = { viewModel.onHoursChange(uiState.hoursInput - 1) }
                            )
                            TimeAdjustmentColumn(
                                value = uiState.minutesInput,
                                label = "M",
                                onIncrease = { viewModel.onMinutesChange(uiState.minutesInput + 1) },
                                onDecrease = { viewModel.onMinutesChange(uiState.minutesInput - 1) }
                            )
                            TimeAdjustmentColumn(
                                value = uiState.secondsInput,
                                label = "S",
                                onIncrease = { viewModel.onSecondsChange(uiState.secondsInput + 1) },
                                onDecrease = { viewModel.onSecondsChange(uiState.secondsInput - 1) }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(PageDesignSettings.mediumSpacer))

                        OutlinedTextField(
                            value = uiState.tag,
                            onValueChange = viewModel::onTagChange,
                            placeholder = { Text(strings.focusTagField, style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier
                                .width(PageDesignSettings.largeComponentSize)
                                .height(48.dp)
                                .padding(horizontal = PageDesignSettings.extraLargePaddingValue),
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            textStyle = MaterialTheme.typography.bodySmall,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            )
                        )

                        Spacer(modifier = Modifier.height(PageDesignSettings.extraSmallPaddingValue))

                        Column(verticalArrangement = Arrangement.spacedBy(PageDesignSettings.extraSmallPaddingValue)) {
                            FocusOptionToggle(
                                label = strings.deepFocusLabel,
                                enabled = uiState.deepFocusEnabled,
                                onToggle = { enabled ->
                                    if (enabled) {
                                        if (permissionManager.isNotificationPolicyAccessGranted()) {
                                            viewModel.toggleDeepFocus(true)
                                        } else {
                                            Toast.makeText(context, strings.dndPermissionRequired, Toast.LENGTH_LONG).show()
                                            permissionManager.requestNotificationPolicyAccess(dndPermissionLauncher)
                                        }
                                    } else {
                                        viewModel.toggleDeepFocus(false)
                                    }
                                }
                            )

                            FocusOptionToggle(
                                label = strings.appTrackingLabel,
                                enabled = uiState.appTrackingEnabled,
                                onToggle = { enabled ->
                                    if (enabled) {
                                        if (permissionManager.hasUsageStatsPermission()) {
                                            viewModel.toggleAppTracking(true)
                                        } else {
                                            Toast.makeText(context, strings.usageStatsPermissionRequired, Toast.LENGTH_LONG).show()
                                            permissionManager.requestUsageStatsPermission(usageStatsPermissionLauncher)
                                        }
                                    } else {
                                        viewModel.toggleAppTracking(false)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(PageDesignSettings.mediumSpacer))
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PageDesignSettings.mediumSpacer),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isRunning || uiState.isPaused) {
                            if (uiState.isRunning) {
                                FilledTonalIconButton(
                                    onClick = viewModel::pauseTimer,
                                    modifier = Modifier.size(PageDesignSettings.extraLargeIconSize)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_pause),
                                        contentDescription = strings.pauseFocusModeButton,
                                        modifier = Modifier.size(PageDesignSettings.mediumIconSize)
                                    )
                                }
                            } else {
                                FilledTonalIconButton(
                                    onClick = viewModel::startTimer,
                                    modifier = Modifier.size(PageDesignSettings.extraLargeIconSize)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = strings.startFocusModeButton,
                                        modifier = Modifier.size(PageDesignSettings.mediumIconSize)
                                    )
                                }
                            }
                            FilledTonalIconButton(
                                onClick = viewModel::stopTimer,
                                modifier = Modifier.size(PageDesignSettings.extraLargeIconSize)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_stop),
                                    contentDescription = strings.stopFocusModeButton,
                                    modifier = Modifier.size(PageDesignSettings.mediumIconSize)
                                )
                            }
                        } else {
                            FilledTonalIconButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !permissionManager.hasNotificationPermission()) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.startTimer()
                                    }
                                },
                                modifier = Modifier.size(PageDesignSettings.extraLargeIconSize)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = strings.startFocusModeButton,
                                    modifier = Modifier.size(PageDesignSettings.mediumIconSize)
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun FocusOptionToggle(
    label: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .width(PageDesignSettings.largeComponentSize)
            .height(28.dp)
            .padding(horizontal = PageDesignSettings.extraLargePaddingValue + PageDesignSettings.mediumPaddingValue),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            modifier = Modifier.scale(0.55f)
        )
    }
}

@Composable
fun BreathingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.1f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val color by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        targetValue = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    Surface(
        modifier = Modifier
            .size(PageDesignSettings.largeComponentSize)
            .scale(scale),
        shape = CircleShape,
        color = color,
        content = {}
    )
}

@Composable
fun TimeAdjustmentColumn(
    value: Int,
    label: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PageDesignSettings.smallPaddingValue)
    ) {
        RepeatingIconButton(onClick = onIncrease) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%02d".format(value),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        RepeatingIconButton(onClick = onDecrease) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RepeatingIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        if (isPressed) {
            currentOnClick()
            delay(500)
            while (true) {
                currentOnClick()
                delay(100)
            }
        }
    }

    IconButton(
        onClick = { },
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier
    ) {
        content()
    }
}
