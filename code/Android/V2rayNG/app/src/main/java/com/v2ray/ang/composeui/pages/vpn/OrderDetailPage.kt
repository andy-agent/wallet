package com.v2ray.ang.composeui.pages.vpn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 订单详情数据
 */
data class OrderDetailData(
    val id: String,
    val planName: String,
    val duration: String,
    val amount: String,
    val discount: String?,
    val totalAmount: String,
    val status: OrderStatus,
    val paymentMethod: String,
    val createdAt: Date,
    val paidAt: Date?,
    val expiresAt: Date?,
    val txHash: String?,
    val email: String
)

/**
 * 订单详情页状态
 */
sealed class OrderDetailState {
    object Idle : OrderDetailState()
    object Loading : OrderDetailState()
    data class Loaded(val order: OrderDetailData) : OrderDetailState()
    data class Error(val message: String) : OrderDetailState()
}

/**
 * 订单详情页ViewModel
 */
class OrderDetailViewModel : ViewModel() {
    private val _state = MutableStateFlow<OrderDetailState>(OrderDetailState.Idle)
    val state: StateFlow<OrderDetailState> = _state

    fun loadOrderDetail(orderId: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        // 模拟加载订单详情
        val order = OrderDetailData(
            id = orderId,
            planName = "季度套餐",
            duration = "3个月",
            amount = "$29.97",
            discount = "$2.98",
            totalAmount = "$26.99",
            status = OrderStatus.COMPLETED,
            paymentMethod = "钱包支付",
            createdAt = dateFormat.parse("2024-01-15 10:30:00") ?: Date(),
            paidAt = dateFormat.parse("2024-01-15 10:32:15"),
            expiresAt = dateFormat.parse("2024-04-15 10:30:00"),
            txHash = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb",
            email = "user@example.com"
        )
        
        _state.value = OrderDetailState.Loaded(order)
    }
}

/**
 * 订单详情页
 * 显示订单的详细信息和状态
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailPage(
    viewModel: OrderDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    orderId: String = "",
    onNavigateBack: () -> Unit = {},
    onPayOrder: (String) -> Unit = {},
    onContactSupport: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(orderId) {
        if (orderId.isNotEmpty()) {
            viewModel.loadOrderDetail(orderId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (state is OrderDetailState.Loaded) {
                val order = (state as OrderDetailState.Loaded).order
                if (order.status == OrderStatus.PENDING) {
                    OrderDetailBottomBar(
                        onPay = { onPayOrder(order.id) },
                        onContactSupport = onContactSupport
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is OrderDetailState.Loaded -> {
                    val order = (state as OrderDetailState.Loaded).order
                    OrderDetailContent(order = order)
                }
                is OrderDetailState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is OrderDetailState.Error -> {
                    ErrorView(message = (state as OrderDetailState.Error).message)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun OrderDetailContent(order: OrderDetailData) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 状态卡片
        OrderStatusCard(status = order.status)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 套餐信息
        PlanInfoCard(order = order)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 价格详情
        PriceDetailCard(order = order)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 订单信息
        OrderInfoCard(order = order, dateFormat = dateFormat)
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun OrderStatusCard(status: OrderStatus) {
    val (icon, title, description, color) = when (status) {
        OrderStatus.PENDING -> Quad(
            Icons.Default.Schedule,
            "待支付",
            "请在30分钟内完成支付",
            Color(0xFFF59E0B)
        )
        OrderStatus.PAID -> Quad(
            Icons.Default.Payment,
            "已支付",
            "订单正在处理中",
            Color(0xFF1D4ED8)
        )
        OrderStatus.COMPLETED -> Quad(
            Icons.Default.CheckCircle,
            "已完成",
            "套餐已激活，可以使用了",
            Color(0xFF22C55E)
        )
        OrderStatus.CANCELLED -> Quad(
            Icons.Default.Cancel,
            "已取消",
            "订单已取消",
            Color(0xFF94A3B8)
        )
        OrderStatus.REFUNDED -> Quad(
            Icons.Default.Replay,
            "已退款",
            "款项已原路退回",
            Color(0xFFEF4444)
        )
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PlanInfoCard(order: OrderDetailData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "套餐信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailRow(label = "套餐名称", value = order.planName)
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow(label = "有效期", value = order.duration)
            
            order.expiresAt?.let { expiresAt ->
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(
                    label = "到期时间", 
                    value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(expiresAt)
                )
            }
        }
    }
}

@Composable
private fun PriceDetailCard(order: OrderDetailData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "价格详情",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailRow(label = "套餐金额", value = order.amount)
            
            order.discount?.let { discount ->
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(
                    label = "优惠", 
                    value = "-$discount",
                    valueColor = Color(0xFF22C55E)
                )
            }
            
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "实付金额",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = order.totalAmount,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun OrderInfoCard(order: OrderDetailData, dateFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "订单信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailRow(label = "订单号", value = order.id)
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow(label = "创建时间", value = dateFormat.format(order.createdAt))
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow(label = "支付方式", value = order.paymentMethod)
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow(label = "邮箱", value = order.email)
            
            order.paidAt?.let { paidAt ->
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(label = "支付时间", value = dateFormat.format(paidAt))
            }
            
            order.txHash?.let { txHash ->
                Spacer(modifier = Modifier.height(12.dp))
                DetailRow(label = "交易哈希", value = txHash)
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String, 
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
private fun OrderDetailBottomBar(
    onPay: () -> Unit,
    onContactSupport: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onContactSupport,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "联系客服",
                    fontSize = 14.sp
                )
            }
            
            Button(
                onClick = onPay,
                modifier = Modifier
                    .weight(2f)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "立即支付",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
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

// 辅助数据类
private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Preview(showBackground = true)
@Composable
fun OrderDetailPagePreview() {
    MaterialTheme {
        OrderDetailPage()
    }
}
