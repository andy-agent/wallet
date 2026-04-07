package com.v2ray.ang.composeui.pages.wallet

import androidx.compose.foundation.background
import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.wallet.WalletBridgeRepository
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

@OptIn(ExperimentalMaterial3Api::class)
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

    LaunchedEffect(symbol) {
        viewModel.onAssetSelected(symbol)
    }

    LaunchedEffect(state) {
        if (state is SendPageState.Validated) {
            onNavigateToConfirm(recipientAddress, amount, selectedAsset)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send ${selectedAsset.uppercase()}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
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
                onRecipientAddressChange = {
                    viewModel.onRecipientAddressChange(it)
                    viewModel.clearError()
                },
                onAmountChange = {
                    viewModel.onAmountChange(it)
                    viewModel.clearError()
                },
                onAssetSelected = { viewModel.onAssetSelected(it) },
                onSetMaxAmount = { viewModel.setMaxAmount() },
                onContinue = { viewModel.validateAndContinue() },
                onScanQR = onScanQR,
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
    onRecipientAddressChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onAssetSelected: (String) -> Unit,
    onSetMaxAmount: () -> Unit,
    onContinue: () -> Unit,
    onScanQR: () -> Unit,
) {
    val assets = listOf("USDT", "ETH", "SOL", "BNB")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            WalletGlassCard(accent = walletAssetAccent(selectedAsset)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        WalletTag(text = "SEND", accent = walletAssetAccent(selectedAsset))
                        Text(
                            text = "资产发送起点",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    WalletTag(text = walletNetworkLabel(selectedAsset), accent = MaterialTheme.colorScheme.secondary)
                }

                Text(
                    text = "可用余额 $balance $selectedAsset",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "保留现有确认页桥接逻辑，只更新表单视觉、步骤感和摘要卡层级。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                WalletMetricStrip(
                    metrics = listOf(
                        WalletOverviewMetric("资产", selectedAsset),
                        WalletOverviewMetric("网络", walletNetworkLabel(selectedAsset)),
                        WalletOverviewMetric("桥接", "兼容"),
                    ),
                )
            }
        }

        item {
            WalletSectionHeading(
                title = "选择资产",
                subtitle = "快速切换主资产，保留余额取值与发送流程入口。",
            )
        }

        item {
            WalletGlassCard(accent = MaterialTheme.colorScheme.secondary) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    assets.forEach { asset ->
                        AssetFilterChip(
                            asset = asset,
                            selected = asset == selectedAsset,
                            onSelected = { onAssetSelected(asset) },
                        )
                    }
                }
            }
        }

        item {
            WalletSectionHeading(
                title = "发送信息",
                subtitle = "地址与金额仍按现有校验规则处理，错误态只调整为新的卡片样式。",
            )
        }

        item {
            WalletGlassCard(accent = walletAssetAccent(selectedAsset)) {
                OutlinedTextField(
                    value = recipientAddress,
                    onValueChange = onRecipientAddressChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("收款地址") },
                    placeholder = { Text("请输入或粘贴链上地址") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Wallet,
                            contentDescription = "Recipient address",
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = onScanQR) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR",
                            )
                        }
                    },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    isError = state is SendPageState.Error && recipientAddress.isBlank(),
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("发送金额") },
                    placeholder = { Text("0.00") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.NorthEast,
                            contentDescription = "Amount",
                        )
                    },
                    trailingIcon = {
                        WalletSecondaryButton(label = "MAX", onClick = onSetMaxAmount)
                    },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    isError = state is SendPageState.Error && amount.isBlank(),
                )

                if (state is SendPageState.Error) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        item {
            WalletSectionHeading(
                title = "发送摘要",
                subtitle = "进入下一步前先确认资产、余额与桥接状态。",
            )
        }

        item {
            WalletGlassCard(accent = MaterialTheme.colorScheme.tertiary) {
                WalletMetricStrip(
                    metrics = listOf(
                        WalletOverviewMetric("数量", amount.ifBlank { "--" }),
                        WalletOverviewMetric("余额", balance),
                        WalletOverviewMetric("手续费", "链上确认"),
                    ),
                )
                Text(
                    text = "确认后会继续进入现有 WalletPaymentConfirmPage2 起点，不改变后续业务流程。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            WalletPrimaryButton(
                label = "下一步",
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            )
        }

        if (state is SendPageState.Validating) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun AssetFilterChip(
    asset: String,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onSelected,
        label = {
            Text(
                text = asset,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .padding(1.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(walletAssetAccent(asset)),
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = walletAssetAccent(asset).copy(alpha = 0.18f),
            selectedLabelColor = MaterialTheme.colorScheme.onSurface,
            selectedLeadingIconColor = walletAssetAccent(asset),
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun SendPagePreview() {
    CryptoVPNTheme {
        SendPage()
    }
}
