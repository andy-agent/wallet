package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.AssetHolding
import com.v2ray.ang.composeui.p0.model.WalletHomeEvent
import com.v2ray.ang.composeui.p0.model.WalletHomeUiState
import com.v2ray.ang.composeui.p0.repository.MockP0Repository
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01ListRow
import com.v2ray.ang.composeui.p0.ui.P01MetricCell
import com.v2ray.ang.composeui.p0.ui.P01MetricGrid
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01Tab
import com.v2ray.ang.composeui.p0.viewmodel.WalletHomeViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun WalletHomeRoute(
    currentRoute: String,
    viewModel: WalletHomeViewModel,
    onBottomNav: (String) -> Unit,
    onReceive: (() -> Unit)? = null,
    onSend: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    WalletHomeScreen(
        currentRoute = currentRoute,
        uiState = uiState,
        onSelectChain = { viewModel.onEvent(WalletHomeEvent.ChainSelected(it)) },
        onBottomNav = onBottomNav,
        onReceive = onReceive ?: { onBottomNav(CryptoVpnRouteSpec.receiveRoute("USDT", "tron")) },
        onSend = onSend ?: { onBottomNav(CryptoVpnRouteSpec.sendRoute("USDT", "tron")) },
    )
}

@Composable
fun WalletHomeScreen(
    currentRoute: String,
    uiState: WalletHomeUiState,
    onSelectChain: (String) -> Unit,
    onBottomNav: (String) -> Unit,
    onReceive: () -> Unit,
    onSend: () -> Unit,
) {
    P01PhoneScaffold(
        statusTime = "18:10",
        currentRoute = currentRoute,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "MULTI-CHAIN WALLET",
            title = "钱包总览",
            subtitle = "P0 里补齐链维度、资产维度、VPN 场景支付维度。",
        )

        P01Card {
            P01CardHeader(
                title = uiState.totalBalanceText,
                trailing = { P01Chip(text = "${uiState.chains.count()} 链已激活") },
                subtitle = uiState.alertBanner,
            )
            P01MetricGrid(
                items = listOf(
                    P01MetricCell("可用余额", uiState.totalBalanceText),
                    P01MetricCell("资产数量", uiState.assets.size.toString()),
                    P01MetricCell("已激活链", uiState.chains.size.toString()),
                    P01MetricCell("当前网络", uiState.chains.firstOrNull()?.label ?: "未配置"),
                ),
            )
        }

        P01Card {
            P01CardHeader(
                title = "网络筛选",
                trailing = { P01Chip(text = "自动补齐") },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("全部链" to "all") + uiState.chains.take(4).map { it.label to it.chainId }
            .forEach { (label, chainId) ->
                    P01Tab(
                        text = label,
                        selected = chainId == "all" || chainId == uiState.selectedChainId,
                        onClick = {
                            if (chainId != "all") onSelectChain(chainId)
                        },
                    )
                }
            }
        }

        P01Card {
            P01CardHeader(
                title = "资产列表",
                trailing = { P01Chip(text = "按余额排序") },
            )
            P01List {
                uiState.assets.ifEmpty {
                    listOf(AssetHolding("--", "暂无资产缓存", "--", "--", "等待真实资产或订单记录", true))
                }.forEach { asset ->
                    P01ListRow(
                        title = asset.symbol,
                        copy = asset.chainLabel,
                        value = asset.balanceText,
                        onClick = {
                            onBottomNav(
                                CryptoVpnRouteSpec.assetDetailRoute(
                                    asset.symbol,
                                    inferChain(asset.chainLabel),
                                ),
                            )
                        },
                    )
                }
            }
        }

        P01Card {
            P01CardHeader(title = "快捷操作")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                com.v2ray.ang.composeui.p0.ui.P01SecondaryButton(
                    text = "收款",
                    onClick = onReceive,
                    modifier = Modifier.weight(1f),
                )
                com.v2ray.ang.composeui.p0.ui.P01PrimaryButton(
                    text = "发送",
                    onClick = onSend,
                    modifier = Modifier.weight(1f),
                )
            }
            P01CardCopy(uiState.alertBanner)
        }
    }
}

private fun inferChain(chainLabel: String): String = when {
    chainLabel.contains("sol", ignoreCase = true) -> "solana"
    chainLabel.contains("eth", ignoreCase = true) -> "ethereum"
    chainLabel.contains("base", ignoreCase = true) -> "base"
    else -> "tron"
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletHomePreview() {
    CryptoVpnTheme {
        WalletHomeScreen(
            currentRoute = CryptoVpnRouteSpec.walletHome.name,
            uiState = WalletHomeViewModel(MockP0Repository()).uiState.value,
            onSelectChain = {},
            onBottomNav = {},
            onReceive = {},
            onSend = {},
        )
    }
}
