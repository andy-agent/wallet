package com.cryptovpn.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.DividerLight
import com.cryptovpn.ui.theme.ElectricBlue
import com.cryptovpn.ui.theme.LayerWhite
import com.cryptovpn.ui.theme.TextMuted

@Composable
fun GlassTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        label = { Text(label) },
        shape = RoundedCornerShape(22.dp),
        trailingIcon = trailing,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = LayerWhite.copy(alpha = 0.90f),
            unfocusedContainerColor = LayerWhite.copy(alpha = 0.82f),
            focusedBorderColor = ElectricBlue.copy(alpha = 0.45f),
            unfocusedBorderColor = DividerLight,
            focusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = ElectricBlue,
            unfocusedLabelColor = TextMuted,
        ),
    )
}
