package com.v2ray.ang.composeui.pages.growth

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

sealed class WithdrawState {
    object Idle : WithdrawState()
    object Validating : WithdrawState()
    object Submitting : WithdrawState()
    data class Success(val txHash: String) : WithdrawState()
    data class Error(val message: String) : WithdrawState()
}

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
        if (state is WithdrawState.Success) {
            onWithdrawSuccess((state as WithdrawState.Success).txHash)
        }
    }

    GrowthPageScaffold(
        title = "提现申请",
        onNavigateBack = onNavigateBack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GrowthPageBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WithdrawHeroCard(
                availableAmount = availableAmount,
                minWithdrawAmount = minWithdrawAmount,
                onSetMax = viewModel::setMaxAmount
            )

            GrowthSectionCard {
                GrowthSectionTitle(
                    title = "提现信息",
                    subtitle = "表单层级按照 Bitget Growth 详情页处理：先余额，再金额，再地址与验证。"
                )
                Spacer(modifier = Modifier.height(18.dp))

                WithdrawInputField(
                    value = withdrawAmount,
                    onValueChange = {
                        viewModel.onWithdrawAmountChange(it)
                        viewModel.clearError()
                    },
                    label = "提现金额",
                    placeholder = "0.00",
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = GrowthAccent) },
                    trailing = {
                        Text(
                            text = "全部",
                            color = GrowthAccent,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    },
                    onTrailingClick = viewModel::setMaxAmount,
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                    onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
                    isError = state is WithdrawState.Error
                )

                Spacer(modifier = Modifier.height(10.dp))
                GrowthInfoRow(label = "可用余额", value = "$$availableAmount", emphasize = true)
                Spacer(modifier = Modifier.height(8.dp))
                GrowthInfoRow(label = "最小提现", value = "$$minWithdrawAmount")

                Spacer(modifier = Modifier.height(16.dp))

                WithdrawInputField(
                    value = walletAddress,
                    onValueChange = {
                        viewModel.onWalletAddressChange(it)
                        viewModel.clearError()
                    },
                    label = "提现地址",
                    placeholder = "请输入 USDT 收款地址",
                    leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = GrowthAccent) },
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
                    isError = state is WithdrawState.Error
                )

                Spacer(modifier = Modifier.height(16.dp))

                WithdrawInputField(
                    value = password,
                    onValueChange = {
                        viewModel.onPasswordChange(it)
                        viewModel.clearError()
                    },
                    label = "钱包密码",
                    placeholder = "请输入钱包密码",
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GrowthAccent) },
                    trailing = {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = GrowthTextSecondary
                        )
                    },
                    onTrailingClick = viewModel::togglePasswordVisibility,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        focusManager.clearFocus()
                        viewModel.submitWithdrawal()
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = state is WithdrawState.Error
                )

                if (state is WithdrawState.Error) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = (state as WithdrawState.Error).message,
                        color = GrowthNegative,
                        fontSize = 13.sp
                    )
                }
            }

            GrowthSectionCard {
                GrowthSectionTitle(
                    title = "提现说明",
                    subtitle = "保留原业务规则，但用 Discover 的卡片信息层统一展示。"
                )
                Spacer(modifier = Modifier.height(16.dp))
                listOf(
                    "最低提现金额为 $$minWithdrawAmount。",
                    "提现申请成功后通常在 1-3 个工作日内到账。",
                    "提现手续费为 1%，到账金额以最终链上处理结果为准。",
                    "请确认地址正确，错误地址可能导致资金无法找回。"
                ).forEachIndexed { index, item ->
                    GrowthBulletItem(text = "${index + 1}. $item")
                    if (index != 3) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.submitWithdrawal()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = state !is WithdrawState.Submitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GrowthAccent,
                    contentColor = Color(0xFF161A1E),
                    disabledContainerColor = GrowthAccent.copy(alpha = 0.4f),
                    disabledContentColor = Color(0xFF161A1E)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                if (state is WithdrawState.Submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(vertical = 2.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF161A1E)
                    )
                } else {
                    Text("确认提现", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun WithdrawHeroCard(
    availableAmount: String,
    minWithdrawAmount: String,
    onSetMax: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GrowthHeroGradient, RoundedCornerShape(28.dp))
            .padding(22.dp)
    ) {
        Text("Discover Withdraw", color = Color(0xFF5E4300), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("$$availableAmount", color = Color(0xFF161A1E), fontSize = 34.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("当前可提余额", color = Color(0xFF5E4300), fontSize = 13.sp)

        Spacer(modifier = Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GrowthStatChip(label = "最低提现", value = "$$minWithdrawAmount", modifier = Modifier.weight(1f))
            GrowthStatChip(label = "到账方式", value = "USDT", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onSetMax,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF161A1E),
                contentColor = GrowthTextPrimary
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("一键填入全部金额", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun WithdrawInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    onTrailingClick: (() -> Unit)? = null,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    onImeAction: () -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label, color = GrowthTextSecondary) },
        placeholder = { Text(placeholder, color = GrowthTextSecondary.copy(alpha = 0.8f)) },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GrowthAccent,
            unfocusedBorderColor = GrowthBorder,
            errorBorderColor = GrowthNegative,
            focusedContainerColor = GrowthSurfaceRaised,
            unfocusedContainerColor = GrowthSurfaceRaised,
            cursorColor = GrowthAccent,
            focusedTextColor = GrowthTextPrimary,
            unfocusedTextColor = GrowthTextPrimary,
            errorTextColor = GrowthTextPrimary
        ),
        leadingIcon = leadingIcon,
        trailingIcon = if (trailing == null) null else {
            {
                if (onTrailingClick != null) {
                    IconButton(onClick = onTrailingClick) { trailing() }
                } else {
                    trailing()
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        visualTransformation = visualTransformation,
        isError = isError
    )
}

@Preview(showBackground = true)
@Composable
private fun WithdrawPagePreview() {
    MaterialTheme {
        WithdrawHeroCard(
            availableAmount = "96.24",
            minWithdrawAmount = "10.00",
            onSetMax = {}
        )
    }
}
