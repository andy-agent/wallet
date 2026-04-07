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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.order.VpnOrderBridge
import com.v2ray.ang.composeui.theme.BackgroundSecondary
import com.v2ray.ang.composeui.theme.BorderDefault
import com.v2ray.ang.composeui.theme.Error as AppError
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.payment.PaymentConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WalletPaymentConfirmState {
    object Idle : WalletPaymentConfirmState()
    object Confirming : WalletPaymentConfirmState()
    data class Confirmed(val txHash: String) : WalletPaymentConfirmState()
    data class Error(val message: String) : WalletPaymentConfirmState()
}

class WalletPaymentConfirmViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<WalletPaymentConfirmState>(WalletPaymentConfirmState.Idle)
    val state: StateFlow<WalletPaymentConfirmState> = _state

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible
    private val bridge = VpnOrderBridge(application)

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun confirmPayment(orderId: String) {
        if (_password.value.isBlank()) {
            _state.value = WalletPaymentConfirmState.Error("请输入钱包密码")
            return
        }
        if (orderId.isBlank()) {
            _state.value = WalletPaymentConfirmState.Error("缺少订单号")
            return
        }

        viewModelScope.launch {
            _state.value = WalletPaymentConfirmState.Confirming
            bridge.refreshOrder(orderId)
                .onSuccess { order ->
                    when (order.status) {
                        PaymentConfig.OrderStatus.PAID_SUCCESS,
                        PaymentConfig.OrderStatus.FULFILLED,
                        -> {
                            _state.value = WalletPaymentConfirmState.Confirmed(
                                order.submittedClientTxHash ?: order.payment.txHash ?: "N/A",
                            )
                        }

                        else -> {
                            _state.value = WalletPaymentConfirmState.Error(
                                "订单当前状态为${order.statusText}，请完成链上支付后重试",
                            )
                        }
                    }
                }
                .onFailure { err ->
                    _state.value = WalletPaymentConfirmState.Error(err.message ?: "确认支付失败")
                }
        }
    }
}

@Composable
fun WalletPaymentConfirmPage(
    viewModel: WalletPaymentConfirmViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    orderId: String = "",
    amount: String = "$26.99",
    onNavigateBack: () -> Unit = {},
    onPaymentSuccess: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()

    LaunchedEffect(state) {
        if (state is WalletPaymentConfirmState.Confirmed) {
            onPaymentSuccess((state as WalletPaymentConfirmState.Confirmed).txHash)
        }
    }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                WalletConfirmBottomBar(
                    isConfirming = state is WalletPaymentConfirmState.Confirming,
                    onConfirm = { viewModel.confirmPayment(orderId) },
                    onCancel = onNavigateBack,
                )
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
                        title = "Wallet Confirm",
                        subtitle = "Authorize the current order with the existing wallet refresh bridge.",
                        onBack = onNavigateBack,
                    )
                }
                item {
                    VpnHeroCard(
                        eyebrow = "WALLET AUTH",
                        title = "Confirm wallet payment for order ${orderId.takeLast(6)}",
                        subtitle = "支付确认页转成 Bitget 风格安全授权卡，但仍沿用 refreshOrder 成功/失败判断。",
                        accent = Primary,
                        metrics = listOf(
                            VpnHeroMetric("Amount", amount),
                            VpnHeroMetric("Order", orderId.ifBlank { "Unknown" }),
                            VpnHeroMetric("Status", if (state is WalletPaymentConfirmState.Confirming) "Confirming" else "Awaiting"),
                        ),
                    )
                }
                item {
                    WalletConfirmDetailsCard(
                        orderId = orderId,
                        amount = amount,
                    )
                }
                item {
                    VpnGlassCard(accent = Primary) {
                        Text(
                            text = "Wallet Password",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                        )
                        Text(
                            text = "输入钱包密码后，当前支付状态会通过既有订单桥接刷新并决定跳转结果页。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = viewModel::onPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = viewModel::togglePasswordVisibility) {
                                    Icon(
                                        imageVector = if (passwordVisible) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                        contentDescription = null,
                                        tint = TextSecondary,
                                    )
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "Enter wallet password",
                                    color = TextSecondary,
                                )
                            },
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            isError = state is WalletPaymentConfirmState.Error,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = BackgroundSecondary.copy(alpha = 0.95f),
                                unfocusedContainerColor = BackgroundSecondary.copy(alpha = 0.95f),
                                focusedBorderColor = Primary.copy(alpha = 0.6f),
                                unfocusedBorderColor = BorderDefault.copy(alpha = 0.82f),
                                cursorColor = Primary,
                            ),
                        )
                        if (state is WalletPaymentConfirmState.Error) {
                            Text(
                                text = (state as WalletPaymentConfirmState.Error).message,
                                style = MaterialTheme.typography.bodySmall,
                                color = AppError,
                                textAlign = TextAlign.Start,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletConfirmDetailsCard(
    orderId: String,
    amount: String,
) {
    VpnGlassCard(accent = Primary) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.padding(end = 4.dp),
                shape = RoundedCornerShape(18.dp),
                color = Primary.copy(alpha = 0.16f),
            ) {
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = Primary,
                )
            }
            Column {
                Text(
                    text = "Payment Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                Text(
                    text = "Strong primary confirmation card with wallet-only settlement language.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        }
        VpnLabelValueRow(label = "Order No.", value = orderId.ifBlank { "Unknown" })
        VpnLabelValueRow(label = "Method", value = "Wallet Balance")
        VpnLabelValueRow(label = "Network Fee", value = "~$0.01")
        VpnLabelValueRow(label = "Payable", value = amount, valueColor = Primary)
    }
}

@Composable
private fun WalletConfirmBottomBar(
    isConfirming: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Surface(
        color = BackgroundSecondary.copy(alpha = 0.98f),
        border = BorderStroke(1.dp, BorderDefault.copy(alpha = 0.9f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = VpnPageHorizontalPadding, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (isConfirming) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Primary.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 12.dp),
                            color = Primary,
                            strokeWidth = 2.dp,
                        )
                        Text(
                            text = "Confirming order status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                        )
                    }
                }
            } else {
                VpnPrimaryButton(
                    text = "Confirm Payment",
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            VpnSecondaryButton(
                text = "Cancel",
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
private fun WalletPaymentConfirmPagePreview() {
    MaterialTheme {
        WalletPaymentConfirmPage()
    }
}
