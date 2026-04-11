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
    val auditFocus = rememberLoopingIndex(itemCount = 3, durationMillis = 4500)
    val requestMetrics = uiState.metrics.take(3).map { it.label to it.value }
    val verificationLabels = listOf("域名已校验", "摘要已匹配", "会话权限已对齐")
    P2ExtendedPageScaffold(
        kicker = "Signature Request",
        title = "签名确认",
        subtitle = "对 DApp发起的操作进行最后确认，补齐钱包交互的关键闭环。",
        hubLabel = "高风险需确认",
        onHubClick = { onEvent(SignMessageConfirmEvent.Refresh) },
        primaryActionLabel = "确认签名",
        onPrimaryAction = { onEvent(SignMessageConfirmEvent.PrimaryActionClicked) },
        secondaryActionLabel = "拒绝",
        onSecondaryAction = { onEvent(SignMessageConfirmEvent.SecondaryActionClicked) },
    ) {
        KpiRow(items = requestMetrics, activeIndex = auditFocus)
        Spacer(modifier = Modifier.height(12.dp))
        P2SignRequestCard(
            dapp = "Jupiter",
            domain = "app.jup.ag",
            operation = "Swap Exact In",
            network = "Solana",
            payload = "580 USDT -> 82.6 SOL",
            gasHint = "约 0.0012 SOL",
            verificationLabel = verificationLabels[auditFocus],
            animated = true,
        )
        Spacer(modifier = Modifier.height(12.dp))
        P2Card(title = "签名前检查", subtitle = "签名后将广播交易并不可回滚。") {
            P2FlowStepCard(
                step = "CHECK 1",
                title = "确认来源域名",
                detail = "app.jup.ag 与当前连接会话一致，避免被钓鱼页面劫持。",
                emphasized = auditFocus == 0,
                animated = true,
            )
            Spacer(modifier = Modifier.height(8.dp))
            P2FlowStepCard(
                step = "CHECK 2",
                title = "核对签名摘要",
                detail = "确认 580 USDT -> 82.6 SOL 与最小到账预期一致。",
                emphasized = auditFocus == 1,
                animated = true,
            )
            Spacer(modifier = Modifier.height(8.dp))
            P2FlowStepCard(
                step = "CHECK 3",
                title = "审计单次授权范围",
                detail = "授权范围限制为单次交易，并在 5 分钟内失效。",
                emphasized = auditFocus == 2,
                animated = true,
            )
            Spacer(modifier = Modifier.height(10.dp))
            P2InlineWarningCard(
                title = "风险提示",
                text = "请核对目标资产与最小到账数量，防止钓鱼签名。",
            )
        }
    }
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
