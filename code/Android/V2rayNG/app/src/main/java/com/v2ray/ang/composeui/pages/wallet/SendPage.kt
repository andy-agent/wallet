package com.v2ray.ang.composeui.pages.wallet

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
import com.v2ray.ang.composeui.bridge.wallet.WalletBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 发送页状态
 */
sealed class SendPageState {
    object Idle : SendPageState()
    object Validating : SendPageState()
    data class Validated(val fee: String) : SendPageState()
    data class Error(val message: String) : SendPageState()
}

/**
 * 发送页ViewModel
 */
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
        // 只允许数字和小数点
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            _amount.value = value
        }
    }

    fun onAssetSelected(asset: String) {
        _selectedAsset.value = asset
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
            _recipientAddress.value.length < 20 -> {
                _state.value = SendPageState.Error("请输入有效的收款地址")
                return
            }
            _amount.value.isBlank() || _amount.value.toDoubleOrNull() == 0.0 -> {
                _state.value = SendPageState.Error("请输入转账金额")
                return
            }
            _amount.value.toDoubleOrNull() ?: 0.0 > _balance.value.toDoubleOrNull() ?: 0.0 -> {
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

/**
 * 发送页
 * 输入收款地址和转账金额
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendPage(
    viewModel: SendPageViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    symbol: String = "ETH",
    onNavigateBack: () -> Unit = {},
    onNavigateToConfirm: (String, String, String) -> Unit = { _, _, _ -> },
    onScanQR: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val recipientAddress by viewModel.recipientAddress.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val selectedAsset by viewModel.selectedAsset.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val focusManager = LocalFocusManager.current

    // 监听验证状态
    LaunchedEffect(state) {
        when (state) {
            is SendPageState.Validated -> {
                onNavigateToConfirm(recipientAddress, amount, selectedAsset)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发送 $selectedAsset") },
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

            // 资产选择器
            AssetSelector(
                selectedAsset = selectedAsset,
                balance = balance,
                onAssetSelected = { viewModel.onAssetSelected(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 收款地址输入
            OutlinedTextField(
                value = recipientAddress,
                onValueChange = { 
                    viewModel.onRecipientAddressChange(it)
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("收款地址") },
                placeholder = { Text("请输入或粘贴地址") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onScanQR) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is SendPageState.Error
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 金额输入
            OutlinedTextField(
                value = amount,
                onValueChange = { 
                    viewModel.onAmountChange(it)
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("转账金额") },
                placeholder = { Text("0.00") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Token,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    TextButton(onClick = { viewModel.setMaxAmount() }) {
                        Text("MAX")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.validateAndContinue()
                    }
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = state is SendPageState.Error
            )

            // 余额提示
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "可用余额: $balance $selectedAsset",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 错误提示
            if (state is SendPageState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (state as SendPageState.Error).message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 下一步按钮
            Button(
                onClick = { 
                    focusManager.clearFocus()
                    viewModel.validateAndContinue() 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = state !is SendPageState.Validating
            ) {
                if (state is SendPageState.Validating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "下一步",
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
private fun AssetSelector(
    selectedAsset: String,
    balance: String,
    onAssetSelected: (String) -> Unit
) {
    val assets = listOf("ETH", "USDT", "BNB", "MATIC")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "选择资产",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                assets.forEach { asset ->
                    AssetChip(
                        asset = asset,
                        isSelected = asset == selectedAsset,
                        onClick = { onAssetSelected(asset) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetChip(
    asset: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) 
            MaterialTheme.colorScheme.primary 
        else 
            MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.height(36.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = asset,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SendPagePreview() {
    MaterialTheme {
        SendPage()
    }
}
