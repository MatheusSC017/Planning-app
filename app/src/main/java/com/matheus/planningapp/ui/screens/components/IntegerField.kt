package com.matheus.planningapp.ui.screens.components

import android.view.MotionEvent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.matheus.planningapp.ui.theme.PageDesignSettings
import com.matheus.planningapp.ui.theme.strings.LocalStrings
import com.matheus.planningapp.ui.theme.strings.StringsRepository
import kotlinx.coroutines.delay

@Composable
fun IntegerField(
    selectedValue: Int,
    onIntegerValueChange: (Int) -> Unit,
    minValue: Int,
    maxValue: Int,
) {
    val strings: StringsRepository = LocalStrings.current
    val delayInMilliseconds = 100L

    var increaseButtonPressed by remember { mutableStateOf(false) }
    var decreaseButtonPressed by remember { mutableStateOf(false) }

    LaunchedEffect(increaseButtonPressed, selectedValue) {
        while (increaseButtonPressed) {
            delay(delayInMilliseconds)
            if (selectedValue < maxValue) onIntegerValueChange(selectedValue + 1)
        }
    }

    LaunchedEffect(decreaseButtonPressed, selectedValue) {
        while (decreaseButtonPressed) {
            delay(delayInMilliseconds)
            if (selectedValue > minValue) onIntegerValueChange(selectedValue - 1)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = selectedValue.toString(),
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = strings.recurrenceValueField,
                    style =
                        TextStyle(
                            fontSize = PageDesignSettings.smallText,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                )
            },
            textStyle =
                TextStyle(
                    fontSize = PageDesignSettings.mediumText,
                    color = MaterialTheme.colorScheme.secondary,
                ),
            modifier =
                Modifier
                    .weight(1f)
                    .height(PageDesignSettings.smallComponentSize),
            singleLine = true,
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
