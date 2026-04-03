package com.cryptovpn.ui.pages.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.CryptoVPNTheme
import com.cryptovpn.ui.components.CommonTopAppBar
import com.cryptovpn.ui.components.LoadingIndicator
import java.math.BigDecimal

/**
 * 发送页状态
 */
sealed class SendPageState {
    data object Loading : SendPageState()
    data class Editing(
        val selectedAsset: AssetInfo,
        val recipientAddress: String = "",
        val amount: String = "",
        val usdValue: String = "",
        val fee: FeeInfo,
        val availableBalance: BigDecimal,
        val isAddressValid: Boolean = false,
        val isAmountValid: Boolean = false,
        val errorMessage: String? = null
    ) : SendPageState()
    data class ReadyToSign(
        val selectedAsset: AssetInfo,
        val recipientAddress: String,
        val amount: BigDecimal,
        val usdValue: String,
        val fee: FeeInfo
    ) : SendPageState()
    data class Broadcasting(
        val message: String = "正在广播交易..."
    ) : SendPageState()
    data class Pending(
        val txHash: String,
        val message: String = "等待确认中..."
    ) : SendPageState()
    data class Success(
        val txHash: String,
        val amount: BigDecimal,
        val recipientAddress: String
    ) : SendPageState()
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : SendPageState()
}

/**
 * 资产信息
 */
data class AssetInfo(
    val symbol: String,
    val name: String,
    val balance: BigDecimal,
    val usdPrice: BigDecimal,
    val iconUrl: String? = null,
    val networkColor: Color
)

/**
 * 手续费信息
 */
data class FeeInfo(
    val amount: BigDecimal,
    val symbol: String,
    val usdValue: String,
    val estimatedTime: String
)

/**
 * 发送页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendPage(
    onNavigateBack: () -> Unit = {},
    onNavigateToResult: (isSuccess: Boolean, txHash: String?) -> Unit = { _, _ -> },
    onScanQRCode: () -> Unit = {},
    viewModel: SendViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showAssetSelector by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (state) {
            is SendPageState.Success -> {
                val successState = state as SendPageState.Success
                onNavigateToResult(true, successState.txHash)
            }
            is SendPageState.Error -> {
                val errorState = state as SendPageState.Error
                if (!errorState.canRetry) {
                    onNavigateToResult(false, null)
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "发送",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1020))
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is SendPageState.Loading -> {
                    LoadingIndicator()
                }
                is SendPageState.Editing -> {
                    SendContent(
                        state = currentState,
                        onAssetClick = { showAssetSelector = true },
                        onAddressChange = { viewModel.updateRecipientAddress(it) },
                        onAmountChange = { viewModel.updateAmount(it) },
                        onMaxClick = { viewModel.setMaxAmount() },
                        onScanClick = onScanQRCode,
                        onConfirmClick = { showConfirmDialog = true }
                    )
                }
                is SendPageState.ReadyToSign -> {
                    showConfirmDialog = true
                }
                is SendPageState.Broadcasting -> {
                    BroadcastingView(message = currentState.message)
                }
                is SendPageState.Pending -> {
                    PendingView(
                        txHash = currentState.txHash,
                        message = currentState.message
                    )
                }
                is SendPageState.Success -> {
                    // 导航到结果页
                }
                is SendPageState.Error -> {
                    ErrorView(
                        message = currentState.message,
                        canRetry = currentState.canRetry,
                        onRetry = { viewModel.retry() }
                    )
                }
            }
        }
    }

    // 资产选择器
    if (showAssetSelector && state is SendPageState.Editing) {
        val editingState = state as SendPageState.Editing
        AssetSelectorDialog(
            currentAsset = editingState.selectedAsset,
            onAssetSelected = { 
                viewModel.selectAsset(it)
                showAssetSelector = false
            },
            onDismiss = { showAssetSelector = false }
        )
    }

    // 确认对话框
    if (showConfirmDialog && state is SendPageState.ReadyToSign) {
        val readyState = state as SendPageState.ReadyToSign
        ConfirmTransactionDialog(
            state = readyState,
            onConfirm = {
                viewModel.confirmTransaction()
                showConfirmDialog = false
            },
            onCancel = {
                viewModel.cancelTransaction()
                showConfirmDialog = false
            }
        )
    }
}

@Composable
private fun SendContent(
    state: SendPageState.Editing,
    onAssetClick: () -> Unit,
    onAddressChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onMaxClick: () -> Unit,
    onScanClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        // 资产选择卡片
        AssetSelectorCard(
            asset = state.selectedAsset,
            onClick = onAssetClick
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 收款地址输入
        AddressInputCard(
            address = state.recipientAddress,
            isValid = state.isAddressValid,
            onAddressChange = onAddressChange,
            onScanClick = onScanClick
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 金额输入
        AmountInputCard(
            amount = state.amount,
            usdValue = state.usdValue,
            availableBalance = state.availableBalance,
            symbol = state.selectedAsset.symbol,
            isValid = state.isAmountValid,
            onAmountChange = onAmountChange,
            onMaxClick = onMaxClick
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 手续费卡片
        FeeCard(fee = state.fee)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 错误提示
        state.errorMessage?.let { error ->
            ErrorMessage(message = error)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // 确认按钮
        ConfirmButton(
            enabled = state.isAddressValid && state.isAmountValid,
            onClick = onConfirmClick
        )
    }
}

@Composable
private fun AssetSelectorCard(
    asset: AssetInfo,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1F2937))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 资产图标
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(asset.networkColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = asset.symbol.take(1),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = asset.symbol,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = asset.name,
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${asset.balance}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "≈ $${asset.balance.multiply(asset.usdPrice)}",
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "选择资产",
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun AddressInputCard(
    address: String,
    isValid: Boolean,
    onAddressChange: (String) -> Unit,
    onScanClick: () -> Unit
) {
    Column {
        Text(
            text = "收款地址",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { 
                Text(
                    text = "输入或粘贴地址",
                    color = Color(0xFF6B7280)
                ) 
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isValid) Color(0xFF22C55E) else Color(0xFF1D4ED8),
                unfocusedBorderColor = Color(0xFF374151),
                focusedContainerColor = Color(0xFF1F2937),
                unfocusedContainerColor = Color(0xFF1F2937),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onScanClick) {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = "扫码",
                        tint = Color(0xFF1D4ED8)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        
        if (isValid) {
            Text(
                text = "地址格式正确",
                color = Color(0xFF22C55E),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
private fun AmountInputCard(
    amount: String,
    usdValue: String,
    availableBalance: java.math.BigDecimal,
    symbol: String,
    isValid: Boolean,
    onAmountChange: (String) -> Unit,
    onMaxClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "转账金额",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            TextButton(onClick = onMaxClick) {
                Text(
                    text = "全部",
                    color = Color(0xFF1D4ED8),
                    fontSize = 13.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { 
                Text(
                    text = "0.00",
                    color = Color(0xFF6B7280)
                ) 
            },
            suffix = {
                Text(
                    text = symbol,
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isValid) Color(0xFF22C55E) else Color(0xFF1D4ED8),
                unfocusedBorderColor = Color(0xFF374151),
                focusedContainerColor = Color(0xFF1F2937),
                unfocusedContainerColor = Color(0xFF1F2937),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedSuffixColor = Color(0xFF9CA3AF),
                unfocusedSuffixColor = Color(0xFF9CA3AF)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "≈ $$usdValue",
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp
            )
            
            Text(
                text = "可用: $availableBalance $symbol",
                color = Color(0xFF6B7280),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun FeeCard(fee: FeeInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1F2937))
            .padding(16.dp)
    ) {
        Text(
            text = "网络手续费",
            color = Color(0xFF9CA3AF),
            fontSize = 13.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalGasStation,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${fee.amount} ${fee.symbol}",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Text(
                text = "≈ $${fee.usdValue}",
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "预计 ${fee.estimatedTime} 确认",
            color = Color(0xFF6B7280),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            color = Color(0xFFEF4444),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ConfirmButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1D4ED8),
            disabledContainerColor = Color(0xFF1D4ED8).copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "确认转账",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BroadcastingView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF1D4ED8),
            strokeWidth = 3.dp,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun PendingView(
    txHash: String,
    message: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFF59E0B),
            strokeWidth = 3.dp,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "交易哈希: ${txHash.take(16)}...",
            color = Color(0xFF9CA3AF),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ErrorView(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        if (canRetry) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("重试")
            }
        }
    }
}

@Composable
private fun AssetSelectorDialog(
    currentAsset: AssetInfo,
    onAssetSelected: (AssetInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val assets = listOf(
        AssetInfo(
            symbol = "USDT",
            name = "Tether USD",
            balance = java.math.BigDecimal("1250.50"),
            usdPrice = java.math.BigDecimal("1.00"),
            networkColor = Color(0xFF26A17B)
        ),
        AssetInfo(
            symbol = "TRX",
            name = "TRON",
            balance = java.math.BigDecimal("5000.00"),
            usdPrice = java.math.BigDecimal("0.12"),
            networkColor = Color(0xFFFF060A)
        ),
        AssetInfo(
            symbol = "SOL",
            name = "Solana",
            balance = java.math.BigDecimal("25.75"),
            usdPrice = java.math.BigDecimal("145.30"),
            networkColor = Color(0xFF9945FF)
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1F2937),
        title = {
            Text(
                text = "选择资产",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                assets.forEach { asset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onAssetSelected(asset) }
                            .background(
                                if (asset.symbol == currentAsset.symbol) 
                                    Color(0xFF1D4ED8).copy(alpha = 0.2f)
                                else 
                                    Color.Transparent
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(asset.networkColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = asset.symbol.take(1),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = asset.symbol,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = asset.name,
                                color = Color(0xFF9CA3AF),
                                fontSize = 12.sp
                            )
                        }
                        
                        Text(
                            text = "${asset.balance}",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    
                    if (asset != assets.last()) {
                        Divider(
                            color = Color(0xFF374151),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = Color(0xFF9CA3AF))
            }
        }
    )
}

@Composable
private fun ConfirmTransactionDialog(
    state: SendPageState.ReadyToSign,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = Color(0xFF1F2937),
        title = {
            Text(
                text = "确认转账",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                // 发送金额
                DetailRow("发送金额", "${state.amount} ${state.selectedAsset.symbol}")
                DetailRow("约等于", "$${state.usdValue}")
                
                Divider(
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                
                // 收款地址
                DetailRow("收款地址", state.recipientAddress.take(12) + "..." + state.recipientAddress.takeLast(8))
                
                Divider(
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                
                // 手续费
                DetailRow("网络手续费", "${state.fee.amount} ${state.fee.symbol}")
                DetailRow("预计时间", state.fee.estimatedTime)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("取消", color = Color(0xFF9CA3AF))
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun SendPagePreview() {
    CryptoVPNTheme {
        SendContent(
            state = SendPageState.Editing(
                selectedAsset = AssetInfo(
                    symbol = "USDT",
                    name = "Tether USD",
                    balance = java.math.BigDecimal("1250.50"),
                    usdPrice = java.math.BigDecimal("1.00"),
                    networkColor = Color(0xFF26A17B)
                ),
                recipientAddress = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t",
                amount = "100",
                usdValue = "100.00",
                fee = FeeInfo(
                    amount = java.math.BigDecimal("1.5"),
                    symbol = "TRX",
                    usdValue = "0.18",
                    estimatedTime = "3-5 分钟"
                ),
                availableBalance = java.math.BigDecimal("1250.50"),
                isAddressValid = true,
                isAmountValid = true,
                errorMessage = null
            ),
            onAssetClick = {},
            onAddressChange = {},
            onAmountChange = {},
            onMaxClick = {},
            onScanClick = {},
            onConfirmClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun SendPageWithErrorPreview() {
    CryptoVPNTheme {
        SendContent(
            state = SendPageState.Editing(
                selectedAsset = AssetInfo(
                    symbol = "USDT",
                    name = "Tether USD",
                    balance = java.math.BigDecimal("1250.50"),
                    usdPrice = java.math.BigDecimal("1.00"),
                    networkColor = Color(0xFF26A17B)
                ),
                recipientAddress = "invalid",
                amount = "999999",
                usdValue = "999999.00",
                fee = FeeInfo(
                    amount = java.math.BigDecimal("1.5"),
                    symbol = "TRX",
                    usdValue = "0.18",
                    estimatedTime = "3-5 分钟"
                ),
                availableBalance = java.math.BigDecimal("1250.50"),
                isAddressValid = false,
                isAmountValid = false,
                errorMessage = "余额不足"
            ),
            onAssetClick = {},
            onAddressChange = {},
            onAmountChange = {},
            onMaxClick = {},
            onScanClick = {},
            onConfirmClick = {}
        )
    }
}
