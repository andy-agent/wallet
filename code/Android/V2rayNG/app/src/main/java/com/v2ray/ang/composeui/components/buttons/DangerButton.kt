package com.v2ray.ang.composeui.components.buttons

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.v2ray.ang.composeui.theme.Error

enum class DangerVariant {
    FILLED,
    OUTLINED,
    TEXT,
}

@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    variant: DangerVariant = DangerVariant.FILLED,
) {
    val colors = ButtonDefaults.buttonColors(containerColor = Error)
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier,
        colors = colors,
    ) {
        if (loading) {
            CircularProgressIndicator()
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}
