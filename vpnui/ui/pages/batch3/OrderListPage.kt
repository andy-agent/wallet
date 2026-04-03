package com.cryptovpn.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.CryptoVPNTheme
import java.text.SimpleDateFormat
import java.util.*

// ==================== Enums & Data Models ====================

enum class OrderStatus {
    PENDING,
    PAID,
    COMPLETED,
    FAILED,
    REFUNDED,
    CANCELLED
}

data class OrderItem(
    val orderId: String,
    val planName: String,
    val status: OrderStatus,
    val amount: String,
    val currency: String,
    val createdAt: Long,
    val duration: String
)

data class OrderListState(
    val orders: List<OrderItem> = emptyList(),
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
    val selectedFilter: OrderStatus? = null
)

// ==================== ViewModel ====================

class OrderListViewModel {
    var state by mutableStateOf(OrderListState())
        private set

    init {
        // Load sample data
        loadOrders()
    }

    private fun loadOrders() {
        val sampleOrders = listOf(
            OrderItem(
                orderId = "ORD-2024-001234",
                planName = "年度高级套餐",
                status = OrderStatus.COMPLETED,
                amount = "99.99",
                currency = "USDT",
                createdAt = System.currentTimeMillis(),
                duration = "365天"
            ),
            OrderItem(
                orderId = "ORD-2024-001233",
                planName = "月度基础套餐",
                status = OrderStatus.PENDING,
                amount = "9.99",
                currency = "USDT",
                createdAt = System.currentTimeMillis() - 86400000,
                duration = "30天"
            ),
            OrderItem(
                orderId = "ORD-2024-001232",
                planName = "季度标准套餐",
                status = OrderStatus.FAILED,
                amount = "29.99",
                currency = "USDT",
                createdAt = System.currentTimeMillis() - 172800000,
                duration = "90天"
            ),
            OrderItem(
                orderId = "ORD-2024-001231",
                planName = "年度高级套餐",
                status = OrderStatus.REFUNDED,
                amount = "99.99",
                currency = "USDT",
                createdAt = System.currentTimeMillis() - 259200000,
                duration = "365天"
            ),
            OrderItem(
                orderId = "ORD-2024-001230",
                planName = "月度基础套餐",
                status = OrderStatus.CANCELLED,
                amount = "9.99",
                currency = "USDT",
                createdAt = System.currentTimeMillis() - 345600000,
                duration = "30天"
            )
        )
        state = state.copy(orders = sampleOrders)
    }

    fun onOrderClick(orderId: String) {
        // Navigate to order detail
    }

    fun onFilterSelected(status: OrderStatus?) {
        state = state.copy(selectedFilter = status)
    }

    fun loadMore() {
        // Load more orders
    }
}

// ==================== Page Composable ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListPage(
    viewModel: OrderListViewModel = remember { OrderListViewModel() },
    onBackClick: () -> Unit = {},
    onOrderClick: (String) -> Unit = {}
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的订单", color = Color.White) },
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
        ) {
            // Filter Chips
            FilterChips(
                selectedFilter = state.selectedFilter,
                onFilterSelected = { viewModel.onFilterSelected(it) }
            )

            // Order List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.orders) { order ->
                    OrderCard(
                        order = order,
                        onClick = { 
                            viewModel.onOrderClick(order.orderId)
                            onOrderClick(order.orderId)
                        }
                    )
                }

                if (state.hasMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF1D4ED8),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChips(
    selectedFilter: OrderStatus?,
    onFilterSelected: (OrderStatus?) -> Unit
) {
    val filters = listOf<Pair<OrderStatus?, String>>(
        null to "全部",
        OrderStatus.PENDING to "待支付",
        OrderStatus.COMPLETED to "已完成",
        OrderStatus.FAILED to "失败"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (status, label) ->
            val isSelected = selectedFilter == status
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(status) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1D4ED8),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF1F2937),
                    labelColor = Color(0xFF9CA3AF)
                ),
                border = null
            )
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Plan Name and Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = order.planName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    OrderStatusChip(status = order.status)
                }

                // Order ID
                Text(
                    text = "订单号: ${order.orderId}",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp
                )

                // Date and Amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(order.createdAt),
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${order.amount} ${order.currency}",
                        color = Color(0xFF22C55E),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View",
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun OrderStatusChip(status: OrderStatus) {
    val (backgroundColor, textColor, label) = when (status) {
        OrderStatus.PENDING -> Triple(
            Color(0xFFF59E0B).copy(alpha = 0.2f),
            Color(0xFFF59E0B),
            "待支付"
        )
        OrderStatus.PAID -> Triple(
            Color(0xFF1D4ED8).copy(alpha = 0.2f),
            Color(0xFF1D4ED8),
            "已支付"
        )
        OrderStatus.COMPLETED -> Triple(
            Color(0xFF22C55E).copy(alpha = 0.2f),
            Color(0xFF22C55E),
            "已完成"
        )
        OrderStatus.FAILED -> Triple(
            Color(0xFFEF4444).copy(alpha = 0.2f),
            Color(0xFFEF4444),
            "失败"
        )
        OrderStatus.REFUNDED -> Triple(
            Color(0xFF9CA3AF).copy(alpha = 0.2f),
            Color(0xFF9CA3AF),
            "已退款"
        )
        OrderStatus.CANCELLED -> Triple(
            Color(0xFF6B7280).copy(alpha = 0.2f),
            Color(0xFF6B7280),
            "已取消"
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ==================== Helper Functions ====================

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// ==================== Preview ====================

@Preview(device = "id:pixel_5")
@Composable
private fun OrderListPagePreview() {
    CryptoVPNTheme {
        OrderListPage()
    }
}

@Preview
@Composable
private fun OrderCardPreview() {
    CryptoVPNTheme {
        OrderCard(
            order = OrderItem(
                orderId = "ORD-2024-001234",
                planName = "年度高级套餐",
                status = OrderStatus.COMPLETED,
                amount = "99.99",
                currency = "USDT",
                createdAt = System.currentTimeMillis(),
                duration = "365天"
            ),
            onClick = {}
        )
    }
}