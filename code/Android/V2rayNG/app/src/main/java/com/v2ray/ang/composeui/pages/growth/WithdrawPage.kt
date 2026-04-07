package com.v2ray.ang.composeui.pages.growth

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusDirection
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
import com.v2ray.ang.composeui.bridge.growth.GrowthBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val WithdrawBg = Color(0xFF0A101A)
private val WithdrawSurface = Color(0xFF121A28)
private val WithdrawSurfaceSoft = Color(0xFF1A2638)
private val WithdrawPrimary = Color(0xFF00E5A8)
private val WithdrawPrimarySoft = Color(0x3313F1B2)
private val WithdrawText = Color(0xFFEAF0F7)
private val WithdrawMuted = Color(0xFF8D9AB0)
private val WithdrawDanger = Color(0xFFF45B69)

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
                _state.value = WithdrawState.Error("低于最小提现金额 ${_minWithdrawAmount.value} USDT")
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
    val minWithdrawAmount by viewModel.minWithdrawAmount.collectAsState()
    val withdrawAmount by viewModel.withdrawAmount.collectAsState()
    val walletAddress by viewModel.walletAddress.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state) {
        when (state) {
            is WithdrawState.Success -> onWithdrawSuccess((state as WithdrawState.Success).txHash)
            else -> Unit
        }
    }

    Scaffold(
        containerColor = WithdrawBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Growth / Withdraw",
                        color = WithdrawText,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = WithdrawText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WithdrawBg,
                    titleContentColor = WithdrawText,
                    navigationIconContentColor = WithdrawText
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WithdrawBg)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            AvailableBalanceCard(
                availableAmount = availableAmount,
                minWithdrawAmount = minWithdrawAmount
            )

            Spacer(modifier = Modifier.height(14.dp))

            WithdrawInputCard(
                state = state,
                withdrawAmount = withdrawAmount,
                walletAddress = walletAddress,
                password = password,
                passwordVisible = passwordVisible,
                availableAmount = availableAmount,
                onWithdrawAmountChange = {
                    viewModel.onWithdrawAmountChange(it)
                    viewModel.clearError()
                },
                onWalletAddressChange = {
                    viewModel.onWalletAddressChange(it)
                    viewModel.clearError()
                },
                onPasswordChange = {
                    viewModel.onPasswordChange(it)
                    viewModel.clearError()
                },
                onTogglePasswordVisibility = { viewModel.togglePasswordVisibility() },
                onSetMaxAmount = { viewModel.setMaxAmount() },
                onSubmit = {
                    focusManager.clearFocus()
                    viewModel.submitWithdrawal()
                },
                focusManager = focusManager
            )

            Spacer(modifier = Modifier.height(12.dp))

            WithdrawalInfoCard(minWithdrawAmount = minWithdrawAmount)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AvailableBalanceCard(
    availableAmount: String,
    minWithdrawAmount: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = WithdrawSurface),
        border = BorderStroke(1.dp, WithdrawPrimarySoft)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0x3322F5C6), Color.Transparent, Color(0x202D3E58))
                    )
                )
                .padding(18.dp)
        ) {
            Text(text = "可提现余额", color = WithdrawMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${availableAmount} USDT",
                color = WithdrawText,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = WithdrawPrimarySoft
            ) {
                Text(
                    text = "最低提现 ${minWithdrawAmount} USDT",
                    color = WithdrawPrimary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun WithdrawInputCard(
    state: WithdrawState,
    withdrawAmount: String,
    walletAddress: String,
    password: String,
    passwordVisible: Boolean,
    availableAmount: String,
    onWithdrawAmountChange: (String) -> Unit,
    onWalletAddressChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSetMaxAmount: () -> Unit,
    onSubmit: () -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WithdrawSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "提现信息（USDT）",
                color = WithdrawText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            OutlinedTextField(
                value = withdrawAmount,
                onValueChange = onWithdrawAmountChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("提现金额 (USDT)") },
                placeholder = { Text("0.00") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    TextButton(onClick = onSetMaxAmount) {
                        Text("全部", color = WithdrawPrimary)
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
                shape = RoundedCornerShape(12.dp),
                isError = state is WithdrawState.Error,
                colors = withdrawInputColors()
            )

            Text(
                text = "当前可用: ${availableAmount} USDT",
                color = WithdrawMuted,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.End)
            )

            OutlinedTextField(
                value = walletAddress,
                onValueChange = onWalletAddressChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("提现地址") },
                placeholder = { Text("请输入USDT收款地址") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = state is WithdrawState.Error,
                colors = withdrawInputColors()
            )

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
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
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = WithdrawMuted
                        )
                    }
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onSubmit()
                    }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = state is WithdrawState.Error,
                colors = withdrawInputColors()
            )

            if (state is WithdrawState.Error) {
                Text(
                    text = state.message,
                    color = WithdrawDanger,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WithdrawPrimary,
                    contentColor = Color(0xFF072117)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = state !is WithdrawState.Submitting
            ) {
                if (state is WithdrawState.Submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color(0xFF072117),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "确认提现", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun withdrawInputColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = WithdrawText,
    unfocusedTextColor = WithdrawText,
    focusedLabelColor = WithdrawPrimary,
    unfocusedLabelColor = WithdrawMuted,
    focusedPlaceholderColor = WithdrawMuted,
    unfocusedPlaceholderColor = WithdrawMuted,
    focusedBorderColor = WithdrawPrimary,
    unfocusedBorderColor = WithdrawSurfaceSoft,
    cursorColor = WithdrawPrimary,
    focusedContainerColor = WithdrawSurfaceSoft,
    unfocusedContainerColor = WithdrawSurfaceSoft
)

@Composable
private fun WithdrawalInfoCard(minWithdrawAmount: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WithdrawSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "提现须知",
                color = WithdrawText,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            InfoItem(text = "最低提现金额为 ${minWithdrawAmount} USDT")
            InfoItem(text = "预计 1-3 个工作日到账")
            InfoItem(text = "链上手续费按实际网络费和平台规则收取")
            InfoItem(text = "请务必确认地址正确，错误地址无法追回")
        }
    }
}

@Composable
private fun InfoItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = "•",
            color = WithdrawPrimary,
            fontSize = 13.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            color = WithdrawMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WithdrawPagePreview() {
    MaterialTheme {
        WithdrawPage()
    }
}
