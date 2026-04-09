package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
import com.v2ray.ang.payment.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class DetailOrderStatus {
    PENDING,
    PAID,
    COMPLETED,
    CANCELLED,
    REFUNDED,
}

data class OrderDetailData(
    val id: String,
    val planName: String,
    val duration: String,
    val amount: String,
    val discount: String?,
    val totalAmount: String,
    val status: DetailOrderStatus,
    val paymentMethod: String,
    val createdAt: Date,
    val paidAt: Date?,
    val expiresAt: Date?,
    val txHash: String?,
    val email: String,
)

sealed class OrderDetailState {
    data object Idle : OrderDetailState()
    data object Loading : OrderDetailState()
    data class Loaded(val order: OrderDetailData) : OrderDetailState()
    data class Error(val message: String) : OrderDetailState()
}

class OrderDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<OrderDetailState>(OrderDetailState.Idle)
    val state: StateFlow<OrderDetailState> = _state
    private val bridge = VpnOrderBridge(application)

    fun loadOrderDetail(orderId: String) {
        _state.value = OrderDetailState.Loading
        viewModelScope.launch {
            bridge.refreshOrder(orderId)
                .onSuccess { order ->
                    _state.value = OrderDetailState.Loaded(order.toOrderDetailData())
                }
                .onFailure { error ->
                    _state.value = OrderDetailState.Error(error.message ?: "加载订单详情失败")
                }
        }
    }
}

private fun Order.toOrderDetailData(): OrderDetailData {
    return OrderDetailData(
        id = orderNo,
        planName = planName,
        duration = planCode,
        amount = "$$quoteUsdAmount",
        discount = null,
        totalAmount = "${payment.amountCrypto} ${payment.assetCode}",
        status = when (status) {
            PaymentConfig.OrderStatus.PENDING_PAYMENT,
            PaymentConfig.OrderStatus.SEEN_ONCHAIN,
            PaymentConfig.OrderStatus.CONFIRMING,
            -> DetailOrderStatus.PENDING

            PaymentConfig.OrderStatus.PAID_SUCCESS -> DetailOrderStatus.PAID
            PaymentConfig.OrderStatus.FULFILLED -> DetailOrderStatus.COMPLETED
            PaymentConfig.OrderStatus.EXPIRED,
            PaymentConfig.OrderStatus.LATE_PAID,
            -> DetailOrderStatus.CANCELLED

            else -> DetailOrderStatus.REFUNDED
        },
        paymentMethod = "${quoteAssetCode}/${quoteNetworkCode}",
        createdAt = createdAt.toDateOrNow(),
        paidAt = confirmedAt?.toDateOrNull(),
        expiresAt = expiresAt.toDateOrNull(),
        txHash = submittedClientTxHash,
        email = "",
    )
}

private fun String.toDateOrNow(): Date = toDateOrNull() ?: Date()

private fun String.toDateOrNull(): Date? {
    val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
    )
    for (format in formats) {
        val date = runCatching { format.parse(this) }.getOrNull()
        if (date != null) return date
    }
    return null
}

@Composable
fun OrderDetailPage(
    viewModel: OrderDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    orderId: String = "",
    onNavigateBack: () -> Unit = {},
    onPayOrder: (String) -> Unit = {},
    onContactSupport: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val selectedTab = remember { mutableIntStateOf(0) }
    val selectedRange = remember { mutableIntStateOf(2) }

    LaunchedEffect(orderId) {
        if (orderId.isNotBlank()) {
            viewModel.loadOrderDetail(orderId)
        }
    }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                val order = (state as? OrderDetailState.Loaded)?.order
                if (order?.status == DetailOrderStatus.PENDING) {
                    Surface(
                        color = VpnSurface,
                        border = BorderStroke(1.dp, VpnOutline),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = VpnPageHorizontalPadding, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            VpnPrimaryButton(
                                text = "继续支付",
                                onClick = { onPayOrder(order.id) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            VpnSecondaryButton(
                                text = "联系客服",
                                onClick = onContactSupport,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = VpnPageHorizontalPadding,
                    end = VpnPageHorizontalPadding,
                    top = VpnPageTopPadding,
                    bottom = VpnPageBottomPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    VpnTopChrome(
                        title = "订单 ${orderId.takeTrailing(6)}",
                        subtitle = "结算与交付控制详情",
                        onBack = onNavigateBack,
                    )
                }
                item {
                    VpnTabStrip(
                        tabs = listOf("状态", "详情"),
                        selectedIndex = selectedTab.intValue,
                        onSelect = { selectedTab.intValue = it },
                    )
                }
                when (val current = state) {
                    is OrderDetailState.Loading,
                    OrderDetailState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "正在同步订单详情",
                                subtitle = "通过既有 refreshOrder 获取结算与激活状态。",
                            )
                        }
                    }

                    is OrderDetailState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "订单详情不可用",
                                subtitle = current.message,
                            )
                        }
                    }

                    is OrderDetailState.Loaded -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                VpnValueBlock(
                                    value = current.order.totalAmount,
                                    change = current.order.status.title(),
                                    helper = current.order.status.label(),
                                    changeColor = current.order.status.accent(),
                                    modifier = Modifier.weight(1.1f),
                                )
                                VpnMetricColumn(
                                    metrics = listOf(
                                        VpnHeroMetric("支付方式", current.order.paymentMethod),
                                        VpnHeroMetric("套餐", current.order.planName.take(8)),
                                        VpnHeroMetric("周期", current.order.duration),
                                        VpnHeroMetric("订单号", current.order.id.takeTrailing(6)),
                                    ),
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                        item {
                            VpnRangeSelector(
                                labels = listOf("创建", "支付", "确认", "完成"),
                                selectedIndex = selectedRange.intValue,
                                trailingIcon = Icons.Default.Tune,
                                onSelect = { selectedRange.intValue = it },
                                onTrailingClick = {},
                            )
                        }
                        item {
                            VpnCandleChart(
                                entries = vpnDemoCandles(current.order.id.length.toFloat()),
                                calloutLines = listOf(
                                    "订单" to current.order.id.takeTrailing(6),
                                    "套餐" to current.order.planName,
                                    "状态" to current.order.status.label(),
                                    "金额" to current.order.totalAmount,
                                ),
                                rightLabels = listOf("101.9", "90.5", "78.5", "67.1"),
                                bottomLabels = listOf("创建", "支付", "确认", "完成"),
                            )
                        }
                        item {
                            VpnGlassCard {
                                Text(
                                    text = "订阅档位信息",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                )
                                VpnLabelValueRow(label = "套餐名称", value = current.order.planName)
                                VpnLabelValueRow(label = "购买周期", value = current.order.duration)
                                VpnLabelValueRow(label = "标价", value = current.order.amount)
                                VpnLabelValueRow(label = "应付", value = current.order.totalAmount, valueColor = current.order.status.accent())
                            }
                        }
                        item {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            VpnGlassCard {
                                Text(
                                    text = "审计时间线",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                )
                                VpnLabelValueRow(label = "创建时间", value = dateFormat.format(current.order.createdAt))
                                current.order.paidAt?.let {
                                    VpnLabelValueRow(label = "支付时间", value = dateFormat.format(it))
                                }
                                current.order.expiresAt?.let {
                                    VpnLabelValueRow(label = "过期时间", value = dateFormat.format(it))
                                }
                                VpnLabelValueRow(label = "支付轨道", value = current.order.paymentMethod)
                                current.order.txHash?.let {
                                    VpnLabelValueRow(label = "交易哈希", value = it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DetailOrderStatus.label(): String {
    return when (this) {
        DetailOrderStatus.PENDING -> "PENDING"
        DetailOrderStatus.PAID -> "PAID"
        DetailOrderStatus.COMPLETED -> "COMPLETED"
        DetailOrderStatus.CANCELLED -> "CANCELLED"
        DetailOrderStatus.REFUNDED -> "REFUNDED"
    }
}

private fun DetailOrderStatus.title(): String {
    return when (this) {
        DetailOrderStatus.PENDING -> "待支付"
        DetailOrderStatus.PAID -> "已支付，待激活"
        DetailOrderStatus.COMPLETED -> "套餐已交付"
        DetailOrderStatus.CANCELLED -> "订单已关闭"
        DetailOrderStatus.REFUNDED -> "退款完成"
    }
}

private fun DetailOrderStatus.accent(): Color {
    return when (this) {
        DetailOrderStatus.PENDING -> ControlPlaneTokens.audit(AuditState.Warn).accent
        DetailOrderStatus.PAID -> ControlPlaneTokens.audit(AuditState.Ok).accent
        DetailOrderStatus.COMPLETED -> ControlPlaneTokens.audit(AuditState.Ok).accent
        DetailOrderStatus.CANCELLED -> ControlPlaneTokens.audit(AuditState.Unknown).accent
        DetailOrderStatus.REFUNDED -> ControlPlaneTokens.audit(AuditState.Critical).accent
    }
}

@Preview
@Composable
private fun OrderDetailPagePreview() {
    MaterialTheme {
        OrderDetailPage()
    }
}
