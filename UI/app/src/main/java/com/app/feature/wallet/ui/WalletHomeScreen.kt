package com.app.feature.wallet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SouthWest
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.*
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.feature.wallet.components.*
import com.app.feature.wallet.viewmodel.WalletViewModel
import com.app.common.model.QuickActionUiModel
import com.app.core.utils.Formatters

@Composable
fun WalletHomeScreen(
    viewModel: WalletViewModel = viewModel(),
    onOpenAssets: () -> Unit = {},
    onOpenToken: (String) -> Unit = {},
    onOpenPlans: () -> Unit = {},
    onOpenMarket: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile ?: return
    val actions = listOf(
        QuickActionUiModel("发送", "链上转账", Icons.Outlined.NorthEast),
        QuickActionUiModel("收款", "二维码收款", Icons.Outlined.SouthWest),
        QuickActionUiModel("套餐", "购买 VPN 套餐", Icons.Outlined.Security),
        QuickActionUiModel("市场", "查看风控监控", Icons.Outlined.Language),
    )
    AppScaffold(title = "", showTopBar = false) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("资产总览", style = MaterialTheme.typography.headlineLarge)
                    Text("多链钱包资产、VPN 订阅和快捷操作统一汇总", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                }
            }
            item { BalanceHeader(profile) }
            item { SectionHeader("快捷操作") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    actions.chunked(2).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            rowItems.forEach { item ->
                                GradientCard(modifier = Modifier.weight(1f), title = item.title, subtitle = item.subtitle) {
                                    Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.height(8.dp))
                                    PrimaryButton(text = "进入", onClick = when (item.title) {
                                        "套餐" -> onOpenPlans
                                        "市场" -> onOpenMarket
                                        else -> onOpenAssets
                                    })
                                }
                            }
                        }
                    }
                }
            }
            item {
                GradientCard(title = "VPN 概览", subtitle = "年度旗舰 · 已同步订阅与节点状态") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        MetricPill("订阅状态", "可用")
                        MetricPill("流量", "124 / 1024 GB")
                    }
                    Spacer(Modifier.height(12.dp))
                    InfoRow("当前线路", "东京 / 新加坡 / 加州")
                    InfoRow("连接方式", "v2rayNG 架构 mock core")
                }
            }
            item { SectionHeader("资产快照", actionText = "查看全部", onActionClick = onOpenAssets) }
            items(state.assets.take(4)) { asset -> AssetCard(asset = asset, onClick = { onOpenToken(asset.symbol) }) }
            item {
                GradientCard(title = "钱包资产结构", subtitle = "当前持仓估值与波动摘要") {
                    state.assets.take(3).forEach { asset ->
                        InfoRow("${asset.symbol} · ${asset.name}", Formatters.money(asset.balance * asset.priceUsd))
                    }
                }
            }
        }
    }
}
