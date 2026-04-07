package com.v2ray.ang.composeui.pages.wallet

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.v2ray.ang.composeui.bridge.wallet.WalletBridgeRepository
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReceivePageState {
    data object Loading : ReceivePageState()

    data class Loaded(
        val symbol: String,
        val walletAddress: String,
    ) : ReceivePageState()

    data class Error(val message: String) : ReceivePageState()
    data class AddressCopied(val address: String) : ReceivePageState()
}

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
                walletAddress = walletBridgeRepository.currentWalletAddressFallback(userId),
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

fun generateReceiveQRCode(content: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE,
                )
            }
        }
        bitmap
    } catch (_: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceivePage(
    viewModel: ReceivePageViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        if (state is ReceivePageState.AddressCopied) {
            snackbarHostState.showSnackbar("地址已复制")
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val currentState = state) {
                            is ReceivePageState.Loaded -> "Receive ${currentState.symbol}"
                            else -> "Receive"
                        },
                    )
                },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        WalletPageBackdrop(modifier = Modifier.padding(paddingValues)) {
            when (val currentState = state) {
                is ReceivePageState.Loaded -> ReceivePageContent(
                    symbol = currentState.symbol,
                    walletAddress = currentState.walletAddress,
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(currentState.walletAddress))
                        viewModel.onAddressCopied(currentState.walletAddress)
                    },
                )

                is ReceivePageState.Error -> ReceiveErrorView(message = currentState.message)

                ReceivePageState.Loading, is ReceivePageState.AddressCopied -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ReceivePageContent(
    symbol: String,
    walletAddress: String,
    onCopy: () -> Unit,
) {
    val qrBitmap = remember(walletAddress) { generateReceiveQRCode(walletAddress) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        WalletGlassCard(accent = walletAssetAccent(symbol)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    WalletTag(text = "RECEIVE", accent = walletAssetAccent(symbol))
                    Text(
                        text = "扫码向我付款",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                WalletTag(text = walletNetworkLabel(symbol), accent = MaterialTheme.colorScheme.secondary)
            }

            Text(
                text = "Bitget 风格二维码入口，继续复用当前地址生成与复制行为。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                WalletGlassCard(
                    modifier = Modifier.width(260.dp),
                    accent = MaterialTheme.colorScheme.secondary,
                    contentPadding = PaddingValues(20.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Receive QR Code",
                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(RoundedCornerShape(22.dp)),
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QRCode placeholder",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(120.dp),
                            )
                        }
                    }
                }
            }

            WalletMetricStrip(
                metrics = listOf(
                    WalletOverviewMetric("资产", symbol),
                    WalletOverviewMetric("网络", walletNetworkLabel(symbol)),
                    WalletOverviewMetric("状态", "Ready"),
                ),
            )
        }

        WalletSectionHeading(
            title = "地址信息",
            subtitle = "复制地址或分享链接仍保持兼容，占位能力不变。",
        )

        WalletGlassCard(accent = MaterialTheme.colorScheme.secondary) {
            Text(
                text = walletAddress,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "请确认转入链路与网络一致，错误网络资产不会自动退回。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WalletPrimaryButton(
                    label = "复制地址",
                    onClick = onCopy,
                    modifier = Modifier.weight(1f),
                )
                WalletSecondaryButton(
                    label = "分享",
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
            }
        }

        WalletGlassCard(accent = MaterialTheme.colorScheme.primary) {
            WalletSectionHeading(
                title = "收款提示",
                subtitle = "沿用原有 placeholder 逻辑，只增强视觉分层与重点提醒。",
            )
            WalletMetricStrip(
                metrics = listOf(
                    WalletOverviewMetric("最小金额", "0.001 $symbol"),
                    WalletOverviewMetric("到账方式", "链上确认"),
                    WalletOverviewMetric("桥接", "兼容"),
                ),
            )
            Text(
                text = "若对方需要文字地址，请直接使用复制按钮；二维码与地址对应同一账户。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun ReceiveErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        WalletGlassCard(accent = MaterialTheme.colorScheme.error) {
            Text(
                text = "收款页不可用",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceivePagePreview() {
    CryptoVPNTheme {
        ReceivePage()
    }
}
