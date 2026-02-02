package com.matheus.planningapp.view.components

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
    onDismiss: () -> Unit
) {
    if (item == null) return

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(item)
                    onDismiss()
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    }
}
