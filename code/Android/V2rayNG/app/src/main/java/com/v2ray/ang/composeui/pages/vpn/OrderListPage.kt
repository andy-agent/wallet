package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.order.VpnOrderBridge
import com.v2ray.ang.composeui.theme.Error as AppError
import com.v2ray.ang.composeui.theme.GlowBlue
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.Warning
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ListOrderStatus {
    PENDING,
    PAID,
    COMPLETED,
    CANCELLED,
    REFUNDED,
}

data class VpnOrderListItem(
    val id: String,
    val planName: String,
    val amount: String,
    val status: ListOrderStatus,
    val createdAt: Date,
    val expiresAt: Date? = null,
)

sealed class OrderListState {
    object Idle : OrderListState()
    object Loading : OrderListState()
    data class Loaded(val orders: List<VpnOrderListItem>) : OrderListState()
    data class Error(val message: String) : OrderListState()
    object Empty : OrderListState()
}

class OrderListViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<OrderListState>(OrderListState.Idle)
    val state: StateFlow<OrderListState> = _state
    private val bridge = VpnOrderBridge(application)

    init {
        loadOrders()
    }

    private fun loadOrders() {
        _state.value = OrderListState.Loading
        viewModelScope.launch {
            bridge.loadCachedOrders()
                .onSuccess { entities ->
                    val orders = entities
                        .sortedByDescending { it.createdAt }
                        .map { it.toUiOrderItem() }
                    _state.value = if (orders.isEmpty()) {
                        OrderListState.Empty
                    } else {
                        OrderListState.Loaded(orders)
                    }
                }
                .onFailure { error ->
                    _state.value = OrderListState.Error(error.message ?: "加载订单失败")
                }
        }
    }
}

private fun OrderEntity.toUiOrderItem(): VpnOrderListItem {
    val uiStatus = when (status) {
        PaymentConfig.OrderStatus.PENDING_PAYMENT,
        PaymentConfig.OrderStatus.SEEN_ONCHAIN,
        PaymentConfig.OrderStatus.CONFIRMING,
        -> ListOrderStatus.PENDING

        PaymentConfig.OrderStatus.PAID_SUCCESS -> ListOrderStatus.PAID
        PaymentConfig.OrderStatus.FULFILLED -> ListOrderStatus.COMPLETED
        PaymentConfig.OrderStatus.EXPIRED,
        PaymentConfig.OrderStatus.LATE_PAID,
        -> ListOrderStatus.CANCELLED

        PaymentConfig.OrderStatus.FAILED,
        PaymentConfig.OrderStatus.UNDERPAID,
        PaymentConfig.OrderStatus.OVERPAID,
        -> ListOrderStatus.REFUNDED

        else -> ListOrderStatus.PENDING
    }
    return VpnOrderListItem(
        id = orderNo,
        planName = planName,
        amount = "${amount} ${assetCode}",
        status = uiStatus,
        createdAt = Date(createdAt),
        expiresAt = expiredAt?.let { Date(it) },
    )
}

@Composable
fun OrderListPage(
    viewModel: OrderListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onOrderClick: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val orders = (state as? OrderListState.Loaded)?.orders.orEmpty()

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { innerPadding ->
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = VpnPageHorizontalPadding,
                    end = VpnPageHorizontalPadding,
                    top = VpnPageTopPadding,
                    bottom = 32.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    VpnTopChrome(
                        title = "Orders",
                        subtitle = "Primary purchase records with overview first, detail later.",
                        onBack = onNavigateBack,
                    )
                }
                item {
                    VpnHeroCard(
                        eyebrow = "ORDER BOOK",
                        title = "Track pending payment, settlement, and fulfilled VPN packages",
                        subtitle = "保留缓存订单桥接，仅把列表切成 Bitget 风格总览卡片和二级明细卡片流。",
                        metrics = listOf(
                            VpnHeroMetric("Pending", orders.count { it.status == ListOrderStatus.PENDING }.toString()),
                            VpnHeroMetric("Completed", orders.count { it.status == ListOrderStatus.COMPLETED }.toString()),
                            VpnHeroMetric("Records", orders.size.toString()),
                        ),
                    )
                }

                when (val current = state) {
                    is OrderListState.Loading,
                    OrderListState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "Loading order book",
                                subtitle = "正在汇总本地缓存订单与待支付状态。",
                            )
                        }
                    }

                    is OrderListState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "Order book unavailable",
                                subtitle = current.message,
                            )
                        }
                    }

                    OrderListState.Empty -> {
                        item {
                            VpnEmptyPanel(
                                title = "No VPN orders yet",
                                subtitle = "订单账本为空，新的套餐购买会在这里以卡片式时间线展示。",
                            )
                        }
                    }

                    is OrderListState.Loaded -> {
                        item {
                            VpnSectionHeading(
                                title = "Recent Activity",
                                subtitle = "Every order card keeps status, amount, and settlement route readable at a glance.",
                            )
                        }
                        items(current.orders, key = { it.id }) { order ->
                            OrderBookCard(
                                order = order,
                                onClick = { onOrderClick(order.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderBookCard(
    order: VpnOrderListItem,
    onClick: () -> Unit,
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val accent = order.status.accentColor()
    VpnGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        accent = accent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.planName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            VpnStatusChip(
                text = order.status.label(),
                containerColor = accent.copy(alpha = 0.16f),
                contentColor = accent,
            )
        }

        Text(
            text = order.id,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "Amount",
                value = order.amount,
            )
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "Created",
                value = dateFormat.format(order.createdAt),
            )
        }

        order.expiresAt?.let { expiresAt ->
            VpnLabelValueRow(
                label = "Expiry",
                value = dateFormat.format(expiresAt),
                valueColor = accent,
            )
        }

        Text(
            text = order.status.description(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun ListOrderStatus.label(): String {
    return when (this) {
        ListOrderStatus.PENDING -> "Pending"
        ListOrderStatus.PAID -> "Paid"
        ListOrderStatus.COMPLETED -> "Completed"
        ListOrderStatus.CANCELLED -> "Cancelled"
        ListOrderStatus.REFUNDED -> "Refunded"
    }
}

private fun ListOrderStatus.description(): String {
    return when (this) {
        ListOrderStatus.PENDING -> "等待链上支付或确认，详情页会展示更细的支付路径与操作按钮。"
        ListOrderStatus.PAID -> "资金已到账，后台正在处理套餐激活。"
        ListOrderStatus.COMPLETED -> "套餐已交付，可在 VPN 首页继续使用。"
        ListOrderStatus.CANCELLED -> "订单已取消或超时关闭。"
        ListOrderStatus.REFUNDED -> "订单异常或退款已处理。"
    }
}

private fun ListOrderStatus.accentColor(): Color {
    return when (this) {
        ListOrderStatus.PENDING -> Warning
        ListOrderStatus.PAID -> GlowBlue
        ListOrderStatus.COMPLETED -> Primary
        ListOrderStatus.CANCELLED -> Color(0xFF6D7B91)
        ListOrderStatus.REFUNDED -> AppError
    }
}

@Preview
@Composable
private fun OrderListPagePreview() {
    MaterialTheme {
        OrderListPage()
    }
}
