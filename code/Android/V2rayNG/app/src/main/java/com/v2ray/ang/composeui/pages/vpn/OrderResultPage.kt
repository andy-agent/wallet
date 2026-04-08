package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
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
    data object Idle : OrderResultState()
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
            OrderResultType.SUCCESS -> "支付成功，套餐已准备激活。"
            OrderResultType.FAILED -> "支付失败，请重试或联系客服。"
            OrderResultType.PENDING -> "订单处理中，请稍后查看最终结果。"
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = VpnPageHorizontalPadding, vertical = VpnPageTopPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VpnCenterTopBar(
                    title = "支付结果",
                    onBack = onNavigateToHome,
                )
                VpnGlassCard {
                    Text(
                        text = "订单背景页",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                    Text(
                        text = "VPN 套餐支付结果",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    VpnLabelValueRow(label = "订单号", value = orderId.ifBlank { "Unknown" })
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(VpnSheetScrim),
            )
            when (val current = state) {
                OrderResultState.Idle -> {
                    VpnBottomSheet(
                        modifier = Modifier.align(Alignment.BottomCenter),
                    ) {
                        Text(
                            text = "正在查询结果",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )
                        Text(
                            text = "同步订单结果并准备后续动作。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    }
                }

                is OrderResultState.Loaded -> {
                    VpnBottomSheet(
                        modifier = Modifier.align(Alignment.BottomCenter),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(current.resultType.accent().copy(alpha = 0.16f), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = current.resultType.icon(),
                                    contentDescription = null,
                                    tint = current.resultType.accent(),
                                )
                            }
                            Column {
                                Text(
                                    text = current.resultType.title(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                )
                                Text(
                                    text = current.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                )
                            }
                        }
                        VpnLabelValueRow(label = "订单号", value = current.orderId.ifBlank { "Unknown" })
                        VpnLabelValueRow(label = "支付金额", value = current.amount)
                        VpnLabelValueRow(label = "状态", value = current.resultType.title(), valueColor = current.resultType.accent())
                        current.txHash?.let {
                            VpnLabelValueRow(label = "交易哈希", value = it)
                        }
                        when (current.resultType) {
                            OrderResultType.SUCCESS -> {
                                VpnPrimaryButton(
                                    text = "返回首页",
                                    onClick = onNavigateToHome,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                VpnSecondaryButton(
                                    text = "查看订单",
                                    onClick = onNavigateToOrders,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .navigationBarsPadding(),
                                )
                            }

                            OrderResultType.FAILED -> {
                                VpnPrimaryButton(
                                    text = "重新支付",
                                    onClick = onRetry,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                VpnSecondaryButton(
                                    text = "返回首页",
                                    onClick = onNavigateToHome,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .navigationBarsPadding(),
                                )
                            }

                            OrderResultType.PENDING -> {
                                VpnPrimaryButton(
                                    text = "查看订单",
                                    onClick = onNavigateToOrders,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                VpnSecondaryButton(
                                    text = "返回首页",
                                    onClick = onNavigateToHome,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .navigationBarsPadding(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun OrderResultType.title(): String {
    return when (this) {
        OrderResultType.SUCCESS -> "支付成功"
        OrderResultType.FAILED -> "支付失败"
        OrderResultType.PENDING -> "处理中"
    }
}

private fun OrderResultType.accent(): Color {
    return when (this) {
        OrderResultType.SUCCESS -> VpnAccent
        OrderResultType.FAILED -> AppError
        OrderResultType.PENDING -> Color(0xFFFFB14A)
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
