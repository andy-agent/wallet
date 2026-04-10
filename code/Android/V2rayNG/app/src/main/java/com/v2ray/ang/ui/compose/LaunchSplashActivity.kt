package com.v2ray.ang.ui.compose

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
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
    private lateinit var progressDetail: TextView
    private lateinit var hubGlow: View
    private lateinit var hubRingOuter: View
    private lateinit var hubRingInner: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_launch_splash)

        progressBar = findViewById(R.id.launch_progress)
        progressDetail = findViewById(R.id.launch_progress_detail)
        hubGlow = findViewById(R.id.launch_hub_glow)
        hubRingOuter = findViewById(R.id.launch_hub_ring_outer)
        hubRingInner = findViewById(R.id.launch_hub_ring_inner)

        startHubAnimation()

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
                detail = "初始化加密模块、节点探测与资产索引…",
            )
            delay(260)

            renderStage(
                progress = 0.34f,
                detail = "读取加密存储、配置项与会话凭据…",
            )
            delay(260)

            snapshot = snapshotDeferred.await()
            renderStage(
                progress = 0.58f,
                detail = "解析钱包账户、订单索引与节点缓存…",
            )
            delay(280)

            renderStage(
                progress = 0.82f,
                detail = snapshot.buildStatus.ifBlank { "准备主界面与安全通道…" },
            )
            delay(260)
        }

        if (elapsed < minVisibleMs) {
            delay(minVisibleMs - elapsed)
        }

        renderStage(
            progress = 1f,
            detail = "安全通道与钱包环境已就绪，正在进入主界面…",
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
        detail: String,
    ) {
        progressDetail.text = detail
        animateProgressTo((progress * 100).toInt())
    }

    private fun animateProgressTo(target: Int) {
        val animator = ValueAnimator.ofInt(progressBar.progress, target)
        animator.duration = 420L
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { progressBar.progress = it.animatedValue as Int }
        animator.start()
    }

    private fun startHubAnimation() {
        ObjectAnimator.ofFloat(hubRingOuter, View.ROTATION, 0f, 360f).apply {
            duration = 8200L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(hubRingInner, View.ROTATION, 360f, 0f).apply {
            duration = 6200L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(hubGlow, View.ALPHA, 0.45f, 0.9f).apply {
            duration = 1400L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = DecelerateInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(hubGlow, View.SCALE_X, 0.96f, 1.08f).apply {
            duration = 1800L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(hubGlow, View.SCALE_Y, 0.96f, 1.08f).apply {
            duration = 1800L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            start()
        }
    }
}
