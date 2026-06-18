package com.matheus.planningapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

@Composable
fun Modifier.stardardBackground() = this.background(
    Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f),
            MaterialTheme.colorScheme.background,
        ),
        start = Offset.Zero,
        end = Offset.Infinite,
    )
)
