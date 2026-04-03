package com.cryptovpn.ui.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*

/**
 * 文字按钮组件
 * 
 * 用于低优先级操作或链接
 * 
 * @param text 按钮文字
 * @param onClick 点击回调
 * @param modifier 修饰符
 * @param enabled 是否可用
 * @param icon 图标（可选）
 * @param iconPosition 图标位置
 * @param textStyle 文字样式
 * @param color 文字颜色
 */
@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.LEADING,
    textStyle: TextStyle = AppTypography.LabelMedium,
    color: Color = Primary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val textColor = when {
        !enabled -> TextDisabled
        isPressed -> color.copy(alpha = 0.8f)
        else -> color
    }
    
    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null && iconPosition == IconPosition.LEADING) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        
        Text(
            text = text,
            style = textStyle,
            color = textColor
        )
        
        if (icon != null && iconPosition == IconPosition.TRAILING) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = textColor
            )
        }
    }
}

/**
 * 链接样式按钮
 */
@Composable
fun LinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        textStyle = AppTypography.Body,
        color = Primary
    )
}

@Preview(name = "Text Button - All States")
@Composable
fun TextButtonPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Default text button
            TextButton(
                text = "Text Button",
                onClick = {}
            )
            
            // With leading icon
            TextButton(
                text = "With Icon",
                onClick = {}
            )
            
            // Link style
            LinkButton(
                text = "Learn More",
                onClick = {}
            )
            
            // Disabled
            TextButton(
                text = "Disabled",
                onClick = {},
                enabled = false
            )
        }
    }
}
