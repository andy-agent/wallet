package com.cryptovpn.ui.pages.legal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 法务文档详情
 */
data class LegalDocumentDetail(
    val id: String,
    val title: String,
    val lastUpdated: String,
    val content: String
)

/**
 * 法务文档详情页状态
 */
sealed class LegalDocumentDetailState {
    object Idle : LegalDocumentDetailState()
    object Loading : LegalDocumentDetailState()
    data class Loaded(val document: LegalDocumentDetail) : LegalDocumentDetailState()
    data class Error(val message: String) : LegalDocumentDetailState()
}

/**
 * 法务文档详情页ViewModel
 */
@HiltViewModel
class LegalDocumentDetailViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<LegalDocumentDetailState>(LegalDocumentDetailState.Idle)
    val state: StateFlow<LegalDocumentDetailState> = _state

    fun loadDocument(documentId: String) {
        val document = when (documentId) {
            "terms" -> LegalDocumentDetail(
                id = "terms",
                title = "用户协议",
                lastUpdated = "2024-01-01",
                content = TERMS_CONTENT
            )
            "privacy" -> LegalDocumentDetail(
                id = "privacy",
                title = "隐私政策",
                lastUpdated = "2024-01-01",
                content = PRIVACY_CONTENT
            )
            "refund" -> LegalDocumentDetail(
                id = "refund",
                title = "退款政策",
                lastUpdated = "2024-01-01",
                content = REFUND_CONTENT
            )
            "affiliate" -> LegalDocumentDetail(
                id = "affiliate",
                title = "推广协议",
                lastUpdated = "2024-01-01",
                content = AFFILIATE_CONTENT
            )
            "cookies" -> LegalDocumentDetail(
                id = "cookies",
                title = "Cookie政策",
                lastUpdated = "2024-01-01",
                content = COOKIES_CONTENT
            )
            else -> null
        }
        
        _state.value = if (document != null) {
            LegalDocumentDetailState.Loaded(document)
        } else {
            LegalDocumentDetailState.Error("文档不存在")
        }
    }
}

/**
 * 法务文档详情页
 * 显示法务文档的完整内容
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentDetailPage(
    viewModel: LegalDocumentDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    documentId: String = "terms",
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when (state) {
                            is LegalDocumentDetailState.Loaded -> 
                                (state as LegalDocumentDetailState.Loaded).document.title
                            else -> "文档详情"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is LegalDocumentDetailState.Loaded -> {
                    val document = (state as LegalDocumentDetailState.Loaded).document
                    DocumentContent(document = document)
                }
                is LegalDocumentDetailState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is LegalDocumentDetailState.Error -> {
                    ErrorView(message = (state as LegalDocumentDetailState.Error).message)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun DocumentContent(document: LegalDocumentDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 标题
        Text(
            text = document.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 更新时间
        Text(
            text = "最后更新: ${document.lastUpdated}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 内容
        Text(
            text = document.content,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp,
            textAlign = TextAlign.Start
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "加载失败",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 文档内容
private val TERMS_CONTENT = """
1. 服务条款

欢迎使用CryptoVPN！本用户协议（以下简称"协议"）是您与CryptoVPN之间关于使用我们提供的VPN服务的法律协议。

1.1 接受条款
通过注册、访问或使用我们的服务，您表示您已阅读、理解并同意受本协议的约束。如果您不同意本协议的任何部分，请不要使用我们的服务。

1.2 服务描述
CryptoVPN提供虚拟私人网络（VPN）服务，帮助用户安全、私密地访问互联网。我们的服务包括但不限于：
- 加密网络连接
- 隐藏IP地址
- 访问地理限制内容
- 保护在线隐私

2. 用户责任

2.1 合法使用
您同意仅将我们的服务用于合法目的。您不得使用我们的服务进行任何非法活动，包括但不限于：
- 发送垃圾邮件
- 进行网络攻击
- 传播恶意软件
- 侵犯他人知识产权

2.2 账户安全
您有责任维护您的账户信息安全。请勿与他人分享您的登录凭据。如发现任何未经授权的使用，请立即通知我们。

3. 服务费用

3.1 定价
我们的服务费用在网站上公布。我们保留随时调整价格的权利，但已购买的套餐不受影响。

3.2 付款
您可以通过我们支持的各种支付方式支付服务费用。所有付款均为预付款。

4. 免责声明

4.1 服务可用性
我们努力确保服务的持续可用性，但不保证服务不会中断。我们不因服务中断或数据丢失而承担责任。

4.2 第三方内容
我们对通过我们的服务访问的第三方内容不承担责任。

5. 协议修改

我们保留随时修改本协议的权利。修改后的协议将在网站上公布，继续使用服务即表示您接受修改后的协议。

6. 终止

我们保留在任何时候因任何原因终止您访问服务的权利，包括但不限于违反本协议。

7. 联系我们

如有任何问题或疑虑，请通过以下方式联系我们：
邮箱: support@cryptovpn.app
""".trimIndent()

private val PRIVACY_CONTENT = """
1. 隐私保护承诺

CryptoVPN高度重视用户的隐私保护。本隐私政策说明我们如何收集、使用、存储和保护您的个人信息。

2. 信息收集

2.1 我们收集的信息
- 账户信息：邮箱地址、密码
- 支付信息：交易记录（不存储完整的支付卡信息）
- 使用数据：连接时间、数据使用量（不记录浏览内容）
- 设备信息：设备类型、操作系统版本

2.2 我们不收集的信息
- 浏览历史
- 访问的网站内容
- DNS查询记录
- 流量内容

3. 信息使用

我们使用收集的信息用于：
- 提供和维护服务
- 处理支付
- 改进服务质量
- 发送服务通知
- 防止欺诈和滥用

4. 信息保护

我们采用业界标准的安全措施保护您的信息，包括：
- 数据加密传输
- 安全的服务器存储
- 定期安全审计

5. 信息共享

我们不会出售您的个人信息。仅在以下情况下可能共享信息：
- 获得您的明确同意
- 法律要求
- 保护我们的合法权益

6. 您的权利

您有权：
- 访问您的个人信息
- 更正不准确的信息
- 删除您的账户和数据
- 导出您的数据

7. Cookie使用

我们使用Cookie来改善用户体验。您可以在浏览器设置中管理Cookie偏好。

8. 政策更新

我们可能会更新本隐私政策。重大变更将通过应用内通知告知用户。
""".trimIndent()

private val REFUND_CONTENT = """
1. 退款政策概述

CryptoVPN致力于提供高质量的VPN服务。如果您对我们的服务不满意，我们提供以下退款政策。

2. 退款条件

2.1 7天无理由退款
- 新用户在购买后7天内可申请全额退款
- 每个账户仅限一次7天无理由退款
- 退款将在5-10个工作日内原路返回

2.2 服务问题退款
- 如因我们的服务问题导致无法正常使用，可申请退款
- 需要提供问题描述和相关证据
- 我们将根据具体情况评估退款金额

3. 不可退款情况

以下情况不予退款：
- 超过7天的订单
- 违反用户协议导致的账户封禁
- 已使用大部分服务时长的订单
- 通过非官方渠道购买的套餐

4. 退款流程

4.1 申请方式
- 通过应用内"帮助与反馈"提交退款申请
- 或发送邮件至 support@cryptovpn.app

4.2 处理时间
- 我们将在3个工作日内审核您的申请
- 审核通过后，退款将在5-10个工作日内到账

5. 特殊说明

- 加密货币支付的退款将按支付时的汇率计算
- 退款金额可能因汇率波动而有所不同
- 部分退款将按比例计算剩余服务时长
""".trimIndent()

private val AFFILIATE_CONTENT = """
1. 推广计划概述

CryptoVPN推广计划允许用户通过邀请新用户注册和购买服务来赚取佣金。

2. 佣金规则

2.1 佣金比例
- 基础佣金比例：被邀请人首单金额的20%
- 续费佣金比例：被邀请人续费金额的10%

2.2 佣金计算
- 佣金以美元计算
- 最低提现金额：$10
- 提现手续费：1%

3. 推广方式

3.1 邀请码
- 每位推广者拥有唯一的邀请码
- 被邀请人注册时填写邀请码即可建立关联

3.2 邀请链接
- 可通过专属邀请链接邀请用户
- 链接包含推广者身份标识

4. 佣金结算

4.1 结算周期
- 佣金在被邀请人订单完成后计入账户
- 每月1日结算上月的可提现佣金

4.2 提现方式
- 支持提现至USDT钱包
- 提现申请将在1-3个工作日内处理

5. 违规处理

以下行为将导致推广资格被取消：
- 使用虚假信息注册账户
- 进行欺诈性推广
- 发送垃圾邮件
- 其他违反用户协议的行为

6. 计划修改

我们保留随时修改推广计划规则的权利。重大变更将提前通知推广者。
""".trimIndent()

private val COOKIES_CONTENT = """
1. Cookie概述

Cookie是网站存储在您设备上的小型文本文件。我们使用Cookie来改善您的使用体验。

2. 我们使用的Cookie类型

2.1 必要Cookie
- 用于网站基本功能
- 无法禁用
- 示例：登录状态保持

2.2 功能Cookie
- 用于提供增强功能
- 示例：语言偏好、主题设置

2.3 分析Cookie
- 用于了解用户如何使用我们的服务
- 帮助我们改进产品
- 示例：使用统计、错误报告

3. Cookie管理

3.1 浏览器设置
您可以通过浏览器设置管理Cookie：
- 接受所有Cookie
- 拒绝所有Cookie
- 提示每个Cookie

3.2 我们的Cookie设置
在应用设置中，您可以选择：
- 启用/禁用分析Cookie
- 启用/禁用功能Cookie

4. 第三方Cookie

我们可能使用第三方服务，这些服务可能会设置Cookie：
- 支付处理商
- 分析服务
- 客户支持工具

5. Cookie安全

我们采取以下措施保护Cookie安全：
- 使用安全的Cookie标志
- 设置适当的过期时间
- 使用HttpOnly标志防止XSS攻击

6. 政策更新

我们可能会更新本Cookie政策。更新后的政策将在应用中公布。
""".trimIndent()

@Preview(showBackground = true)
@Composable
fun LegalDocumentDetailPagePreview() {
    MaterialTheme {
        LegalDocumentDetailPage()
    }
}
