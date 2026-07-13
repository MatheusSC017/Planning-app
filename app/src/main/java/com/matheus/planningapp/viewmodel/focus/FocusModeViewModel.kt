package com.matheus.planningapp.viewmodel.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matheus.planningapp.data.focus.FocusSessionEntity
import com.matheus.planningapp.data.focus.FocusSessionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class FocusModeViewModel(private val focusSessionRepository: FocusSessionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FocusModeUiState())
    val uiState: StateFlow<FocusModeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: Long? = null
    private var initialDurationSeconds: Long = 0

    fun onHoursChange(hours: Int) {
        if (hours in 0..12) {
            _uiState.update { it.copy(hoursInput = hours) }
        }
    }

    fun onMinutesChange(minutes: Int) {
        if (minutes in 0..59) {
            _uiState.update { it.copy(minutesInput = minutes) }
        } else {
            if (minutes >= 60) {
                onHoursChange(_uiState.value.hoursInput + 1)
                _uiState.update { it.copy(minutesInput = 0) }
            } else {
                if (_uiState.value.hoursInput > 0) {
                    onHoursChange(_uiState.value.hoursInput - 1)
                    _uiState.update { it.copy(minutesInput = 59) }
                }
            }
        }
    }

    fun onSecondsChange(seconds: Int) {
        if (seconds in 0..59) {
            _uiState.update { it.copy(secondsInput = seconds) }
        } else {
            if (seconds >= 60) {
                onMinutesChange(_uiState.value.minutesInput + 1)
                _uiState.update { it.copy(secondsInput = 0) }
            } else {
                if (_uiState.value.minutesInput > 0 || _uiState.value.hoursInput > 0) {
                    onMinutesChange(_uiState.value.minutesInput - 1)
                    _uiState.update { it.copy(secondsInput = 59) }
                }
            }
        }
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

        timerJob?.cancel()
        val startTime = Clock.System.now()
        val endTime = startTime.plus(duration)

        _uiState.update {
            it.copy(
                totalTimeSeconds = duration.inWholeSeconds,
                timeRemainingSeconds = duration.inWholeSeconds,
                isRunning = true,
                isPaused = false
            )
        }

        timerJob = viewModelScope.launch {
            while (Clock.System.now() < endTime) {
                val remaining = (endTime - Clock.System.now()).inWholeSeconds
                _uiState.update { it.copy(timeRemainingSeconds = remaining) }
                delay(1000)
            }
            _uiState.update { it.copy(timeRemainingSeconds = 0, isRunning = false) }
            saveSession(completed = true)
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                isRunning = false,
                isPaused = true
            )
        }
    }

    fun stopTimer() {
        saveSession(completed = false)
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false, isPaused = false, timeRemainingSeconds = 0) }
    }

    fun onExit(onExitConfirmed: () -> Unit) {
        if (_uiState.value.isRunning || _uiState.value.isPaused) {
            saveSession(completed = false)
        }
        timerJob?.cancel()
        onExitConfirmed()
    }

    private fun saveSession(completed: Boolean) {
        val startTime = sessionStartTime ?: return
        val totalSeconds = initialDurationSeconds
        val remainingSeconds = _uiState.value.timeRemainingSeconds
        val elapsedSeconds = totalSeconds - remainingSeconds
        val durationMinutes = (elapsedSeconds / 60).toInt()

        viewModelScope.launch {
            focusSessionRepository.insertSession(
                FocusSessionEntity(
                    startTime = startTime,
                    durationMinutes = durationMinutes,
                    completed = completed
                )
            )
        }
        
        sessionStartTime = null
        initialDurationSeconds = 0
    }

    override fun onCleared() {
        super.onCleared()
        if (_uiState.value.isRunning || _uiState.value.isPaused) {
            saveSession(completed = false)
        }
        timerJob?.cancel()
    }
}

data class FocusModeUiState(
    val timeRemainingSeconds: Long = 0,
    val totalTimeSeconds: Long = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val hoursInput: Int = 0,
    val minutesInput: Int = 30,
    val secondsInput: Int = 0
) {
    val progress: Float
        get() = if (totalTimeSeconds > 0L) timeRemainingSeconds.toFloat() / totalTimeSeconds.toFloat() else 0f
    
    val formattedTime: String
        get() {
            val h = timeRemainingSeconds / 3600
            val m = (timeRemainingSeconds % 3600) / 60
            val s = timeRemainingSeconds % 60
            return if (h > 0) {
                "%02d:%02d:%02d".format(h, m, s)
            } else {
                "%02d:%02d".format(m, s)
            }
        }
}
