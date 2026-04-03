package com.cryptovpn.ui.pages.growth

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

/**
 * 邀请中心ViewModel
 */
@HiltViewModel
class InviteCenterViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<InviteCenterState>(InviteCenterState.Loading)
    val uiState: StateFlow<InviteCenterState> = _uiState.asStateFlow()

    // 当前邀请数据
    private var inviteData: InviteData? = null

    init {
        loadInviteData()
    }

    /**
     * 加载邀请数据
     */
    private fun loadInviteData() {
        viewModelScope.launch {
            _uiState.value = InviteCenterState.Loading
            
            try {
                // 模拟网络请求
                kotlinx.coroutines.delay(800)
                
                // 模拟获取邀请数据
                inviteData = InviteData(
                    inviteCode = "CRYPTO2024",
                    inviteLink = "https://cryptovpn.app/invite/CRYPTO2024",
                    level1Count = 15,
                    level2Count = 42,
                    level1Earnings = BigDecimal("125.50"),
                    level2Earnings = BigDecimal("45.25"),
                    withdrawableBalance = BigDecimal("85.30"),
                    commissionRates = CommissionRates(
                        level1Rate = "10%",
                        level2Rate = "5%"
                    )
                )
                
                _uiState.value = InviteCenterState.Loaded(
                    inviteCode = inviteData!!.inviteCode,
                    inviteLink = inviteData!!.inviteLink,
                    level1Count = inviteData!!.level1Count,
                    level2Count = inviteData!!.level2Count,
                    level1Earnings = inviteData!!.level1Earnings,
                    level2Earnings = inviteData!!.level2Earnings,
                    totalEarnings = inviteData!!.level1Earnings.add(inviteData!!.level2Earnings),
                    withdrawableBalance = inviteData!!.withdrawableBalance,
                    commissionRates = inviteData!!.commissionRates
                )
            } catch (e: Exception) {
                _uiState.value = InviteCenterState.Error(
                    message = "加载数据失败: ${e.message}",
                    canRetry = true
                )
            }
        }
    }

    /**
     * 分享邀请链接
     */
    fun shareInviteLink() {
        inviteData?.let { data ->
            val shareText = """
                邀请您加入 CryptoVPN！
                
                使用我的邀请码: ${data.inviteCode}
                注册链接: ${data.inviteLink}
                
                注册即可获得专属优惠，安全、快速的VPN服务！
            """.trimIndent()
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            
            val chooser = Intent.createChooser(shareIntent, "分享邀请")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }

    /**
     * 检查提现状态
     */
    fun checkWithdrawStatus() {
        viewModelScope.launch {
            val currentState = _uiState.value as? InviteCenterState.Loaded ?: return@launch
            
            try {
                // 模拟检查提现状态
                kotlinx.coroutines.delay(300)
                
                // 模拟提现被禁用的情况
                val isWithdrawDisabled = false
                
                if (isWithdrawDisabled) {
                    _uiState.value = InviteCenterState.WithdrawDisabled(
                        reason = "您的账户存在异常，请联系客服处理",
                        inviteCode = currentState.inviteCode
                    )
                }
            } catch (e: Exception) {
                // 保持当前状态
            }
        }
    }

    /**
     * 检查绑定锁定状态
     */
    fun checkBindingLockStatus() {
        viewModelScope.launch {
            try {
                // 模拟检查绑定锁定状态
                kotlinx.coroutines.delay(300)
                
                // 模拟未被锁定
                val isLocked = false
                
                if (isLocked) {
                    _uiState.value = InviteCenterState.BindingLocked(
                        message = "由于违反平台规则，您的邀请功能已被暂时锁定",
                        unlockTime = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000 // 7天后解锁
                    )
                }
            } catch (e: Exception) {
                // 保持当前状态
            }
        }
    }

    /**
     * 刷新数据
     */
    fun refreshData() {
        loadInviteData()
    }

    /**
     * 重试
     */
    fun retry() {
        loadInviteData()
    }

    /**
     * 邀请数据
     */
    private data class InviteData(
        val inviteCode: String,
        val inviteLink: String,
        val level1Count: Int,
        val level2Count: Int,
        val level1Earnings: BigDecimal,
        val level2Earnings: BigDecimal,
        val withdrawableBalance: BigDecimal,
        val commissionRates: CommissionRates
    )
}
