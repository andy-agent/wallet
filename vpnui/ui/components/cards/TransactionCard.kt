package com.cryptovpn.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*

/**
 * 交易记录卡片
 * 
 * 用于展示交易记录
 * 
 * @param type 交易类型
 * @param amount 金额
 * @param currency 币种
 * @param time 时间
 * @param status 状态
 * @param statusColor 状态颜色
 * @param icon 图标
 * @param iconBackgroundColor 图标背景色
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
fun TransactionCard(
    type: String,
    amount: String,
    currency: String,
    time: String,
    status: String,
    statusColor: Color = TextSecondary,
    icon: ImageVector,
    iconBackgroundColor: Color = Primary.copy(alpha = 0.15f),
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShape.ListItem)
            .background(BackgroundSecondary)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = iconBackgroundColor,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when {
                    iconBackgroundColor == SuccessLight -> Success
                    iconBackgroundColor == ErrorLight -> Error
                    else -> Primary
                },
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = type,
                    style = AppTypography.Body,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = amount,
                    style = AppTypography.NumberSmall,
                    color = if (amount.startsWith("+")) Success else TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time,
                    style = AppTypography.BodySmall,
                    color = TextTertiary
                )
                Text(
                    text = status,
                    style = AppTypography.LabelSmall,
                    color = statusColor
                )
            }
        }
    }
}

/**
 * 交易记录列表项（更紧凑）
 */
@Composable
fun TransactionListItem(
    type: String,
    amount: String,
    time: String,
    isIncome: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Type indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isIncome) Success else Error)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = type,
                style = AppTypography.Body,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = time,
                style = AppTypography.BodySmall,
                color = TextTertiary
            )
        }
        
        Text(
            text = amount,
            style = AppTypography.NumberSmall,
            color = if (isIncome) Success else Error
        )
    }
}

/**
 * 交易详情卡片
 */
@Composable
fun TransactionDetailCard(
    transactionId: String,
    type: String,
    amount: String,
    currency: String,
    status: String,
    statusColor: Color,
    time: String,
    fromAddress: String? = null,
    toAddress: String? = null,
    networkFee: String? = null,
    modifier: Modifier = Modifier
) {
    BaseCard(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = type,
                style = AppTypography.H4,
                color = TextPrimary
            )
            Box(
                modifier = Modifier
                    .background(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = AppShape.Tag
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = status,
                    style = AppTypography.LabelSmall,
                    color = statusColor
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Amount
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = amount,
                style = AppTypography.NumberLarge,
                color = if (amount.startsWith("+")) Success else TextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = currency,
                style = AppTypography.H4,
                color = TextSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BackgroundTertiary)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Details
        TransactionDetailItem(label = "交易单号", value = transactionId)
        TransactionDetailItem(label = "交易时间", value = time)
        
        if (fromAddress != null) {
            TransactionDetailItem(label = "发送地址", value = fromAddress, isAddress = true)
        }
        if (toAddress != null) {
            TransactionDetailItem(label = "接收地址", value = toAddress, isAddress = true)
        }
        if (networkFee != null) {
            TransactionDetailItem(label = "网络费用", value = networkFee)
        }
    }
}

/**
 * 交易详情项
 */
@Composable
private fun TransactionDetailItem(
    label: String,
    value: String,
    isAddress: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTypography.Body,
            color = TextSecondary
        )
        Text(
            text = if (isAddress) value.take(8) + "..." + value.takeLast(8) else value,
            style = AppTypography.Body,
            color = TextPrimary
        )
    }
}

@Preview(name = "Transaction Card")
@Composable
fun TransactionCardPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Deposit transaction
            TransactionCard(
                type = "充值",
                amount = "+100.00 USDT",
                currency = "USDT",
                time = "2024-12-01 10:30:00",
                status = "已完成",
                statusColor = Success,
                icon = androidx.compose.material.icons.Icons.Default.ArrowDownward,
                iconBackgroundColor = SuccessLight,
                onClick = {}
            )
            
            // Withdrawal transaction
            TransactionCard(
                type = "提现",
                amount = "-50.00 USDT",
                currency = "USDT",
                time = "2024-12-01 14:20:00",
                status = "处理中",
                statusColor = Warning,
                icon = androidx.compose.material.icons.Icons.Default.ArrowUpward,
                iconBackgroundColor = WarningLight,
                onClick = {}
            )
            
            // Purchase transaction
            TransactionCard(
                type = "购买套餐",
                amount = "-59.99 USDT",
                currency = "USDT",
                time = "2024-12-01 09:15:00",
                status = "已完成",
                statusColor = Success,
                icon = androidx.compose.material.icons.Icons.Default.ShoppingCart,
                iconBackgroundColor = Primary.copy(alpha = 0.15f),
                onClick = {}
            )
            
            // Transaction detail
            TransactionDetailCard(
                transactionId = "TXN123456789",
                type = "充值",
                amount = "+100.00",
                currency = "USDT",
                status = "已完成",
                statusColor = Success,
                time = "2024-12-01 10:30:00",
                fromAddress = "0x1234567890abcdef",
                toAddress = "0xfedcba0987654321",
                networkFee = "1.00 USDT"
            )
        }
    }
}
