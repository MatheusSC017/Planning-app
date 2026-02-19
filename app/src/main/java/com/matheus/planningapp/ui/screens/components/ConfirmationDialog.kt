package com.matheus.planningapp.ui.screens.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun <T> ConfirmationDialog(
    item: T?,
    showDialog: Boolean,
    title: String,
    message: String,
    onConfirm: (T) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (item == null) return

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(item)
                    onDismissRequest()
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) { Text("Cancel") }
            }
        )
    }
}
