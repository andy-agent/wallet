package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.v2ray.ang.composeui.bridge.order.VpnOrderBridge
import com.v2ray.ang.composeui.theme.BackgroundSecondary
import com.v2ray.ang.composeui.theme.BorderDefault
import com.v2ray.ang.composeui.theme.GlowBlue
import com.v2ray.ang.composeui.theme.Info
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.Warning
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class PaymentMethod {
    WALLET,
    CRYPTO,
}

sealed class OrderCheckoutState {
    object Idle : OrderCheckoutState()
    object Loading : OrderCheckoutState()
    data class Loaded(
        val orderId: String,
        val planName: String,
        val duration: String,
        val amount: String,
        val discount: String?,
        val totalAmount: String,
        val selectedPaymentMethod: PaymentMethod,
        val walletAddress: String?,
        val expiresIn: Int,
    ) : OrderCheckoutState()
    data class Error(val message: String) : OrderCheckoutState()
}

class OrderCheckoutViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<OrderCheckoutState>(OrderCheckoutState.Idle)
    val state: StateFlow<OrderCheckoutState> = _state
    private val bridge = VpnOrderBridge(application)
    private var currentPlanCode: String = ""

    fun loadOrderDetails(planCode: String) {
        if (planCode.isBlank()) {
            _state.value = OrderCheckoutState.Error("缺少套餐参数")
            return
        }
        currentPlanCode = planCode
        createOrderWithCurrentMethod()
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        val currentState = _state.value
        if (currentState is OrderCheckoutState.Loaded) {
            _state.value = currentState.copy(selectedPaymentMethod = method)
            createOrderWithCurrentMethod()
        }
    }

    private fun createOrderWithCurrentMethod() {
        val selected = (state.value as? OrderCheckoutState.Loaded)?.selectedPaymentMethod
            ?: PaymentMethod.WALLET
        _state.value = OrderCheckoutState.Loading
        viewModelScope.launch {
            bridge.createOrder(
                planCode = currentPlanCode,
                useWalletPath = selected == PaymentMethod.WALLET,
            ).onSuccess { data ->
                _state.value = OrderCheckoutState.Loaded(
                    orderId = data.orderNo,
                    planName = data.planName,
                    duration = data.duration,
                    amount = data.amount,
                    discount = null,
                    totalAmount = data.totalAmount,
                    selectedPaymentMethod = selected,
                    walletAddress = data.receiveAddress,
                    expiresIn = data.expiresInSeconds,
                )
            }.onFailure { error ->
                _state.value = OrderCheckoutState.Error(error.message ?: "创建订单失败")
            }
        }
    }
}

fun generateQRCode(content: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    } catch (_: Exception) {
        null
    }
}

@Composable
fun OrderCheckoutPage(
    viewModel: OrderCheckoutViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    planId: String = "",
    onNavigateBack: () -> Unit = {},
    onPayWithWallet: (String, String) -> Unit = { _, _ -> },
    onPayWithCrypto: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(planId) {
        viewModel.loadOrderDetails(planId)
    }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                val loadedState = state as? OrderCheckoutState.Loaded
                if (loadedState != null) {
                    CheckoutActionBar(
                        totalAmount = loadedState.totalAmount,
                        method = loadedState.selectedPaymentMethod,
                        onPay = {
                            when (loadedState.selectedPaymentMethod) {
                                PaymentMethod.WALLET -> onPayWithWallet(
                                    loadedState.orderId,
                                    loadedState.totalAmount,
                                )

                                PaymentMethod.CRYPTO -> onPayWithCrypto(loadedState.orderId)
                            }
                        },
                    )
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
                        title = "Checkout",
                        subtitle = "Order desk with clear pay rail selection and strong call to action.",
                        onBack = onNavigateBack,
                    )
                }

                when (val current = state) {
                    is OrderCheckoutState.Loading,
                    OrderCheckoutState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "Preparing checkout desk",
                                subtitle = "正在创建订单并拉取支付参数。",
                            )
                        }
                    }

                    is OrderCheckoutState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "Checkout unavailable",
                                subtitle = current.message,
                            )
                        }
                    }

                    is OrderCheckoutState.Loaded -> {
                        item {
                            CheckoutHero(state = current)
                        }
                        item {
                            VpnSectionHeading(
                                title = "Payment Rail",
                                subtitle = "Switch between wallet and on-chain settlement without changing the bridge behavior.",
                            )
                        }
                        item {
                            PaymentRailCard(
                                icon = Icons.Default.AccountBalanceWallet,
                                title = "Wallet Balance",
                                subtitle = "Fast confirmation through the existing wallet confirmation step.",
                                badge = "PRIMARY",
                                accent = Primary,
                                isSelected = current.selectedPaymentMethod == PaymentMethod.WALLET,
                                onClick = { viewModel.selectPaymentMethod(PaymentMethod.WALLET) },
                            )
                        }
                        item {
                            PaymentRailCard(
                                icon = Icons.Default.CurrencyBitcoin,
                                title = "Crypto Address",
                                subtitle = "TRON / USDT style on-chain payment with QR and address exposure.",
                                badge = "ON-CHAIN",
                                accent = Warning,
                                isSelected = current.selectedPaymentMethod == PaymentMethod.CRYPTO,
                                onClick = { viewModel.selectPaymentMethod(PaymentMethod.CRYPTO) },
                            )
                        }
                        item {
                            CheckoutSummaryCard(state = current)
                        }
                        item {
                            if (current.selectedPaymentMethod == PaymentMethod.WALLET) {
                                WalletRailDetail(amount = current.totalAmount)
                            } else {
                                CryptoRailDetail(
                                    walletAddress = current.walletAddress.orEmpty(),
                                    amount = current.totalAmount,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutHero(state: OrderCheckoutState.Loaded) {
    val countdown = rememberCountdownSeconds(state.expiresIn)
    VpnHeroCard(
        eyebrow = "ORDER ${state.orderId.takeLast(6)}",
        title = "Confirm payment route for ${state.planName}",
        subtitle = "收银台把订单、金额、支付轨道和过期时间抬到第一层，同时保持现有 createOrder 兼容。",
        accent = if (state.selectedPaymentMethod == PaymentMethod.WALLET) Primary else Warning,
        metrics = listOf(
            VpnHeroMetric("Rail", state.selectedPaymentMethod.label()),
            VpnHeroMetric("Amount", state.totalAmount),
            VpnHeroMetric("Expires", countdown.toCountdownLabel()),
        ),
    )
}

@Composable
private fun PaymentRailCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String,
    accent: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    VpnGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        accent = accent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = accent.copy(alpha = 0.16f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        androidx.compose.material3.Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accent,
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }
            VpnStatusChip(
                text = if (isSelected) "SELECTED" else badge,
                containerColor = accent.copy(alpha = 0.16f),
                contentColor = accent,
            )
        }
        VpnPrimaryButton(
            text = if (isSelected) "Current Payment Rail" else "Use This Rail",
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CheckoutSummaryCard(state: OrderCheckoutState.Loaded) {
    VpnGlassCard(accent = GlowBlue) {
        Text(
            text = "Order Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        VpnLabelValueRow(label = "Order No.", value = state.orderId)
        VpnLabelValueRow(label = "Package", value = state.planName)
        VpnLabelValueRow(label = "Duration", value = state.duration)
        VpnLabelValueRow(label = "Listed Price", value = state.amount)
        state.discount?.let {
            VpnLabelValueRow(
                label = "Discount",
                value = it,
                valueColor = Primary,
            )
        }
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
        VpnLabelValueRow(
            label = "Payable",
            value = state.totalAmount,
            valueColor = if (state.selectedPaymentMethod == PaymentMethod.WALLET) Primary else Warning,
        )
    }
}

@Composable
private fun WalletRailDetail(amount: String) {
    VpnGlassCard(accent = Primary) {
        Text(
            text = "Wallet Confirmation",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Text(
            text = "下一步会进入支付确认页，沿用现有 refreshOrder 结果判断和成功回跳。",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "Settlement",
                value = "Wallet",
            )
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "Amount",
                value = amount,
            )
        }
        VpnStatusChip(text = "Password confirmation required")
    }
}

@Composable
private fun CryptoRailDetail(
    walletAddress: String,
    amount: String,
) {
    val qrBitmap = remember(walletAddress) {
        generateQRCode(walletAddress)
    }

    VpnGlassCard(accent = Warning) {
        Text(
            text = "Crypto Address",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Text(
            text = "链上支付仍走既有订单桥接，只在视觉上切成更明确的扫码支付二级层。",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )

        qrBitmap?.let { bitmap ->
            Surface(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Warning.copy(alpha = 0.24f)),
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .size(220.dp)
                        .padding(18.dp),
                )
            }
        }

        VpnLabelValueRow(label = "Payable", value = amount, valueColor = Warning)

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = BackgroundSecondary.copy(alpha = 0.92f),
            border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.84f)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = walletAddress.ifBlank { "Address unavailable" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                    )
                }
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = Info,
                )
            }
        }
    }
}

@Composable
private fun CheckoutActionBar(
    totalAmount: String,
    method: PaymentMethod,
    onPay: () -> Unit,
) {
    Surface(
        color = BackgroundSecondary.copy(alpha = 0.98f),
        border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.9f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VpnPageHorizontalPadding, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Payable",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
                Text(
                    text = totalAmount,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (method == PaymentMethod.WALLET) Primary else Warning,
                )
            }
            VpnPrimaryButton(
                text = if (method == PaymentMethod.WALLET) "Go to Wallet Confirm" else "I Have Sent Payment",
                onClick = onPay,
            )
        }
    }
}

@Composable
private fun rememberCountdownSeconds(initialSeconds: Int): Int {
    var remaining by remember(initialSeconds) {
        mutableStateOf(initialSeconds.coerceAtLeast(0))
    }
    LaunchedEffect(initialSeconds) {
        remaining = initialSeconds.coerceAtLeast(0)
        while (remaining > 0) {
            delay(1000)
            remaining -= 1
        }
    }
    return remaining
}

private fun Int.toCountdownLabel(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

private fun PaymentMethod.label(): String {
    return when (this) {
        PaymentMethod.WALLET -> "Wallet"
        PaymentMethod.CRYPTO -> "Crypto"
    }
}

@Preview
@Composable
private fun OrderCheckoutPagePreview() {
    MaterialTheme {
        OrderCheckoutPage()
    }
}
