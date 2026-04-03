package com.cryptovpn.ui.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.CryptoVPNTheme

// ==================== Enums & Data Models ====================

enum class OrderResultStatus {
    COMPLETED,
    FAILED,
    EXPIRED,
    REVIEW_PENDING
}

data class OrderResultState(
    val status: OrderResultStatus = OrderResultStatus.COMPLETED,
    val orderId: String = "ORD-2024-001234",
    val planName: String = "年度高级套餐",
    val amount: String = "99.99",
    val currency: String = "USDT",
    val transactionHash: String? = "0x742d35...5f0bEb",
    val message: String? = null
)

// ==================== ViewModel ====================

class OrderResultViewModel {
    var state by mutableStateOf(OrderResultState())
        private set

    fun setStatus(status: OrderResultStatus) {
        state = state.copy(status = status)
    }

    fun updateState(newState: OrderResultState) {
        state = newState
    }
}

// ==================== Page Composable ====================

@Composable
fun OrderResultPage(
    viewModel: OrderResultViewModel = remember { OrderResultViewModel() },
    onCloseClick: () -> Unit = {},
    onViewOrderClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onBackHomeClick: () -> Unit = {}
) {
    val state = viewModel.state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Close Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF9CA3AF)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Result Icon with Animation
                ResultIcon(status = state.status)

                Spacer(modifier = Modifier.height(32.dp))

                // Status Title
                Text(
                    text = getStatusTitle(state.status),
                    color = getStatusColor(state.status),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Status Description
                Text(
                    text = getStatusDescription(state.status),
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Order Details Card
                OrderDetailsCard(state = state)

                Spacer(modifier = Modifier.weight(1f))

                // Action Buttons
                ResultActionButtons(
                    status = state.status,
                    onViewOrderClick = onViewOrderClick,
                    onRetryClick = onRetryClick,
                    onBackHomeClick = onBackHomeClick
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ResultIcon(status: OrderResultStatus) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val (backgroundColor, iconColor, icon) = when (status) {
        OrderResultStatus.COMPLETED -> Triple(
            Color(0xFF22C55E).copy(alpha = 0.2f),
            Color(0xFF22C55E),
            Icons.Default.Check
        )
        OrderResultStatus.FAILED -> Triple(
            Color(0xFFEF4444).copy(alpha = 0.2f),
            Color(0xFFEF4444),
            Icons.Default.Close
        )
        OrderResultStatus.EXPIRED -> Triple(
            Color(0xFFF59E0B).copy(alpha = 0.2f),
            Color(0xFFF59E0B),
            Icons.Default.Schedule
        )
        OrderResultStatus.REVIEW_PENDING -> Triple(
            Color(0xFF1D4ED8).copy(alpha = 0.2f),
            Color(0xFF1D4ED8),
            Icons.Default.HourglassEmpty
        )
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = status.name,
            tint = iconColor,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
private fun OrderDetailsCard(state: OrderResultState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailRow(label = "订单编号", value = state.orderId)
            DetailRow(label = "套餐名称", value = state.planName)
            DetailRow(label = "支付金额", value = "${state.amount} ${state.currency}")
            
            state.transactionHash?.let { hash ->
                Divider(color = Color(0xFF374151))
                DetailRow(label = "交易哈希", value = hash, isMono = true)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, isMono: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = if (isMono) androidx.compose.ui.text.font.FontFamily.Monospace else null
        )
    }
}

@Composable
private fun ResultActionButtons(
    status: OrderResultStatus,
    onViewOrderClick: () -> Unit,
    onRetryClick: () -> Unit,
    onBackHomeClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (status) {
            OrderResultStatus.COMPLETED -> {
                Button(
                    onClick = onViewOrderClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF22C55E)
                    )
                ) {
                    Text(
                        text = "查看订单",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            OrderResultStatus.FAILED, OrderResultStatus.EXPIRED -> {
                Button(
                    onClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D4ED8)
                    )
                ) {
                    Text(
                        text = "重新支付",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            OrderResultStatus.REVIEW_PENDING -> {
                // No primary action for pending
            }
        }

        OutlinedButton(
            onClick = onBackHomeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF9CA3AF)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color(0xFF374151)
            )
        ) {
            Text(
                text = "返回首页",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ==================== Helper Functions ====================

private fun getStatusTitle(status: OrderResultStatus): String = when (status) {
    OrderResultStatus.COMPLETED -> "支付成功"
    OrderResultStatus.FAILED -> "支付失败"
    OrderResultStatus.EXPIRED -> "订单已过期"
    OrderResultStatus.REVIEW_PENDING -> "审核中"
}

private fun getStatusDescription(status: OrderResultStatus): String = when (status) {
    OrderResultStatus.COMPLETED -> "您的订单已成功支付，VPN服务已激活"
    OrderResultStatus.FAILED -> "支付过程中出现问题，请检查后重试"
    OrderResultStatus.EXPIRED -> "订单支付超时，请重新下单"
    OrderResultStatus.REVIEW_PENDING -> "您的交易正在审核中，请耐心等待"
}

private fun getStatusColor(status: OrderResultStatus): Color = when (status) {
    OrderResultStatus.COMPLETED -> Color(0xFF22C55E)
    OrderResultStatus.FAILED -> Color(0xFFEF4444)
    OrderResultStatus.EXPIRED -> Color(0xFFF59E0B)
    OrderResultStatus.REVIEW_PENDING -> Color(0xFF1D4ED8)
}

// ==================== Preview ====================

@Preview(device = "id:pixel_5")
@Composable
private fun OrderResultPageSuccessPreview() {
    CryptoVPNTheme {
        OrderResultPage()
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun OrderResultPageFailedPreview() {
    CryptoVPNTheme {
        val viewModel = remember { OrderResultViewModel() }
        viewModel.setStatus(OrderResultStatus.FAILED)
        OrderResultPage(viewModel = viewModel)
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun OrderResultPageExpiredPreview() {
    CryptoVPNTheme {
        val viewModel = remember { OrderResultViewModel() }
        viewModel.setStatus(OrderResultStatus.EXPIRED)
        OrderResultPage(viewModel = viewModel)
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun OrderResultPagePendingPreview() {
    CryptoVPNTheme {
        val viewModel = remember { OrderResultViewModel() }
        viewModel.setStatus(OrderResultStatus.REVIEW_PENDING)
        OrderResultPage(viewModel = viewModel)
    }
}