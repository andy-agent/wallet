package com.cryptovpn.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.*

// Order Checkout State
sealed class OrderCheckoutState {
    object Loading : OrderCheckoutState()
    data class AwaitingPayment(
        val orderId: String,
        val planName: String,
        val planDuration: String,
        val amount: String,
        val currency: String,
        val timeRemainingSeconds: Int,
        val qrCodeData: String,
        val paymentAddress: String,
        val network: String
    ) : OrderCheckoutState()
    data class PaymentDetected(val message: String) : OrderCheckoutState()
    data class Confirming(val txHash: String?) : OrderCheckoutState()
    data class Expired(val orderId: String) : OrderCheckoutState()
    data class Error(val message: String) : OrderCheckoutState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCheckoutPage(
    state: OrderCheckoutState = OrderCheckoutState.Loading,
    onBackClick: () -> Unit = {},
    onCopyAddressClick: () -> Unit = {},
    onUseWalletClick: () -> Unit = {},
    onPaidClick: () -> Unit = {},
    onRefreshStatusClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("确认订单", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundPrimary)
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        when (state) {
            is OrderCheckoutState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            is OrderCheckoutState.Error -> {
                ErrorState(
                    title = "加载失败",
                    message = (state as OrderCheckoutState.Error).message,
                    onRetry = onRefreshStatusClick
                )
            }
            is OrderCheckoutState.AwaitingPayment -> {
                OrderCheckoutContent(
                    state = state as OrderCheckoutState.AwaitingPayment,
                    paddingValues = paddingValues,
                    onCopyAddressClick = onCopyAddressClick,
                    onUseWalletClick = onUseWalletClick,
                    onPaidClick = onPaidClick,
                    onRefreshStatusClick = onRefreshStatusClick
                )
            }
            is OrderCheckoutState.PaymentDetected -> {
                PaymentDetectedContent(
                    message = (state as OrderCheckoutState.PaymentDetected).message,
                    paddingValues = paddingValues
                )
            }
            is OrderCheckoutState.Confirming -> {
                ConfirmingContent(
                    txHash = (state as OrderCheckoutState.Confirming).txHash,
                    paddingValues = paddingValues
                )
            }
            is OrderCheckoutState.Expired -> {
                ExpiredContent(
                    orderId = (state as OrderCheckoutState.Expired).orderId,
                    paddingValues = paddingValues,
                    onReorderClick = onBackClick
                )
            }
        }
    }
}

@Composable
private fun OrderCheckoutContent(
    state: OrderCheckoutState.AwaitingPayment,
    paddingValues: PaddingValues,
    onCopyAddressClick: () -> Unit,
    onUseWalletClick: () -> Unit,
    onPaidClick: () -> Unit,
    onRefreshStatusClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Order ID
        Text(
            text = "订单号: ${state.orderId}",
            color = TextTertiary,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Order Info Card
        OrderInfoCard(
            planName = state.planName,
            planDuration = state.planDuration,
            amount = state.amount,
            currency = state.currency
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Countdown Timer
        CountdownDisplay(secondsRemaining = state.timeRemainingSeconds)

        Spacer(modifier = Modifier.height(24.dp))

        // QR Code Area
        QRCodeDisplay(
            data = state.qrCodeData,
            size = 200.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Instructions
        Text(
            text = "请使用 ${state.network} 钱包扫码支付",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Address
        PaymentAddressCard(
            address = state.paymentAddress,
            onCopyClick = onCopyAddressClick
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        PrimaryButton(
            text = "使用内置钱包支付",
            onClick = onUseWalletClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryButton(
                text = "我已支付",
                onClick = onPaidClick,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = "刷新状态",
                onClick = onRefreshStatusClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OrderInfoCard(
    planName: String,
    planDuration: String,
    amount: String,
    currency: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundSecondary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = planName,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = planDuration,
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = DividerColor)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "应付金额",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "$amount $currency",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CountdownDisplay(secondsRemaining: Int) {
    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            tint = Warning,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "支付倒计时: ",
            color = Warning,
            fontSize = 14.sp
        )
        Text(
            text = timeText,
            color = Warning,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QRCodeDisplay(
    data: String,
    size: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, BorderDefault, RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder for QR Code - in real implementation use QR code library
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = "QR Code",
                modifier = Modifier.size(size * 0.6f),
                tint = Color.Black
            )
            Text(
                text = "[QR Code]",
                color = Color.Black,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun PaymentAddressCard(
    address: String,
    onCopyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundSecondary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "收款地址",
                color = TextTertiary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = address.take(12) + "..." + address.takeLast(8),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onCopyClick) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "复制地址",
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentDetectedContent(
    message: String,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Success, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "检测到支付",
                color = Success,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ConfirmingContent(
    txHash: String?,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Primary, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "确认中...",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "正在等待区块链确认",
                color = TextSecondary,
                fontSize = 14.sp
            )
            txHash?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "TX: ${it.take(10)}...${it.takeLast(6)}",
                    color = Primary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ExpiredContent(
    orderId: String,
    paddingValues: PaddingValues,
    onReorderClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.TimerOff,
                contentDescription = null,
                tint = Warning,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "订单已过期",
                color = Warning,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "订单号: $orderId",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "支付超时，请重新下单",
                color = TextTertiary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            PrimaryButton(
                text = "重新下单",
                onClick = onReorderClick,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }
    }
}

// Previews
@Preview
@Composable
fun OrderCheckoutPagePreview() {
    CryptoVPNTheme {
        OrderCheckoutPage(
            state = OrderCheckoutState.AwaitingPayment(
                orderId = "ORD20241201001",
                planName = "年度套餐",
                planDuration = "12个月",
                amount = "0.0523",
                currency = "ETH",
                timeRemainingSeconds = 899,
                qrCodeData = "ethereum:0x742d...?amount=0.0523",
                paymentAddress = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb8",
                network = "Ethereum"
            )
        )
    }
}

@Preview
@Composable
fun OrderCheckoutPageConfirmingPreview() {
    CryptoVPNTheme {
        OrderCheckoutPage(
            state = OrderCheckoutState.Confirming("0xabc123def456")
        )
    }
}