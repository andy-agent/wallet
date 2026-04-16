package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.composeui.p0.model.P0LoadState
import com.v2ray.ang.composeui.p0.model.SplashEvent
import com.v2ray.ang.composeui.p0.model.SplashUiState
import com.v2ray.ang.composeui.p0.repository.P0Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class SplashViewModel(
    private val repository: P0Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()
    private var bootJob: Job? = null

    init {
        onEvent(SplashEvent.Refresh)
    }

    fun onEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.Refresh -> {
                bootJob?.cancel()
                bootJob = viewModelScope.launch {
                    runBootSequence()
                }
            }

            SplashEvent.Continue -> Unit
        }
    }

    private suspend fun runBootSequence() {
        _uiState.value = initialSplashState()
        val snapshotDeferred = viewModelScope.async { repository.getSplashState() }
        val minVisibleMs = 700L

        val elapsed = measureTimeMillis {
            updateStage(
                progress = 0.12f,
                headline = "检查登录状态",
                detail = "正在确认账号会话和本地缓存是否可用。",
            )
            delay(140)

            updateStage(
                progress = 0.34f,
                headline = "读取本地数据",
                detail = "正在加载订单、钱包、节点和配置状态。",
            )
            delay(140)

            val snapshot = snapshotDeferred.await()
            _uiState.value = snapshot.copy(
                progress = when (snapshot.loadState) {
                    P0LoadState.READY -> 1f
                    P0LoadState.EMPTY -> 0.82f
                    P0LoadState.ERROR, P0LoadState.UNAVAILABLE -> 0.58f
                    P0LoadState.LOADING -> 0.42f
                },
                progressHeadline = snapshot.progressHeadline.ifBlank {
                    when (snapshot.loadState) {
                        P0LoadState.READY -> "准备完成"
                        P0LoadState.EMPTY -> "需要处理空态"
                        P0LoadState.ERROR -> "启动校验失败"
                        P0LoadState.UNAVAILABLE -> "服务暂不可用"
                        P0LoadState.LOADING -> "启动中"
                    }
                },
                progressDetail = snapshot.progressDetail.ifBlank {
                    snapshot.errorMessage ?: snapshot.unavailableMessage ?: "启动检查中"
                },
            )
        }

        if (elapsed < minVisibleMs) {
            delay(minVisibleMs - elapsed)
        }
    }

    private fun updateStage(
        progress: Float,
        headline: String,
        detail: String,
    ) {
        _uiState.value = _uiState.value.copy(
            progress = progress,
            progressHeadline = headline,
            progressDetail = detail,
            buildStatus = "启动中",
            authResolved = false,
            readyToNavigate = false,
            loadState = P0LoadState.LOADING,
            errorMessage = null,
            unavailableMessage = null,
        )
    }

    private fun initialSplashState(): SplashUiState {
        return SplashUiState(
            versionLabel = "v${BuildConfig.VERSION_NAME}",
            buildStatus = "正在检查登录状态",
            progressHeadline = "系统正在准备",
            progressDetail = "正在读取账号、订单、节点和钱包状态。",
        )
    }
}
