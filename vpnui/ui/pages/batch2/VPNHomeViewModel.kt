package com.cryptovpn.ui.pages.vpn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * VPN首页 ViewModel
 * 管理VPN连接状态、订阅状态、模式切换等
 */
@HiltViewModel
class VPNHomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<VPNHomeState>(VPNHomeState.ActiveReady(
        planName = "专业版",
        remainingDays = 30
    ))
    val state: StateFlow<VPNHomeState> = _state.asStateFlow()

    // 连接计时任务
    private var connectionTimerJob: Job? = null
    
    // 连接进度任务
    private var connectionProgressJob: Job? = null

    // 连接时长（秒）
    private var connectionDurationSeconds = 0

    /**
     * 切换VPN连接状态
     */
    fun toggleConnection() {
        when (val currentState = _state.value) {
            is VPNHomeState.ActiveReady -> {
                startConnecting()
            }
            is VPNHomeState.Connected -> {
                disconnect()
            }
            is VPNHomeState.NoSubscription -> {
                // 导航到套餐页
            }
            is VPNHomeState.Suspended -> {
                // 恢复服务
            }
            is VPNHomeState.Expired -> {
                // 导航到续费页
            }
            is VPNHomeState.Connecting -> {
                // 连接中，不做处理
            }
        }
    }

    /**
     * 开始连接
     */
    private fun startConnecting() {
        _state.update { currentState ->
            VPNHomeState.Connecting(
                progress = 0f,
                mode = currentState.mode,
                selectedRegion = currentState.selectedRegion
            )
        }

        // 模拟连接进度
        connectionProgressJob?.cancel()
        connectionProgressJob = viewModelScope.launch {
            var progress = 0f
            while (progress < 1f) {
                delay(100)
                progress += 0.05f
                _state.update { currentState ->
                    if (currentState is VPNHomeState.Connecting) {
                        currentState.copy(progress = progress.coerceAtMost(1f))
                    } else {
                        currentState
                    }
                }
            }
            // 连接成功
            onConnected()
        }
    }

    /**
     * 连接成功
     */
    private fun onConnected() {
        val currentState = _state.value
        connectionDurationSeconds = 0
        
        _state.update {
            VPNHomeState.Connected(
                duration = formatDuration(0),
                uploadSpeed = "0 KB/s",
                downloadSpeed = "0 KB/s",
                mode = currentState.mode,
                selectedRegion = currentState.selectedRegion
            )
        }

        // 启动计时器
        startConnectionTimer()
    }

    /**
     * 启动连接计时器
     */
    private fun startConnectionTimer() {
        connectionTimerJob?.cancel()
        connectionTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                connectionDurationSeconds++
                
                // 模拟速度变化
                val uploadSpeed = "${(100..5000).random() / 1000.0} MB/s"
                val downloadSpeed = "${(1000..50000).random() / 1000.0} MB/s"
                
                _state.update { currentState ->
                    if (currentState is VPNHomeState.Connected) {
                        currentState.copy(
                            duration = formatDuration(connectionDurationSeconds),
                            uploadSpeed = uploadSpeed,
                            downloadSpeed = downloadSpeed
                        )
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    /**
     * 断开连接
     */
    private fun disconnect() {
        connectionTimerJob?.cancel()
        connectionTimerJob = null
        connectionDurationSeconds = 0
        
        val currentState = _state.value
        _state.update {
            VPNHomeState.ActiveReady(
                planName = "专业版",
                remainingDays = 30,
                mode = currentState.mode,
                selectedRegion = currentState.selectedRegion
            )
        }
    }

    /**
     * 切换VPN模式
     */
    fun switchMode(mode: VPNMode) {
        _state.update { currentState ->
            when (currentState) {
                is VPNHomeState.NoSubscription -> currentState.copy(mode = mode)
                is VPNHomeState.ActiveReady -> currentState.copy(mode = mode)
                is VPNHomeState.Connecting -> currentState.copy(mode = mode)
                is VPNHomeState.Connected -> currentState.copy(mode = mode)
                is VPNHomeState.Suspended -> currentState.copy(mode = mode)
                is VPNHomeState.Expired -> currentState.copy(mode = mode)
            }
        }
    }

    /**
     * 选择区域
     */
    fun selectRegion(region: RegionInfo) {
        _state.update { currentState ->
            when (currentState) {
                is VPNHomeState.NoSubscription -> currentState.copy(selectedRegion = region)
                is VPNHomeState.ActiveReady -> currentState.copy(selectedRegion = region)
                is VPNHomeState.Connecting -> currentState.copy(selectedRegion = region)
                is VPNHomeState.Connected -> currentState.copy(selectedRegion = region)
                is VPNHomeState.Suspended -> currentState.copy(selectedRegion = region)
                is VPNHomeState.Expired -> currentState.copy(selectedRegion = region)
            }
        }
    }

    /**
     * 设置无订阅状态
     */
    fun setNoSubscription() {
        connectionTimerJob?.cancel()
        _state.value = VPNHomeState.NoSubscription()
    }

    /**
     * 设置活跃就绪状态
     */
    fun setActiveReady(planName: String, remainingDays: Int) {
        _state.update { currentState ->
            VPNHomeState.ActiveReady(
                planName = planName,
                remainingDays = remainingDays,
                mode = currentState.mode,
                selectedRegion = currentState.selectedRegion
            )
        }
    }

    /**
     * 设置暂停状态
     */
    fun setSuspended(reason: String, planName: String, remainingDays: Int) {
        connectionTimerJob?.cancel()
        _state.update { currentState ->
            VPNHomeState.Suspended(
                reason = reason,
                planName = planName,
                remainingDays = remainingDays,
                mode = currentState.mode,
                selectedRegion = currentState.selectedRegion
            )
        }
    }

    /**
     * 设置过期状态
     */
    fun setExpired(expiredDate: String) {
        connectionTimerJob?.cancel()
        _state.update { currentState ->
            VPNHomeState.Expired(
                expiredDate = expiredDate,
                mode = currentState.mode,
                selectedRegion = currentState.selectedRegion
            )
        }
    }

    /**
     * 格式化时长
     */
    private fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    override fun onCleared() {
        super.onCleared()
        connectionTimerJob?.cancel()
        connectionProgressJob?.cancel()
    }
}
