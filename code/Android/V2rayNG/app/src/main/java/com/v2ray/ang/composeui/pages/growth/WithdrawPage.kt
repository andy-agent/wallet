package com.v2ray.ang.composeui.pages.growth

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

data class WithdrawalHistoryItem(
    val requestNo: String,
    val amount: String,
    val assetCode: String,
    val networkCode: String,
    val payoutAddress: String,
    val status: String,
    val createdAt: String,
    val txHash: String? = null,
    val failReason: String? = null,
)

data class WithdrawUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val availableAmount: String = "0.00",
    val minWithdrawAmount: String = "0.00",
    val settlementAssetCode: String = "USDT",
    val settlementNetworkCode: String = "SOLANA",
    val withdrawAmount: String = "",
    val walletAddress: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val withdrawals: List<WithdrawalHistoryItem> = emptyList(),
    val errorMessage: String? = null,
    val successRequestNo: String? = null,
)

class WithdrawViewModel(application: Application) : AndroidViewModel(application) {
    private val growthBridgeRepository = GrowthBridgeRepository(application)
    private val _uiState = MutableStateFlow(WithdrawUiState())
    val uiState: StateFlow<WithdrawUiState> = _uiState

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            val current = _uiState.value
            _uiState.value = current.copy(isLoading = true, errorMessage = null)

            var nextState = current
            var firstError: String? = null

            growthBridgeRepository.getCommissionSummary().onSuccess { summary ->
                nextState = nextState.copy(
                    availableAmount = summary.availableAmount,
                    settlementAssetCode = summary.settlementAssetCode,
                    settlementNetworkCode = summary.settlementNetworkCode,
                )
            }.onFailure {
                firstError = firstError ?: (it.message ?: "加载提现概览失败")
            }

            growthBridgeRepository.getReferralOverview().onSuccess { referral ->
                nextState = nextState.copy(minWithdrawAmount = referral.minWithdrawAmountUsdt)
            }.onFailure {
                firstError = firstError ?: (it.message ?: "加载提现规则失败")
            }

            growthBridgeRepository.getWithdrawals().onSuccess { items ->
                nextState = nextState.copy(
                    withdrawals = items.map {
                        WithdrawalHistoryItem(
                            requestNo = it.requestNo,
                            amount = it.amount,
                            assetCode = it.assetCode,
                            networkCode = it.networkCode,
                            payoutAddress = it.payoutAddress,
                            status = it.status,
                            createdAt = it.createdAt,
                            txHash = it.txHash,
                            failReason = it.failReason,
                        )
                    },
                )
            }.onFailure {
                firstError = firstError ?: (it.message ?: "加载提现历史失败")
            }

            _uiState.value = nextState.copy(
                isLoading = false,
                errorMessage = firstError,
                successRequestNo = null,
            )
        }
    }

    fun onWithdrawAmountChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.value = _uiState.value.copy(withdrawAmount = value, errorMessage = null)
        }
    }

    fun onWalletAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(walletAddress = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(passwordVisible = !_uiState.value.passwordVisible)
    }

    fun setMaxAmount() {
        _uiState.value = _uiState.value.copy(
            withdrawAmount = _uiState.value.availableAmount,
            errorMessage = null,
        )
    }

    fun validateForm(): Boolean {
        val state = _uiState.value
        val message = when {
            state.withdrawAmount.isBlank() || state.withdrawAmount.toDoubleOrNull() == 0.0 -> "请输入提现金额"
            (state.withdrawAmount.toDoubleOrNull() ?: 0.0) > (state.availableAmount.toDoubleOrNull()
                ?: 0.0) -> "提现金额超过可用余额"
            (state.withdrawAmount.toDoubleOrNull() ?: 0.0) < (state.minWithdrawAmount.toDoubleOrNull()
                ?: 0.0) -> "低于最小提现金额 ${state.minWithdrawAmount}"
            state.walletAddress.isBlank() -> "请输入钱包地址"
            state.walletAddress.length < 20 -> "请输入有效的钱包地址"
            state.password.isBlank() -> "请输入钱包密码"
            else -> null
        }

        return if (message == null) {
            true
        } else {
            _uiState.value = state.copy(errorMessage = message)
            false
        }
    }

    fun submitWithdrawal() {
        if (!validateForm()) return

        val state = _uiState.value
        if (state.isSubmitting) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true, errorMessage = null)
            growthBridgeRepository.createWithdrawal(
                amount = state.withdrawAmount,
                payoutAddress = state.walletAddress,
            ).onSuccess {
                _uiState.value = state.copy(
                    isSubmitting = false,
                    withdrawAmount = "",
                    walletAddress = "",
                    password = "",
                    successRequestNo = it.requestNo,
                )
            }.onFailure {
                _uiState.value = state.copy(
                    isSubmitting = false,
                    errorMessage = it.message ?: "提现失败",
                )
            }
        }
    }

    fun consumeSuccess() {
        _uiState.value = _uiState.value.copy(successRequestNo = null)
    }
}

@Composable
fun WithdrawPage(
    viewModel: WithdrawViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onWithdrawSuccess: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    var showConfirmSheet by rememberSaveable { mutableStateOf(false) }
    var selectedHistory by remember { mutableStateOf<WithdrawalHistoryItem?>(null) }

    LaunchedEffect(uiState.successRequestNo) {
        uiState.successRequestNo?.let {
            viewModel.consumeSuccess()
            onWithdrawSuccess(it)
        }
    }

    GrowthPageScaffold(
        topBar = {
            GrowthTopBar(
                title = "提现申请",
                subtitle = "摘要区优先，表单与历史记录共用同一深色容器语言",
                onNavigateBack = onNavigateBack,
            )
        },
        bottomBar = {
            if (!uiState.isLoading || uiState.withdrawals.isNotEmpty()) {
                Surface(color = GrowthPageBackground.copy(alpha = 0.98f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                    ) {
                        GrowthPrimaryButton(
                            text = if (uiState.isSubmitting) "提现提交中" else "确认提现",
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSubmitting,
                            onClick = {
                                if (viewModel.validateForm()) {
                                    showConfirmSheet = true
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        if (!uiState.isLoading || uiState.withdrawals.isNotEmpty() || uiState.availableAmount != "0.00") {
            WithdrawPageContent(
                uiState = uiState,
                paddingValues = paddingValues,
                onWithdrawAmountChange = viewModel::onWithdrawAmountChange,
                onWalletAddressChange = viewModel::onWalletAddressChange,
                onPasswordChange = viewModel::onPasswordChange,
                onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                onSetMaxAmount = viewModel::setMaxAmount,
                onHistoryClick = { selectedHistory = it },
            )
        }
    }

    if (showConfirmSheet) {
        WithdrawConfirmSheet(
            uiState = uiState,
            onDismiss = { showConfirmSheet = false },
            onConfirm = {
                showConfirmSheet = false
                viewModel.submitWithdrawal()
            },
        )
    }

    selectedHistory?.let { history ->
        WithdrawalDetailSheet(
            item = history,
            onDismiss = { selectedHistory = null },
        )
    }
}

@Composable
private fun WithdrawPageContent(
    uiState: WithdrawUiState,
    paddingValues: PaddingValues,
    onWithdrawAmountChange: (String) -> Unit,
    onWalletAddressChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSetMaxAmount: () -> Unit,
    onHistoryClick: (WithdrawalHistoryItem) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 6.dp,
            bottom = 108.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            WithdrawHeroCard(uiState = uiState, onSetMaxAmount = onSetMaxAmount)
        }

        item {
            GrowthSectionCard {
                GrowthSectionTitle(
                    title = "提现信息",
                    subtitle = "输入优先，其次是网络和安全校验，CTA 固定到底部。",
                )

                WithdrawInputField(
                    value = uiState.withdrawAmount,
                    onValueChange = onWithdrawAmountChange,
                    label = "提现金额",
                    placeholder = "0.00",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = GrowthAccent,
                        )
                    },
                    trailing = {
                        Surface(
                            modifier = Modifier.clickable(onClick = onSetMaxAmount),
                            shape = RoundedCornerShape(999.dp),
                            color = GrowthAccentSoft,
                        ) {
                            Text(
                                text = "全部",
                                color = GrowthAccent,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            )
                        }
                    },
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions.Default,
                    visualTransformation = VisualTransformation.None,
                    isError = uiState.errorMessage != null,
                )

                GrowthInfoRow(
                    label = "可用余额",
                    value = "${uiState.availableAmount} ${uiState.settlementAssetCode}",
                    emphasize = true,
                )
                GrowthInfoRow(
                    label = "最小提现",
                    value = "${uiState.minWithdrawAmount} ${uiState.settlementAssetCode}",
                )

                WithdrawInputField(
                    value = uiState.walletAddress,
                    onValueChange = onWalletAddressChange,
                    label = "提现地址",
                    placeholder = "请输入 ${uiState.settlementAssetCode} 收款地址",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = GrowthAccent,
                        )
                    },
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    keyboardActions = KeyboardActions.Default,
                    visualTransformation = VisualTransformation.None,
                    isError = uiState.errorMessage != null,
                )

                WithdrawInputField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    label = "钱包密码",
                    placeholder = "请输入钱包密码",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = GrowthAccent,
                        )
                    },
                    trailing = {
                        IconButton(onClick = onTogglePasswordVisibility) {
                            Icon(
                                imageVector = if (uiState.passwordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = null,
                                tint = GrowthTextSecondary,
                            )
                        }
                    },
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions.Default,
                    visualTransformation = if (uiState.passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = uiState.errorMessage != null,
                )

                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        color = GrowthNegative,
                        fontSize = 13.sp,
                    )
                }
            }
        }

        item {
            GrowthSectionCard {
                GrowthSectionTitle(
                    title = "提现说明",
                    subtitle = "规则以摘要卡和说明卡表达，不把长文案挤到主操作上方。",
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GrowthBulletItem(text = "最低提现金额为 ${uiState.minWithdrawAmount} ${uiState.settlementAssetCode}。")
                    GrowthBulletItem(text = "本次提现固定走 ${uiState.settlementNetworkCode} 网络，链上地址请确认无误。")
                    GrowthBulletItem(text = "审核和链上处理通常需要 1-3 个工作日，状态变化会同步到下面的历史记录。")
                    GrowthBulletItem(text = "提交后若出现失败原因，可在历史记录详情中查看。")
                }
            }
        }

        item {
            GrowthSectionCard {
                GrowthSectionTitle(
                    title = "提现历史",
                    subtitle = "缺图页复用单摘要区 + 连续内容区的节奏，历史明细通过底部 sheet 查看。",
                    trailing = { GrowthBadge(text = "${uiState.withdrawals.size} 条") },
                )

                if (uiState.withdrawals.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = GrowthSurfaceRaised,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "暂无提现记录",
                                color = GrowthTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "提交提现申请后，这里会展示 requestNo、状态和链上结果。",
                                color = GrowthTextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                } else {
                    uiState.withdrawals.forEachIndexed { index, item ->
                        WithdrawalHistoryRow(
                            item = item,
                            onClick = { onHistoryClick(item) },
                        )
                        if (index != uiState.withdrawals.lastIndex) {
                            GrowthListDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WithdrawHeroCard(
    uiState: WithdrawUiState,
    onSetMaxAmount: () -> Unit,
) {
    val pendingCount = uiState.withdrawals.count {
        it.status.equals("PENDING", true) || it.status.equals("REVIEWING", true)
    }

    GrowthHighlightCard {
        GrowthBadge(text = "${uiState.settlementAssetCode} / ${uiState.settlementNetworkCode}")
        Text(
            text = "${uiState.availableAmount} ${uiState.settlementAssetCode}",
            color = GrowthTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
        )
        Text(
            text = "当前可提余额",
            color = GrowthTextSecondary,
            fontSize = 14.sp,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GrowthStatChip(
                label = "最低提现",
                value = uiState.minWithdrawAmount,
                modifier = Modifier.weight(1f),
            )
            GrowthStatChip(
                label = "处理中",
                value = pendingCount.toString(),
                modifier = Modifier.weight(1f),
            )
            GrowthStatChip(
                label = "历史笔数",
                value = uiState.withdrawals.size.toString(),
                modifier = Modifier.weight(1f),
            )
        }
        GrowthSecondaryButton(
            text = "一键填入全部金额",
            modifier = Modifier.fillMaxWidth(),
            onClick = onSetMaxAmount,
        )
    }
}

@Composable
private fun WithdrawInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    trailing: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation,
    isError: Boolean,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = label, color = GrowthTextSecondary)
        },
        placeholder = {
            Text(text = placeholder, color = GrowthTextTertiary)
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailing,
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GrowthAccent,
            unfocusedBorderColor = GrowthBorder,
            errorBorderColor = GrowthNegative,
            focusedContainerColor = GrowthSurfaceRaised,
            unfocusedContainerColor = GrowthSurfaceRaised,
            errorContainerColor = GrowthSurfaceRaised,
            focusedTextColor = GrowthTextPrimary,
            unfocusedTextColor = GrowthTextPrimary,
            errorTextColor = GrowthTextPrimary,
            cursorColor = GrowthAccent,
        ),
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
    )
}

@Composable
private fun WithdrawalHistoryRow(
    item: WithdrawalHistoryItem,
    onClick: () -> Unit,
) {
    val statusColor = when (item.status.uppercase()) {
        "COMPLETED" -> GrowthPositive
        "PENDING", "REVIEWING" -> GrowthWarningText
        "FAILED", "REJECTED" -> GrowthNegative
        else -> GrowthTextSecondary
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = item.requestNo,
                    color = GrowthTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
                Text(
                    text = "${formatWithdrawDate(item.createdAt)}  ·  ${item.networkCode}",
                    color = GrowthTextSecondary,
                    fontSize = 12.sp,
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${item.amount} ${item.assetCode}",
                    color = GrowthTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
                GrowthBadge(
                    text = item.status,
                    containerColor = statusColor.copy(alpha = 0.16f),
                    contentColor = statusColor,
                )
            }
        }
        Text(
            text = item.payoutAddress,
            color = GrowthTextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WithdrawConfirmSheet(
    uiState: WithdrawUiState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GrowthSurface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(44.dp)
                    .height(5.dp),
                shape = RoundedCornerShape(999.dp),
                color = GrowthBorder,
            ) {}
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "确认提现",
                color = GrowthTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "保留原有确认流程，但改成 Bitget 风格底部 sheet。",
                color = GrowthTextSecondary,
                fontSize = 13.sp,
            )

            GrowthSectionCard(contentPadding = PaddingValues(16.dp)) {
                GrowthInfoRow(
                    label = "提现金额",
                    value = "${uiState.withdrawAmount} ${uiState.settlementAssetCode}",
                    emphasize = true,
                )
                GrowthListDivider()
                GrowthInfoRow(label = "提现网络", value = uiState.settlementNetworkCode)
                GrowthListDivider()
                GrowthInfoRow(label = "收款地址", value = uiState.walletAddress)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GrowthSecondaryButton(
                    text = "取消",
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss,
                )
                GrowthPrimaryButton(
                    text = if (uiState.isSubmitting) "提交中" else "确认",
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isSubmitting,
                    onClick = onConfirm,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WithdrawalDetailSheet(
    item: WithdrawalHistoryItem,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GrowthSurface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(44.dp)
                    .height(5.dp),
                shape = RoundedCornerShape(999.dp),
                color = GrowthBorder,
            ) {}
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "提现详情",
                color = GrowthTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            GrowthSectionCard(contentPadding = PaddingValues(16.dp)) {
                GrowthInfoRow(label = "requestNo", value = item.requestNo, emphasize = true)
                GrowthListDivider()
                GrowthInfoRow(label = "提现金额", value = "${item.amount} ${item.assetCode}")
                GrowthListDivider()
                GrowthInfoRow(label = "网络", value = item.networkCode)
                GrowthListDivider()
                GrowthInfoRow(label = "状态", value = item.status)
                GrowthListDivider()
                GrowthInfoRow(label = "创建时间", value = formatWithdrawDate(item.createdAt))
                GrowthListDivider()
                GrowthInfoRow(label = "收款地址", value = item.payoutAddress)
                item.txHash?.let {
                    GrowthListDivider()
                    GrowthInfoRow(label = "Tx Hash", value = it)
                }
                item.failReason?.let {
                    GrowthListDivider()
                    GrowthInfoRow(label = "失败原因", value = it, valueColor = GrowthNegative)
                }
            }
            GrowthPrimaryButton(
                text = "关闭",
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismiss,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private fun formatWithdrawDate(value: String): String {
    val normalized = value.replace("T", " ").replace("Z", "")
    return if (normalized.length >= 16) normalized.substring(0, 16) else normalized
}

@Preview(showBackground = true)
@Composable
private fun WithdrawPagePreview() {
    MaterialTheme {
        GrowthBitgetBackground {
            WithdrawPageContent(
                uiState = WithdrawUiState(
                    isLoading = false,
                    availableAmount = "96.24",
                    minWithdrawAmount = "10.00",
                    settlementAssetCode = "USDT",
                    settlementNetworkCode = "SOLANA",
                    withdrawAmount = "32.00",
                    walletAddress = "SoLx1234567890abcdefghijklmn",
                    password = "password",
                    withdrawals = listOf(
                        WithdrawalHistoryItem(
                            requestNo = "WD-20260408-01",
                            amount = "50.00",
                            assetCode = "USDT",
                            networkCode = "SOLANA",
                            payoutAddress = "SoLx1234567890abcdefghijklmn",
                            status = "PENDING",
                            createdAt = "2026-04-08T09:24:00Z",
                        ),
                    ),
                ),
                paddingValues = PaddingValues(),
                onWithdrawAmountChange = {},
                onWalletAddressChange = {},
                onPasswordChange = {},
                onTogglePasswordVisibility = {},
                onSetMaxAmount = {},
                onHistoryClick = {},
            )
        }
    }
}
