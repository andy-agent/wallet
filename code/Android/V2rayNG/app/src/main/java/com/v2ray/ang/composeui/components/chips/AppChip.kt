package com.v2ray.ang.composeui.components.chips

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.theme.AppTheme

enum class AppChipTone {
    Neutral,
    Brand,
    Success,
    Warning,
    Error,
    Info,
}

@Composable
fun AppChip(
    text: String,
    modifier: Modifier = Modifier,
    tone: AppChipTone = AppChipTone.Neutral,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colors = AppTheme.colors
    val (containerColor, contentColor, borderColor) = when (tone) {
        AppChipTone.Neutral -> Triple(
            if (selected) colors.bgSubtle else colors.surfaceElevated,
            colors.textSecondary,
            colors.dividerSubtle,
        )

        AppChipTone.Brand -> Triple(
            if (selected) colors.infoBg else colors.surfaceElevated,
            colors.brandPrimary,
            if (selected) colors.borderFocus else colors.dividerSubtle,
        )

        AppChipTone.Success -> Triple(colors.successBg, colors.success, colors.success.copy(alpha = 0.35f))
        AppChipTone.Warning -> Triple(colors.warningBg, colors.warning, colors.warning.copy(alpha = 0.35f))
        AppChipTone.Error -> Triple(colors.errorBg, colors.error, colors.error.copy(alpha = 0.35f))
        AppChipTone.Info -> Triple(colors.infoBg, colors.info, colors.info.copy(alpha = 0.35f))
    }

    Surface(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
        ),
        color = containerColor,
        shape = RoundedCornerShape(AppTheme.shapes.radiusPill),
        border = BorderStroke(1.dp, borderColor),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
