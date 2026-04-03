package com.cryptovpn.ui.components.buttons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*

/**
 * 图标按钮组件
 * 
 * 支持多种尺寸和样式变体
 * 
 * @param icon 图标
 * @param onClick 点击回调
 * @param modifier 修饰符
 * @param size 按钮尺寸
 * @param enabled 是否可用
 * @param loading 是否加载中
 * @param variant 变体样式
 * @param contentDescription 无障碍描述
 */

enum class IconButtonSize {
    LARGE, MEDIUM, SMALL, XSMALL
}

enum class IconButtonVariant {
    FILLED, OUTLINED, GHOST, GLASS
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
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.92f else 1f,
        label = "icon_button_scale"
    )
    
    val (containerSize, iconSize) = when (size) {
        IconButtonSize.LARGE -> Pair(56.dp, 28.dp)
        IconButtonSize.MEDIUM -> Pair(44.dp, 24.dp)
        IconButtonSize.SMALL -> Pair(36.dp, 20.dp)
        IconButtonSize.XSMALL -> Pair(28.dp, 16.dp)
    }
    
    val (backgroundColor, iconColor, borderColor) = when (variant) {
        IconButtonVariant.FILLED -> Triple(
            when {
                !enabled -> Primary.copy(alpha = 0.3f)
                isPressed -> PrimaryPressed
                else -> Primary
            },
            TextPrimary,
            Color.Transparent
        )
        IconButtonVariant.OUTLINED -> Triple(
            when {
                isPressed && enabled -> Primary.copy(alpha = 0.1f)
                else -> Color.Transparent
            },
            when {
                !enabled -> TextDisabled
                else -> Primary
            },
            when {
                !enabled -> BorderPrimary.copy(alpha = 0.3f)
                else -> BorderPrimary
            }
        )
        IconButtonVariant.GHOST -> Triple(
            when {
                isPressed && enabled -> BackgroundTertiary
                else -> Color.Transparent
            },
            when {
                !enabled -> TextDisabled
                else -> TextPrimary
            },
            Color.Transparent
        )
        IconButtonVariant.GLASS -> Triple(
            when {
                isPressed && enabled -> BackgroundSecondary.copy(alpha = 0.8f)
                else -> BackgroundSecondary.copy(alpha = 0.5f)
            },
            when {
                !enabled -> TextDisabled
                else -> TextPrimary
            },
            Color.Transparent
        )
    }
    
    Box(
        modifier = modifier
            .size(containerSize)
            .scale(scale)
            .then(
                if (variant == IconButtonVariant.OUTLINED) {
                    Modifier.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = AppShape.Avatar
                    )
                } else Modifier
            )
            .background(
                color = backgroundColor,
                shape = AppShape.Avatar
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(iconSize * 0.8f),
                color = iconColor,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                tint = iconColor
            )
        }
    }
}

/**
 * 返回按钮（专用组件）
 */
@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        icon = androidx.compose.material.icons.Icons.Default.ArrowBack,
        onClick = onClick,
        modifier = modifier,
        size = IconButtonSize.MEDIUM,
        enabled = enabled,
        variant = IconButtonVariant.GHOST,
        contentDescription = "返回"
    )
}

/**
 * 关闭按钮（专用组件）
 */
@Composable
fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        icon = androidx.compose.material.icons.Icons.Default.Close,
        onClick = onClick,
        modifier = modifier,
        size = IconButtonSize.MEDIUM,
        enabled = enabled,
        variant = IconButtonVariant.GHOST,
        contentDescription = "关闭"
    )
}

@Preview(name = "Icon Button - All Variants")
@Composable
fun IconButtonPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Filled
                IconButton(
                    icon = androidx.compose.material.icons.Icons.Default.Add,
                    onClick = {},
                    variant = IconButtonVariant.FILLED
                )
                
                // Outlined
                IconButton(
                    icon = androidx.compose.material.icons.Icons.Default.Edit,
                    onClick = {},
                    variant = IconButtonVariant.OUTLINED
                )
                
                // Ghost
                IconButton(
                    icon = androidx.compose.material.icons.Icons.Default.Settings,
                    onClick = {},
                    variant = IconButtonVariant.GHOST
                )
                
                // Glass
                IconButton(
                    icon = androidx.compose.material.icons.Icons.Default.MoreVert,
                    onClick = {},
                    variant = IconButtonVariant.GLASS
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Different sizes
                IconButton(
                    icon = androidx.compose.material.icons.Icons.Default.Home,
                    onClick = {},
                    size = IconButtonSize.LARGE
                )
                IconButton(
                    icon = androidx.compose.material.icons.Icons.Default.Home,
                    onClick = {},
                    size = IconButtonSize.MEDIUM
                )
                IconButton(
                    icon = androidx.compose.material.icons.Icons.Default.Home,
                    onClick = {},
                    size = IconButtonSize.SMALL
                )
                IconButton(
                    icon = androidx.compose.material.icons.Icons.Default.Home,
                    onClick = {},
                    size = IconButtonSize.XSMALL
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Loading
                IconButton(
                    icon = androidx.compose.material.icons.Icons.Default.Refresh,
                    onClick = {},
                    loading = true
                )
                
                // Disabled
                IconButton(
                    icon = androidx.compose.material.icons.Icons.Default.Delete,
                    onClick = {},
                    enabled = false
                )
            }
            
            // Back button
            BackButton(onClick = {})
            
            // Close button
            CloseButton(onClick = {})
        }
    }
}
