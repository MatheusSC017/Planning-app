package com.matheus.planningapp.ui.screens.components

import android.view.MotionEvent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes

@Composable
fun TimeStepperField(
    time: Instant,
    isEndTime: Boolean = false,
    onTimeChange: (Instant) -> Unit,
) {
    val strings: StringsRepository = LocalStrings.current
    val delayInMilliseconds = 50L
    val timeStepMinutes = 30.minutes
    val minutesPerHour = 60
    val hoursPerDay = 24

    var selectedTime by remember { mutableStateOf(time) }
    val localDateTime: LocalDateTime = selectedTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val currentDayOfMonth: Int = remember { localDateTime.dayOfMonth }
    val hourLimit: Int = if (isEndTime) 24 else 23
    val minuteLimit: Int = if (isEndTime) 30 else 0

    var increaseButtonPressed by remember { mutableStateOf(false) }
    var decreaseButtonPressed by remember { mutableStateOf(false) }

    LaunchedEffect(increaseButtonPressed, selectedTime) {
        val localDateTime: LocalDateTime = selectedTime.toLocalDateTime(TimeZone.currentSystemDefault())

        while (increaseButtonPressed) {
            delay(delayInMilliseconds)
            if ((!isEndTime && (localDateTime.hour < hourLimit || localDateTime.minute == 0)) ||
                (isEndTime && localDateTime.dayOfMonth == currentDayOfMonth)
            ) {
                onTimeChange(selectedTime + timeStepMinutes)
                selectedTime += timeStepMinutes
            }
        }
    }

    LaunchedEffect(decreaseButtonPressed, selectedTime) {
        val localDateTime: LocalDateTime = selectedTime.toLocalDateTime(TimeZone.currentSystemDefault())

        while (decreaseButtonPressed) {
            delay(delayInMilliseconds)
            if (((localDateTime.hour * minutesPerHour) + localDateTime.minute > minuteLimit) ||
                (localDateTime.dayOfMonth > currentDayOfMonth)
            ) {
                onTimeChange(selectedTime - timeStepMinutes)
                selectedTime -= timeStepMinutes
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value =
                strings.hourFormat.format(
                    if (localDateTime.dayOfMonth == currentDayOfMonth) localDateTime.hour else hoursPerDay,
                    localDateTime.minute,
                ),
            onValueChange = {},
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
            singleLine = true,
            textStyle =
                TextStyle(
                    fontSize = PageDesignSettings.largeText,
                    color = MaterialTheme.colorScheme.secondary,
                ),
            modifier = Modifier.weight(1f),
        )

        Column {
            IconButton(
                onClick = {},
                modifier =
                    Modifier.pointerInteropFilter {
                        increaseButtonPressed =
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> true
                                else -> false
                            }
                        true
                    },
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = strings.increaseButton)
            }

            IconButton(
                onClick = {},
                modifier =
                    Modifier.pointerInteropFilter {
                        decreaseButtonPressed =
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> true
                                else -> false
                            }
                        true
                    },
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = strings.decreaseButton)
            }
        }
    }
}
