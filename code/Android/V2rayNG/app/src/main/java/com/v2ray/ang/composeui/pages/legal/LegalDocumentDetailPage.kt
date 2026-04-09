package com.v2ray.ang.composeui.pages.legal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.v2ray.ang.composeui.bridge.legal.LegalBridgeRepository
import com.v2ray.ang.composeui.components.tags.StatusTag
import com.v2ray.ang.composeui.components.tags.StatusType
import com.v2ray.ang.composeui.theme.ControlPlaneIntent
import com.v2ray.ang.composeui.theme.ControlPlaneLayer
import com.v2ray.ang.composeui.theme.ControlPlaneTokens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LegalDocumentDetail(
    val id: String,
    val title: String,
    val description: String,
    val lastUpdated: String,
    val content: String,
)

sealed class LegalDocumentDetailState {
    data object Loading : LegalDocumentDetailState()
    data class Loaded(val document: LegalDocumentDetail) : LegalDocumentDetailState()
    data class Error(val message: String) : LegalDocumentDetailState()
}

class LegalDocumentDetailViewModel : ViewModel() {
    private val legalBridgeRepository = LegalBridgeRepository()
    private val _state = MutableStateFlow<LegalDocumentDetailState>(LegalDocumentDetailState.Loading)
    val state: StateFlow<LegalDocumentDetailState> = _state

    fun loadDocument(documentId: String) {
        _state.value = LegalDocumentDetailState.Loading
        val doc = legalBridgeRepository.getDocument(documentId)
        _state.value = if (doc == null) {
            LegalDocumentDetailState.Error("文档不存在")
        } else {
            LegalDocumentDetailState.Loaded(
                LegalDocumentDetail(
                    id = doc.id,
                    title = doc.title,
                    description = doc.description,
                    lastUpdated = doc.lastUpdated,
                    content = resolveDocumentContent(doc.id, doc.content),
                ),
            )
        }
    }
}

@Composable
fun LegalDocumentDetailPage(
    viewModel: LegalDocumentDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    documentId: String = "terms",
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }

    LegalPageScaffold(
        topBar = {
            LegalTopBar(
                title = when (val currentState = state) {
                    is LegalDocumentDetailState.Loaded -> currentState.document.title
                    else -> "文档详情"
                },
                subtitle = "法务详情更接近简单 chrome + 长文内容容器",
                onNavigateBack = onNavigateBack,
            )
        },
    ) { paddingValues ->
        when (val currentState = state) {
            LegalDocumentDetailState.Loading -> Unit
            is LegalDocumentDetailState.Error -> {
                LegalStatusView(
                    title = "无法加载文档",
                    message = currentState.message,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is LegalDocumentDetailState.Loaded -> {
                LegalDocumentDetailContent(
                    document = currentState.document,
                    paddingValues = paddingValues,
                )
            }
        }
    }
}

@Composable
private fun LegalDocumentDetailContent(
    document: LegalDocumentDetail,
    paddingValues: PaddingValues,
) {
    val blocks = splitDocumentBlocks(document.content)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 8.dp,
            bottom = 28.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            LegalHighlightCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        LegalBadge(text = "Legal Detail", intent = ControlPlaneIntent.Infra)
                        Text(
                            text = document.title,
                            color = LegalTextPrimary,
                            fontSize = 28.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    StatusTag(text = "正文有效", type = StatusType.OK)
                }
                Text(
                    text = document.description,
                    color = LegalTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LegalBadge(
                        text = "最后更新 ${document.lastUpdated}",
                        intent = ControlPlaneIntent.Neutral,
                    )
                    LegalBadge(
                        text = document.id.uppercase(),
                        intent = ControlPlaneIntent.Finance,
                    )
                }
            }
        }

        item {
            LegalCard(
                layer = ControlPlaneLayer.Level2,
                accentWash = ControlPlaneTokens.Warning.container.copy(alpha = 0.72f),
            ) {
                LegalSectionTitle(
                    title = "阅读提示",
                    subtitle = "详情页不抢 CTA，把阅读辅助信息收纳在正文之前。",
                )
                listOf(
                    "当前展示的是本地文档 Provider 对应的正式文本版本。",
                    "如涉及退款、推广奖励或账户处理，请以对应条款章节为准。",
                    "长文内容采用单列阅读容器，避免多卡片打碎阅读节奏。",
                ).forEach {
                    Text(
                        text = it,
                        color = LegalTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                    )
                }
            }
        }

        item {
            LegalCard(
                layer = ControlPlaneLayer.Level1,
                accentWash = ControlPlaneTokens.Infra.container.copy(alpha = 0.28f),
            ) {
                LegalSectionTitle(
                    title = "正文",
                    subtitle = "法律详情页派生自极简容器，正文是唯一主角。",
                )
                blocks.forEachIndexed { index, block ->
                    DocumentBlock(block = block)
                    if (index != blocks.lastIndex) {
                        LegalListDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentBlock(block: DocumentBlockModel) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = block.heading,
            color = LegalTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
        block.bodyLines.forEach { line ->
            Text(
                text = line,
                color = LegalTextSecondary,
                fontSize = 14.sp,
                lineHeight = 22.sp,
            )
        }
    }
}

private data class DocumentBlockModel(
    val heading: String,
    val bodyLines: List<String>,
)

private fun splitDocumentBlocks(content: String): List<DocumentBlockModel> {
    return content
        .trim()
        .split("\n\n")
        .mapNotNull { rawBlock ->
            val lines = rawBlock.lines().map { it.trim() }.filter { it.isNotBlank() }
            if (lines.isEmpty()) {
                null
            } else {
                val heading = lines.first()
                val body = if (lines.size > 1) lines.drop(1) else emptyList()
                DocumentBlockModel(heading = heading, bodyLines = body)
            }
        }
}

private fun resolveDocumentContent(documentId: String, fallback: String): String {
    if (fallback.length > 80) return fallback

    return when (documentId) {
        "terms" -> TERMS_CONTENT
        "privacy" -> PRIVACY_CONTENT
        "refund" -> REFUND_CONTENT
        "affiliate" -> AFFILIATE_CONTENT
        "cookies" -> COOKIES_CONTENT
        else -> fallback
    }
}

private const val TERMS_CONTENT = """
1. 服务条款
您访问或使用 CryptoVPN 相关服务，即表示已经阅读并接受本协议。若不同意任一条款，请停止使用相关功能。

2. 账户与服务
我们为账户提供订阅管理、支付、邀请返佣和售后支持等能力。部分服务会基于账户状态、订阅周期和安全策略决定是否开放。

3. 使用限制
用户不得将服务用于违法用途、恶意攻击、欺诈支付或绕过平台风控。若系统检测到异常，我们有权暂停服务并要求补充验证。

4. 费用与结算
订阅和增值服务采用预付费模式。若页面中展示了价格、退款资格或推广返佣比例，应以对应说明页的最新文本为准。

5. 协议更新
当服务能力、支付方式或合规要求发生变化时，平台可能更新协议并在应用内展示最新版本。
"""

private const val PRIVACY_CONTENT = """
1. 隐私政策
我们仅在提供服务所必需的范围内处理账户、订单、设备和安全相关信息，不记录用户浏览内容本身。

2. 信息收集
账户邮箱、登录状态、订单信息、邀请关系、支付回执和设备基础信息会用于服务交付、售后支持与安全审计。

3. 信息使用
收集的数据主要用于身份验证、订单履约、风控检查、退款审核和客服定位问题，不会用于出售用户画像。

4. 数据安全
我们通过访问控制、日志审计和加密传输来保护个人信息。若检测到高风险行为，平台可能触发重新登录或额外验证。

5. 用户权利
用户可以查看、修改、删除或导出与账户相关的部分信息。涉及监管要求或支付争议的数据，会按照法律义务保留。
"""

private const val REFUND_CONTENT = """
1. 退款政策
符合条件的订单可以申请退款。退款资格取决于购买时间、使用情况、支付方式和当前售后规则。

2. 申请条件
首次购买、明显服务异常或未使用的订单更可能满足退款条件；超出退款时效或存在违规行为的订单可能被拒绝。

3. 审核流程
退款申请提交后会进入审核队列，平台可能要求补充订单号、问题截图或支付凭证。审核结果会在帮助中心或工单中同步。

4. 到账说明
原路退款通常需要一定处理时间。若使用链上支付或受支付渠道限制，到账时间可能晚于应用内的审核完成时间。

5. 异议处理
若用户对退款结果有异议，可通过帮助入口继续提交补充信息，由客服和风控团队复核。
"""

private const val AFFILIATE_CONTENT = """
1. 推广协议
推广计划允许用户通过邀请码或专属链接邀请新用户，并按照规则获得返佣。

2. 返佣计算
返佣金额会基于受邀用户的有效订单计算，实际入账时间、可提现时间和冻结期以账本页和提现页展示为准。

3. 邀请关系
邀请码一旦绑定，将作为后续奖励归因的重要依据。任何伪造关系、批量注册或异常套利行为都可能导致奖励取消。

4. 提现规则
当返佣金额达到最小提现门槛后，用户可以发起提现申请。提现网络、审核周期和失败原因会展示在提现记录中。

5. 风险控制
如果推广行为触发风控或合规审查，平台可能延迟结算、冻结部分收益或要求补充材料。
"""

private const val COOKIES_CONTENT = """
1. Cookie 政策
我们使用必要的 Cookie 或类似技术维持登录态、保存基础偏好并提升服务稳定性。

2. 使用场景
必要 Cookie 用于身份验证和会话保持；功能类 Cookie 用于主题、语言或页面记忆；分析类技术用于诊断问题和优化体验。

3. 第三方服务
部分支付、客服或分析服务可能设置自己的标识符。我们会尽量限制其用途，并遵循适用的披露要求。

4. 管理方式
用户可以在系统或浏览器设置中调整 Cookie 行为，但关闭必要项可能影响登录或订单处理等核心功能。

5. 更新说明
当依赖的基础设施或合规要求变化时，Cookie 政策也会同步更新，并以应用内可访问的文本为准。
"""

@Preview(showBackground = true)
@Composable
private fun LegalDocumentDetailPagePreview() {
    MaterialTheme {
        LegalBitgetBackground {
            LegalDocumentDetailContent(
                document = LegalDocumentDetail(
                    id = "terms",
                    title = "用户协议",
                    description = "使用服务的条款和条件",
                    lastUpdated = "2026-04-01",
                    content = TERMS_CONTENT,
                ),
                paddingValues = PaddingValues(),
            )
        }
    }
}
