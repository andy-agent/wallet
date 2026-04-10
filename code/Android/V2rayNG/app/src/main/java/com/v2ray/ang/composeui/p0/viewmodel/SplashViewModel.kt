package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        _uiState.value = SplashUiState()

        val snapshotDeferred = viewModelScope.async { repository.getSplashState() }
        var snapshot = SplashUiState()
        val minVisibleMs = 2200L

        val elapsed = measureTimeMillis {
            updateStage(
                progress = 0.12f,
                headline = "连接钱包与网络",
                detail = "初始化加密模块、节点探测与资产索引…",
            )
            delay(260)

            updateStage(
                progress = 0.34f,
                headline = "装载本地安全环境",
                detail = "读取加密存储、配置项与会话凭据…",
            )
            delay(260)

            snapshot = snapshotDeferred.await()
            _uiState.value = snapshot.copy(
                progress = 0.58f,
                progressHeadline = "同步账户与缓存",
                progressDetail = "解析钱包账户、订单索引与节点缓存…",
                authResolved = false,
                readyToNavigate = false,
            )
            delay(280)

            _uiState.value = snapshot.copy(
                progress = 0.82f,
                progressHeadline = "校验安全状态",
                progressDetail = snapshot.buildStatus.ifBlank { "准备主界面与安全通道…" },
                authResolved = false,
                readyToNavigate = false,
            )
            delay(260)
        }

        if (elapsed < minVisibleMs) {
            delay(minVisibleMs - elapsed)
        }

        _uiState.value = snapshot.copy(
            progress = 1f,
            progressHeadline = "准备完成",
            progressDetail = "安全通道与钱包环境已就绪，正在进入主界面…",
            authResolved = true,
            readyToNavigate = true,
        )
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
            authResolved = false,
            readyToNavigate = false,
        )
    }
}
