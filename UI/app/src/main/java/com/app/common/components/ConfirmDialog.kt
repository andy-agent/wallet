package com.app.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
    visible: Boolean,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { PrimaryButton(text = "确认", onClick = onConfirm) },
        dismissButton = { SecondaryButton(text = "取消", onClick = onDismiss) },
        title = { Text(title) },
        text = { Text(message) },
    )
}
