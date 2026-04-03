package com.cryptovpn.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.CryptoVPNTheme
import java.text.SimpleDateFormat
import java.util.*

// ==================== Data Models ====================

data class TimelineEvent(
    val status: String,
    val description: String,
    val timestamp: Long,
    val isCompleted: Boolean,
    val isCurrent: Boolean = false
)

data class OrderDetailState(
    val orderId: String = "ORD-2024-001234",
    val planName: String = "年度高级套餐",
    val planDescription: String = "全球服务器访问 + 无限制流量 + 最高速度",
    val status: OrderStatus = OrderStatus.COMPLETED,
    val amount: String = "99.99",
    val currency: String = "USDT",
    val originalPrice: String = "129.99",
    val discount: String = "30.00",
    val createdAt: Long = System.currentTimeMillis() - 86400000,
    val paidAt: Long? = System.currentTimeMillis() - 86000000,
    val completedAt: Long? = System.currentTimeMillis() - 85000000,
    val transactionHash: String? = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb",
    val paymentMethod: String = "USDT-TRC20",
    val duration: String = "365天",
    val expiryDate: String = "2025-01-15",
    val timeline: List<TimelineEvent> = emptyList()
)

// ==================== ViewModel ====================

class OrderDetailViewModel {
    var state by mutableStateOf(OrderDetailState())
        private set

    init {
        loadOrderDetail()
    }

    private fun loadOrderDetail() {
        val now = System.currentTimeMillis()
        val timeline = listOf(
            TimelineEvent(
                status = "订单创建",
                description = "订单已创建，等待支付",
                timestamp = now - 86400000,
                isCompleted = true
            ),
            TimelineEvent(
                status = "支付成功",
                description = "区块链交易已确认",
                timestamp = now - 86000000,
                isCompleted = true
            ),
            TimelineEvent(
                status = "订单完成",
                description = "VPN服务已激活",
                timestamp = now - 85000000,
                isCompleted = true,
                isCurrent = true
            )
        )
        state = state.copy(timeline = timeline)
    }

    fun copyTransactionHash() {
        state.transactionHash?.let {
            // Copy to clipboard
        }
    }

    fun onContactSupport() {
        // Open support chat
    }
}

// ==================== Page Composable ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailPage(
    viewModel: OrderDetailViewModel = remember { OrderDetailViewModel() },
    onBackClick: () -> Unit = {},
    onViewTransactionClick: () -> Unit = {},
    onContactSupportClick: () -> Unit = {}
) {
    val state = viewModel.state
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单详情", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B1020)
                )
            )
        },
        containerColor = Color(0xFF0B1020)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Order Header
            OrderHeader(state = state)

            // Order Info Card
            OrderInfoCard(state = state)

            // Timeline
            OrderTimeline(timeline = state.timeline)

            // Transaction Hash
            state.transactionHash?.let { hash ->
                TransactionHashCard(
                    hash = hash,
                    onCopyClick = { viewModel.copyTransactionHash() },
                    onViewClick = onViewTransactionClick
                )
            }

            // Action Buttons
            OrderDetailActions(
                status = state.status,
                onContactSupportClick = {
                    viewModel.onContactSupport()
                    onContactSupportClick()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OrderHeader(state: OrderDetailState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Icon
        val (icon, iconColor, bgColor) = when (state.status) {
            OrderStatus.COMPLETED -> Triple(
                Icons.Default.CheckCircle,
                Color(0xFF22C55E),
                Color(0xFF22C55E).copy(alpha = 0.2f)
            )
            OrderStatus.PENDING -> Triple(
                Icons.Default.Schedule,
                Color(0xFFF59E0B),
                Color(0xFFF59E0B).copy(alpha = 0.2f)
            )
            OrderStatus.FAILED -> Triple(
                Icons.Default.Error,
                Color(0xFFEF4444),
                Color(0xFFEF4444).copy(alpha = 0.2f)
            )
            else -> Triple(
                Icons.Default.Info,
                Color(0xFF9CA3AF),
                Color(0xFF9CA3AF).copy(alpha = 0.2f)
            )
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = state.status.name,
                tint = iconColor,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Text
        Text(
            text = getOrderStatusText(state.status),
            color = iconColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Order ID
        Text(
            text = state.orderId,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun OrderInfoCard(state: OrderDetailState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Plan Info
            Column {
                Text(
                    text = state.planName,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = state.planDescription,
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp
                )
            }

            Divider(color = Color(0xFF374151))

            // Price Details
            DetailRow(label = "原价", value = "${state.originalPrice} ${state.currency}", isStrikethrough = true)
            DetailRow(label = "优惠", value = "-${state.discount} ${state.currency}", valueColor = Color(0xFF22C55E))
            DetailRow(label = "实付金额", value = "${state.amount} ${state.currency}", isBold = true, valueColor = Color(0xFF22C55E))

            Divider(color = Color(0xFF374151))

            // Order Details
            DetailRow(label = "服务时长", value = state.duration)
            DetailRow(label = "到期日期", value = state.expiryDate)
            DetailRow(label = "支付方式", value = state.paymentMethod)
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isStrikethrough: Boolean = false,
    valueColor: Color = Color.White
) {
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
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun OrderTimeline(timeline: List<TimelineEvent>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "订单进度",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            timeline.forEachIndexed { index, event ->
                TimelineItem(
                    event = event,
                    isLast = index == timeline.size - 1
                )
            }
        }
    }
}

@Composable
private fun TimelineItem(event: TimelineEvent, isLast: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Timeline Line and Dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            event.isCurrent -> Color(0xFF1D4ED8)
                            event.isCompleted -> Color(0xFF22C55E)
                            else -> Color(0xFF6B7280)
                        }
                    )
            )

            // Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(50.dp)
                        .background(
                            if (event.isCompleted) Color(0xFF22C55E).copy(alpha = 0.5f)
                            else Color(0xFF374151)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Event Content
        Column(
            modifier = Modifier.padding(bottom = if (isLast) 0.dp else 24.dp)
        ) {
            Text(
                text = event.status,
                color = if (event.isCompleted || event.isCurrent) Color.White else Color(0xFF6B7280),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = event.description,
                color = Color(0xFF9CA3AF),
                fontSize = 12.sp
            )
            Text(
                text = formatDateTime(event.timestamp),
                color = Color(0xFF6B7280),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun TransactionHashCard(
    hash: String,
    onCopyClick: () -> Unit,
    onViewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "交易哈希",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )

            Text(
                text = hash,
                color = Color.White,
                fontSize = 13.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF9CA3AF)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFF374151)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("复制", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onViewClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1D4ED8)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFF1D4ED8)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "View",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("查看", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun OrderDetailActions(
    status: OrderStatus,
    onContactSupportClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (status == OrderStatus.PENDING) {
            Button(
                onClick = { /* Continue payment */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                )
            ) {
                Text(
                    text = "继续支付",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        OutlinedButton(
            onClick = onContactSupportClick,
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
            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = "Support",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "联系客服",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ==================== Helper Functions ====================

private fun getOrderStatusText(status: OrderStatus): String = when (status) {
    OrderStatus.PENDING -> "待支付"
    OrderStatus.PAID -> "已支付"
    OrderStatus.COMPLETED -> "已完成"
    OrderStatus.FAILED -> "支付失败"
    OrderStatus.REFUNDED -> "已退款"
    OrderStatus.CANCELLED -> "已取消"
}

private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// ==================== Preview ====================

@Preview(device = "id:pixel_5")
@Composable
private fun OrderDetailPagePreview() {
    CryptoVPNTheme {
        OrderDetailPage()
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun OrderDetailPagePendingPreview() {
    CryptoVPNTheme {
        val viewModel = remember { OrderDetailViewModel() }
        viewModel.state = OrderDetailState(
            status = OrderStatus.PENDING,
            paidAt = null,
            completedAt = null,
            transactionHash = null
        )
        OrderDetailPage(viewModel = viewModel)
    }
}