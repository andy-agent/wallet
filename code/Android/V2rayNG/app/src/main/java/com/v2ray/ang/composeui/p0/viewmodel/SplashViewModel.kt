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
        val minVisibleMs = 2200L
        runCatching {
            val snapshotDeferred = viewModelScope.async { repository.getSplashState() }
            var snapshot = SplashUiState()

            val elapsed = measureTimeMillis {
                updateStage(
                    progress = 0.12f,
                    headline = "校验本地会话与缓存",
                    detail = "读取账号、订单缓存和本地节点索引…",
                )
                delay(260)

                updateStage(
                    progress = 0.34f,
                    headline = "装载本地运行环境",
                    detail = "同步登录态、配置项与路由入口…",
                )
                delay(260)

                snapshot = snapshotDeferred.await()
                _uiState.value = snapshot.copy(
                    progress = 0.58f,
                    progressHeadline = "解析缓存状态",
                    progressDetail = snapshot.progressDetail.ifBlank { "检查账号、订单和本地节点缓存…" },
                    authResolved = false,
                    readyToNavigate = false,
                    errorMessage = null,
                )
                delay(280)

                _uiState.value = snapshot.copy(
                    progress = 0.82f,
                    progressHeadline = "确认首页入口",
                    progressDetail = snapshot.buildStatus.ifBlank { "准备进入首页…" },
                    authResolved = false,
                    readyToNavigate = false,
                    errorMessage = null,
                )
                delay(260)
            }

            if (elapsed < minVisibleMs) {
                delay(minVisibleMs - elapsed)
            }

            _uiState.value = snapshot.copy(
                progress = 1f,
                progressHeadline = "准备完成",
                progressDetail = "启动检查完成，正在进入主界面…",
                authResolved = true,
                readyToNavigate = true,
                errorMessage = null,
            )
        }.onFailure { throwable ->
            _uiState.value = SplashUiState(
                checkingSecureBoot = false,
                progress = 0f,
                progressHeadline = "启动检查失败",
                progressDetail = "本地状态读取失败，请重试启动检查。",
                readyToNavigate = false,
                authResolved = false,
                isLoading = false,
                errorMessage = throwable.message ?: "启动检查失败",
            )
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
            authResolved = false,
            readyToNavigate = false,
        )
    }
}
