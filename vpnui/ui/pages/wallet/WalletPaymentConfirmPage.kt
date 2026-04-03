package com.cryptovpn.ui.pages.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 钱包支付确认页状态（用于订单支付）
 */
sealed class WalletPaymentConfirmState2 {
    object Idle : WalletPaymentConfirmState2()
    object Confirming : WalletPaymentConfirmState2()
    data class Confirmed(val txHash: String) : WalletPaymentConfirmState2()
    data class Error(val message: String) : WalletPaymentConfirmState2()
}

/**
 * 钱包支付确认页ViewModel（用于订单支付）
 */
@HiltViewModel
class WalletPaymentConfirmViewModel2 @Inject constructor() : ViewModel() {
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
            delay(2000) // 模拟交易处理
            _state.value = WalletPaymentConfirmState2.Confirmed("0x742d35...5f0bEb")
        }
    }

    fun clearError() {
        if (_state.value is WalletPaymentConfirmState2.Error) {
            _state.value = WalletPaymentConfirmState2.Idle
        }
    }
}

/**
 * 钱包支付确认页（用于订单支付）
 * 确认订单支付详情并输入密码
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletPaymentConfirmPage2(
    viewModel: WalletPaymentConfirmViewModel2 = androidx.lifecycle.viewmodel.compose.viewModel(),
    orderId: String = "ORD-20240115-001",
    planName: String = "季度套餐",
    amount: String = "$26.99",
    onNavigateBack: () -> Unit = {},
    onPaymentSuccess: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val focusManager = LocalFocusManager.current

    // 监听支付状态
    LaunchedEffect(state) {
        when (state) {
            is WalletPaymentConfirmState2.Confirmed -> {
                val txHash = (state as WalletPaymentConfirmState2.Confirmed).txHash
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
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Order",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 套餐名称
            Text(
                text = planName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 支付金额
            Text(
                text = amount,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 订单详情
            OrderPaymentDetailsCard(orderId = orderId, amount = amount)

            Spacer(modifier = Modifier.height(32.dp))

            // 密码输入
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    viewModel.onPasswordChange(it)
                    viewModel.clearError()
                },
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
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.confirmPayment()
                    }
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is WalletPaymentConfirmState2.Error
            )

            // 错误提示
            if (state is WalletPaymentConfirmState2.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (state as WalletPaymentConfirmState2.Error).message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 确认按钮
            Button(
                onClick = { 
                    focusManager.clearFocus()
                    viewModel.confirmPayment() 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = state !is WalletPaymentConfirmState2.Confirming
            ) {
                if (state is WalletPaymentConfirmState2.Confirming) {
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
private fun OrderPaymentDetailsCard(orderId: String, amount: String) {
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
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WalletPaymentConfirmPage2Preview() {
    MaterialTheme {
        WalletPaymentConfirmPage2()
    }
}
