package com.app.feature.wallet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.*
import com.app.core.theme.AppDimens
import com.app.core.ui.AppScaffold
import com.app.feature.wallet.components.*
import com.app.feature.wallet.viewmodel.WalletViewModel
import com.app.core.utils.Formatters

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SouthWest
import com.app.common.model.QuickActionUiModel

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
    AppScaffold(title = "总览首页") { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { BalanceHeader(profile) }
            item { SectionHeader("快捷操作") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    actions.chunked(2).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            rowItems.forEach { item ->
                                GradientCard(modifier = Modifier.weight(1f), title = item.title, subtitle = item.subtitle) {
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
                    InfoRow("订阅状态", "可用")
                    InfoRow("流量概览", "124 / 1024 GB")
                    InfoRow("节点质量", "东京 / 新加坡 / 加州")
                }
            }
            item { SectionHeader("资产快照", actionText = "查看全部", onActionClick = onOpenAssets) }
            items(state.assets.take(4)) { asset -> AssetCard(asset = asset, onClick = { onOpenToken(asset.symbol) }) }
        }
    }
}
