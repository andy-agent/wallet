package com.v2ray.ang.composeui.pages.growth

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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.growth.GrowthBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 提现页状态
 */
sealed class WithdrawState {
    object Idle : WithdrawState()
    object Validating : WithdrawState()
    object Submitting : WithdrawState()
    data class Success(val txHash: String) : WithdrawState()
    data class Error(val message: String) : WithdrawState()
}

/**
 * 提现页ViewModel
 */
class WithdrawViewModel(application: Application) : AndroidViewModel(application) {
    private val growthBridgeRepository = GrowthBridgeRepository(application)
    private val _state = MutableStateFlow<WithdrawState>(WithdrawState.Idle)
    val state: StateFlow<WithdrawState> = _state

    private val _availableAmount = MutableStateFlow("0.00")
    val availableAmount: StateFlow<String> = _availableAmount

    private val _minWithdrawAmount = MutableStateFlow("0.00")
    val minWithdrawAmount: StateFlow<String> = _minWithdrawAmount

    private val _withdrawAmount = MutableStateFlow("")
    val withdrawAmount: StateFlow<String> = _withdrawAmount

    private val _walletAddress = MutableStateFlow("")
    val walletAddress: StateFlow<String> = _walletAddress

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible

    init {
        refreshBalance()
    }

    private fun refreshBalance() {
        viewModelScope.launch {
            growthBridgeRepository.getCommissionSummary().onSuccess {
                _availableAmount.value = it.availableAmount
            }
            growthBridgeRepository.getReferralOverview().onSuccess {
                _minWithdrawAmount.value = it.minWithdrawAmountUsdt
            }
        }
    }

    fun onWithdrawAmountChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            _withdrawAmount.value = value
        }
    }

    fun onWalletAddressChange(value: String) {
        _walletAddress.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun setMaxAmount() {
        _withdrawAmount.value = _availableAmount.value
    }

    fun submitWithdrawal() {
        when {
            _withdrawAmount.value.isBlank() || _withdrawAmount.value.toDoubleOrNull() == 0.0 -> {
                _state.value = WithdrawState.Error("请输入提现金额")
                return
            }
            (_withdrawAmount.value.toDoubleOrNull() ?: 0.0) > (_availableAmount.value.toDoubleOrNull() ?: 0.0) -> {
                _state.value = WithdrawState.Error("提现金额超过可用余额")
                return
            }
            (_withdrawAmount.value.toDoubleOrNull() ?: 0.0) < (_minWithdrawAmount.value.toDoubleOrNull() ?: 0.0) -> {
                _state.value = WithdrawState.Error("低于最小提现金额 ${_minWithdrawAmount.value}")
                return
            }
            _walletAddress.value.isBlank() -> {
                _state.value = WithdrawState.Error("请输入钱包地址")
                return
            }
            _walletAddress.value.length < 20 -> {
                _state.value = WithdrawState.Error("请输入有效的钱包地址")
                return
            }
            _password.value.isBlank() -> {
                _state.value = WithdrawState.Error("请输入钱包密码")
                return
            }
        }

        viewModelScope.launch {
            _state.value = WithdrawState.Submitting
            growthBridgeRepository.createWithdrawal(
                amount = _withdrawAmount.value,
                payoutAddress = _walletAddress.value
            ).onSuccess {
                _state.value = WithdrawState.Success(it.requestNo)
                refreshBalance()
            }.onFailure {
                _state.value = WithdrawState.Error(it.message ?: "提现失败")
            }
        }
    }

    fun clearError() {
        if (_state.value is WithdrawState.Error) {
            _state.value = WithdrawState.Idle
        }
    }
}

/**
 * 提现申请页
 * 输入提现金额和钱包地址
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawPage(
    viewModel: WithdrawViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onWithdrawSuccess: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val availableAmount by viewModel.availableAmount.collectAsState()
    val withdrawAmount by viewModel.withdrawAmount.collectAsState()
    val walletAddress by viewModel.walletAddress.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val focusManager = LocalFocusManager.current

    // 监听提现状态
    LaunchedEffect(state) {
        when (state) {
            is WithdrawState.Success -> {
                val txHash = (state as WithdrawState.Success).txHash
                onWithdrawSuccess(txHash)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("提现申请") },
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 可用余额卡片
            AvailableBalanceCard(availableAmount = availableAmount)

            Spacer(modifier = Modifier.height(24.dp))

            // 提现金额输入
            OutlinedTextField(
                value = withdrawAmount,
                onValueChange = { 
                    viewModel.onWithdrawAmountChange(it)
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("提现金额") },
                placeholder = { Text("0.00") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    TextButton(onClick = { viewModel.setMaxAmount() }) {
                        Text("全部")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is WithdrawState.Error
            )

            // 可用余额提示
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "可用余额: \$$availableAmount",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 钱包地址输入
            OutlinedTextField(
                value = walletAddress,
                onValueChange = { 
                    viewModel.onWalletAddressChange(it)
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("提现地址") },
                placeholder = { Text("请输入USDT收款地址") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is WithdrawState.Error
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                        contentDescription = null
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.submitWithdrawal()
                    }
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is WithdrawState.Error
            )

            // 错误提示
            if (state is WithdrawState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (state as WithdrawState.Error).message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 提现说明
            WithdrawalInfoCard()

            Spacer(modifier = Modifier.height(16.dp))

            // 提交按钮
            Button(
                onClick = { 
                    focusManager.clearFocus()
                    viewModel.submitWithdrawal() 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = state !is WithdrawState.Submitting
            ) {
                if (state is WithdrawState.Submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "确认提现",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AvailableBalanceCard(availableAmount: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "可提现金额",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "\$$availableAmount",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun WithdrawalInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "提现说明",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val infos = listOf(
                "最低提现金额为 $10",
                "提现将在1-3个工作日内到账",
                "提现手续费为 1%",
                "请确保地址正确，错误地址将导致资金丢失"
            )
            
            infos.forEach { info ->
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = info,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WithdrawPagePreview() {
    MaterialTheme {
        WithdrawPage()
    }
}
