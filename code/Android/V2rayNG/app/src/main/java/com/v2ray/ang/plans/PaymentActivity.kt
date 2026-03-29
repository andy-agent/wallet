package com.v2ray.ang.plans

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
                R.id.radioSol -> PaymentConfig.AssetCode.SOL
                R.id.radioUsdt -> PaymentConfig.AssetCode.USDT_TRC20
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
                Toast.makeText(this@PaymentActivity, "创建订单失败: ${error.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun displayOrder(order: Order) {
        binding.layoutPaymentDetails.visibility = View.VISIBLE

        // 显示支付信息
        binding.textOrderNo.text = "订单号: ${order.orderNo}"
        binding.textAssetCode.text = "支付方式: ${order.payment.assetCode}"
        binding.textAmount.text = order.payment.amountCrypto
        binding.textReceiveAddress.text = order.payment.receiveAddress

        // 生成二维码
        val qrBitmap = generateQRCode(order.payment.qrText)
        binding.imageViewQR.setImageBitmap(qrBitmap)

        // 启动倒计时
        startCountdown(order)
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
        AlertDialog.Builder(this)
            .setTitle("支付成功")
            .setMessage("您的订阅已开通！")
            .setPositiveButton("确定") { _, _ ->
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
        // Log error for debugging
        android.util.Log.e("PaymentActivity", "Payment polling error: $error")
        
        // Show error to user
        runOnUiThread {
            Toast.makeText(this@PaymentActivity, "查询状态失败: $error", Toast.LENGTH_SHORT).show()
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
