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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.v2ray.ang.composeui.bridge.order.VpnOrderBridge
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class PaymentMethod {
    WALLET,
    CRYPTO,
}

sealed class OrderCheckoutState {
    data object Idle : OrderCheckoutState()
    data object Loading : OrderCheckoutState()
    data class Loaded(
        val orderId: String,
        val planName: String,
        val duration: String,
        val amount: String,
        val discount: String?,
        val totalAmount: String,
        val selectedPaymentMethod: PaymentMethod,
        val walletAddress: String?,
        val qrText: String?,
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
                    qrText = data.qrText,
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
    val selectedRange = remember { mutableIntStateOf(0) }

    LaunchedEffect(planId) {
        viewModel.loadOrderDetails(planId)
    }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                VpnModeDock(
                    items = listOf(
                        VpnDockItem("钱包", Icons.Default.AccountBalanceWallet),
                        VpnDockItem("链上", Icons.Default.CurrencyBitcoin),
                        VpnDockItem("订单", Icons.Default.ReceiptLong),
                        VpnDockItem("帮助", Icons.Default.HelpOutline),
                    ),
                    selectedIndex = when ((state as? OrderCheckoutState.Loaded)?.selectedPaymentMethod) {
                        PaymentMethod.CRYPTO -> 1
                        else -> 0
                    },
                    onSelect = { index ->
                        when (index) {
                            0 -> viewModel.selectPaymentMethod(PaymentMethod.WALLET)
                            1 -> viewModel.selectPaymentMethod(PaymentMethod.CRYPTO)
                        }
                    },
                    onClose = onNavigateBack,
                    modifier = Modifier.padding(horizontal = VpnPageHorizontalPadding, vertical = 12.dp),
                )
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
                    VpnCenterTopBar(
                        title = "订单支付",
                        onBack = onNavigateBack,
                        backIcon = Icons.Default.Close,
                    )
                }
                item {
                    VpnWarningStrip(
                        text = "当前订单使用统一结算控制面，支付轨道可切换，订单与支付桥接保持原样。",
                    )
                }

                when (val current = state) {
                    is OrderCheckoutState.Loading,
                    OrderCheckoutState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "正在创建订单",
                                subtitle = "正在同步结算轨道、订单快照与支付状态。",
                            )
                        }
                    }

                    is OrderCheckoutState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "订单支付不可用",
                                subtitle = current.message,
                            )
                        }
                    }

                    is OrderCheckoutState.Loaded -> {
                        item {
                            VpnSwapDeck(
                                onSwap = {
                                    viewModel.selectPaymentMethod(
                                        if (current.selectedPaymentMethod == PaymentMethod.WALLET) {
                                            PaymentMethod.CRYPTO
                                        } else {
                                            PaymentMethod.WALLET
                                        },
                                    )
                                },
                                topCard = {
                                    SwapPaymentCard(state = current)
                                },
                                bottomCard = {
                                    SwapPlanCard(state = current)
                                },
                            )
                        }
                        item {
                            VpnPrimaryButton(
                                text = if (current.selectedPaymentMethod == PaymentMethod.WALLET) "去钱包确认" else "我已完成转账",
                                onClick = {
                                    when (current.selectedPaymentMethod) {
                                        PaymentMethod.WALLET -> onPayWithWallet(current.orderId, current.totalAmount)
                                        PaymentMethod.CRYPTO -> onPayWithCrypto(current.orderId)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        item {
                            VpnGlassCard {
                                Text(
                                    text = "结算快照",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                )
                                VpnLabelValueRow(label = "订单号", value = current.orderId)
                                VpnLabelValueRow(label = "套餐", value = current.planName)
                                VpnLabelValueRow(label = "周期", value = current.duration)
                                VpnLabelValueRow(label = "标价", value = current.amount)
                                VpnLabelValueRow(label = "应付", value = current.totalAmount, valueColor = VpnAccent)
                            }
                        }
                        item {
                            VpnRangeSelector(
                                labels = listOf("支付", "订单", "激活"),
                                selectedIndex = selectedRange.intValue,
                                onSelect = { selectedRange.intValue = it },
                                trailingIcon = Icons.Default.SwapVert,
                                onTrailingClick = {
                                    viewModel.selectPaymentMethod(
                                        if (current.selectedPaymentMethod == PaymentMethod.WALLET) {
                                            PaymentMethod.CRYPTO
                                        } else {
                                            PaymentMethod.WALLET
                                        },
                                    )
                                },
                            )
                        }
                        item {
                            if (current.selectedPaymentMethod == PaymentMethod.WALLET) {
                                WalletRailDetail(current)
                            } else {
                                CryptoRailDetail(current)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwapPaymentCard(state: OrderCheckoutState.Loaded) {
    val countdown = rememberCountdownSeconds(state.expiresIn)
    val railLabel = if (state.selectedPaymentMethod == PaymentMethod.WALLET) "钱包余额" else "链上支付"
    val assetLabel = if (state.selectedPaymentMethod == PaymentMethod.WALLET) "SOL" else "USDT"
    Text(
        text = "支付方式",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
    )
    Text(
        text = state.totalAmount,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (state.selectedPaymentMethod == PaymentMethod.WALLET) {
                "$railLabel · 快速确认"
            } else {
                "$railLabel · 扫码转账"
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            VpnCodeBadge(text = assetLabel.take(1), backgroundColor = VpnAccentSoft, contentColor = VpnAccent)
            Text(
                text = assetLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
        }
    }
    Text(
        text = "剩余 ${countdown.toCountdownLabel()}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
    )
}

@Composable
private fun SwapPlanCard(state: OrderCheckoutState.Loaded) {
    Text(
        text = "购买套餐",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
    )
    Text(
        text = state.amount,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = state.planName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
            )
            Text(
                text = state.duration,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
        VpnStatusChip(
            text = "VPN",
            containerColor = VpnAccentSoft,
            contentColor = VpnAccent,
        )
    }
}

@Composable
private fun WalletRailDetail(state: OrderCheckoutState.Loaded) {
    VpnGlassCard {
        Text(
            text = "钱包结算轨道",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        Text(
            text = "下一步进入钱包授权校验，沿用现有 refreshOrder 成功/失败回桥。",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "支付轨道",
                value = "Wallet / Internal",
            )
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "订单",
                value = state.orderId.takeTrailing(6),
            )
        }
    }
}

@Composable
private fun CryptoRailDetail(state: OrderCheckoutState.Loaded) {
    val qrBitmap = remember(state.qrText ?: state.walletAddress.orEmpty()) {
        generateQRCode(state.qrText ?: state.walletAddress.orEmpty())
    }
    VpnGlassCard {
        Text(
            text = "链上结算地址",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        qrBitmap?.let { bitmap ->
            Surface(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, VpnAccentSoft),
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
        VpnLabelValueRow(label = "应付金额", value = state.totalAmount, valueColor = VpnAccent)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = VpnSurfaceStrong,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = state.walletAddress.orEmpty().ifBlank { "地址暂不可用" },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun rememberCountdownSeconds(initialSeconds: Int): Int {
    val remaining = remember(initialSeconds) { mutableIntStateOf(initialSeconds.coerceAtLeast(0)) }
    LaunchedEffect(initialSeconds) {
        remaining.intValue = initialSeconds.coerceAtLeast(0)
        while (remaining.intValue > 0) {
            delay(1000)
            remaining.intValue -= 1
        }
    }
    return remaining.intValue
}

private fun Int.toCountdownLabel(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Preview
@Composable
private fun OrderCheckoutPagePreview() {
    MaterialTheme {
        OrderCheckoutPage()
    }
}
