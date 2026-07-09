package com.matheus.planningapp.viewmodel.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class FocusModeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FocusModeUiState())
    private var timerDuration = 30.minutes
    val uiState: StateFlow<FocusModeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun setTimerDuration(duration: Duration) {
        timerDuration = duration
    }

    fun startTimer() {
        timerJob?.cancel()
        val startTime = Clock.System.now()
        val endTime = startTime.plus(timerDuration)
        
        _uiState.update { 
            it.copy(
                totalTimeSeconds = timerDuration.inWholeSeconds,
                timeRemainingSeconds = timerDuration.inWholeSeconds,
                isRunning = true
            ) 
        }

        timerJob = viewModelScope.launch {
            while (Clock.System.now() < endTime) {
                val remaining = (endTime - Clock.System.now()).inWholeSeconds
                _uiState.update { it.copy(timeRemainingSeconds = remaining) }
                delay(1000)
            }
            _uiState.update { it.copy(timeRemainingSeconds = 0, isRunning = false) }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

data class FocusModeUiState(
    val timeRemainingSeconds: Long = 0,
    val totalTimeSeconds: Long = 0,
    val isRunning: Boolean = false
) {
    val progress: Float
        get() = if (totalTimeSeconds > 0L) timeRemainingSeconds.toFloat() / totalTimeSeconds.toFloat() else 0f
    
    val formattedTime: String
        get() {
            val minutes = timeRemainingSeconds / 60
            val seconds = timeRemainingSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
}
