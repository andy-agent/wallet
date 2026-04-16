package com.v2ray.ang.composeui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.theme.AppTheme

enum class AppCardVariant {
    Default,
    Elevated,
    Highlight,
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    variant: AppCardVariant = AppCardVariant.Default,
    contentPadding: androidx.compose.ui.unit.Dp = AppTheme.spacing.cardPadding,
    shape: Shape = RoundedCornerShape(AppTheme.shapes.radiusLg),
    content: @Composable ColumnScope.() -> Unit,
) {
    val containerColor = when (variant) {
        AppCardVariant.Default -> AppTheme.colors.surfaceCard
        AppCardVariant.Elevated -> AppTheme.colors.surfaceElevated
        AppCardVariant.Highlight -> AppTheme.colors.surfaceCard
    }
    val shadowElevation = when (variant) {
        AppCardVariant.Default -> AppTheme.elevation.card
        AppCardVariant.Elevated -> AppTheme.elevation.floating
        AppCardVariant.Highlight -> AppTheme.elevation.hero
    }
    val overlay = when (variant) {
        AppCardVariant.Highlight -> AppTheme.gradients.cardGlowGradient
        else -> Brush.verticalGradient(listOf(containerColor, containerColor))
    }
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = shape,
        border = BorderStroke(1.dp, AppTheme.colors.dividerSubtle),
        tonalElevation = 0.dp,
        shadowElevation = shadowElevation,
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(overlay),
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content,
            )
        }
    }
}
