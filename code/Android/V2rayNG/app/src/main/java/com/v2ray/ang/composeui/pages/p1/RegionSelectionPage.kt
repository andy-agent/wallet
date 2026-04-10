package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
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
import com.v2ray.ang.composeui.p0.ui.P01SearchField
import com.v2ray.ang.composeui.p0.ui.P01Tab
import com.v2ray.ang.composeui.p1.model.RegionSelectionEvent
import com.v2ray.ang.composeui.p1.model.RegionSelectionUiState
import com.v2ray.ang.composeui.p1.model.regionSelectionPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.RegionSelectionViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun RegionSelectionRoute(
    viewModel: RegionSelectionViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    RegionSelectionScreen(
        uiState = uiState,
        onSearchChange = { viewModel.onEvent(RegionSelectionEvent.FieldChanged("search", it)) },
        onPrimaryAction = {
            viewModel.onEvent(RegionSelectionEvent.PrimaryActionClicked)
            onPrimaryAction()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun RegionSelectionScreen(
    uiState: RegionSelectionUiState,
    onSearchChange: (String) -> Unit,
    onPrimaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val searchValue = uiState.fields.firstOrNull()?.value.orEmpty()
    val nodes = regionNodes(uiState)
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    P01PhoneScaffold(
        statusTime = "18:07",
        currentRoute = CryptoVpnRouteSpec.plans.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "SMART ROUTING",
            title = "选择最佳节点",
            subtitle = "用延迟、速度、用途标签来选区，而不是只看国家名。",
        )

        P01SearchField(
            value = searchValue,
            onValueChange = onSearchChange,
            placeholder = "搜索国家 / 城市 / 用途",
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("推荐", "亚洲", "欧洲", "美洲", "低延迟").forEachIndexed { index, label ->
                P01Tab(
                    text = label,
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                )
            }
        }

        P01Card {
            P01CardHeader(
                title = "当前最优",
                trailing = { P01Chip(text = "AI排序") },
                subtitle = nodes.firstOrNull()?.subtitle ?: "东京 · JP-01 · 专线出口 · 视频流媒体 · 钱包交易确认优先",
            )
            P01MetricGrid(
                items = listOf(
                    P01MetricCell("Latency", uiState.metrics.getOrNull(0)?.value ?: "41 ms"),
                    P01MetricCell("Speed", "89 Mbps"),
                ),
            )
        }

        P01Card {
            P01CardHeader(
                title = "全部节点",
                trailing = { P01Chip(text = "${nodes.size.coerceAtLeast(62)} 可用") },
            )
            P01List {
                nodes.forEach { node ->
                    P01ListRow(
                        title = node.title,
                        copy = node.subtitle,
                        value = node.trailing,
                        onClick = {
                            onPrimaryAction()
                        },
                    )
                }
            }
        }
    }
}

private fun regionNodes(uiState: RegionSelectionUiState): List<FeatureListItem> =
    uiState.highlights.ifEmpty {
        listOf(
            FeatureListItem("东京 · JP-01", "最快 · 入口/出口已双向检测", "41 ms", "PREMIUM"),
            FeatureListItem("新加坡 · SG-09", "稳定 · 入口/出口已双向检测", "52 ms", "PREMIUM"),
            FeatureListItem("法兰克福 · DE-03", "低拥堵 · 入口/出口已双向检测", "138 ms", "STANDARD"),
            FeatureListItem("洛杉矶 · US-12", "流媒体 · 入口/出口已双向检测", "168 ms", "STANDARD"),
            FeatureListItem("香港 · HK-06", "智能分流 · 入口/出口已双向检测", "46 ms", "PREMIUM"),
        )
    }

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun RegionSelectionPreview() {
    CryptoVpnTheme {
        RegionSelectionScreen(
            uiState = regionSelectionPreviewState(),
            onSearchChange = {},
            onPrimaryAction = {},
        )
    }
}
