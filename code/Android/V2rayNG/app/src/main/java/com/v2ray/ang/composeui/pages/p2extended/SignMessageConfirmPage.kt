package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.SignMessageConfirmEvent
import com.v2ray.ang.composeui.p2extended.model.SignMessageConfirmUiState
import com.v2ray.ang.composeui.p2extended.model.signMessageConfirmPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.SignMessageConfirmViewModel

@Composable
fun SignMessageConfirmRoute(
    viewModel: SignMessageConfirmViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SignMessageConfirmScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SignMessageConfirmEvent.PrimaryActionClicked -> onPrimaryAction()
                SignMessageConfirmEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SignMessageConfirmScreen(
    uiState: SignMessageConfirmUiState,
    onEvent: (SignMessageConfirmEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val note = uiState.note.takeMeaningfulSignText()
        ?: "当前未返回签名摘要。"
    val auditSteps = uiState.checklist
        .mapNotNull { bullet ->
            val title = bullet.title.takeMeaningfulSignText()
            val detail = bullet.detail.takeMeaningfulSignText()
            if (title == null || detail == null) null else title to detail
        }
        .take(3)
        .ifEmpty {
            listOf(
                "确认来源信息" to "来源域名与会话信息待接入，请以后端返回为准。",
                "核对签名摘要" to note,
                "审计授权范围" to "授权范围与失效时间待接入。",
            )
        }
    val auditFocus = rememberLoopingIndex(itemCount = maxOf(auditSteps.size, 1), durationMillis = 4500)
    val requestMetrics = uiState.metrics.take(3).map { it.label to it.value }.ifEmpty {
        listOf("状态" to "待接入")
    }
    val verificationLabels = auditSteps.map { it.first }
    val requestSource = requestMetrics.getOrNull(0)?.second.takeMeaningfulSignText() ?: "签名请求"
    val network = requestMetrics.getOrNull(1)?.second.takeMeaningfulSignText() ?: "网络待接入"
    val operation = requestMetrics.getOrNull(2)?.second.takeMeaningfulSignText() ?: uiState.title
    val payload = uiState.fields.firstOrNull { it.key.contains("payload") || it.label.contains("摘要") }?.value
        .takeMeaningfulSignText() ?: note
    val domain = uiState.fields.firstOrNull { it.key.contains("domain") || it.label.contains("域名") }?.value
        .takeMeaningfulSignText() ?: "来源域名待接入"
    val gasHint = uiState.checklist.firstOrNull { it.title.contains("费") }?.detail.takeMeaningfulSignText()
        ?: "网络费待接入"
    val riskMessage = uiState.highlights
        .mapNotNull { it.subtitle.takeMeaningfulSignText() }
        .firstOrNull()
        ?: "请核对签名来源、摘要与权限范围。"
    P2ExtendedPageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = "",
        currentRoute = "sign_message_confirm",
        onBottomNav = onBottomNav,
        hubLabel = uiState.badge.takeMeaningfulSignText() ?: "待确认",
        onHubClick = { onEvent(SignMessageConfirmEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(SignMessageConfirmEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel ?: "拒绝",
        onSecondaryAction = { onEvent(SignMessageConfirmEvent.SecondaryActionClicked) },
    ) {
        KpiRow(items = requestMetrics, activeIndex = auditFocus)
        Spacer(modifier = Modifier.height(12.dp))
        P2SignRequestCard(
            dapp = requestSource,
            domain = domain,
            operation = operation,
            network = network,
            payload = payload,
            gasHint = gasHint,
            verificationLabel = verificationLabels[auditFocus % verificationLabels.size],
            animated = true,
        )
        Spacer(modifier = Modifier.height(12.dp))
        P2Card(title = "签名前检查") {
            auditSteps.forEachIndexed { index, (title, detail) ->
                P2FlowStepCard(
                    step = "CHECK ${index + 1}",
                    title = title,
                    detail = detail,
                    emphasized = auditFocus % auditSteps.size == index,
                    animated = true,
                )
                if (index < auditSteps.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

private fun String?.takeMeaningfulSignText(): String? {
    val normalized = this?.trim().orEmpty()
    return normalized.takeUnless { it.isBlank() || it.isSignPlaceholderText() }
}

private fun String.isSignPlaceholderText(): Boolean {
    val lower = lowercase()
    val markers = listOf(
        "待接入",
        "待同步",
        "阻塞",
        "默认",
        "占位",
        "未返回",
    )
    return markers.any(lower::contains)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SignMessageConfirmPreview() {
    CryptoVpnTheme {
        SignMessageConfirmScreen(
            uiState = signMessageConfirmPreviewState(),
            onEvent = {},
        )
    }
}
