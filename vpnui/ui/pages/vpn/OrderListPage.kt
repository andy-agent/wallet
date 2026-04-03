package com.cryptovpn.ui.pages.vpn

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*

/**
 * 订单状态
 */
enum class OrderStatus {
    PENDING,    // 待支付
    PAID,       // 已支付
    COMPLETED,  // 已完成
    CANCELLED,  // 已取消
    REFUNDED    // 已退款
}

/**
 * 订单信息
 */
data class OrderItem(
    val id: String,
    val planName: String,
    val amount: String,
    val status: OrderStatus,
    val createdAt: Date,
    val expiresAt: Date? = null
)

/**
 * 订单列表页状态
 */
sealed class OrderListState {
    object Idle : OrderListState()
    object Loading : OrderListState()
    data class Loaded(val orders: List<OrderItem>) : OrderListState()
    data class Error(val message: String) : OrderListState()
    object Empty : OrderListState()
}

/**
 * 订单列表页ViewModel
 */
@HiltViewModel
class OrderListViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<OrderListState>(OrderListState.Idle)
    val state: StateFlow<OrderListState> = _state

    init {
        loadOrders()
    }

    private fun loadOrders() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        val orders = listOf(
            OrderItem(
                id = "ORD-20240115-001",
                planName = "季度套餐",
                amount = "$26.99",
                status = OrderStatus.COMPLETED,
                createdAt = dateFormat.parse("2024-01-15 10:30:00") ?: Date(),
                expiresAt = dateFormat.parse("2024-04-15 10:30:00")
            ),
            OrderItem(
                id = "ORD-20231210-002",
                planName = "月度套餐",
                amount = "$9.99",
                status = OrderStatus.COMPLETED,
                createdAt = dateFormat.parse("2023-12-10 14:20:00") ?: Date(),
                expiresAt = dateFormat.parse("2024-01-10 14:20:00")
            ),
            OrderItem(
                id = "ORD-20231105-003",
                planName = "年度套餐",
                amount = "$89.99",
                status = OrderStatus.COMPLETED,
                createdAt = dateFormat.parse("2023-11-05 09:15:00") ?: Date(),
                expiresAt = dateFormat.parse("2024-11-05 09:15:00")
            ),
            OrderItem(
                id = "ORD-20240120-004",
                planName = "月度套餐",
                amount = "$9.99",
                status = OrderStatus.PENDING,
                createdAt = dateFormat.parse("2024-01-20 16:45:00") ?: Date()
            ),
            OrderItem(
                id = "ORD-20231001-005",
                planName = "季度套餐",
                amount = "$26.99",
                status = OrderStatus.REFUNDED,
                createdAt = dateFormat.parse("2023-10-01 11:00:00") ?: Date()
            )
        )
        
        _state.value = if (orders.isEmpty()) {
            OrderListState.Empty
        } else {
            OrderListState.Loaded(orders)
        }
    }
}

/**
 * 订单列表页
 * 显示用户的历史订单列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListPage(
    viewModel: OrderListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onOrderClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的订单") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is OrderListState.Loaded -> {
                    val orders = (state as OrderListState.Loaded).orders
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(orders) { order ->
                            OrderListItem(
                                order = order,
                                onClick = { onOrderClick(order.id) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
                is OrderListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is OrderListState.Empty -> {
                    EmptyOrderView()
                }
                is OrderListState.Error -> {
                    ErrorView(message = (state as OrderListState.Error).message)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun OrderListItem(
    order: OrderItem,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 状态图标
            OrderStatusIcon(status = order.status)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 订单信息
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.planName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = order.amount,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateFormat.format(order.createdAt),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OrderStatusBadge(status = order.status)
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 箭头
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Detail",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OrderStatusIcon(status: OrderStatus) {
    val (icon, color) = when (status) {
        OrderStatus.PENDING -> Pair(Icons.Default.Schedule, Color(0xFFF59E0B))
        OrderStatus.PAID -> Pair(Icons.Default.Payment, Color(0xFF1D4ED8))
        OrderStatus.COMPLETED -> Pair(Icons.Default.CheckCircle, Color(0xFF22C55E))
        OrderStatus.CANCELLED -> Pair(Icons.Default.Cancel, Color(0xFF94A3B8))
        OrderStatus.REFUNDED -> Pair(Icons.Default.Replay, Color(0xFFEF4444))
    }
    
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.size(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = status.name,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun OrderStatusBadge(status: OrderStatus) {
    val (text, color) = when (status) {
        OrderStatus.PENDING -> Pair("待支付", Color(0xFFF59E0B))
        OrderStatus.PAID -> Pair("已支付", Color(0xFF1D4ED8))
        OrderStatus.COMPLETED -> Pair("已完成", Color(0xFF22C55E))
        OrderStatus.CANCELLED -> Pair("已取消", Color(0xFF94A3B8))
        OrderStatus.REFUNDED -> Pair("已退款", Color(0xFFEF4444))
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun EmptyOrderView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ReceiptLong,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "暂无订单",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "您还没有购买任何套餐",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "加载失败",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrderListPagePreview() {
    MaterialTheme {
        OrderListPage()
    }
}
