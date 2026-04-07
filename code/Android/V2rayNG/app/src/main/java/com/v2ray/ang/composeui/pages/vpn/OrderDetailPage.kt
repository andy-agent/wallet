package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.v2ray.ang.composeui.theme.BackgroundSecondary
import com.v2ray.ang.composeui.theme.BorderDefault
import com.v2ray.ang.composeui.theme.Error as AppError
import com.v2ray.ang.composeui.theme.GlowBlue
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.Warning
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.launch

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
    object Idle : OrderDetailState()
    object Loading : OrderDetailState()
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
                    OrderDetailActionBar(
                        onPay = { onPayOrder(order.id) },
                        onContactSupport = onContactSupport,
                    )
                }
            },
        ) { innerPadding ->
            androidx.compose.foundation.lazy.LazyColumn(
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
                        title = "Order Detail",
                        subtitle = "Status-first breakdown with clear payment and settlement hierarchy.",
                        onBack = onNavigateBack,
                    )
                }
                when (val current = state) {
                    is OrderDetailState.Loading,
                    OrderDetailState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "Loading order detail",
                                subtitle = "正在同步订单详情与支付状态。",
                            )
                        }
                    }

                    is OrderDetailState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "Order detail unavailable",
                                subtitle = current.message,
                            )
                        }
                    }

                    is OrderDetailState.Loaded -> {
                        item {
                            DetailHeroCard(order = current.order)
                        }
                        item {
                            DetailPackageCard(order = current.order)
                        }
                        item {
                            DetailPricingCard(order = current.order)
                        }
                        item {
                            DetailMetaCard(order = current.order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHeroCard(order: OrderDetailData) {
    VpnHeroCard(
        eyebrow = order.status.label(),
        title = order.status.title(),
        subtitle = order.status.description(),
        accent = order.status.accent(),
        metrics = listOf(
            VpnHeroMetric("Amount", order.totalAmount),
            VpnHeroMetric("Method", order.paymentMethod),
            VpnHeroMetric("Order", order.id.takeLast(6)),
        ),
    )
}

@Composable
private fun DetailPackageCard(order: OrderDetailData) {
    VpnGlassCard(accent = GlowBlue) {
        Text(
            text = "Package",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        VpnLabelValueRow(label = "Plan", value = order.planName)
        VpnLabelValueRow(label = "Duration", value = order.duration)
        order.expiresAt?.let { expiresAt ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            VpnLabelValueRow(label = "Expires", value = dateFormat.format(expiresAt))
        }
    }
}

@Composable
private fun DetailPricingCard(order: OrderDetailData) {
    VpnGlassCard(accent = Warning) {
        Text(
            text = "Pricing",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        VpnLabelValueRow(label = "Listed", value = order.amount)
        order.discount?.let {
            VpnLabelValueRow(label = "Discount", value = it, valueColor = Primary)
        }
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
        VpnLabelValueRow(label = "Payable", value = order.totalAmount, valueColor = order.status.accent())
    }
}

@Composable
private fun DetailMetaCard(order: OrderDetailData) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    VpnGlassCard(accent = order.status.accent()) {
        Text(
            text = "Order Metadata",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        VpnLabelValueRow(label = "Order No.", value = order.id)
        VpnLabelValueRow(label = "Created", value = dateFormat.format(order.createdAt))
        VpnLabelValueRow(label = "Method", value = order.paymentMethod)
        VpnLabelValueRow(label = "Email", value = if (order.email.isBlank()) "Current account" else order.email)
        order.paidAt?.let {
            VpnLabelValueRow(label = "Paid At", value = dateFormat.format(it))
        }
        order.txHash?.let {
            VpnLabelValueRow(label = "Tx Hash", value = it)
        }
    }
}

@Composable
private fun OrderDetailActionBar(
    onPay: () -> Unit,
    onContactSupport: () -> Unit,
) {
    Surface(
        color = BackgroundSecondary.copy(alpha = 0.98f),
        border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.9f)),
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VpnPageHorizontalPadding, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VpnPrimaryButton(
                text = "Continue Payment",
                onClick = onPay,
                modifier = Modifier.fillMaxWidth(),
            )
            VpnSecondaryButton(
                text = "Contact Support",
                onClick = onContactSupport,
                modifier = Modifier.fillMaxWidth(),
            )
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
        DetailOrderStatus.PENDING -> "Awaiting Settlement"
        DetailOrderStatus.PAID -> "Paid, Activating"
        DetailOrderStatus.COMPLETED -> "Package Fulfilled"
        DetailOrderStatus.CANCELLED -> "Order Closed"
        DetailOrderStatus.REFUNDED -> "Refunded"
    }
}

private fun DetailOrderStatus.description(): String {
    return when (this) {
        DetailOrderStatus.PENDING -> "待支付订单保持底部强 CTA，方便直接回到支付确认流程。"
        DetailOrderStatus.PAID -> "资金已到账，当前正在进行套餐激活。"
        DetailOrderStatus.COMPLETED -> "订单已交付，VPN 业务链路已完成。"
        DetailOrderStatus.CANCELLED -> "订单已取消或超过支付时效。"
        DetailOrderStatus.REFUNDED -> "支付异常或退款已处理。"
    }
}

private fun DetailOrderStatus.accent(): Color {
    return when (this) {
        DetailOrderStatus.PENDING -> Warning
        DetailOrderStatus.PAID -> GlowBlue
        DetailOrderStatus.COMPLETED -> Primary
        DetailOrderStatus.CANCELLED -> Color(0xFF6D7B91)
        DetailOrderStatus.REFUNDED -> AppError
    }
}

@Preview
@Composable
private fun OrderDetailPagePreview() {
    MaterialTheme {
        OrderDetailPage()
    }
}
