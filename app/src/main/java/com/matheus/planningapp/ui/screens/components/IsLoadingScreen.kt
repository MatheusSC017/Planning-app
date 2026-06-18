package com.matheus.planningapp.ui.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun IsLoadingScreen(
    paddingValues: PaddingValues
){
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .stardardBackground(),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}
