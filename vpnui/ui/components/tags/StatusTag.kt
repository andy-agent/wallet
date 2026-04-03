package com.cryptovpn.ui.components.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*

/**
 * 状态标签组件
 * 
 * 用于展示各种状态
 * 
 * @param text 标签文字
 * @param type 状态类型
 * @param modifier 修饰符
 * @param icon 图标（可选）
 * @param size 尺寸
 */

enum class StatusType {
    ACTIVE,      // 活跃/已连接
    INACTIVE,    // 未活跃
    PENDING,     // 待处理
    PROCESSING,  // 处理中
    COMPLETED,   // 已完成
    FAILED,      // 失败
    EXPIRED,     // 已过期
    CANCELLED,   // 已取消
    WARNING,     // 警告
    INFO         // 信息
}

enum class TagSize {
    SMALL, MEDIUM, LARGE
}

@Composable
fun StatusTag(
    text: String,
    type: StatusType,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    size: TagSize = TagSize.MEDIUM
) {
    val (backgroundColor, textColor) = when (type) {
        StatusType.ACTIVE -> SuccessLight to Success
        StatusType.INACTIVE -> BackgroundTertiary to TextTertiary
        StatusType.PENDING -> WarningLight to Warning
        StatusType.PROCESSING -> InfoLight to Info
        StatusType.COMPLETED -> SuccessLight to Success
        StatusType.FAILED -> ErrorLight to Error
        StatusType.EXPIRED -> BackgroundTertiary to TextTertiary
        StatusType.CANCELLED -> BackgroundTertiary to TextTertiary
        StatusType.WARNING -> WarningLight to Warning
        StatusType.INFO -> InfoLight to Info
    }
    
    val (horizontalPadding, verticalPadding, textStyle) = when (size) {
        TagSize.SMALL -> Triple(6.dp, 2.dp, AppTypography.LabelSmall)
        TagSize.MEDIUM -> Triple(10.dp, 4.dp, AppTypography.LabelMedium)
        TagSize.LARGE -> Triple(14.dp, 6.dp, AppTypography.LabelLarge)
    }
    
    val iconSize = when (size) {
        TagSize.SMALL -> 12.dp
        TagSize.MEDIUM -> 14.dp
        TagSize.LARGE -> 16.dp
    }
    
    Row(
        modifier = modifier
            .clip(AppShape.Tag)
            .background(backgroundColor)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
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
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            style = textStyle,
            color = textColor
        )
    }
}

/**
 * VPN连接状态标签
 */
@Composable
fun VpnStatusTag(
    isConnected: Boolean,
    serverName: String? = null,
    modifier: Modifier = Modifier
) {
    val text = when {
        isConnected && serverName != null -> "已连接 · $serverName"
        isConnected -> "已连接"
        else -> "未连接"
    }
    
    StatusTag(
        text = text,
        type = if (isConnected) StatusType.ACTIVE else StatusType.INACTIVE,
        modifier = modifier,
        icon = if (isConnected) {
            androidx.compose.material.icons.Icons.Default.CheckCircle
        } else null
    )
}

/**
 * 订单状态标签
 */
@Composable
fun OrderStatusTag(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (text, type) = when (status) {
        OrderStatus.PENDING -> "待支付" to StatusType.PENDING
        OrderStatus.PAID -> "已支付" to StatusType.PROCESSING
        OrderStatus.COMPLETED -> "已完成" to StatusType.COMPLETED
        OrderStatus.FAILED -> "失败" to StatusType.FAILED
        OrderStatus.CANCELLED -> "已取消" to StatusType.CANCELLED
        OrderStatus.REFUNDED -> "已退款" to StatusType.INFO
    }
    
    StatusTag(
        text = text,
        type = type,
        modifier = modifier
    )
}

enum class OrderStatus {
    PENDING, PAID, COMPLETED, FAILED, CANCELLED, REFUNDED
}

/**
 * 胶囊标签（用于筛选等）
 */
@Composable
fun PillTag(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(AppShape.TagPill)
            .background(
                if (isSelected) Primary else BackgroundSecondary
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.LabelMedium,
            color = if (isSelected) TextPrimary else TextSecondary
        )
    }
}

@Preview(name = "Status Tag")
@Composable
fun StatusTagPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // All status types
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusTag(text = "活跃", type = StatusType.ACTIVE)
                StatusTag(text = "待处理", type = StatusType.PENDING)
                StatusTag(text = "处理中", type = StatusType.PROCESSING)
                StatusTag(text = "已完成", type = StatusType.COMPLETED)
                StatusTag(text = "失败", type = StatusType.FAILED)
                StatusTag(text = "已过期", type = StatusType.EXPIRED)
                StatusTag(text = "警告", type = StatusType.WARNING)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // VPN status
            VpnStatusTag(isConnected = true, serverName = "美国-纽约")
            VpnStatusTag(isConnected = false)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Order status
            OrderStatusTag(status = OrderStatus.PENDING)
            OrderStatusTag(status = OrderStatus.COMPLETED)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Pill tags
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillTag(text = "全部", isSelected = true, onClick = {})
                PillTag(text = "充值", isSelected = false, onClick = {})
                PillTag(text = "提现", isSelected = false, onClick = {})
            }
        }
    }
}

// 简单的FlowRow实现
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val hGapPx = 8.dp.roundToPx()
        val vGapPx = 8.dp.roundToPx()
        
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        val rowWidths = mutableListOf<Int>()
        val rowHeights = mutableListOf<Int>()
        
        var rowMeasurables = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var rowWidth = 0
        var rowHeight = 0
        
        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints)
            
            if (rowWidth + placeable.width + if (rowMeasurables.isNotEmpty()) hGapPx else 0 > constraints.maxWidth) {
                rows.add(rowMeasurables)
                rowWidths.add(rowWidth)
                rowHeights.add(rowHeight)
                
                rowMeasurables = mutableListOf()
                rowWidth = 0
                rowHeight = 0
            }
            
            rowMeasurables.add(placeable)
            rowWidth += placeable.width + if (rowMeasurables.size > 1) hGapPx else 0
            rowHeight = maxOf(rowHeight, placeable.height)
        }
        
        if (rowMeasurables.isNotEmpty()) {
            rows.add(rowMeasurables)
            rowWidths.add(rowWidth)
            rowHeights.add(rowHeight)
        }
        
        val width = constraints.maxWidth
        val height = rowHeights.sum() + (rowHeights.size - 1).coerceAtLeast(0) * vGapPx
        
        layout(width, height) {
            var y = 0
            rows.forEachIndexed { rowIndex, row ->
                var x = when (horizontalArrangement) {
                    Arrangement.Start, Arrangement.SpaceBetween -> 0
                    Arrangement.End -> width - rowWidths[rowIndex]
                    Arrangement.Center -> (width - rowWidths[rowIndex]) / 2
                    else -> 0
                }
                
                row.forEachIndexed { index, placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + hGapPx
                }
                
                y += rowHeights[rowIndex] + vGapPx
            }
        }
    }
}
