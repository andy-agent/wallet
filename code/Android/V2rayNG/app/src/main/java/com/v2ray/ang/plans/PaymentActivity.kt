package com.v2ray.ang.plans

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.net.VpnService
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.handler.AngConfigManager
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.handler.SettingsManager
import com.v2ray.ang.handler.V2RayServiceManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.v2ray.ang.databinding.ActivityPaymentBinding
import com.v2ray.ang.fmt.VlessFmt
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.api.VpnConfigIssueData
import com.v2ray.ang.payment.data.model.Order
import com.v2ray.ang.payment.data.repository.PaymentRepository
import com.v2ray.ang.payment.ui.OrderPollingUseCase
import com.v2ray.ang.payment.ui.activity.LoginActivity
import com.v2ray.ang.util.Utils
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
    private var currentPayableAmountRaw: String = ""

    private data class QuoteSelection(
        val assetCode: String,
        val networkCode: String
    )

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // 登录成功，重新尝试创建订单
            val planId = intent.getStringExtra("plan_id") ?: return@registerForActivityResult
            val quote = getSelectedQuote()
            createOrder(planId, quote.assetCode, quote.networkCode)
        } else {
            // 登录取消或失败，返回上一页
            Toast.makeText(this, "登录已取消", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private val requestVpnPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            V2RayServiceManager.startVService(this)
        } else {
            Toast.makeText(this, "VPN 权限未授予，配置已导入，可稍后手动连接。", Toast.LENGTH_LONG).show()
            setResult(RESULT_OK)
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

        val initialQuote = getSelectedQuote()
        createOrder(planId, initialQuote.assetCode, initialQuote.networkCode)

        // 复制地址按钮
        binding.buttonCopyAddress.setOnClickListener {
            copyToClipboard("收款地址", binding.textReceiveAddress.text.toString())
        }

        // 复制金额按钮
        binding.buttonCopyAmount.setOnClickListener {
            val amount = currentPayableAmountRaw.ifBlank { currentOrder?.payment?.amountCrypto.orEmpty() }
            copyToClipboard(getString(com.v2ray.ang.R.string.payment_amount_copy_label), amount)
        }

        // 刷新状态按钮
        binding.buttonRefresh.setOnClickListener {
            currentOrder?.let { order ->
                pollingUseCase.pollImmediately(order.orderNo)
            }
        }

        binding.buttonSubmitTxFallback.setOnClickListener {
            currentOrder?.let { order ->
                promptForTransactionHash(order)
            }
        }
    }

    private fun setupPaymentMethodSelection(supportsSol: Boolean, supportsUsdt: Boolean) {
        if (!supportsSol && supportsUsdt) {
            binding.radioUsdtSolana.isChecked = true
        }

        // 支付方式选择
        binding.radioGroupPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
            val quote = getQuoteForButton(checkedId)
            currentOrder?.let { order ->
                createOrder(order.plan.id, quote.assetCode, quote.networkCode)
            }
        }

        // 显示/隐藏支付方式选项
        binding.radioSol.visibility = if (supportsSol) View.VISIBLE else View.GONE
        binding.radioUsdtSolana.visibility = if (supportsUsdt) View.VISIBLE else View.GONE
        binding.radioUsdt.visibility = if (supportsUsdt) View.VISIBLE else View.GONE
    }

    private fun getSelectedQuote(): QuoteSelection {
        return getQuoteForButton(binding.radioGroupPaymentMethod.checkedRadioButtonId)
    }

    private fun getQuoteForButton(checkedId: Int): QuoteSelection {
        return when (checkedId) {
            com.v2ray.ang.R.id.radioUsdtSolana -> QuoteSelection(
                assetCode = PaymentConfig.AssetCode.USDT,
                networkCode = PaymentConfig.NetworkCode.SOLANA
            )
            com.v2ray.ang.R.id.radioUsdt -> QuoteSelection(
                assetCode = PaymentConfig.AssetCode.USDT,
                networkCode = PaymentConfig.NetworkCode.TRON
            )
            else -> QuoteSelection(
                assetCode = PaymentConfig.AssetCode.SOL,
                networkCode = PaymentConfig.NetworkCode.SOLANA
            )
        }
    }

    private fun createOrder(planId: String, assetCode: String, networkCode: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.layoutPaymentDetails.visibility = View.GONE

            val result = repository.createOrder(planId, assetCode, networkCode)

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
        binding.textOrderNo.text = order.orderNo
        binding.textAssetCode.text = getString(
            com.v2ray.ang.R.string.payment_method_value,
            getPaymentMethodDisplay(order.payment.assetCode, order.paymentTarget?.networkCode),
        )
        
        currentPayableAmountRaw = order.payment.amountCrypto
        binding.textAmount.text = getString(
            com.v2ray.ang.R.string.payment_amount_value,
            order.payment.amountCrypto,
            order.payment.assetCode,
        )
        binding.textBaseAmount.text = getString(
            com.v2ray.ang.R.string.payment_base_amount_value,
            order.paymentTarget?.baseAmount ?: order.baseAmount ?: order.quoteUsdAmount,
            order.payment.assetCode,
        )
        binding.textUniqueDelta.text = getString(
            com.v2ray.ang.R.string.payment_unique_delta_value,
            order.paymentTarget?.uniqueAmountDelta ?: order.uniqueAmountDelta ?: "0",
            order.payment.assetCode,
        )
        
        binding.textReceiveAddress.text = order.payment.receiveAddress

        // 生成二维码
        val qrBitmap = generateQRCode(order.payment.qrText)
        binding.imageViewQR.setImageBitmap(qrBitmap)

        // 启动倒计时
        startCountdown(order)
    }
    
    private fun getPaymentMethodDisplay(assetCode: String, networkCode: String?): String {
        return when (assetCode) {
            "SOL" -> "SOL (Solana)"
            "USDT" -> if (networkCode == PaymentConfig.NetworkCode.TRON) "USDT (TRC20)" else "USDT (Solana)"
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
        pollingUseCase.startPolling(order.orderNo)
    }

    private fun promptForTransactionHash(order: Order) {
        val input = EditText(this).apply {
            hint = getString(com.v2ray.ang.R.string.payment_txhash_input_hint)
            setText(order.submittedClientTxHash.orEmpty())
        }

        AlertDialog.Builder(this)
            .setTitle(com.v2ray.ang.R.string.payment_txhash_fallback_title)
            .setMessage(com.v2ray.ang.R.string.payment_txhash_fallback_message)
            .setView(input)
            .setPositiveButton(com.v2ray.ang.R.string.payment_submit_fallback) { _, _ ->
                val txHash = input.text?.toString()?.trim().orEmpty()
                if (txHash.isBlank()) {
                    Toast.makeText(this, com.v2ray.ang.R.string.payment_txhash_empty, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                submitClientTransaction(order, txHash)
            }
            .setNegativeButton(com.v2ray.ang.R.string.cancel, null)
            .show()
    }

    private fun submitClientTransaction(order: Order, txHash: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            val submitResult = repository.submitClientTx(
                orderNo = order.orderNo,
                txHash = txHash,
                networkCode = order.quoteNetworkCode,
            )
            binding.progressBar.visibility = View.GONE

            submitResult.onSuccess {
                currentOrder = order.copy(submittedClientTxHash = txHash)
                binding.textStatus.text = getString(com.v2ray.ang.R.string.payment_status_fallback_submitted)
                pollingUseCase.pollImmediately(order.orderNo)
            }.onFailure { error ->
                Toast.makeText(
                    this@PaymentActivity,
                    getString(com.v2ray.ang.R.string.payment_submit_txhash_failed, error.message),
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
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
        val txSuffix = order.matchedOnchainTxHash?.takeLast(6)?.let {
            getString(com.v2ray.ang.R.string.payment_status_auto_matched_suffix, it)
        }.orEmpty()
        binding.textStatus.text = getString(com.v2ray.ang.R.string.payment_status_value, "${order.statusText}$txSuffix")
    }

    override fun onPaymentSuccess(order: Order) {
        binding.textStatus.text = "支付成功"
        lifecycleScope.launch {
            provisionVpnAndConnect(order)
        }
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

    private suspend fun provisionVpnAndConnect(order: Order) {
        binding.progressBar.visibility = View.VISIBLE
        val subscriptionResult = repository.getSubscription()
        val subscription = subscriptionResult.getOrNull()
        val subscriptionUrl = repository.getSavedSubscriptionUrl()?.takeIf { it.isNotBlank() }
            ?: subscription?.subscriptionUrl?.takeIf { it.isNotBlank() }

        if (!subscriptionUrl.isNullOrBlank()) {
            val imported = repository.importSubscriptionUrl(
                subscriptionUrl = subscriptionUrl,
                remarks = "Purchase ${order.planCode}",
            )
            binding.progressBar.visibility = View.GONE
            if (imported) {
                startVpnConnection()
                return
            }
        }
        binding.progressBar.visibility = View.GONE
        AlertDialog.Builder(this@PaymentActivity)
            .setTitle("支付成功")
            .setMessage("订单已完成，但订阅尚未同步到本地节点。请返回首页后稍后重试。")
            .setPositiveButton("返回首页") { _, _ ->
                setResult(RESULT_OK)
                finish()
            }
            .show()
    }

    private fun importIssuedVpnConfig(config: VpnConfigIssueData): Boolean {
        val profile = VlessFmt.parse(config.configPayload) ?: return false
        val subscriptionId = Utils.getUuid()
        MmkvManager.encodeSubscription(
            subscriptionId,
            SubscriptionItem(
                remarks = "Purchase ${config.regionCode}",
                url = config.configPayload,
                enabled = true,
                lastUpdated = System.currentTimeMillis(),
                autoUpdate = false,
            ),
        )
        profile.subscriptionId = subscriptionId
        profile.remarks = "Purchase ${config.regionCode}"
        profile.description = AngConfigManager.generateDescription(profile)
        val guid = MmkvManager.encodeServerConfig("", profile)
        MmkvManager.setSelectServer(guid)
        return true
    }

    private fun startVpnConnection() {
        if (MmkvManager.getSelectServer().isNullOrEmpty()) {
            Toast.makeText(this, "没有可连接的 VPN 配置", Toast.LENGTH_LONG).show()
            setResult(RESULT_OK)
            finish()
            return
        }

        if (SettingsManager.isVpnMode()) {
            val intent = VpnService.prepare(this)
            if (intent == null) {
                V2RayServiceManager.startVService(this)
                setResult(RESULT_OK)
                finish()
            } else {
                requestVpnPermission.launch(intent)
            }
        } else {
            V2RayServiceManager.startVService(this)
            setResult(RESULT_OK)
            finish()
        }
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
