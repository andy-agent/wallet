package com.v2ray.ang.ui.compose

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
    private lateinit var hubGlow: View
    private lateinit var hubRingOuter: View
    private lateinit var hubRingInner: View
    private lateinit var row1Copy: TextView
    private lateinit var row1Value: TextView
    private lateinit var row2Copy: TextView
    private lateinit var row2Value: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )
        hideSystemBars()
        setContentView(R.layout.activity_launch_splash)

        progressBar = findViewById(R.id.launch_progress)
        hubGlow = findViewById(R.id.launch_hub_glow)
        hubRingOuter = findViewById(R.id.launch_hub_ring_outer)
        hubRingInner = findViewById(R.id.launch_hub_ring_inner)
        row1Copy = findViewById(R.id.launch_row_1_copy)
        row1Value = findViewById(R.id.launch_row_1_value)
        row2Copy = findViewById(R.id.launch_row_2_copy)
        row2Value = findViewById(R.id.launch_row_2_value)

        startHubAnimation()

        lifecycleScope.launch {
            runLaunchSequence()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    private suspend fun runLaunchSequence() {
        val repository = RealP0Repository(applicationContext)
        val minVisibleMs = 2200L
        val snapshotDeferred = lifecycleScope.async(Dispatchers.IO) { repository.getSplashState() }
        var snapshot = SplashUiState()

        val elapsed = measureTimeMillis {
            renderStage(
                progress = 0.12f,
                row1CopyText = "本地密钥环境与生物识别策略正在装载。",
                row1ValueText = "检查中",
                row2CopyText = "节点健康探测和智能路由优先级待启动。",
                row2ValueText = "待启动",
            )
            delay(260)

            renderStage(
                progress = 0.34f,
                row1CopyText = "本地密钥环境与生物识别策略已经装载。",
                row1ValueText = "已就绪",
                row2CopyText = "节点健康探测和智能路由优先级正在同步。",
                row2ValueText = "同步中",
            )
            delay(260)

            snapshot = snapshotDeferred.await()
            renderStage(
                progress = 0.58f,
                row1CopyText = "本地密钥环境与生物识别策略已经装载。",
                row1ValueText = "已就绪",
                row2CopyText = "解析钱包账户、订单索引与节点缓存…",
                row2ValueText = "同步中",
            )
            delay(280)

            renderStage(
                progress = 0.82f,
                row1CopyText = "本地密钥环境与生物识别策略已经装载。",
                row1ValueText = "已就绪",
                row2CopyText = snapshot.buildStatus.ifBlank { "准备主界面与安全通道…" },
                row2ValueText = "校验中",
            )
            delay(260)
        }

        if (elapsed < minVisibleMs) {
            delay(minVisibleMs - elapsed)
        }

        renderStage(
            progress = 1f,
            row1CopyText = "本地密钥环境与生物识别策略已经装载。",
            row1ValueText = "已就绪",
            row2CopyText = "安全通道与钱包环境已就绪，正在进入主界面…",
            row2ValueText = "已完成",
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
        row1CopyText: String,
        row1ValueText: String,
        row2CopyText: String,
        row2ValueText: String,
    ) {
        row1Copy.text = row1CopyText
        row1Value.text = row1ValueText
        row2Copy.text = row2CopyText
        row2Value.text = row2ValueText
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

    private fun hideSystemBars() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
