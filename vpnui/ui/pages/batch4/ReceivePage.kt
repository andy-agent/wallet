package com.cryptovpn.ui.pages.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.CryptoVPNTheme
import com.cryptovpn.ui.components.CommonTopAppBar
import com.cryptovpn.ui.components.LoadingIndicator
import com.cryptovpn.ui.components.ErrorRetryView
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.common.BitMatrix

/**
 * 收款页状态
 */
sealed class ReceivePageState {
    data object Loading : ReceivePageState()
    data class Ready(
        val walletAddress: String,
        val network: String,
        val qrCodeBitmap: Bitmap? = null
    ) : ReceivePageState()
    data class SyncingAddress(
        val message: String = "同步地址中..."
    ) : ReceivePageState()
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : ReceivePageState()
}

/**
 * 收款页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceivePage(
    onNavigateBack: () -> Unit = {},
    viewModel: ReceiveViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showCopiedSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "收款",
                onNavigateBack = onNavigateBack
            )
        },
        snackbarHost = {
            if (showCopiedSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = Color(0xFF22C55E),
                    contentColor = Color.White
                ) {
                    Text("地址已复制到剪贴板")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1020))
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is ReceivePageState.Loading -> {
                    LoadingIndicator()
                }
                is ReceivePageState.SyncingAddress -> {
                    SyncingAddressView(message = currentState.message)
                }
                is ReceivePageState.Ready -> {
                    ReceiveContent(
                        walletAddress = currentState.walletAddress,
                        network = currentState.network,
                        qrCodeBitmap = currentState.qrCodeBitmap,
                        onCopyAddress = {
                            viewModel.copyAddressToClipboard()
                            showCopiedSnackbar = true
                        },
                        onShareAddress = {
                            viewModel.shareAddress()
                        }
                    )
                }
                is ReceivePageState.Error -> {
                    ErrorRetryView(
                        message = currentState.message,
                        canRetry = currentState.canRetry,
                        onRetry = { viewModel.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncingAddressView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF1D4ED8),
            strokeWidth = 3.dp,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun ReceiveContent(
    walletAddress: String,
    network: String,
    qrCodeBitmap: Bitmap?,
    onCopyAddress: () -> Unit,
    onShareAddress: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 说明文字
        Text(
            text = "扫描二维码向我转账",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 二维码区域
        QRCodeCard(
            qrCodeBitmap = qrCodeBitmap,
            walletAddress = walletAddress
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 地址卡片
        AddressCard(
            walletAddress = walletAddress,
            network = network,
            onCopyAddress = onCopyAddress
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 操作按钮
        ActionButtons(
            onCopyAddress = onCopyAddress,
            onShareAddress = onShareAddress
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 网络提示
        NetworkWarning(network = network)
    }
}

@Composable
private fun QRCodeCard(
    qrCodeBitmap: Bitmap?,
    walletAddress: String
) {
    val bitmap = qrCodeBitmap ?: remember(walletAddress) {
        generateQRCode(walletAddress, 240)
    }
    
    Box(
        modifier = Modifier
            .size(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "收款二维码",
                modifier = Modifier.size(240.dp)
            )
        } ?: run {
            CircularProgressIndicator(
                color = Color(0xFF1D4ED8),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun AddressCard(
    walletAddress: String,
    network: String,
    onCopyAddress: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1F2937))
            .padding(16.dp)
    ) {
        Text(
            text = "我的 $network 地址",
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = walletAddress,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            
            IconButton(
                onClick = onCopyAddress,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "复制地址",
                    tint = Color(0xFF1D4ED8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onCopyAddress: () -> Unit,
    onShareAddress: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 复制按钮
        Button(
            onClick = onCopyAddress,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "复制地址",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        // 分享按钮
        OutlinedButton(
            onClick = onShareAddress,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color(0xFF374151)
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "分享地址",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NetworkWarning(network: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFEF3C7).copy(alpha = 0.1f))
            .border(
                width = 1.dp,
                color = Color(0xFFF59E0B).copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = "重要提示",
                color = Color(0xFFF59E0B),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "请确保发送方使用 $network 网络进行转账，否则资产可能永久丢失。",
                color = Color(0xFFF59E0B).copy(alpha = 0.8f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * 生成二维码Bitmap
 */
private fun generateQRCode(content: String, size: Int): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun ReceivePagePreview() {
    CryptoVPNTheme {
        ReceiveContent(
            walletAddress = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb",
            network = "TRON",
            qrCodeBitmap = null,
            onCopyAddress = {},
            onShareAddress = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun ReceivePageLoadingPreview() {
    CryptoVPNTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1020))
        ) {
            LoadingIndicator()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun ReceivePageErrorPreview() {
    CryptoVPNTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1020))
        ) {
            ErrorRetryView(
                message = "无法获取钱包地址，请检查网络连接",
                canRetry = true,
                onRetry = {}
            )
        }
    }
}
