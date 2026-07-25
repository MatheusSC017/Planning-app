package com.matheus.planningapp.ui.screens.focus

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    val scrollState = rememberScrollState()

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
            AnimatedVisibility(
                visible = !uiState.isRunning,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = strings.focusModeMenuButton,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
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
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .stardardBackground()
                    .padding(
                        top = if (uiState.isRunning) PageDesignSettings.zeroPaddingValue else paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                    ),
                contentAlignment = Alignment.Center,
            ) {

                if (uiState.isRunning) {
                    Column {
                        BreathingAnimation()
                        Spacer(modifier = Modifier.height(PageDesignSettings.extraLargeIconSize + PageDesignSettings.mediumPaddingValue))
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = if (uiState.isRunning) Arrangement.Center else Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = PageDesignSettings.mediumPaddingValue, vertical = PageDesignSettings.largePaddingValue)
                ) {
                    val indicatorSize = if (uiState.isRunning) PageDesignSettings.largeComponentSize + 24.dp else 200.dp
                    
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = PageDesignSettings.mediumPaddingValue)) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(indicatorSize),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            strokeWidth = 10.dp,
                            strokeCap = StrokeCap.Round,
                        )
                        CircularProgressIndicator(
                            progress = { if (uiState.isRunning || uiState.isPaused) uiState.progress else 1f },
                            modifier = Modifier.size(indicatorSize),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 10.dp,
                            trackColor = Color.Transparent,
                            strokeCap = StrokeCap.Round,
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.formattedTime,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = if (uiState.isRunning) 64.sp else 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-2).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (uiState.tag.isNotEmpty()) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = uiState.tag,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(PageDesignSettings.mediumPaddingValue))

                    if (!uiState.isRunning && !uiState.isPaused) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(PageDesignSettings.largePaddingValue),
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
                        
                        Spacer(modifier = Modifier.height(PageDesignSettings.mediumPaddingValue))

                        OutlinedTextField(
                            value = uiState.tag,
                            onValueChange = viewModel::onTagChange,
                            placeholder = { Text(strings.focusTagField, style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier
                                .width(PageDesignSettings.largeComponentSize)
                                .padding(horizontal = PageDesignSettings.extraLargePaddingValue),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(PageDesignSettings.mediumPaddingValue))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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

                        Spacer(modifier = Modifier.height(PageDesignSettings.mediumPaddingValue))
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PageDesignSettings.largePaddingValue),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = PageDesignSettings.mediumPaddingValue)
                    ) {
                        if (uiState.isRunning || uiState.isPaused) {
                            if (uiState.isRunning) {
                                ActionButton(
                                    onClick = viewModel::pauseTimer,
                                    icon = R.drawable.ic_pause,
                                    contentDescription = strings.pauseFocusModeButton,
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            } else {
                                ActionButton(
                                    onClick = viewModel::startTimer,
                                    icon = Icons.Default.PlayArrow,
                                    contentDescription = strings.startFocusModeButton,
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            }
                            ActionButton(
                                onClick = viewModel::stopTimer,
                                icon = R.drawable.ic_stop,
                                contentDescription = strings.stopFocusModeButton,
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        } else {
                            FilledTonalButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !permissionManager.hasNotificationPermission()) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.startTimer()
                                    }
                                },
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, null)
                                Spacer(Modifier.width(8.dp))
                                Text(strings.startFocusModeButton, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    icon: Any,
    contentDescription: String,
    containerColor: Color
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        shape = RoundedCornerShape(20.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = containerColor)
    ) {
        if (icon is Int) {
            Icon(painterResource(id = icon), contentDescription = contentDescription, modifier = Modifier.size(28.dp))
        } else if (icon is ImageVector) {
            Icon(imageVector = icon, contentDescription = contentDescription, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun FocusOptionToggle(
    label: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        onClick = { onToggle(!enabled) },
        shape = RoundedCornerShape(16.dp),
        color = if (enabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        border = if (enabled) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.width(PageDesignSettings.largeComponentSize)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                modifier = Modifier.scale(0.8f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                thumbContent = if (enabled) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else null
            )
        }
    }
}

@Composable
fun BreathingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(PageDesignSettings.largeComponentSize + 80.dp)
            .scale(scale)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha), CircleShape),
        contentAlignment = Alignment.Center
    ) {}
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
        verticalArrangement = Arrangement.Center
    ) {
        RepeatingIconButton(onClick = onIncrease) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            modifier = Modifier.size(width = 64.dp, height = 72.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "%02d".format(value),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        RepeatingIconButton(onClick = onDecrease) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
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
