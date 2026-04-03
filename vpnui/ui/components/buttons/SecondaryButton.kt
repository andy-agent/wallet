package com.cryptovpn.ui.components.buttons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
 * 次要按钮组件（边框按钮）
 * 
 * 用于次要操作，带边框样式
 * 
 * @param text 按钮文字
 * @param onClick 点击回调
 * @param modifier 修饰符
 * @param size 按钮尺寸
 * @param enabled 是否可用
 * @param loading 是否加载中
 * @param icon 左侧图标（可选）
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.LARGE,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
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
    
    val borderColor = when {
        !enabled -> BorderPrimary.copy(alpha = 0.5f)
        isPressed -> Primary
        else -> BorderPrimary
    }
    
    val backgroundColor = when {
        isPressed && enabled -> Primary.copy(alpha = 0.1f)
        else -> Color.Transparent
    }
    
    val textColor = when {
        !enabled -> TextDisabled
        isPressed -> Primary
        else -> TextPrimary
    }
    
    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .scale(scale)
            .border(
                border = BorderStroke(1.dp, borderColor),
                shape = AppShape.ButtonLarge
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
                color = Primary,
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

@Preview(name = "Secondary Button - All States")
@Composable
fun SecondaryButtonPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryButton(
                text = "Secondary Button",
                onClick = {}
            )
            
            SecondaryButton(
                text = "Medium Secondary",
                onClick = {},
                size = ButtonSize.MEDIUM
            )
            
            SecondaryButton(
                text = "Disabled Secondary",
                onClick = {},
                enabled = false
            )
            
            SecondaryButton(
                text = "Loading Secondary",
                onClick = {},
                loading = true
            )
        }
    }
}
