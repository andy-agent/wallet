package com.v2ray.ang.composeui.pages.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
 * 资产信息
 */
data class AssetInfo(
    val symbol: String,
    val name: String,
    val balance: String,
    val value: String,
    val iconUrl: String? = null
)

/**
 * 钱包首页状态
 */
sealed class WalletHomeState {
    object Idle : WalletHomeState()
    object Loading : WalletHomeState()
    data class Loaded(
        val walletAddress: String,
        val totalValue: String,
        val assets: List<AssetInfo>
    ) : WalletHomeState()
    data class Error(val message: String) : WalletHomeState()
}

/**
 * 钱包首页ViewModel
 */
class WalletHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val walletBridgeRepository = WalletBridgeRepository(application)
    private val _state = MutableStateFlow<WalletHomeState>(WalletHomeState.Idle)
    val state: StateFlow<WalletHomeState> = _state

    private val _isBalanceVisible = MutableStateFlow(true)
    val isBalanceVisible: StateFlow<Boolean> = _isBalanceVisible

    init {
        loadWalletData()
    }

    private fun loadWalletData() {
        viewModelScope.launch {
            _state.value = WalletHomeState.Loading
            val userId = walletBridgeRepository.getCurrentUserId()
            if (userId.isNullOrBlank()) {
                _state.value = WalletHomeState.Error("当前未登录")
                return@launch
            }
            val orders = walletBridgeRepository.getCachedOrders(userId)
            val paidOrders = orders.filter { it.status == "COMPLETED" || it.status == "PAID" || it.status == "FULFILLED" }
            val assets = mutableListOf<AssetInfo>()
            val assetGroups = paidOrders.groupBy { it.assetCode.uppercase() }
            assetGroups.forEach { (symbol, assetOrders) ->
                val amount = assetOrders.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
                assets += AssetInfo(
                    symbol = symbol,
                    name = symbol,
                    balance = String.format("%.4f", amount),
                    value = "$${String.format("%.2f", amount)}"
                )
            }
            if (assets.isEmpty()) {
                assets += AssetInfo(symbol = "USDT", name = "Tether USD", balance = "0.0000", value = "$0.00")
            }
            val total = assets.sumOf { it.balance.toDoubleOrNull() ?: 0.0 }
            _state.value = WalletHomeState.Loaded(
                walletAddress = walletBridgeRepository.currentWalletAddressFallback(userId),
                totalValue = "$${String.format("%.2f", total)}",
                assets = assets
            )
        }
    }

    fun toggleBalanceVisibility() {
        _isBalanceVisible.value = !_isBalanceVisible.value
    }

    fun refreshData() {
        loadWalletData()
    }
}

/**
 * 钱包首页
 * 显示总资产、资产列表和操作按钮
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletHomePage(
    viewModel: WalletHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToReceive: () -> Unit = {},
    onNavigateToSend: () -> Unit = {},
    onNavigateToAssetDetail: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val isBalanceVisible by viewModel.isBalanceVisible.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的钱包") },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is WalletHomeState.Loaded -> {
                    val loadedState = state as WalletHomeState.Loaded
                    WalletHomeContent(
                        state = loadedState,
                        isBalanceVisible = isBalanceVisible,
                        onToggleVisibility = { viewModel.toggleBalanceVisibility() },
                        onNavigateToReceive = onNavigateToReceive,
                        onNavigateToSend = onNavigateToSend,
                        onNavigateToAssetDetail = onNavigateToAssetDetail
                    )
                }
                is WalletHomeState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is WalletHomeState.Error -> {
                    ErrorView(message = (state as WalletHomeState.Error).message)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun WalletHomeContent(
    state: WalletHomeState.Loaded,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onNavigateToReceive: () -> Unit,
    onNavigateToSend: () -> Unit,
    onNavigateToAssetDetail: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 总资产卡片
        TotalBalanceCard(
            walletAddress = state.walletAddress,
            totalValue = state.totalValue,
            isBalanceVisible = isBalanceVisible,
            onToggleVisibility = onToggleVisibility,
            onNavigateToReceive = onNavigateToReceive,
            onNavigateToSend = onNavigateToSend
        )

        // 资产列表标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "我的资产",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${state.assets.size} 种",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 资产列表
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(state.assets) { asset ->
                AssetItem(
                    asset = asset,
                    isBalanceVisible = isBalanceVisible,
                    onClick = { onNavigateToAssetDetail(asset.symbol) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TotalBalanceCard(
    walletAddress: String,
    totalValue: String,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onNavigateToReceive: () -> Unit,
    onNavigateToSend: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // 钱包地址
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "钱包地址",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = walletAddress.take(8) + "..." + walletAddress.takeLast(6),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 总资产
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBalanceVisible) totalValue else "****",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle visibility",
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    icon = Icons.Default.ArrowDownward,
                    label = "收款",
                    onClick = onNavigateToReceive
                )
                ActionButton(
                    icon = Icons.Default.ArrowUpward,
                    label = "发送",
                    onClick = onNavigateToSend
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun AssetItem(
    asset: AssetInfo,
    isBalanceVisible: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 资产图标
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = asset.symbol.take(1),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 资产信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.symbol,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = asset.name,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 余额和价值
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isBalanceVisible) "${asset.balance} ${asset.symbol}" else "****",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isBalanceVisible) asset.value else "****",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "加载失败",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WalletHomePagePreview() {
    MaterialTheme {
        WalletHomePage()
    }
}
