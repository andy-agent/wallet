package com.v2ray.ang.plans

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.v2ray.ang.databinding.ActivityPaymentBinding
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.model.Order
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.payment.ui.OrderPollingUseCase
import com.v2ray.ang.payment.ui.activity.LoginActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * 支付页面
 */
class PaymentActivity : AppCompatActivity(), OrderPollingUseCase.PollingCallback {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var repository: PaymentRepository
    private lateinit var pollingUseCase: OrderPollingUseCase

    private var currentOrder: Order? = null
    private var countDownTimer: CountDownTimer? = null

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // 登录成功，重新尝试创建订单
            val planId = intent.getStringExtra("plan_id") ?: return@registerForActivityResult
            val assetCode = when (binding.radioGroupPaymentMethod.checkedRadioButtonId) {
                com.v2ray.ang.R.id.radioSol -> PaymentConfig.AssetCode.SOL
                com.v2ray.ang.R.id.radioUsdt -> PaymentConfig.AssetCode.USDT_TRC20
                else -> PaymentConfig.AssetCode.SOL
            }
            createOrder(planId, assetCode)
        } else {
            // 登录取消或失败，返回上一页
            Toast.makeText(this, "登录已取消", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "支付订单"

        repository = PaymentRepository(this)
        pollingUseCase = OrderPollingUseCase(repository, this)

        // 获取传入参数
        val planId = intent.getStringExtra("plan_id") ?: return
        val supportsSol = intent.getBooleanExtra("supports_sol", true)
        val supportsUsdt = intent.getBooleanExtra("supports_usdt", true)

        setupPaymentMethodSelection(supportsSol, supportsUsdt)

        // 默认选择 SOL 创建订单
        createOrder(planId, PaymentConfig.AssetCode.SOL)

        // 复制地址按钮
        binding.buttonCopyAddress.setOnClickListener {
            copyToClipboard("收款地址", binding.textReceiveAddress.text.toString())
        }

        // 复制金额按钮
        binding.buttonCopyAmount.setOnClickListener {
            copyToClipboard("支付金额", binding.textAmount.text.toString())
        }

        // 刷新状态按钮
        binding.buttonRefresh.setOnClickListener {
            currentOrder?.let { order ->
                pollingUseCase.pollImmediately(order.orderId)
            }
        }
    }

    private fun setupPaymentMethodSelection(supportsSol: Boolean, supportsUsdt: Boolean) {
        // 支付方式选择
        binding.radioGroupPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
            val assetCode = when (checkedId) {
                com.v2ray.ang.R.id.radioSol -> PaymentConfig.AssetCode.SOL
                com.v2ray.ang.R.id.radioUsdt -> PaymentConfig.AssetCode.USDT_TRC20
                else -> PaymentConfig.AssetCode.SOL
            }
            currentOrder?.let { order ->
                createOrder(order.plan.id, assetCode)
            }
        }

        // 显示/隐藏支付方式选项
        binding.radioSol.visibility = if (supportsSol) View.VISIBLE else View.GONE
        binding.radioUsdt.visibility = if (supportsUsdt) View.VISIBLE else View.GONE
    }

    private fun createOrder(planId: String, assetCode: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.layoutPaymentDetails.visibility = View.GONE

            val result = repository.createOrder(planId, assetCode)

            binding.progressBar.visibility = View.GONE

            result.onSuccess { order ->
                currentOrder = order
                displayOrder(order)
                startPolling(order)
            }.onFailure { error ->
                val errorMessage = error.message ?: "创建订单失败"
                
                // 检查是否是 401 未授权错误
                if (errorMessage.contains("401") || 
                    errorMessage.contains("Unauthorized") ||
                    errorMessage.contains("未登录") ||
                    errorMessage.contains("未授权")) {
                    
                    // 清除过期的 Token
                    repository.clearAuth()
                    
                    // 显示对话框引导登录
                    AlertDialog.Builder(this@PaymentActivity)
                        .setTitle("需要登录")
                        .setMessage("您的登录已过期，请重新登录后继续购买。")
                        .setPositiveButton("去登录") { _, _ ->
                            val intent = Intent(this@PaymentActivity, LoginActivity::class.java)
                            loginLauncher.launch(intent)
                        }
                        .setNegativeButton("取消") { _, _ ->
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                } else {
                    // 其他错误，显示 Toast
                    Toast.makeText(this@PaymentActivity, "创建订单失败: $errorMessage", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun displayOrder(order: Order) {
        binding.layoutPaymentDetails.visibility = View.VISIBLE

        // 显示支付信息
        binding.textOrderNo.text = "订单号: ${order.orderNo}"
        binding.textAssetCode.text = "支付方式: ${getPaymentMethodDisplay(order.payment.assetCode)}"
        
        // 格式化金额显示，添加等值信息
        val amountDisplay = formatAmountDisplay(order.payment.assetCode, order.payment.amountCrypto)
        binding.textAmount.text = amountDisplay
        
        binding.textReceiveAddress.text = order.payment.receiveAddress

        // 生成二维码
        val qrBitmap = generateQRCode(order.payment.qrText)
        binding.imageViewQR.setImageBitmap(qrBitmap)

        // 启动倒计时
        startCountdown(order)
    }
    
    private fun getPaymentMethodDisplay(assetCode: String): String {
        return when (assetCode) {
            "SOL" -> "SOL (Solana)"
            "USDT" -> "USDT (默认 Solana)"
            else -> assetCode
        }
    }
    
    private fun formatAmountDisplay(assetCode: String, amountCrypto: String): String {
        return when (assetCode) {
            "SOL" -> "$amountCrypto SOL"
            "USDT" -> "$amountCrypto USDT"
            else -> amountCrypto
        }
    }

    private fun startCountdown(order: Order) {
        countDownTimer?.cancel()

        val expireTime = parseIsoDate(order.expiresAt)
        val now = System.currentTimeMillis()
        val remaining = expireTime - now

        if (remaining > 0) {
            countDownTimer = object : CountDownTimer(remaining, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val minutes = millisUntilFinished / 1000 / 60
                    val seconds = (millisUntilFinished / 1000) % 60
                    binding.textCountdown.text = String.format("剩余时间: %02d:%02d", minutes, seconds)
                }

                override fun onFinish() {
                    binding.textCountdown.text = "订单已过期"
                    pollingUseCase.stopPolling()
                }
            }.start()
        } else {
            binding.textCountdown.text = "订单已过期"
        }
    }

    private fun startPolling(order: Order) {
        pollingUseCase.startPolling(order.orderId)
    }

    private fun generateQRCode(content: String): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$label 已复制", Toast.LENGTH_SHORT).show()
    }

    private fun parseIsoDate(dateStr: String): Long {
        return try {
            // Parse ISO 8601 date format (e.g., "2024-01-15T10:30:00Z" or "2024-01-15T10:30:00.000Z")
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = isoFormat.parse(dateStr)
            date?.time ?: (System.currentTimeMillis() + 15 * 60 * 1000)
        } catch (e: Exception) {
            try {
                // Fallback format without milliseconds
                val isoFormatNoMs = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = isoFormatNoMs.parse(dateStr)
                date?.time ?: (System.currentTimeMillis() + 15 * 60 * 1000)
            } catch (e2: Exception) {
                // Fallback to default 15 minutes on parse failure
                System.currentTimeMillis() + 15 * 60 * 1000
            }
        }
    }

    // PollingCallback 实现
    override fun onStatusUpdate(order: Order) {
        binding.textStatus.text = "状态: ${order.statusText}"
    }

    override fun onPaymentSuccess(order: Order) {
        binding.textStatus.text = "支付成功"
        AlertDialog.Builder(this@PaymentActivity)
            .setTitle("支付成功")
            .setMessage("您的订阅已开通。\n\n当前版本流程已切换为选择区域后签发 VPN 配置，不再自动导入旧式订阅链接。\n\n请返回首页继续。")
            .setPositiveButton("返回首页") { _, _ ->
                setResult(RESULT_OK)
                finish()
            }
            .setNegativeButton("稍后再说") { _, _ ->
                setResult(RESULT_OK)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onPaymentFailed(error: String) {
        binding.textStatus.text = "支付失败: $error"
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    override fun onExpired() {
        binding.textStatus.text = "订单已过期"
        binding.textCountdown.text = "订单已过期"
    }

    override fun onError(error: String) {
        android.util.Log.e("PaymentActivity", "Payment polling error: $error")
        
        // 检查是否是 401 错误
        if (error.contains("401") || error.contains("Unauthorized") || error.contains("未登录")) {
            lifecycleScope.launch {
                pollingUseCase.stopPolling()
                repository.clearAuth()
                
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@PaymentActivity)
                        .setTitle("需要登录")
                        .setMessage("您的登录已过期，请重新登录后查看订单状态。")
                        .setPositiveButton("去登录") { _, _ ->
                            val intent = Intent(this@PaymentActivity, LoginActivity::class.java)
                            loginLauncher.launch(intent)
                        }
                        .setNegativeButton("取消") { _, _ ->
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        } else {
            // 其他错误，继续轮询
            runOnUiThread {
                Toast.makeText(this@PaymentActivity, "查询状态失败: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pollingUseCase.stopPolling()
        countDownTimer?.cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
