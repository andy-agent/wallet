package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.order.VpnOrderBridge
import com.v2ray.ang.payment.PaymentConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 钱包支付确认页状态
 */
sealed class WalletPaymentConfirmState {
    object Idle : WalletPaymentConfirmState()
    object Confirming : WalletPaymentConfirmState()
    data class Confirmed(val txHash: String) : WalletPaymentConfirmState()
    data class Error(val message: String) : WalletPaymentConfirmState()
}

/**
 * 钱包支付确认页ViewModel
 */
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
                        PaymentConfig.OrderStatus.FULFILLED -> {
                            _state.value = WalletPaymentConfirmState.Confirmed(
                                order.submittedClientTxHash ?: order.payment.txHash ?: "N/A"
                            )
                        }
                        else -> {
                            _state.value = WalletPaymentConfirmState.Error(
                                "订单当前状态为${order.statusText}，请完成链上支付后重试"
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

/**
 * 钱包支付确认页
 * 确认订单支付详情并输入密码
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletPaymentConfirmPage(
    viewModel: WalletPaymentConfirmViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    orderId: String = "",
    amount: String = "$26.99",
    onNavigateBack: () -> Unit = {},
    onPaymentSuccess: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()

    // 监听支付状态
    LaunchedEffect(state) {
        when (state) {
            is WalletPaymentConfirmState.Confirmed -> {
                val txHash = (state as WalletPaymentConfirmState.Confirmed).txHash
                onPaymentSuccess(txHash)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("确认支付") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 支付图标
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 支付金额
            Text(
                text = amount,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "将从您的钱包扣除",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 订单详情
            PaymentDetailsCard(orderId = orderId, amount = amount)

            Spacer(modifier = Modifier.height(32.dp))

            // 密码输入
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("钱包密码") },
                placeholder = { Text("请输入钱包密码") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) 
                    androidx.compose.ui.text.input.VisualTransformation.None 
                else 
                    androidx.compose.ui.text.input.PasswordVisualTransformation(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is WalletPaymentConfirmState.Error
            )

            // 错误提示
            if (state is WalletPaymentConfirmState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (state as WalletPaymentConfirmState.Error).message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 确认按钮
            Button(
                onClick = { viewModel.confirmPayment(orderId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = state !is WalletPaymentConfirmState.Confirming
            ) {
                if (state is WalletPaymentConfirmState.Confirming) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
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

            Spacer(modifier = Modifier.height(16.dp))

            // 取消按钮
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "取消",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PaymentDetailsCard(orderId: String, amount: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "支付详情",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 订单号
            PaymentDetailRow(label = "订单号", value = orderId)

            Spacer(modifier = Modifier.height(12.dp))

            // 支付方式
            PaymentDetailRow(label = "支付方式", value = "钱包余额")

            Spacer(modifier = Modifier.height(12.dp))

            // 网络费用
            PaymentDetailRow(label = "网络费用", value = "~$0.01")

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // 总计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "总计",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = amount,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PaymentDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WalletPaymentConfirmPagePreview() {
    MaterialTheme {
        WalletPaymentConfirmPage()
    }
}
