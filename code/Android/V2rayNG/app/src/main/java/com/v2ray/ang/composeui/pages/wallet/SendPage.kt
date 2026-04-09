package com.v2ray.ang.composeui.pages.wallet

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.wallet.WalletBridgeRepository
import com.v2ray.ang.composeui.components.tags.StatusTag
import com.v2ray.ang.composeui.components.tags.StatusType
import com.v2ray.ang.composeui.theme.ControlPlaneIntent
import com.v2ray.ang.composeui.theme.ControlPlaneLayer
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SendPageState {
    data object Idle : SendPageState()
    data object Validating : SendPageState()
    data class Validated(val fee: String) : SendPageState()
    data class Error(val message: String) : SendPageState()
}

class SendPageViewModel(application: Application) : AndroidViewModel(application) {
    private val walletBridgeRepository = WalletBridgeRepository(application)
    private val _state = MutableStateFlow<SendPageState>(SendPageState.Idle)
    val state: StateFlow<SendPageState> = _state

    private val _recipientAddress = MutableStateFlow("")
    val recipientAddress: StateFlow<String> = _recipientAddress

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    private val _selectedAsset = MutableStateFlow("USDT")
    val selectedAsset: StateFlow<String> = _selectedAsset

    private val _balance = MutableStateFlow("0.00")
    val balance: StateFlow<String> = _balance

    init {
        refreshBalance()
    }

    fun onRecipientAddressChange(value: String) {
        _recipientAddress.value = value
    }

    fun onAmountChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            _amount.value = value
        }
    }

    fun onAssetSelected(asset: String) {
        _selectedAsset.value = asset.uppercase()
        refreshBalance()
    }

    fun setMaxAmount() {
        _amount.value = _balance.value
    }

    fun validateAndContinue() {
        when {
            _recipientAddress.value.isBlank() -> {
                _state.value = SendPageState.Error("请输入收款地址")
                return
            }

            _recipientAddress.value.length < 12 -> {
                _state.value = SendPageState.Error("请输入有效的收款地址")
                return
            }

            _amount.value.isBlank() || _amount.value.toDoubleOrNull() == 0.0 -> {
                _state.value = SendPageState.Error("请输入转账金额")
                return
            }

            (_amount.value.toDoubleOrNull() ?: 0.0) > (_balance.value.toDoubleOrNull() ?: 0.0) -> {
                _state.value = SendPageState.Error("余额不足")
                return
            }
        }

        viewModelScope.launch {
            _state.value = SendPageState.Validating
            _state.value = SendPageState.Validated(fee = "network fee by chain")
        }
    }

    fun clearError() {
        if (_state.value is SendPageState.Error) {
            _state.value = SendPageState.Idle
        }
    }

    private fun refreshBalance() {
        viewModelScope.launch {
            val userId = walletBridgeRepository.getCurrentUserId() ?: return@launch
            val orders = walletBridgeRepository.getCachedOrders(userId)
            val symbol = _selectedAsset.value
            val amount = orders
                .filter { it.assetCode.equals(symbol, true) }
                .sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
            _balance.value = String.format("%.4f", amount)
        }
    }
}

@Composable
fun SendPage(
    viewModel: SendPageViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    symbol: String = "ETH",
    onNavigateBack: () -> Unit = {},
    onNavigateToConfirm: (String, String, String) -> Unit = { _, _, _ -> },
    onScanQR: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val recipientAddress by viewModel.recipientAddress.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val selectedAsset by viewModel.selectedAsset.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(symbol) {
        viewModel.onAssetSelected(symbol)
    }

    LaunchedEffect(state) {
        if (state is SendPageState.Validated) {
            onNavigateToConfirm(recipientAddress, amount, selectedAsset)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            WalletPrimaryButton(
                label = if (state is SendPageState.Validating) "验证中..." else "下一步",
                onClick = { viewModel.validateAndContinue() },
                enabled = state !is SendPageState.Validating,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = WalletPagePadding, vertical = 16.dp),
                icon = Icons.Default.NorthEast,
            )
        },
    ) { paddingValues ->
        WalletPageBackdrop(modifier = Modifier.padding(paddingValues)) {
            SendPageContent(
                state = state,
                recipientAddress = recipientAddress,
                amount = amount,
                selectedAsset = selectedAsset,
                balance = balance,
                onNavigateBack = onNavigateBack,
                onRecipientAddressChange = {
                    viewModel.onRecipientAddressChange(it)
                    viewModel.clearError()
                },
                onAmountChange = {
                    viewModel.onAmountChange(it)
                    viewModel.clearError()
                },
                onAssetSelected = viewModel::onAssetSelected,
                onSetMaxAmount = viewModel::setMaxAmount,
                onScanQR = onScanQR,
                onPaste = {
                    val clipboardText = clipboardManager.getText()?.text.orEmpty()
                    if (clipboardText.isNotBlank()) {
                        viewModel.onRecipientAddressChange(clipboardText)
                        viewModel.clearError()
                    }
                },
            )
        }
    }
}

@Composable
private fun SendPageContent(
    state: SendPageState,
    recipientAddress: String,
    amount: String,
    selectedAsset: String,
    balance: String,
    onNavigateBack: () -> Unit,
    onRecipientAddressChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onAssetSelected: (String) -> Unit,
    onSetMaxAmount: () -> Unit,
    onScanQR: () -> Unit,
    onPaste: () -> Unit,
) {
    val assets = remember { listOf("USDT", "ETH", "SOL", "BNB") }
    var selectedTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = WalletPagePadding,
            end = WalletPagePadding,
            top = 12.dp,
            bottom = 18.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            WalletTopBar(
                title = "收款地址",
                onBack = onNavigateBack,
                trailingIcon = null,
            )
        }

        item {
            WalletGlassCard(
                layer = ControlPlaneLayer.Level3,
                accent = walletAssetAccent(selectedAsset),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    WalletConsoleHeader(
                        eyebrow = "TRANSFER AUTHORIZATION",
                        title = "链上转账",
                        detail = selectedAsset,
                        modifier = Modifier.weight(1f),
                    )
                    StatusTag(
                        text = when (state) {
                            SendPageState.Validating -> "校验中"
                            is SendPageState.Error -> "待修正"
                            else -> "待授权"
                        },
                        type = when (state) {
                            SendPageState.Validating -> StatusType.OK
                            is SendPageState.Error -> StatusType.WARN
                            else -> StatusType.UNKNOWN
                        },
                    )
                }
                WalletMetricStrip(
                    metrics = listOf(
                        WalletOverviewMetric("可用余额", "$balance $selectedAsset"),
                        WalletOverviewMetric("网络", walletNetworkLabel(selectedAsset)),
                        WalletOverviewMetric("路由", "Wallet Live"),
                    ),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WalletIntentBadge(text = "INFRA ROUTE", intent = ControlPlaneIntent.Infra)
                    WalletIntentBadge(text = "SETTLEMENT CHECK", intent = ControlPlaneIntent.Settlement)
                    WalletIntentBadge(text = selectedAsset, intent = ControlPlaneIntent.Finance)
                }
            }
        }

        item {
            WalletInputField(
                value = recipientAddress,
                onValueChange = onRecipientAddressChange,
                placeholder = "请输入接收转账的钱包地址",
                singleLine = false,
                minLines = 4,
                maxLines = 5,
                isError = state is SendPageState.Error && recipientAddress.isBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                WalletAssistPillButton(
                    label = "扫一扫",
                    icon = Icons.Default.QrCodeScanner,
                    onClick = onScanQR,
                    modifier = Modifier.weight(1f),
                )
                WalletAssistPillButton(
                    label = "粘贴",
                    icon = Icons.Default.ContentPaste,
                    onClick = onPaste,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                WalletSelectionChip(
                    label = "我的钱包",
                    selected = selectedTab == 0,
                    intent = ControlPlaneIntent.Infra,
                    onClick = { selectedTab = 0 },
                )
                WalletSelectionChip(
                    label = "地址本",
                    selected = selectedTab == 1,
                    intent = ControlPlaneIntent.Finance,
                    onClick = { selectedTab = 1 },
                )
            }
        }

        item {
            WalletGlassCard(
                layer = ControlPlaneLayer.Level1,
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .background(WalletSurfaceStrong, shape = CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wallet,
                                contentDescription = null,
                                tint = WalletTextTertiary,
                                modifier = Modifier.size(42.dp),
                            )
                        }
                        Text(
                            text = if (selectedTab == 0) "暂无其他钱包" else "地址本为空",
                            style = MaterialTheme.typography.titleMedium,
                            color = WalletTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = if (selectedTab == 0) {
                                "当前只保留手动输入地址入口，现有发送校验和跳转链路保持不变。"
                            } else {
                                "后续地址本数据接入后，这里会直接承接同一转账确认流程。"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = WalletTextSecondary,
                        )
                    }
                }
            }
        }

        item {
            WalletGlassCard(
                accent = walletAssetAccent(selectedAsset),
                layer = ControlPlaneLayer.Level2,
            ) {
                Text(
                    text = "发送金额",
                    style = MaterialTheme.typography.titleLarge,
                    color = WalletTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    assets.forEach { asset ->
                        WalletSelectionChip(
                            label = asset,
                            selected = asset == selectedAsset,
                            intent = if (asset == selectedAsset) {
                                ControlPlaneIntent.Finance
                            } else {
                                ControlPlaneIntent.Neutral
                            },
                            onClick = { onAssetSelected(asset) },
                        )
                    }
                }
                WalletInputField(
                    value = amount,
                    onValueChange = onAmountChange,
                    placeholder = "0.00",
                    leadingIcon = Icons.Default.NorthEast,
                    isError = state is SendPageState.Error && amount.isBlank(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    trailingContent = {
                        Text(
                            text = "MAX",
                            modifier = Modifier.clickable(onClick = onSetMaxAmount),
                            style = MaterialTheme.typography.labelLarge,
                            color = WalletAccent,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                )
                WalletInfoRow(label = "可用余额", value = "$balance $selectedAsset")
                WalletInfoRow(label = "网络", value = walletNetworkLabel(selectedAsset))
                WalletInfoRow(label = "手续费", value = "链上确认")
                if (state is SendPageState.Error) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = WalletDanger,
                    )
                }
            }
        }

        if (state is SendPageState.Validating) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = WalletAccent)
                }
            }
        }
    }
}

@Composable
private fun SendTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        modifier = Modifier.clickable(onClick = onClick),
        style = MaterialTheme.typography.titleMedium,
        color = if (selected) WalletTextPrimary else WalletTextTertiary,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
    )
}

@Composable
private fun SendAssetChip(
    asset: String,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(
                color = if (selected) walletAssetAccent(asset).copy(alpha = 0.18f) else WalletSurfaceStrong,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp),
            )
            .clickable(onClick = onSelected)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(walletAssetAccent(asset), shape = CircleShape),
        )
        Text(
            text = asset,
            style = MaterialTheme.typography.titleSmall,
            color = if (selected) WalletTextPrimary else WalletTextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SendPagePreview() {
    CryptoVPNTheme {
        SendPage()
    }
}
