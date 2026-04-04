package com.v2ray.ang.composeui.components.buttons

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

enum class IconButtonSize {
    LARGE,
    MEDIUM,
    SMALL,
    XSMALL,
}

enum class IconButtonVariant {
    FILLED,
    OUTLINED,
    GHOST,
    GLASS,
}

@Composable
fun IconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: IconButtonSize = IconButtonSize.MEDIUM,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: IconButtonVariant = IconButtonVariant.GHOST,
    contentDescription: String? = null,
) {
    if (loading) {
        CircularProgressIndicator(modifier = modifier)
        return
    }
    androidx.compose.material3.IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(
        icon = androidx.compose.material.icons.Icons.Default.ArrowBack,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    )
}

@Composable
fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(
        icon = androidx.compose.material.icons.Icons.Default.Close,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    )
}
