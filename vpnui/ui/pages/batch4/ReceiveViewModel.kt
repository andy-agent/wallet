package com.cryptovpn.ui.pages.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 收款页ViewModel
 */
@HiltViewModel
class ReceiveViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReceivePageState>(ReceivePageState.Loading)
    val uiState: StateFlow<ReceivePageState> = _uiState.asStateFlow()

    // 模拟钱包地址和网络
    private var walletAddress: String = ""
    private var network: String = ""

    init {
        loadWalletAddress()
    }

    /**
     * 加载钱包地址
     */
    private fun loadWalletAddress() {
        viewModelScope.launch {
            _uiState.value = ReceivePageState.Loading
            
            try {
                // 模拟网络请求延迟
                kotlinx.coroutines.delay(800)
                
                // 模拟获取钱包地址
                walletAddress = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t"
                network = "TRON (TRC20)"
                
                // 生成二维码
                val qrCodeBitmap = generateQRCodeBitmap(walletAddress, 240)
                
                _uiState.value = ReceivePageState.Ready(
                    walletAddress = walletAddress,
                    network = network,
                    qrCodeBitmap = qrCodeBitmap
                )
            } catch (e: Exception) {
                _uiState.value = ReceivePageState.Error(
                    message = "无法获取钱包地址: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    /**
     * 同步公钥地址
     */
    fun syncPublicAddress() {
        viewModelScope.launch {
            _uiState.value = ReceivePageState.SyncingAddress("正在同步地址...")
            
            try {
                kotlinx.coroutines.delay(1500)
                
                // 同步完成后重新加载
                loadWalletAddress()
            } catch (e: Exception) {
                _uiState.value = ReceivePageState.Error(
                    message = "地址同步失败: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    /**
     * 复制地址到剪贴板
     */
    fun copyAddressToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Wallet Address", walletAddress)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * 分享地址
     */
    fun shareAddress() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "我的 $network 钱包地址: $walletAddress")
        }
        
        val chooser = Intent.createChooser(shareIntent, "分享钱包地址")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    /**
     * 重试加载
     */
    fun retry() {
        loadWalletAddress()
    }

    /**
     * 生成二维码Bitmap
     */
    private fun generateQRCodeBitmap(content: String, size: Int): Bitmap? {
        return try {
            val writer = com.google.zxing.qrcode.QRCodeWriter()
            val bitMatrix = writer.encode(content, com.google.zxing.BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}
