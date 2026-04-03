package com.cryptovpn.ui.components.buttons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*

/**
 * 危险按钮组件
 * 
 * 用于危险操作，如删除、退出等
 * 
 * @param text 按钮文字
 * @param onClick 点击回调
 * @param modifier 修饰符
 * @param size 按钮尺寸
 * @param enabled 是否可用
 * @param loading 是否加载中
 * @param icon 图标（可选）
 * @param variant 变体样式（Filled/Outlined/Text）
 */
enum class DangerVariant {
    FILLED, OUTLINED, TEXT
}

@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.LARGE,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    variant: DangerVariant = DangerVariant.FILLED
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.98f else 1f,
        label = "button_scale"
    )
    
    val (height, textStyle, iconSize) = when (size) {
        ButtonSize.LARGE -> Triple(
            Dimens.ButtonHeightLarge,
            AppTypography.LabelLarge,
            Dimens.IconSizeMedium
        )
        ButtonSize.MEDIUM -> Triple(
            Dimens.ButtonHeightMedium,
            AppTypography.LabelMedium,
            Dimens.IconSizeSmall
        )
        ButtonSize.SMALL -> Triple(
            Dimens.ButtonHeightSmall,
            AppTypography.LabelSmall,
            Dimens.IconSizeXSmall
        )
    }
    
    val (backgroundColor, textColor, borderColor) = when (variant) {
        DangerVariant.FILLED -> Triple(
            when {
                !enabled -> Error.copy(alpha = 0.5f)
                isPressed -> Error.copy(alpha = 0.8f)
                else -> Error
            },
            TextPrimary,
            Color.Transparent
        )
        DangerVariant.OUTLINED -> Triple(
            when {
                isPressed && enabled -> Error.copy(alpha = 0.1f)
                else -> Color.Transparent
            },
            when {
                !enabled -> Error.copy(alpha = 0.5f)
                else -> Error
            },
            when {
                !enabled -> Error.copy(alpha = 0.3f)
                else -> Error
            }
        )
        DangerVariant.TEXT -> Triple(
            when {
                isPressed && enabled -> Error.copy(alpha = 0.1f)
                else -> Color.Transparent
            },
            when {
                !enabled -> Error.copy(alpha = 0.5f)
                else -> Error
            },
            Color.Transparent
        )
    }
    
    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (variant == DangerVariant.OUTLINED) {
                    Modifier.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = AppShape.ButtonLarge
                    )
                } else Modifier
            )
            .background(
                color = backgroundColor,
                shape = AppShape.ButtonLarge
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
                modifier = Modifier.size(iconSize),
                color = textColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = textColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Text(
                    text = text,
                    style = textStyle,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(name = "Danger Button - All Variants")
@Composable
fun DangerButtonPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Filled variant
            DangerButton(
                text = "Delete Account",
                onClick = {},
                variant = DangerVariant.FILLED
            )
            
            // Outlined variant
            DangerButton(
                text = "Remove Item",
                onClick = {},
                variant = DangerVariant.OUTLINED
            )
            
            // Text variant
            DangerButton(
                text = "Cancel Subscription",
                onClick = {},
                variant = DangerVariant.TEXT
            )
            
            // Disabled
            DangerButton(
                text = "Disabled Danger",
                onClick = {},
                enabled = false
            )
            
            // Loading
            DangerButton(
                text = "Deleting...",
                onClick = {},
                loading = true
            )
        }
    }
}
