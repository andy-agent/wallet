package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.order.VpnOrderBridge
import com.v2ray.ang.composeui.theme.Error as AppError
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.Warning
import com.v2ray.ang.payment.PaymentConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class OrderResultType {
    SUCCESS,
    FAILED,
    PENDING,
}

sealed class OrderResultState {
    object Idle : OrderResultState()
    data class Loaded(
        val resultType: OrderResultType,
        val orderId: String,
        val amount: String,
        val message: String,
        val txHash: String? = null,
    ) : OrderResultState()
}

class OrderResultViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<OrderResultState>(OrderResultState.Idle)
    val state: StateFlow<OrderResultState> = _state
    private val bridge = VpnOrderBridge(application)

    fun loadResult(orderId: String, fallbackType: OrderResultType) {
        if (orderId.isBlank()) {
            _state.value = fallbackLoaded(fallbackType, "", "未知")
            return
        }
        viewModelScope.launch {
            bridge.loadOrderResult(orderId)
                .onSuccess { data ->
                    val type = when (data.status) {
                        PaymentConfig.OrderStatus.PAID_SUCCESS,
                        PaymentConfig.OrderStatus.FULFILLED,
                        -> OrderResultType.SUCCESS

                        PaymentConfig.OrderStatus.PENDING_PAYMENT,
                        PaymentConfig.OrderStatus.SEEN_ONCHAIN,
                        PaymentConfig.OrderStatus.CONFIRMING,
                        -> OrderResultType.PENDING

                        else -> OrderResultType.FAILED
                    }
                    _state.value = fallbackLoaded(type, data.orderNo, data.amount, data.txHash)
                }
                .onFailure {
                    _state.value = fallbackLoaded(fallbackType, orderId, "未知")
                }
        }
    }

    private fun fallbackLoaded(
        resultType: OrderResultType,
        orderId: String,
        amount: String,
        txHash: String? = null,
    ): OrderResultState.Loaded {
        val message = when (resultType) {
            OrderResultType.SUCCESS -> "支付成功，您的套餐已激活。"
            OrderResultType.FAILED -> "支付失败，请重试或联系客服。"
            OrderResultType.PENDING -> "订单处理中，请稍后查看结果。"
        }
        return OrderResultState.Loaded(resultType, orderId, amount, message, txHash)
    }
}

@Composable
fun OrderResultPage(
    viewModel: OrderResultViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    orderId: String = "",
    resultType: OrderResultType = OrderResultType.SUCCESS,
    onNavigateToHome: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(orderId, resultType) {
        viewModel.loadResult(orderId = orderId, fallbackType = resultType)
    }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
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
                        title = "Order Result",
                        subtitle = "Clear post-payment state with actions grouped by outcome.",
                    )
                }
                when (val current = state) {
                    OrderResultState.Idle -> {
                        item {
                            VpnLoadingPanel(
                                title = "Loading payment result",
                                subtitle = "正在查询订单最终状态。",
                            )
                        }
                    }

                    is OrderResultState.Loaded -> {
                        item {
                            ResultHeroCard(state = current)
                        }
                        item {
                            ResultDetailsCard(state = current)
                        }
                        item {
                            ResultActionCard(
                                resultType = current.resultType,
                                onNavigateToHome = onNavigateToHome,
                                onNavigateToOrders = onNavigateToOrders,
                                onRetry = onRetry,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultHeroCard(state: OrderResultState.Loaded) {
    VpnGlassCard(accent = state.resultType.accent()) {
        VpnStatusChip(
            text = state.resultType.label(),
            containerColor = state.resultType.accent().copy(alpha = 0.16f),
            contentColor = state.resultType.accent(),
        )
        Icon(
            imageVector = state.resultType.icon(),
            contentDescription = null,
            tint = state.resultType.accent(),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = state.resultType.title(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            text = state.message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "Order",
                value = state.orderId.ifBlank { "Unknown" },
            )
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "Amount",
                value = state.amount,
            )
        }
    }
}

@Composable
private fun ResultDetailsCard(state: OrderResultState.Loaded) {
    VpnGlassCard(accent = state.resultType.accent()) {
        Text(
            text = "Settlement Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        VpnLabelValueRow(label = "Order No.", value = state.orderId.ifBlank { "Unknown" })
        VpnLabelValueRow(label = "Amount", value = state.amount)
        VpnLabelValueRow(label = "Status", value = state.resultType.title(), valueColor = state.resultType.accent())
        state.txHash?.let {
            VpnLabelValueRow(label = "Transaction Hash", value = it)
        }
    }
}

@Composable
private fun ResultActionCard(
    resultType: OrderResultType,
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onRetry: () -> Unit,
) {
    VpnGlassCard(accent = resultType.accent()) {
        Text(
            text = "Next Action",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Text(
            text = resultType.actionHint(),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        when (resultType) {
            OrderResultType.SUCCESS -> {
                VpnPrimaryButton(
                    text = "Return to Home",
                    onClick = onNavigateToHome,
                    modifier = Modifier.fillMaxWidth(),
                )
                VpnSecondaryButton(
                    text = "Open Orders",
                    onClick = onNavigateToOrders,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            OrderResultType.FAILED -> {
                VpnPrimaryButton(
                    text = "Retry Payment",
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                )
                VpnSecondaryButton(
                    text = "Back to Home",
                    onClick = onNavigateToHome,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            OrderResultType.PENDING -> {
                VpnPrimaryButton(
                    text = "View Orders",
                    onClick = onNavigateToOrders,
                    modifier = Modifier.fillMaxWidth(),
                )
                VpnSecondaryButton(
                    text = "Back to Home",
                    onClick = onNavigateToHome,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private fun OrderResultType.title(): String {
    return when (this) {
        OrderResultType.SUCCESS -> "Payment Successful"
        OrderResultType.FAILED -> "Payment Failed"
        OrderResultType.PENDING -> "Processing"
    }
}

private fun OrderResultType.label(): String {
    return when (this) {
        OrderResultType.SUCCESS -> "SUCCESS"
        OrderResultType.FAILED -> "FAILED"
        OrderResultType.PENDING -> "PENDING"
    }
}

private fun OrderResultType.actionHint(): String {
    return when (this) {
        OrderResultType.SUCCESS -> "资金已完成入账，可回首页继续使用 VPN，或去订单页查看完整明细。"
        OrderResultType.FAILED -> "建议重新进入支付流程，必要时联系支持或改用其他支付轨道。"
        OrderResultType.PENDING -> "链上状态仍在推进，订单页会承接后续确认与结果刷新。"
    }
}

private fun OrderResultType.accent(): Color {
    return when (this) {
        OrderResultType.SUCCESS -> Primary
        OrderResultType.FAILED -> AppError
        OrderResultType.PENDING -> Warning
    }
}

private fun OrderResultType.icon() = when (this) {
    OrderResultType.SUCCESS -> Icons.Default.CheckCircle
    OrderResultType.FAILED -> Icons.Default.Close
    OrderResultType.PENDING -> Icons.Default.Schedule
}

@Preview
@Composable
private fun OrderResultPagePreview() {
    MaterialTheme {
        OrderResultPage()
    }
}
