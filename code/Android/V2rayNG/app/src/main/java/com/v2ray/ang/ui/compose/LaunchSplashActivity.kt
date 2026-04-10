package com.v2ray.ang.ui.compose

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.v2ray.ang.R
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.SplashUiState
import com.v2ray.ang.composeui.p0.repository.RealP0Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class LaunchSplashActivity : ComponentActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var progressHeadline: TextView
    private lateinit var progressDetail: TextView
    private lateinit var buildStatus: TextView
    private lateinit var versionLabel: TextView
    private lateinit var walletMetric: TextView
    private lateinit var vpnMetric: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_launch_splash)

        progressBar = findViewById(R.id.launch_progress)
        progressHeadline = findViewById(R.id.launch_progress_headline)
        progressDetail = findViewById(R.id.launch_progress_detail)
        buildStatus = findViewById(R.id.launch_build_status)
        versionLabel = findViewById(R.id.launch_version_label)
        walletMetric = findViewById(R.id.launch_wallet_metric)
        vpnMetric = findViewById(R.id.launch_vpn_metric)

        walletMetric.text = "4 链架构"
        vpnMetric.text = "62 节点"

        lifecycleScope.launch {
            runLaunchSequence()
        }
    }

    private suspend fun runLaunchSequence() {
        val repository = RealP0Repository(applicationContext)
        val minVisibleMs = 2200L
        val snapshotDeferred = lifecycleScope.async(Dispatchers.IO) { repository.getSplashState() }
        var snapshot = SplashUiState()

        val elapsed = measureTimeMillis {
            renderStage(
                progress = 0.12f,
                headline = "连接钱包与网络",
                detail = "初始化加密模块、节点探测与资产索引…",
                buildText = "正在建立安全启动环境",
                versionText = "--",
            )
            delay(260)

            renderStage(
                progress = 0.34f,
                headline = "装载本地安全环境",
                detail = "读取加密存储、配置项与会话凭据…",
                buildText = "正在装载本地凭据与偏好",
                versionText = "--",
            )
            delay(260)

            snapshot = snapshotDeferred.await()
            renderStage(
                progress = 0.58f,
                headline = "同步账户与缓存",
                detail = "解析钱包账户、订单索引与节点缓存…",
                buildText = snapshot.buildStatus,
                versionText = snapshot.versionLabel,
            )
            delay(280)

            renderStage(
                progress = 0.82f,
                headline = "校验安全状态",
                detail = snapshot.buildStatus.ifBlank { "准备主界面与安全通道…" },
                buildText = snapshot.buildStatus,
                versionText = snapshot.versionLabel,
            )
            delay(260)
        }

        if (elapsed < minVisibleMs) {
            delay(minVisibleMs - elapsed)
        }

        renderStage(
            progress = 1f,
            headline = "准备完成",
            detail = "安全通道与钱包环境已就绪，正在进入主界面…",
            buildText = snapshot.buildStatus,
            versionText = snapshot.versionLabel,
        )
        delay(280)

        startActivity(
            ComposeContainerActivity.createIntent(
                context = this,
                startRoute = CryptoVpnRouteSpec.emailLogin.pattern,
            ),
        )
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun renderStage(
        progress: Float,
        headline: String,
        detail: String,
        buildText: String,
        versionText: String,
    ) {
        progressHeadline.text = headline
        progressDetail.text = detail
        buildStatus.text = buildText
        versionLabel.text = versionText
        animateProgressTo((progress * 100).toInt())
    }

    private fun animateProgressTo(target: Int) {
        val animator = ValueAnimator.ofInt(progressBar.progress, target)
        animator.duration = 420L
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { progressBar.progress = it.animatedValue as Int }
        animator.start()
    }
}
