package com.cryptovpn.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.CryptoVPNTheme

// ==================== Enums & Data Models ====================

enum class BlockchainChain {
    SOLANA,
    TRON
}

data class ChainInfo(
    val chain: BlockchainChain,
    val name: String,
    val symbol: String,
    val color: Color
)

data class AssetItem(
    val symbol: String,
    val name: String,
    val balance: String,
    val usdValue: String,
    val priceChange: String,
    val isPositive: Boolean
)

data class WalletHomeState(
    val selectedChain: BlockchainChain = BlockchainChain.SOLANA,
    val totalUsdValue: String = "1,234.56",
    val totalBtcValue: String = "0.0284",
    val assets: List<AssetItem> = emptyList(),
    val walletAddress: String = "0x742d...5f0bEb",
    val isAddressCopied: Boolean = false
)

// ==================== ViewModel ====================

class WalletHomeViewModel {
    var state by mutableStateOf(WalletHomeState())
        private set

    init {
        loadAssets()
    }

    private fun loadAssets() {
        val sampleAssets = listOf(
            AssetItem(
                symbol = "SOL",
                name = "Solana",
                balance = "45.23",
                usdValue = "4,523.00",
                priceChange = "+5.2%",
                isPositive = true
            ),
            AssetItem(
                symbol = "USDC",
                name = "USD Coin",
                balance = "500.00",
                usdValue = "500.00",
                priceChange = "0.0%",
                isPositive = true
            ),
            AssetItem(
                symbol = "USDT",
                name = "Tether",
                balance = "300.00",
                usdValue = "300.00",
                priceChange = "0.0%",
                isPositive = true
            ),
            AssetItem(
                symbol = "BONK",
                name = "Bonk",
                balance = "1000000",
                usdValue = "11.56",
                priceChange = "-2.1%",
                isPositive = false
            )
        )
        state = state.copy(assets = sampleAssets)
    }

    fun onChainSelected(chain: BlockchainChain) {
        state = state.copy(selectedChain = chain)
        // Reload assets for selected chain
    }

    fun onCopyAddress() {
        state = state.copy(isAddressCopied = true)
        // Reset after delay
    }

    fun onSendClick() {
        // Navigate to send page
    }

    fun onReceiveClick() {
        // Navigate to receive page
    }

    fun onSwapClick() {
        // Navigate to swap page
    }

    fun onAssetClick(symbol: String) {
        // Navigate to asset detail
    }
}

// ==================== Page Composable ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletHomePage(
    viewModel: WalletHomeViewModel = remember { WalletHomeViewModel() },
    onSendClick: () -> Unit = {},
    onReceiveClick: () -> Unit = {},
    onSwapClick: () -> Unit = {},
    onAssetClick: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "我的钱包",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B1020)
                )
            )
        },
        containerColor = Color(0xFF0B1020)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Total Balance Card
            TotalBalanceCard(state = state)

            // Chain Selector
            ChainSelector(
                selectedChain = state.selectedChain,
                onChainSelected = { viewModel.onChainSelected(it) }
            )

            // Assets List Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "资产",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "隐藏小额资产",
                    color = Color(0xFF1D4ED8),
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { }
                )
            }

            // Assets List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.assets) { asset ->
                    AssetCard(
                        asset = asset,
                        onClick = { 
                            viewModel.onAssetClick(asset.symbol)
                            onAssetClick(asset.symbol)
                        }
                    )
                }
            }
        }
    }

    // Bottom Action Bar overlay
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomActionBar(
            onSendClick = {
                viewModel.onSendClick()
                onSendClick()
            },
            onReceiveClick = {
                viewModel.onReceiveClick()
                onReceiveClick()
            },
            onSwapClick = {
                viewModel.onSwapClick()
                onSwapClick()
            }
        )
    }
}

@Composable
private fun TotalBalanceCard(state: WalletHomeState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1D4ED8),
                            Color(0xFF3B82F6)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                // Address Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = state.walletAddress,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    IconButton(
                        onClick = { /* Copy address */ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (state.isAddressCopied) 
                                Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total Balance
                Text(
                    text = "$${state.totalUsdValue}",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // BTC Value
                Text(
                    text = "≈ ${state.totalBtcValue} BTC",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ChainSelector(
    selectedChain: BlockchainChain,
    onChainSelected: (BlockchainChain) -> Unit
) {
    val chains = listOf(
        ChainInfo(BlockchainChain.SOLANA, "Solana", "SOL", Color(0xFF9945FF)),
        ChainInfo(BlockchainChain.TRON, "TRON", "TRX", Color(0xFFFF060A))
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        chains.forEach { chain ->
            val isSelected = selectedChain == chain.chain
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onChainSelected(chain.chain) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) chain.color.copy(alpha = 0.2f) 
                        else Color(0xFF1F2937)
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = chain.color
                ) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(chain.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = chain.name,
                        color = if (isSelected) chain.color else Color.White,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetCard(
    asset: AssetItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Token Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF374151)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = asset.symbol.take(2),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = asset.symbol,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = asset.name,
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${asset.balance} ${asset.symbol}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${asset.usdValue}",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                    Text(
                        text = asset.priceChange,
                        color = if (asset.isPositive) Color(0xFF22C55E) else Color(0xFFEF4444),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onSwapClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = Icons.Default.ArrowUpward,
                label = "发送",
                onClick = onSendClick
            )
            ActionButton(
                icon = Icons.Default.ArrowDownward,
                label = "收款",
                onClick = onReceiveClick
            )
            ActionButton(
                icon = Icons.Default.SwapHoriz,
                label = "兑换",
                onClick = onSwapClick
            )
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
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF374151)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF1D4ED8),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

// ==================== Preview ====================

@Preview(device = "id:pixel_5")
@Composable
private fun WalletHomePagePreview() {
    CryptoVPNTheme {
        WalletHomePage()
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun WalletHomePageTronPreview() {
    CryptoVPNTheme {
        val viewModel = remember { WalletHomeViewModel() }
        viewModel.state = WalletHomeState(selectedChain = BlockchainChain.TRON)
        WalletHomePage(viewModel = viewModel)
    }
}