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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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

sealed class WalletPaymentConfirmState {
    data object Idle : WalletPaymentConfirmState()
    data object Confirming : WalletPaymentConfirmState()
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
                    title = "钱包结算授权",
                    onBack = onNavigateBack,
                )
                VpnGlassCard {
                    Text(
                        text = "结算背景",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                    Text(
                        text = "VPN 订阅结算",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    VpnLabelValueRow(label = "订单号", value = orderId.ifBlank { "Unknown" })
                    VpnLabelValueRow(label = "支付金额", value = amount, valueColor = VpnAccent)
                    VpnLabelValueRow(label = "结算轨道", value = "Wallet Balance")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(VpnSheetScrim),
            )

            VpnBottomSheet(
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Text(
                    text = "授权确认",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = "授权查看订单结算状态，并在校验成功后跳转结果页。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(VpnAccentSoft, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallet,
                            contentDescription = null,
                            tint = VpnAccent,
                        )
                    }
                    Column {
                        Text(
                            text = "钱包授权",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                        )
                        Text(
                            text = "订单 ${orderId.takeTrailing(6)} · $amount",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                }
                TextField(
                    value = password,
                    onValueChange = viewModel::onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    placeholder = {
                        Text(
                            text = "输入钱包密码",
                            color = TextSecondary,
                        )
                    },
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
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = TextSecondary,
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = VpnSurfaceStrong,
                        unfocusedContainerColor = VpnSurfaceStrong,
                        disabledContainerColor = VpnSurfaceStrong,
                        cursorColor = VpnAccent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
                if (state is WalletPaymentConfirmState.Error) {
                    Text(
                        text = (state as WalletPaymentConfirmState.Error).message,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppError,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    VpnSecondaryButton(
                        text = "取消",
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                    )
                    if (state is WalletPaymentConfirmState.Confirming) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(VpnAccent, RoundedCornerShape(26.dp))
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color(0xFF041012),
                                    strokeWidth = 2.dp,
                                )
                                Text(
                                    text = "校验中",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF041012),
                                )
                            }
                        }
                    } else {
                        VpnPrimaryButton(
                            text = "确认授权",
                            onClick = { viewModel.confirmPayment(orderId) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
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
