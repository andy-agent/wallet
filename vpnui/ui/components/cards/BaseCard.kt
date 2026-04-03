package com.cryptovpn.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*

/**
 * 基础卡片组件
 * 
 * 所有卡片的基类，提供统一的样式
 * 
 * @param modifier 修饰符
 * @param onClick 点击回调（可选）
 * @param enabled 是否可点击
 * @param padding 内边距
 * @param backgroundColor 背景色
 * @param borderColor 边框色
 * @param borderWidth 边框宽度
 * @param content 内容
 */
@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    padding: Dp = Dimens.Space16,
    backgroundColor: Color = BackgroundSecondary,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(enabled = enabled, onClick = onClick)
    } else Modifier
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShape.Card)
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(
                        width = borderWidth,
                        color = borderColor,
                        shape = AppShape.Card
                    )
                } else Modifier
            )
            .background(
                color = backgroundColor,
                shape = AppShape.Card
            )
            .then(clickableModifier)
            .padding(padding),
        content = content
    )
}

/**
 * 可展开卡片
 */
@Composable
fun ExpandableCard(
    title: @Composable () -> Unit,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BaseCard(
        modifier = modifier,
        onClick = { onExpandChange(!expanded) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            title()
            androidx.compose.material3.Icon(
                imageVector = if (expanded) {
                    androidx.compose.material.icons.Icons.Default.ExpandLess
                } else {
                    androidx.compose.material.icons.Icons.Default.ExpandMore
                },
                contentDescription = if (expanded) "收起" else "展开",
                tint = TextSecondary
            )
        }
        
        androidx.compose.animation.AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }
}

@Preview(name = "Base Card")
@Composable
fun BaseCardPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Basic card
            BaseCard {
                Text(
                    text = "基础卡片",
                    style = AppTypography.H4,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "这是卡片内容",
                    style = AppTypography.Body,
                    color = TextSecondary
                )
            }
            
            // Clickable card
            BaseCard(
                onClick = {},
                borderColor = Primary,
                borderWidth = 1.dp
            ) {
                Text(
                    text = "可点击卡片",
                    style = AppTypography.H4,
                    color = TextPrimary
                )
            }
            
            // Expandable card
            var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
            ExpandableCard(
                title = {
                    Text(
                        text = "可展开卡片",
                        style = AppTypography.H4,
                        color = TextPrimary
                    )
                },
                expanded = expanded,
                onExpandChange = { expanded = it }
            ) {
                Text(
                    text = "这是展开的详细内容",
                    style = AppTypography.Body,
                    color = TextSecondary
                )
            }
        }
    }
}
