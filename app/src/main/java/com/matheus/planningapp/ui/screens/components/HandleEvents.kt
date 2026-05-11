package com.matheus.planningapp.ui.screens.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.matheus.planningapp.util.DatabaseUiEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun <T : DatabaseUiEvent> HandleEvents(
    events: Flow<T>,
    handler: suspend (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(events) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            events.collect(handler)
        }
    }
}
