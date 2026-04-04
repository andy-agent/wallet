package com.v2ray.ang.composeui.effects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Glow effect components skeleton
 * Full implementation will be added in subsequent tasks
 */

@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Skeleton implementation
    content()
}

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Skeleton implementation
}
