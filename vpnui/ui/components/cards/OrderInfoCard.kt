package com.cryptovpn.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.*

/**
 * 订单信息卡片
 * 
 * 用于展示订单详情
 * 
 * @param orderId 订单ID
 * @param planName 套餐名称
 * @param amount 金额
 * @param createTime 创建时间
 * @param status 订单状态
 * @param modifier 修饰符
 */
@Composable
fun OrderInfoCard(
    orderId: String,
    planName: String,
    amount: String,
    createTime: String,
    status: String,
    statusColor: androidx.compose.ui.graphics.Color = TextSecondary,
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
                text = "订单信息",
                style = AppTypography.H4,
                color = TextPrimary
            )
            Text(
                text = status,
                style = AppTypography.LabelMedium,
                color = statusColor
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Order details
        OrderInfoItem(
            label = "订单编号",
            value = orderId
        )
        OrderInfoItem(
            label = "套餐名称",
            value = planName
        )
        OrderInfoItem(
            label = "支付金额",
            value = amount,
            isHighlight = true
        )
        OrderInfoItem(
            label = "创建时间",
            value = createTime
        )
    }
}

/**
 * 订单信息项
 */
@Composable
private fun OrderInfoItem(
    label: String,
    value: String,
    isHighlight: Boolean = false
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
            text = value,
            style = if (isHighlight) AppTypography.NumberSmall else AppTypography.Body,
            color = if (isHighlight) Primary else TextPrimary
        )
    }
}

/**
 * 支付信息卡片
 */
@Composable
fun PaymentInfoCard(
    paymentMethod: String,
    paymentTime: String? = null,
    transactionId: String? = null,
    modifier: Modifier = Modifier
) {
    BaseCard(modifier = modifier) {
        Text(
            text = "支付信息",
            style = AppTypography.H4,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OrderInfoItem(
            label = "支付方式",
            value = paymentMethod
        )
        
        if (paymentTime != null) {
            OrderInfoItem(
                label = "支付时间",
                value = paymentTime
            )
        }
        
        if (transactionId != null) {
            OrderInfoItem(
                label = "交易单号",
                value = transactionId
            )
        }
    }
}

@Preview(name = "Order Info Card")
@Composable
fun OrderInfoCardPreview() {
    CryptoVPNTheme {
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pending order
            OrderInfoCard(
                orderId = "ORD202412010001",
                planName = "年度会员",
                amount = "$59.99",
                createTime = "2024-12-01 10:30:00",
                status = "待支付",
                statusColor = Warning
            )
            
            // Completed order
            OrderInfoCard(
                orderId = "ORD202412010002",
                planName = "月度会员",
                amount = "$9.99",
                createTime = "2024-12-01 14:20:00",
                status = "已完成",
                statusColor = Success
            )
            
            // Payment info
            PaymentInfoCard(
                paymentMethod = "USDT (TRC20)",
                paymentTime = "2024-12-01 14:25:30",
                transactionId = "TXN123456789"
            )
        }
    }
}
