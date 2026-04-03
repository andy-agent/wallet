package com.cryptovpn.ui.pages.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * 用户信息数据类
 */
data class UserInfo(
    val email: String,
    val userId: String,
    val avatarText: String = email.take(1).uppercase()
)

/**
 * 订阅信息数据类
 */
data class SubscriptionInfo(
    val planName: String,
    val expireDate: Date
)

/**
 * 我的页面状态
 */
sealed class ProfileState {
    object Loading : ProfileState()
    
    data class Loaded(
        val userInfo: UserInfo,
        val subscription: SubscriptionInfo?,
        val appVersion: String
    ) : ProfileState()
    
    object SessionEvicted : ProfileState()
    
    data class Error(val message: String) : ProfileState()
}

/**
 * 我的页面ViewModel
 */
@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            
            try {
                delay(800) // 模拟网络请求
                
                // 模拟数据
                val userInfo = UserInfo(
                    email = "user@example.com",
                    userId = "USER123456"
                )
                
                val subscription = SubscriptionInfo(
                    planName = "年度套餐",
                    expireDate = Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000)
                )
                
                _state.value = ProfileState.Loaded(
                    userInfo = userInfo,
                    subscription = subscription,
                    appVersion = "1.2.3"
                )
            } catch (e: Exception) {
                _state.value = ProfileState.Error("加载失败，请稍后重试")
            }
        }
    }

    fun onEditProfileClick() {
        // 处理编辑个人资料
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            // 模拟登出操作
            delay(500)
            // 清空用户数据，跳转到登录页
            _state.value = ProfileState.SessionEvicted
        }
    }

    /**
     * 模拟会话被踢出（在其他设备登录）
     */
    fun simulateSessionEvicted() {
        _state.value = ProfileState.SessionEvicted
    }

    /**
     * 检查会话状态
     */
    fun checkSessionStatus() {
        viewModelScope.launch {
            // 模拟检查会话状态
            val isSessionValid = (0..10).random() > 1 // 90%概率有效
            
            if (!isSessionValid) {
                _state.value = ProfileState.SessionEvicted
            }
        }
    }
}
