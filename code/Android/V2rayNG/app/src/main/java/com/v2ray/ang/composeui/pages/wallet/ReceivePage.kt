package com.v2ray.ang.composeui.pages.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/**
 * 收款页状态
 */
sealed class ReceivePageState {
    object Loading : ReceivePageState()
    data class Loaded(
        val symbol: String,
        val walletAddress: String,
    ) : ReceivePageState()
    data class Error(val message: String) : ReceivePageState()
    data class AddressCopied(val address: String) : ReceivePageState()
}

/**
 * 收款页ViewModel
 */
class ReceivePageViewModel(application: Application) : AndroidViewModel(application) {
    private val walletBridgeRepository = WalletBridgeRepository(application)
    private val _state = MutableStateFlow<ReceivePageState>(ReceivePageState.Loading)
    val state: StateFlow<ReceivePageState> = _state
    private var loadedSnapshot: ReceivePageState.Loaded? = null

    init {
        viewModelScope.launch {
            val userId = walletBridgeRepository.getCurrentUserId()
            if (userId.isNullOrBlank()) {
                _state.value = ReceivePageState.Error("当前未登录")
                return@launch
            }
            val orders = walletBridgeRepository.getCachedOrders(userId)
            val symbol = orders.firstOrNull()?.assetCode?.uppercase() ?: "USDT"
            val loaded = ReceivePageState.Loaded(
                symbol = symbol,
                walletAddress = walletBridgeRepository.currentWalletAddressFallback(userId)
            )
            loadedSnapshot = loaded
            _state.value = loaded
        }
    }

    fun onAddressCopied(address: String) {
        _state.value = ReceivePageState.AddressCopied(address)
    }

    fun resetState() {
        loadedSnapshot?.let { _state.value = it }
    }
}

/**
 * 生成二维码Bitmap
 */
fun generateReceiveQRCode(content: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

/**
 * 收款页
 * 显示收款二维码和地址
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceivePage(
    viewModel: ReceivePageViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    // 生成二维码
    // 监听复制状态
    LaunchedEffect(state) {
        when (state) {
            is ReceivePageState.AddressCopied -> {
                snackbarHostState.showSnackbar("地址已复制")
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val s = state) {
                            is ReceivePageState.Loaded -> "接收 ${s.symbol}"
                            else -> "接收"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val currentState = state) {
            is ReceivePageState.Loaded -> ReceivePageContent(
                symbol = currentState.symbol,
                walletAddress = currentState.walletAddress,
                paddingValues = paddingValues,
                onCopy = {
                    clipboardManager.setText(AnnotatedString(currentState.walletAddress))
                    viewModel.onAddressCopied(currentState.walletAddress)
                }
            )
            is ReceivePageState.Error -> ErrorView(message = currentState.message)
            is ReceivePageState.Loading, is ReceivePageState.AddressCopied -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
    }
}

@Composable
private fun ReceivePageContent(
    symbol: String,
    walletAddress: String,
    paddingValues: PaddingValues,
    onCopy: () -> Unit,
) {
    val qrBitmap = remember(walletAddress) { generateReceiveQRCode(walletAddress) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 提示文字
            Text(
                text = "扫描下方二维码或复制地址向我转账",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 二维码卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 二维码
                    qrBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 地址
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = walletAddress,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    onCopy()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 资产信息
            AssetInfoCard(symbol = symbol)

            Spacer(modifier = Modifier.weight(1f))

            // 分享按钮
            Button(
                onClick = { /* 分享地址 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "分享地址",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AssetInfoCard(symbol: String) {
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
                text = "资产信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(label = "资产", value = symbol)
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(label = "网络", value = when (symbol) {
                "ETH" -> "Ethereum Mainnet"
                "BNB" -> "BSC Mainnet"
                "MATIC" -> "Polygon Mainnet"
                else -> "Mainnet"
            })
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow(label = "最小转账金额", value = "0.001 $symbol")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ErrorView(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

@Preview(showBackground = true)
@Composable
fun ReceivePagePreview() {
    MaterialTheme {
        ReceivePage()
    }
}
