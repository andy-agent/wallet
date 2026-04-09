package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
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
import com.v2ray.ang.composeui.theme.AuditState
import com.v2ray.ang.composeui.theme.ControlPlaneTokens
import com.v2ray.ang.composeui.theme.TextPrimary
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
    data object Idle : OrderListState()
    data object Loading : OrderListState()
    data class Loaded(val orders: List<VpnOrderListItem>) : OrderListState()
    data class Error(val message: String) : OrderListState()
    data object Empty : OrderListState()
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
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { innerPadding ->
            LazyColumn(
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
                    VpnCenterTopBar(
                        title = "订单审计列表",
                        onBack = onNavigateBack,
                    )
                }
                item {
                    VpnGlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            VpnMetricPill(
                                modifier = Modifier.weight(1f),
                                label = "待支付",
                                value = orders.count { it.status == ListOrderStatus.PENDING }.toString(),
                                valueColor = Color(0xFFFFB14A),
                            )
                            VpnMetricPill(
                                modifier = Modifier.weight(1f),
                                label = "已完成",
                                value = orders.count { it.status == ListOrderStatus.COMPLETED }.toString(),
                                valueColor = VpnAccent,
                            )
                            VpnMetricPill(
                                modifier = Modifier.weight(1f),
                                label = "总记录",
                                value = orders.size.toString(),
                            )
                        }
                    }
                }

                when (val current = state) {
                    is OrderListState.Loading,
                    OrderListState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "正在同步订单列表",
                                subtitle = "从现有缓存订单桥接聚合结算记录。",
                            )
                        }
                    }

                    is OrderListState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "订单列表不可用",
                                subtitle = current.message,
                            )
                        }
                    }

                    OrderListState.Empty -> {
                        item {
                            VpnEmptyPanel(
                                title = "暂无订阅结算记录",
                                subtitle = "新的套餐订单会按时间顺序出现在这里。",
                            )
                        }
                    }

                    is OrderListState.Loaded -> {
                        item {
                            VpnGlassCard(accent = VpnOutline, contentPadding = PaddingValues(vertical = 6.dp)) {
                                current.orders.forEachIndexed { index, order ->
                                    OrderRow(
                                        order = order,
                                        onClick = { onOrderClick(order.id) },
                                    )
                                    if (index != current.orders.lastIndex) {
                                        VpnListDivider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderRow(
    order: VpnOrderListItem,
    onClick: () -> Unit,
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    VpnGroupRow(
        title = order.planName,
        subtitle = dateFormat.format(order.createdAt),
        onClick = onClick,
        leading = {
            VpnCodeBadge(
                text = "订",
                backgroundColor = order.status.accentColor().copy(alpha = 0.18f),
                contentColor = order.status.accentColor(),
            )
        },
        trailing = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = order.amount,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                VpnStatusChip(
                    text = order.status.label(),
                    containerColor = order.status.accentColor().copy(alpha = 0.14f),
                    contentColor = order.status.accentColor(),
                )
            }
        },
    )
}

private fun ListOrderStatus.label(): String {
    return when (this) {
        ListOrderStatus.PENDING -> "待支付"
        ListOrderStatus.PAID -> "已支付"
        ListOrderStatus.COMPLETED -> "已完成"
        ListOrderStatus.CANCELLED -> "已关闭"
        ListOrderStatus.REFUNDED -> "已退款"
    }
}

private fun ListOrderStatus.accentColor(): Color {
    return when (this) {
        ListOrderStatus.PENDING -> ControlPlaneTokens.audit(AuditState.Warn).accent
        ListOrderStatus.PAID -> ControlPlaneTokens.audit(AuditState.Ok).accent
        ListOrderStatus.COMPLETED -> ControlPlaneTokens.audit(AuditState.Ok).accent
        ListOrderStatus.CANCELLED -> ControlPlaneTokens.audit(AuditState.Unknown).accent
        ListOrderStatus.REFUNDED -> ControlPlaneTokens.audit(AuditState.Critical).accent
    }
}

@Preview
@Composable
private fun OrderListPagePreview() {
    MaterialTheme {
        OrderListPage()
    }
}
