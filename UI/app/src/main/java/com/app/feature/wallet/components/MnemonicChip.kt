package com.app.feature.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.theme.BluePrimary

@Composable
fun MnemonicChip(word: String, selected: Boolean, onClick: (() -> Unit)? = null) {
    Text(
        word,
        modifier = Modifier
            .background(if (selected) BluePrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        style = MaterialTheme.typography.bodyMedium,
    )
}
