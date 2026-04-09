package com.v2ray.ang.composeui.pages.wallet

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.v2ray.ang.composeui.components.tags.StatusTag
import com.v2ray.ang.composeui.components.tags.StatusType
import com.v2ray.ang.composeui.theme.ControlPlaneIntent
import com.v2ray.ang.composeui.theme.ControlPlaneLayer
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
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (state is ReceivePageState.Loaded) {
                WalletPrimaryButton(
                    label = "分享给 TA",
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = WalletPagePadding, vertical = 16.dp),
                    icon = Icons.Default.Share,
                )
            }
        },
    ) { paddingValues ->
        WalletPageBackdrop(modifier = Modifier.padding(paddingValues)) {
            when (val currentState = state) {
                is ReceivePageState.Loaded -> ReceivePageContent(
                    symbol = currentState.symbol,
                    walletAddress = currentState.walletAddress,
                    onNavigateBack = onNavigateBack,
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
                    CircularProgressIndicator(color = WalletAccent)
                }
            }
        }
    }
}

@Composable
private fun ReceivePageContent(
    symbol: String,
    walletAddress: String,
    onNavigateBack: () -> Unit,
    onCopy: () -> Unit,
) {
    val qrBitmap = remember(walletAddress) { generateReceiveQRCode(walletAddress) }

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
            WalletCloseBar(onClose = onNavigateBack)
        }

        item {
            WalletGlassCard(
                layer = ControlPlaneLayer.Level3,
                accent = walletAssetAccent(symbol),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    WalletConsoleHeader(
                        eyebrow = "SETTLEMENT INTAKE",
                        title = "$symbol 收款",
                        detail = walletNetworkLabel(symbol),
                        modifier = Modifier.weight(1f),
                    )
                    StatusTag(text = "可接收", type = StatusType.OK)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    WalletTokenBadge(symbol = symbol, modifier = Modifier.size(78.dp))
                    Text(
                        text = "仅支持接收 ${walletNetworkLabel(symbol)} 网络资产",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WalletTextSecondary,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WalletIntentBadge(text = "INFRA ROUTE", intent = ControlPlaneIntent.Infra)
                    WalletIntentBadge(text = "SETTLEMENT READY", intent = ControlPlaneIntent.Settlement)
                }
            }
        }

        item {
            WalletGlassCard(
                accent = walletAssetAccent(symbol),
                layer = ControlPlaneLayer.Level2,
                contentPadding = PaddingValues(22.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(292.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(28.dp))
                            .background(androidx.compose.ui.graphics.Color.White),
                            contentAlignment = Alignment.Center,
                    ) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Receive QR Code",
                                modifier = Modifier.size(252.dp),
                            )
                        } else {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QRCode placeholder",
                                tint = WalletTextTertiary,
                                modifier = Modifier.size(140.dp),
                            )
                        }
                    }
                }
            }
        }

        item {
            WalletGlassCard(
                layer = ControlPlaneLayer.Level1,
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            ) {
                Text(
                    text = "收款地址",
                    style = MaterialTheme.typography.titleMedium,
                    color = WalletTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = walletAddress,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = WalletTextSecondary,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(WalletSurfaceStrong, shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                            .clickable(onClick = onCopy),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "copy address",
                            tint = WalletTextPrimary,
                        )
                    }
                }
            }
        }

        item {
            WalletGlassCard(
                accent = WalletAccent,
                layer = ControlPlaneLayer.Level2,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Redeem,
                        contentDescription = null,
                        tint = WalletAccent,
                    )
                    Text(
                        text = "${walletNetworkLabel(symbol)} 链转账，享每日 3 笔免 Gas 福利！",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WalletTextPrimary,
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReceiveErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WalletPagePadding),
        contentAlignment = Alignment.Center,
    ) {
        WalletGlassCard(accent = WalletDanger) {
            Text(
                text = "收款页不可用",
                style = MaterialTheme.typography.headlineSmall,
                color = WalletTextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = WalletDanger,
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
