package com.matheus.planningapp.viewmodel.focus

import android.app.NotificationManager
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.R
import com.matheus.planningapp.data.commitment.CommitmentRepository
import com.matheus.planningapp.data.focus.FocusSessionEntity
import com.matheus.planningapp.data.focus.FocusSessionRepository
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import com.matheus.planningapp.util.notification.NotificationConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class FocusModeViewModel(
    private val focusSessionRepository: FocusSessionRepository,
    private val commitmentRepository: CommitmentRepository,
    private val context: Context,
    private val strings: StringsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusModeUiState())
    val uiState: StateFlow<FocusModeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var quoteJob: Job? = null
    private var trackingJob: Job? = null
    
    private var sessionStartTime: Long? = null
    private var initialDurationSeconds: Long = 0

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    fun setCommitmentId(id: Long?) {
        _uiState.update { it.copy(commitmentId = id) }
        id?.let { loadCommitmentDuration(it) }
    }

    private fun loadCommitmentDuration(id: Long) {
        viewModelScope.launch {
            commitmentRepository.getCommitment(id)?.let { commitment ->
                val duration = commitment.endDateTime - commitment.startDateTime
                val totalSeconds = duration.inWholeSeconds
                
                _uiState.update { state ->
                    state.copy(
                        hoursInput = (totalSeconds / 3600).toInt().coerceIn(0, 12),
                        minutesInput = ((totalSeconds % 3600) / 60).toInt().coerceIn(0, 59),
                        secondsInput = (totalSeconds % 60).toInt().coerceIn(0, 59)
                    )
                }
            }
        }
    }

    fun onHoursChange(hours: Int) {
        if (hours in 0..12) _uiState.update { it.copy(hoursInput = hours) }
    }

    fun onMinutesChange(minutes: Int) {
        when {
            minutes >= 60 -> {
                onHoursChange(_uiState.value.hoursInput + 1)
                _uiState.update { it.copy(minutesInput = 0) }
            }
            minutes < 0 -> {
                if (_uiState.value.hoursInput > 0) {
                    onHoursChange(_uiState.value.hoursInput - 1)
                    _uiState.update { it.copy(minutesInput = 59) }
                }
            }
            else -> _uiState.update { it.copy(minutesInput = minutes) }
        }
    }

    fun onSecondsChange(seconds: Int) {
        when {
            seconds >= 60 -> {
                onMinutesChange(_uiState.value.minutesInput + 1)
                _uiState.update { it.copy(secondsInput = 0) }
            }
            seconds < 0 -> {
                if (_uiState.value.minutesInput > 0 || _uiState.value.hoursInput > 0) {
                    onMinutesChange(_uiState.value.minutesInput - 1)
                    _uiState.update { it.copy(secondsInput = 59) }
                }
            }
            else -> _uiState.update { it.copy(secondsInput = seconds) }
        }
    }

    fun toggleDeepFocus(enabled: Boolean) {
        _uiState.update { it.copy(deepFocusEnabled = enabled) }
    }

    fun toggleAppTracking(enabled: Boolean) {
        _uiState.update { it.copy(appTrackingEnabled = enabled) }
    }

    fun startTimer() {
        val duration = if (_uiState.value.isPaused) {
            _uiState.value.timeRemainingSeconds.seconds
        } else {
            val total = _uiState.value.hoursInput.hours +
                    _uiState.value.minutesInput.minutes +
                    _uiState.value.secondsInput.seconds
            
            initialDurationSeconds = total.inWholeSeconds
            sessionStartTime = Clock.System.now().toEpochMilliseconds()
            total
        }
        
        if (duration.inWholeSeconds <= 0) return

        stopActiveJobs()
        
        val endTime = Clock.System.now().plus(duration)

        _uiState.update {
            it.copy(
                totalTimeSeconds = duration.inWholeSeconds,
                timeRemainingSeconds = duration.inWholeSeconds,
                isRunning = true,
                isPaused = false
            )
        }

        enableDndIfRequested()

        timerJob = viewModelScope.launch {
            while (Clock.System.now() < endTime) {
                val remaining = (endTime - Clock.System.now()).inWholeSeconds
                _uiState.update { it.copy(timeRemainingSeconds = remaining) }
                delay(1.seconds)
            }
            _uiState.update { it.copy(timeRemainingSeconds = 0, isRunning = false) }
            completeSession(completed = true)
        }
        
        startQuoteJob()
        if (_uiState.value.appTrackingEnabled) startTrackingJob()
    }

    fun pauseTimer() {
        _uiState.update { it.copy(isRunning = false, isPaused = true) }
        stopActiveJobs()
        disableDndIfEnabled()
    }

    fun stopTimer() {
        completeSession(completed = false)
        _uiState.update { it.copy(isRunning = false, isPaused = false, timeRemainingSeconds = 0) }
    }

    fun onExit(onExitConfirmed: () -> Unit) {
        if (_uiState.value.isRunning || _uiState.value.isPaused) {
            completeSession(completed = false)
        }
        onExitConfirmed()
    }

    private fun completeSession(completed: Boolean) {
        saveSession(completed)
        stopActiveJobs()
        disableDndIfEnabled()
    }

    private fun stopActiveJobs() {
        timerJob?.cancel()
        quoteJob?.cancel()
        trackingJob?.cancel()
        timerJob = null
        quoteJob = null
        trackingJob = null
    }

    private fun enableDndIfRequested() {
        if (_uiState.value.deepFocusEnabled && notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        }
    }

    private fun disableDndIfEnabled() {
        if (_uiState.value.deepFocusEnabled && notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }

    private fun startQuoteJob() {
        quoteJob = viewModelScope.launch {
            while (true) {
                delay(5.minutes)
                _uiState.update { it.copy(quoteIndex = it.quoteIndex + 1) }
            }
        }
    }

    private fun startTrackingJob() {
        trackingJob = viewModelScope.launch {
            while (true) {
                delay(TRACKING_INTERVAL_MS.seconds)
                if (isDistractingAppInForeground()) {
                    sendNudgeNotification()
                }
            }
        }
    }

    private fun isDistractingAppInForeground(): Boolean {
        val time = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 60_000, time)
        val foregroundApp = stats?.maxByOrNull { it.lastTimeUsed }?.packageName
        return foregroundApp != null && foregroundApp != context.packageName && DISTRACTING_APPS.contains(foregroundApp)
    }

    private fun sendNudgeNotification() {
        val builder = NotificationCompat.Builder(context, NotificationConfig.NUDGE_CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_notifications_24)
            .setContentTitle(strings.distractingAppNudgeTitle)
            .setContentText(strings.distractingAppNudgeMessage)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
        
        notificationManager.notify(NUDGE_NOTIFICATION_ID, builder.build())
    }

    private fun saveSession(completed: Boolean) {
        val startTime = sessionStartTime ?: return
        val durationSeconds = initialDurationSeconds - _uiState.value.timeRemainingSeconds

        viewModelScope.launch {
            focusSessionRepository.insertSession(
                FocusSessionEntity(
                    startTime = startTime,
                    durationSeconds = durationSeconds.toInt(),
                    completed = completed,
                    commitmentId = _uiState.value.commitmentId
                )
            )
        }
        
        sessionStartTime = null
        initialDurationSeconds = 0
    }

    override fun onCleared() {
        super.onCleared()
        if (_uiState.value.isRunning || _uiState.value.isPaused) {
            completeSession(completed = false)
        }
    }

    companion object {
        private const val NUDGE_NOTIFICATION_ID = 999
        private const val TRACKING_INTERVAL_MS = 5L
        private val DISTRACTING_APPS = setOf(
            "com.facebook.katana",
            "com.instagram.android",
            "com.twitter.android",
            "com.tiktok.android.mobile.ticker",
            "com.zhiliaoapp.musically",
            "com.whatsapp",
            "com.google.android.youtube",
            "com.netflix.mediaclient",
            "com.disney.disneyplus",
            "com.amazon.avod.thirdpartyclient",
            "com.snapchat.android"
        )
    }
}

data class FocusModeUiState(
    val timeRemainingSeconds: Long = 0,
    val totalTimeSeconds: Long = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val hoursInput: Int = 0,
    val minutesInput: Int = 30,
    val secondsInput: Int = 0,
    val quoteIndex: Int = 0,
    val commitmentId: Long? = null,
    val deepFocusEnabled: Boolean = false,
    val appTrackingEnabled: Boolean = false
) {
    val progress: Float
        get() = if (totalTimeSeconds > 0L) timeRemainingSeconds.toFloat() / totalTimeSeconds.toFloat() else 0f
    
    val formattedTime: String
        get() {
            val h = timeRemainingSeconds / 3600
            val m = (timeRemainingSeconds % 3600) / 60
            val s = timeRemainingSeconds % 60
            return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
        }
}
