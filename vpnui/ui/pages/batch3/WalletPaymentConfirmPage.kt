package com.cryptovpn.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.CryptoVPNTheme

// ==================== Data Models ====================

data class WalletPaymentConfirmState(
    val tokenSymbol: String = "USDT",
    val tokenIcon: String = "",
    val tokenName: String = "Tether USD",
    val paymentAmount: String = "29.99",
    val networkFee: String = "0.50",
    val totalAmount: String = "30.49",
    val recipientAddress: String = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb",
    val networkName: String = "TRC20",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// ==================== ViewModel ====================

class WalletPaymentConfirmViewModel {
    var state by mutableStateOf(WalletPaymentConfirmState())
        private set

    fun onConfirmClick() {
        state = state.copy(isLoading = true)
        // Simulate payment processing
    }

    fun onCancelClick() {
        // Navigate back
    }

    fun updateState(newState: WalletPaymentConfirmState) {
        state = newState
    }
}

// ==================== Page Composable ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletPaymentConfirmPage(
    viewModel: WalletPaymentConfirmViewModel = remember { WalletPaymentConfirmViewModel() },
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("确认支付", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B1020)
                )
            )
        },
        containerColor = Color(0xFF0B1020)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Asset Info Card
            AssetInfoCard(state = state)

            // Recipient Address Card
            RecipientAddressCard(address = state.recipientAddress)

            // Risk Warning Box
            RiskWarningBox()

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            ActionButtons(
                isLoading = state.isLoading,
                onConfirmClick = {
                    viewModel.onConfirmClick()
                    onConfirmClick()
                },
                onCancelClick = {
                    viewModel.onCancelClick()
                    onCancelClick()
                }
            )
        }
    }
}

@Composable
private fun AssetInfoCard(state: WalletPaymentConfirmState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Token Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Token Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF26A17B), Color(0xFF1A7A5C))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "₮",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = state.tokenName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = state.networkName,
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                }
            }

            Divider(color = Color(0xFF374151))

            // Payment Details
            PaymentDetailRow(label = "支付金额", value = "${state.paymentAmount} ${state.tokenSymbol}")
            PaymentDetailRow(label = "网络费用", value = "${state.networkFee} ${state.tokenSymbol}")

            Divider(color = Color(0xFF374151))

            // Total
            PaymentDetailRow(
                label = "总计",
                value = "${state.totalAmount} ${state.tokenSymbol}",
                isTotal = true
            )
        }
    }
}

@Composable
private fun PaymentDetailRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (isTotal) Color.White else Color(0xFF9CA3AF),
            fontSize = if (isTotal) 16.sp else 14.sp
        )
        Text(
            text = value,
            color = if (isTotal) Color(0xFF22C55E) else Color.White,
            fontSize = if (isTotal) 18.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun RecipientAddressCard(address: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "收款地址",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )
            Text(
                text = address,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun RiskWarningBox() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF3C7).copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color(0xFFF59E0B).copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "风险提示",
                    color = Color(0xFFF59E0B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "请确认收款地址正确，区块链交易不可逆。错误的地址可能导致资金永久丢失。",
                    color = Color(0xFFF59E0B).copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    isLoading: Boolean,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onConfirmClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "确认支付",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        OutlinedButton(
            onClick = onCancelClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF9CA3AF)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color(0xFF374151)
            )
        ) {
            Text(
                text = "取消",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ==================== Preview ====================

@Preview(device = "id:pixel_5")
@Composable
private fun WalletPaymentConfirmPagePreview() {
    CryptoVPNTheme {
        WalletPaymentConfirmPage()
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun WalletPaymentConfirmPageLoadingPreview() {
    CryptoVPNTheme {
        val viewModel = remember { WalletPaymentConfirmViewModel() }
        viewModel.updateState(WalletPaymentConfirmState(isLoading = true))
        WalletPaymentConfirmPage(viewModel = viewModel)
    }
}