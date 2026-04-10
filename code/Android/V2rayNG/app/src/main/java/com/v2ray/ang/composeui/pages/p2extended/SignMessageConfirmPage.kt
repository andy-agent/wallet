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
        P2Card(title = "Jupiter 请求签名", subtitle = "请检查合约、数量、网络与 gas 费用。") {
            FieldRow("操作类型", "Swap Exact In")
            Spacer(modifier = Modifier.height(8.dp))
            FieldRow("网络", "Solana")
            Spacer(modifier = Modifier.height(8.dp))
            FieldRow("支付资产", "580 USDT")
            Spacer(modifier = Modifier.height(8.dp))
            FieldRow("目标资产", "82.6 SOL")
            Spacer(modifier = Modifier.height(8.dp))
            FieldRow("预估费用", "0.0012 SOL")
            Spacer(modifier = Modifier.height(12.dp))
            NoteCard(title = "风险提示", text = "授权范围为本次交易，未检测到无限授权")
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
