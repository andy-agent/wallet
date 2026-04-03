package com.cryptovpn.ui.pages.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 会话失效弹窗ViewModel
 * 用于管理弹窗的显示状态和原因
 */
@HiltViewModel
class SessionEvictedViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SessionEvictedState())
    val state: StateFlow<SessionEvictedState> = _state.asStateFlow()

    /**
     * 显示会话失效弹窗
     * @param reason 会话失效原因
     */
    fun show(reason: SessionEvictionReason = SessionEvictionReason.LOGIN_ON_OTHER_DEVICE) {
        _state.update {
            it.copy(
                isVisible = true,
                reason = reason
            )
        }
    }

    /**
     * 隐藏弹窗
     */
    fun dismiss() {
        _state.update {
            it.copy(isVisible = false)
        }
    }

    /**
     * 处理账号在其他设备登录的情况
     */
    fun onLoginOnOtherDevice() {
        show(SessionEvictionReason.LOGIN_ON_OTHER_DEVICE)
    }

    /**
     * 处理会话过期的情况
     */
    fun onSessionExpired() {
        show(SessionEvictionReason.SESSION_EXPIRED)
    }

    /**
     * 处理账号被禁用的情况
     */
    fun onAccountDisabled() {
        show(SessionEvictionReason.ACCOUNT_DISABLED)
    }

    /**
     * 处理安全异常的情况
     */
    fun onSecurityViolation() {
        show(SessionEvictionReason.SECURITY_VIOLATION)
    }

    /**
     * 处理系统维护的情况
     */
    fun onServerMaintenance() {
        show(SessionEvictionReason.SERVER_MAINTENANCE)
    }

    /**
     * 根据错误码显示对应的弹窗
     */
    fun showByErrorCode(errorCode: String) {
        val reason = when (errorCode) {
            "SESSION_EXPIRED" -> SessionEvictionReason.SESSION_EXPIRED
            "LOGIN_ON_OTHER_DEVICE" -> SessionEvictionReason.LOGIN_ON_OTHER_DEVICE
            "ACCOUNT_DISABLED" -> SessionEvictionReason.ACCOUNT_DISABLED
            "SECURITY_VIOLATION" -> SessionEvictionReason.SECURITY_VIOLATION
            "SERVER_MAINTENANCE" -> SessionEvictionReason.SERVER_MAINTENANCE
            else -> SessionEvictionReason.LOGIN_ON_OTHER_DEVICE
        }
        show(reason)
    }
}

/**
 * 全局会话管理器
 * 用于在应用级别监听会话状态变化
 */
@HiltViewModel
class GlobalSessionManager @Inject constructor() : ViewModel() {

    private val _sessionEvicted = MutableStateFlow<SessionEvictionReason?>(null)
    val sessionEvicted: StateFlow<SessionEvictionReason?> = _sessionEvicted.asStateFlow()

    /**
     * 通知会话失效
     */
    fun notifySessionEvicted(reason: SessionEvictionReason) {
        viewModelScope.launch {
            _sessionEvicted.emit(reason)
        }
    }

    /**
     * 清除会话失效状态
     */
    fun clearSessionEvicted() {
        viewModelScope.launch {
            _sessionEvicted.emit(null)
        }
    }

    /**
     * 检查响应是否需要显示会话失效弹窗
     */
    fun checkResponseForSessionEviction(errorCode: String?, errorMessage: String?): Boolean {
        val sessionErrorCodes = listOf(
            "SESSION_EXPIRED",
            "TOKEN_EXPIRED",
            "INVALID_TOKEN",
            "LOGIN_ON_OTHER_DEVICE",
            "ACCOUNT_DISABLED",
            "SECURITY_VIOLATION"
        )
        
        return if (errorCode in sessionErrorCodes) {
            val reason = when (errorCode) {
                "SESSION_EXPIRED", "TOKEN_EXPIRED" -> SessionEvictionReason.SESSION_EXPIRED
                "LOGIN_ON_OTHER_DEVICE" -> SessionEvictionReason.LOGIN_ON_OTHER_DEVICE
                "ACCOUNT_DISABLED" -> SessionEvictionReason.ACCOUNT_DISABLED
                "SECURITY_VIOLATION" -> SessionEvictionReason.SECURITY_VIOLATION
                else -> SessionEvictionReason.LOGIN_ON_OTHER_DEVICE
            }
            notifySessionEvicted(reason)
            true
        } else {
            false
        }
    }
}

/**
 * 会话状态观察者
 * 可在MainActivity或根Composable中使用
 */
@Composable
fun SessionStateObserver(
    sessionManager: GlobalSessionManager = hiltViewModel(),
    onSessionEvicted: (SessionEvictionReason) -> Unit
) {
    val sessionEvicted by sessionManager.sessionEvicted.collectAsState()
    
    LaunchedEffect(sessionEvicted) {
        sessionEvicted?.let { reason ->
            onSessionEvicted(reason)
            sessionManager.clearSessionEvicted()
        }
    }
}
