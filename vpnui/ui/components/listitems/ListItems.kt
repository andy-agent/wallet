package com.cryptovpn.ui.components.listitems

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.components.tags.StatusTag
import com.cryptovpn.ui.components.tags.StatusType
import com.cryptovpn.ui.theme.*

/**
 * 区域列表项组件
 * 
 * 用于展示VPN服务器区域
 * 
 * @param regionName 区域名称
 * @param flagEmoji 国旗emoji
 * @param serverCount 服务器数量
 * @param latency 延迟（ms）
 * @param isSelected 是否选中
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
fun RegionListItem(
    regionName: String,
    flagEmoji: String,
    serverCount: Int,
    latency: Int? = null,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val latencyColor = when {
        latency == null -> TextTertiary
        latency < 100 -> Success
        latency < 200 -> Warning
        else -> Error
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShape.ListItem)
            .background(if (isSelected) Primary.copy(alpha = 0.1f) else BackgroundSecondary)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flag
        Text(
            text = flagEmoji,
            style = AppTypography.H3,
            modifier = Modifier.width(40.dp)
        )
        
        // Region info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = regionName,
                style = AppTypography.Body,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$serverCount 个服务器",
                style = AppTypography.BodySmall,
                color = TextSecondary
            )
        }
        
        // Latency
        if (latency != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(latencyColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${latency}ms",
                    style = AppTypography.BodySmall,
                    color = latencyColor
                )
            }
        }
        
        // Selected indicator
        if (isSelected) {
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "已选择",
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 订单列表项组件
 * 
 * @param orderId 订单ID
 * @param planName 套餐名称
 * @param amount 金额
 * @param date 日期
 * @param status 状态
 * @param statusType 状态类型
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
fun OrderListItem(
    orderId: String,
    planName: String,
    amount: String,
    date: String,
    status: String,
    statusType: StatusType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShape.ListItem)
            .background(BackgroundSecondary)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Order icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = Primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Order info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = planName,
                style = AppTypography.Body,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = "订单号: ${orderId.takeLast(8)}",
                    style = AppTypography.BodySmall,
                    color = TextTertiary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = date,
                    style = AppTypography.BodySmall,
                    color = TextTertiary
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Amount and status
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = amount,
                style = AppTypography.NumberSmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            StatusTag(
                text = status,
                type = statusType,
                size = com.cryptovpn.ui.components.tags.TagSize.SMALL
            )
        }
    }
}

/**
 * 佣金列表项组件
 * 
 * @param username 用户名
 * @param level 级别
 * @param commission 佣金金额
 * @param date 日期
 * @param avatarUrl 头像URL（可选）
 * @param modifier 修饰符
 */
@Composable
fun CommissionListItem(
    username: String,
    level: String,
    commission: String,
    date: String,
    avatarUrl: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Primary.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = username.take(1).uppercase(),
                style = AppTypography.LabelLarge,
                color = Primary
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // User info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = username,
                style = AppTypography.Body,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = level,
                    style = AppTypography.BodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = date,
                    style = AppTypography.BodySmall,
                    color = TextTertiary
                )
            }
        }
        
        // Commission
        Text(
            text = "+$commission",
            style = AppTypography.NumberSmall,
            color = Success
        )
    }
}

/**
 * 文档列表项组件
 * 
 * @param title 标题
 * @param description 描述
 * @param date 日期
 * @param icon 图标
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
fun DocumentListItem(
    title: String,
    description: String? = null,
    date: String? = null,
    icon: ImageVector = Icons.Default.Description,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShape.ListItem)
            .background(BackgroundSecondary)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = Info.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Info,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Document info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = AppTypography.Body,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (description != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = AppTypography.BodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (date != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = date,
                    style = AppTypography.BodySmall,
                    color = TextTertiary
                )
            }
        }
        
        // Arrow
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * 设置列表项组件
 * 
 * @param title 标题
 * @param icon 图标
 * @param value 值（可选）
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
fun SettingsListItem(
    title: String,
    icon: ImageVector,
    value: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = TextSecondary
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            style = AppTypography.Body,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        
        if (value != null) {
            Text(
                text = value,
                style = AppTypography.Body,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(name = "List Items")
@Composable
fun ListItemsPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Region list item
            RegionListItem(
                regionName = "美国 - 纽约",
                flagEmoji = "🇺🇸",
                serverCount = 5,
                latency = 85,
                isSelected = true,
                onClick = {}
            )
            
            RegionListItem(
                regionName = "日本 - 东京",
                flagEmoji = "🇯🇵",
                serverCount = 3,
                latency = 156,
                isSelected = false,
                onClick = {}
            )
            
            RegionListItem(
                regionName = "新加坡",
                flagEmoji = "🇸🇬",
                serverCount = 4,
                latency = null,
                isSelected = false,
                onClick = {}
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Order list item
            OrderListItem(
                orderId = "ORD202412010001",
                planName = "年度会员",
                amount = "$59.99",
                date = "2024-12-01",
                status = "已完成",
                statusType = StatusType.COMPLETED,
                onClick = {}
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Commission list item
            CommissionListItem(
                username = "张三",
                level = "一级邀请",
                commission = "$5.00",
                date = "2024-12-01"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Document list item
            DocumentListItem(
                title = "用户服务协议",
                description = "请仔细阅读我们的服务条款",
                date = "更新于 2024-01-01",
                onClick = {}
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Settings list item
            SettingsListItem(
                title = "账号与安全",
                icon = Icons.Default.Security,
                value = "已绑定",
                onClick = {}
            )
            
            SettingsListItem(
                title = "通知设置",
                icon = Icons.Default.Notifications,
                onClick = {}
            )
        }
    }
}
