package com.cryptovpn.ui.pages.legal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import com.cryptovpn.ui.theme.PrimaryBlue
import com.cryptovpn.ui.theme.SuccessGreen
import com.cryptovpn.ui.theme.WarningYellow
import com.cryptovpn.ui.theme.ErrorRed

/**
 * 法务文档类型
 */
enum class LegalDocumentType(
    val icon: ImageVector,
    val iconColor: Color
) {
    SERVICE_AGREEMENT(
        icon = Icons.Default.Description,
        iconColor = PrimaryBlue
    ),
    PRIVACY_POLICY(
        icon = Icons.Default.Policy,
        iconColor = SuccessGreen
    ),
    COMMISSION_AGREEMENT(
        icon = Icons.Default.Gavel,
        iconColor = WarningYellow
    ),
    DISCLAIMER(
        icon = Icons.Default.Shield,
        iconColor = ErrorRed
    )
}

/**
 * 法务文档数据类
 */
data class LegalDocument(
    val id: String,
    val title: String,
    val version: String,
    val updateDate: Date,
    val effectiveDate: Date,
    val type: LegalDocumentType,
    val content: String
)

/**
 * 法务文档列表状态
 */
sealed class LegalDocumentsState {
    object Loading : LegalDocumentsState()
    data class Loaded(val documents: List<LegalDocument>) : LegalDocumentsState()
    data class Error(val message: String) : LegalDocumentsState()
}

/**
 * 法务文档ViewModel
 */
@HiltViewModel
class LegalDocumentsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<LegalDocumentsState>(LegalDocumentsState.Loading)
    val state: StateFlow<LegalDocumentsState> = _state.asStateFlow()

    init {
        loadDocuments()
    }

    fun loadDocuments() {
        viewModelScope.launch {
            _state.value = LegalDocumentsState.Loading
            
            try {
                delay(600) // 模拟网络请求
                
                val documents = listOf(
                    LegalDocument(
                        id = "service_agreement",
                        title = "服务协议",
                        version = "2.1.0",
                        updateDate = Date(1704067200000), // 2024-01-01
                        effectiveDate = Date(1706745600000), // 2024-02-01
                        type = LegalDocumentType.SERVICE_AGREEMENT,
                        content = getServiceAgreementContent()
                    ),
                    LegalDocument(
                        id = "privacy_policy",
                        title = "隐私政策",
                        version = "1.5.0",
                        updateDate = Date(1698796800000), // 2023-11-01
                        effectiveDate = Date(1701388800000), // 2023-12-01
                        type = LegalDocumentType.PRIVACY_POLICY,
                        content = getPrivacyPolicyContent()
                    ),
                    LegalDocument(
                        id = "commission_agreement",
                        title = "分佣协议",
                        version = "1.0.0",
                        updateDate = Date(1706745600000), // 2024-02-01
                        effectiveDate = Date(1706745600000), // 2024-02-01
                        type = LegalDocumentType.COMMISSION_AGREEMENT,
                        content = getCommissionAgreementContent()
                    ),
                    LegalDocument(
                        id = "disclaimer",
                        title = "免责声明",
                        version = "1.2.0",
                        updateDate = Date(1696118400000), // 2023-10-01
                        effectiveDate = Date(1698796800000), // 2023-11-01
                        type = LegalDocumentType.DISCLAIMER,
                        content = getDisclaimerContent()
                    )
                )
                
                _state.value = LegalDocumentsState.Loaded(documents)
            } catch (e: Exception) {
                _state.value = LegalDocumentsState.Error("加载文档失败，请稍后重试")
            }
        }
    }

    fun getDocumentById(id: String): LegalDocument? {
        val currentState = _state.value as? LegalDocumentsState.Loaded ?: return null
        return currentState.documents.find { it.id == id }
    }

    private fun getServiceAgreementContent(): String {
        return """
# 服务协议

## 1. 服务概述

CryptoVPN 提供安全、高速的虚拟私人网络服务，帮助用户保护在线隐私和安全。

## 2. 用户责任

- 您必须年满18周岁才能使用本服务
- 您不得将本服务用于任何非法活动
- 您有责任保护自己的账户信息安全

## 3. 服务条款

### 3.1 服务可用性

我们致力于提供99.9%的服务可用性，但不保证服务永不中断。

### 3.2 带宽限制

根据您选择的套餐，可能会有带宽或流量限制。

### 3.3 退款政策

- 7天内无理由退款
- 超过7天按剩余天数比例退款

## 4. 隐私保护

我们严格保护用户隐私，不会记录用户的上网活动日志。

## 5. 协议修改

我们保留随时修改本协议的权利，修改后的协议将在网站上公布。

## 6. 联系我们

如有任何问题，请联系客服：support@cryptovpn.com
        """.trimIndent()
    }

    private fun getPrivacyPolicyContent(): String {
        return """
# 隐私政策

## 1. 信息收集

我们收集的信息包括：
- 账户信息（邮箱、密码）
- 支付信息
- 设备信息（用于优化服务）

## 2. 信息使用

我们使用您的信息用于：
- 提供和维护服务
- 处理支付
- 发送服务通知
- 改进用户体验

## 3. 信息保护

我们采用业界标准的安全措施保护您的信息：
- 数据加密传输
- 安全的服务器存储
- 定期安全审计

## 4. 信息共享

我们不会向第三方出售您的个人信息。

## 5. Cookie政策

我们使用Cookie来改善用户体验。

## 6. 您的权利

您有权：
- 访问您的个人信息
- 更正不准确的信息
- 删除您的账户

## 7. 政策更新

本政策可能会定期更新，更新将在网站上公布。
        """.trimIndent()
    }

    private fun getCommissionAgreementContent(): String {
        return """
# 分佣协议

## 1. 分佣计划概述

CryptoVPN 分佣计划允许用户通过推荐新用户获得佣金奖励。

## 2. 佣金比例

- 直接推荐：30%佣金
- 二级推荐：10%佣金

## 3. 结算方式

- 最低提现金额：10 USDT
- 结算周期：实时结算
- 提现方式：USDT (Solana网络)

## 4. 佣金计算

佣金 = 被推荐用户支付金额 × 佣金比例

## 5. 禁止行为

以下行为将导致分佣资格被取消：
- 自我推荐
- 使用虚假信息
- 垃圾邮件推广

## 6. 协议终止

我们保留随时终止分佣计划的权利。

## 7. 争议解决

如有争议，双方应友好协商解决。
        """.trimIndent()
    }

    private fun getDisclaimerContent(): String {
        return """
# 免责声明

## 1. 服务使用风险

使用 CryptoVPN 服务的风险由用户自行承担。

## 2. 服务可用性

我们不保证：
- 服务永不中断
- 服务完全无错误
- 服务满足所有用户需求

## 3. 第三方内容

对于通过本服务访问的第三方内容，我们不承担责任。

## 4. 法律合规

用户有责任确保其使用本服务符合当地法律法规。

## 5. 责任限制

在法律允许的最大范围内，我们对任何间接、附带、特殊或后果性损害不承担责任。

## 6. 赔偿

用户同意赔偿因使用本服务而产生的任何索赔或损失。

## 7. 适用法律

本免责声明受中华人民共和国法律管辖。
        """.trimIndent()
    }
}
