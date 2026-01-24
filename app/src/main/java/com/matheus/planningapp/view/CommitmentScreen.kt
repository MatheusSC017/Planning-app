package com.matheus.planningapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.Instant

@Composable
fun CommitmentScreen(
    instant: Instant
) {
    Scaffold (
        content = { paddingValues ->
            CommitmentForm(
                modifier = Modifier
                    .padding(paddingValues)
            )
        }
    )
}

@Composable
fun CommitmentForm(
    modifier: Modifier
) {
    Column() { }
}
