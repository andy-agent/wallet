package com.v2ray.ang.composeui.pages.wallet

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.wallet.WalletBridgeRepository
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WalletPaymentConfirmState2 {
    data object Idle : WalletPaymentConfirmState2()
    data object Confirming : WalletPaymentConfirmState2()
    data class Confirmed(val txHash: String) : WalletPaymentConfirmState2()
    data class Error(val message: String) : WalletPaymentConfirmState2()
}

class WalletPaymentConfirmViewModel2(application: Application) : AndroidViewModel(application) {
    private val walletBridgeRepository = WalletBridgeRepository(application)
    private val _state = MutableStateFlow<WalletPaymentConfirmState2>(WalletPaymentConfirmState2.Idle)
    val state: StateFlow<WalletPaymentConfirmState2> = _state

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun confirmPayment() {
        if (_password.value.isBlank()) {
            _state.value = WalletPaymentConfirmState2.Error("请输入钱包密码")
            return
        }

        viewModelScope.launch {
            _state.value = WalletPaymentConfirmState2.Confirming
            val currentOrderId = walletBridgeRepository.getCurrentOrderId()
            if (currentOrderId.isNullOrBlank()) {
                _state.value = WalletPaymentConfirmState2.Error("缺少当前订单")
                return@launch
            }
            walletBridgeRepository.getOrder(currentOrderId)
                .onSuccess { order ->
                    val txOrOrder = order.payment.txHash ?: order.orderNo
                    _state.value = WalletPaymentConfirmState2.Confirmed(txOrOrder)
                }
                .onFailure {
                    _state.value = WalletPaymentConfirmState2.Error(it.message ?: "订单状态刷新失败")
                }
        }
    }

    fun clearError() {
        if (_state.value is WalletPaymentConfirmState2.Error) {
            _state.value = WalletPaymentConfirmState2.Idle
        }
    }
}

@Composable
fun WalletPaymentConfirmPage2(
    viewModel: WalletPaymentConfirmViewModel2 = androidx.lifecycle.viewmodel.compose.viewModel(),
    orderId: String = "ORD-20240115-001",
    planName: String = "季度套餐",
    amount: String = "$26.99",
    onNavigateBack: () -> Unit = {},
    onPaymentSuccess: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state) {
        if (state is WalletPaymentConfirmState2.Confirmed) {
            onPaymentSuccess((state as WalletPaymentConfirmState2.Confirmed).txHash)
        }
    }

    WalletPageBackdrop {
        Box(modifier = Modifier.fillMaxSize()) {
            WalletPaymentBackdropContent(
                planName = planName,
                amount = amount,
                onNavigateBack = onNavigateBack,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.22f)),
            )
            WalletBottomSheetCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
            ) {
                Text(
                    text = "提示",
                    style = MaterialTheme.typography.headlineSmall,
                    color = WalletTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "授权确认当前钱包支付，继续后会刷新既有订单桥接状态并跳转结果页。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WalletTextSecondary,
                )
                WalletGlassCard(
                    accent = WalletAccent,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    WalletInfoRow(label = "支付项目", value = planName)
                    WalletInfoRow(label = "参考编号", value = walletShortAddress(orderId))
                    WalletInfoRow(label = "总计", value = amount, valueColor = WalletAccent)
                }
                WalletInputField(
                    value = password,
                    onValueChange = {
                        viewModel.onPasswordChange(it)
                        viewModel.clearError()
                    },
                    placeholder = "请输入钱包密码",
                    leadingIcon = Icons.Default.Lock,
                    isError = state is WalletPaymentConfirmState2.Error,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.confirmPayment()
                        },
                    ),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingContent = {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clickable(onClick = viewModel::togglePasswordVisibility),
                            contentAlignment = Alignment.Center,
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = "toggle password visibility",
                                tint = WalletTextSecondary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    },
                )
                if (state is WalletPaymentConfirmState2.Error) {
                    Text(
                        text = (state as WalletPaymentConfirmState2.Error).message,
                        style = MaterialTheme.typography.bodySmall,
                        color = WalletDanger,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    WalletSecondaryButton(
                        label = "取消",
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                    )
                    WalletPrimaryButton(
                        label = if (state is WalletPaymentConfirmState2.Confirming) {
                            "确认中..."
                        } else {
                            "确认"
                        },
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.confirmPayment()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = state !is WalletPaymentConfirmState2.Confirming,
                    )
                }
                if (state is WalletPaymentConfirmState2.Confirming) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = WalletAccent,
                            strokeWidth = 2.dp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletPaymentBackdropContent(
    planName: String,
    amount: String,
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = WalletPagePadding, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        WalletTopBar(
            title = "",
            onBack = onNavigateBack,
            trailingIcon = Icons.AutoMirrored.Filled.HelpOutline,
            trailingDescription = "help",
            onTrailingClick = {},
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            WalletGlassCard(
                accent = Color(0xFF99A3B3),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 168.dp, height = 104.dp)
                        .background(
                            color = Color(0xFF181A1C),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(54.dp),
                    )
                }
            }
        }
        Text(
            text = "用加密资产完成支付",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF10252D),
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = planName,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF183640),
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = amount,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D1A1E),
        )
        WalletGlassCard(
            accent = WalletAccent,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Text(
                text = "支付权益",
                style = MaterialTheme.typography.titleMedium,
                color = WalletTextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            WalletInfoRow(label = "0 手续费", value = "日常消费无磨损")
            WalletInfoRow(label = "快速确认", value = "刷新后直接判断订单状态")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun WalletPaymentConfirmPage2Preview() {
    CryptoVPNTheme {
        WalletPaymentConfirmPage2()
    }
}
