package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01List
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
    var selectedNodeId by rememberSaveable { mutableStateOf(nodes.firstOrNull()?.id.orEmpty()) }
    val filteredNodes = remember(nodes, selectedTabIndex, searchValue) {
        filterRegionNodes(
            nodes = nodes,
            selectedTabIndex = selectedTabIndex,
            searchValue = searchValue,
        )
    }

    LaunchedEffect(filteredNodes) {
        if (filteredNodes.none { it.id == selectedNodeId }) {
            selectedNodeId = filteredNodes.firstOrNull()?.id.orEmpty()
        }
    }

    val selectedNode = filteredNodes.firstOrNull { it.id == selectedNodeId } ?: filteredNodes.firstOrNull() ?: nodes.firstOrNull()

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

        P1SelectableCard(
            selected = true,
            accentColor = Color(0xFF20C4F4),
        ) {
            P01CardHeader(
                title = "当前最优",
                trailing = {
                    P01Chip(
                        text = if (selectedNode == nodes.firstOrNull()) "AI排序" else "已选择",
                    )
                },
                subtitle = selectedNode?.subtitle ?: "暂无本地节点数据",
            )
            P01MetricGrid(
                items = listOf(
                    P01MetricCell("Latency", selectedNode?.latencyText ?: "--"),
                    P01MetricCell("Speed", selectedNode?.speedText ?: "--"),
                ),
            )
        }

        P01Card {
            P01CardHeader(
                title = "全部节点",
                trailing = { P01Chip(text = "${filteredNodes.size} 可用") },
            )
            P01List {
                filteredNodes.forEach { node ->
                    P1FeedbackRow(
                        title = node.title,
                        copy = node.subtitle,
                        value = node.latencyText,
                        selected = node.id == selectedNodeId,
                        accentColor = Color(0xFF20C4F4),
                        onClick = {
                            if (selectedNodeId == node.id) {
                                onPrimaryAction()
                            } else {
                                selectedNodeId = node.id
                            }
                        },
                    )
                }
                if (filteredNodes.isEmpty()) {
                    P01CardCopy("没有匹配节点，尝试调整筛选或关键词。")
                }
            }
        }
    }
}

private data class RegionNodeUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val latencyText: String,
    val speedText: String,
    val region: String,
)

private fun regionNodes(uiState: RegionSelectionUiState): List<RegionNodeUi> {
    val contentNodes = uiState.highlights.p1ContentItems()
    if (contentNodes.isNotEmpty()) {
        return contentNodes.mapIndexed { index, item ->
            RegionNodeUi(
                id = item.badge.ifBlank { "node-$index" },
                title = item.title,
                subtitle = item.subtitle,
                latencyText = item.trailing.ifBlank { "${48 + (index * 9)} ms" },
                speedText = listOf("89 Mbps", "76 Mbps", "61 Mbps", "54 Mbps", "72 Mbps")[index % 5],
                region = inferRegion(item.title),
            )
        }
    }
    return listOf(
        RegionNodeUi(
            id = "jp-01",
            title = "东京 · JP-01",
            subtitle = "最快 · 入口/出口已双向检测",
            latencyText = "41 ms",
            speedText = "89 Mbps",
            region = "亚洲",
        ),
        RegionNodeUi(
            id = "sg-09",
            title = "新加坡 · SG-09",
            subtitle = "稳定 · 入口/出口已双向检测",
            latencyText = "52 ms",
            speedText = "76 Mbps",
            region = "亚洲",
        ),
        RegionNodeUi(
            id = "de-03",
            title = "法兰克福 · DE-03",
            subtitle = "低拥堵 · 入口/出口已双向检测",
            latencyText = "138 ms",
            speedText = "61 Mbps",
            region = "欧洲",
        ),
        RegionNodeUi(
            id = "us-12",
            title = "洛杉矶 · US-12",
            subtitle = "流媒体 · 入口/出口已双向检测",
            latencyText = "168 ms",
            speedText = "54 Mbps",
            region = "美洲",
        ),
        RegionNodeUi(
            id = "hk-06",
            title = "香港 · HK-06",
            subtitle = "智能分流 · 入口/出口已双向检测",
            latencyText = "46 ms",
            speedText = "72 Mbps",
            region = "亚洲",
        ),
    )
}

private fun filterRegionNodes(
    nodes: List<RegionNodeUi>,
    selectedTabIndex: Int,
    searchValue: String,
): List<RegionNodeUi> =
    nodes
        .filter { node ->
            when (selectedTabIndex) {
                1 -> node.region == "亚洲"
                2 -> node.region == "欧洲"
                3 -> node.region == "美洲"
                else -> true
            }
        }
        .let { tabbedNodes ->
            if (selectedTabIndex == 4) {
                tabbedNodes.sortedBy { latencyValue(it.latencyText) }
            } else {
                tabbedNodes
            }
        }
        .filter { node ->
            if (searchValue.isBlank()) {
                true
            } else {
                val tokens = searchValue
                    .lowercase()
                    .split("/", " ", "·")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                tokens.all { token ->
                    listOf(node.title, node.subtitle, node.region).any { value ->
                        value.lowercase().contains(token)
                    }
                }
            }
        }

private fun inferRegion(title: String): String =
    when {
        listOf("东京", "新加坡", "香港", "首尔", "东京").any { it in title } -> "亚洲"
        listOf("法兰克福", "伦敦", "巴黎", "柏林").any { it in title } -> "欧洲"
        listOf("洛杉矶", "纽约", "多伦多", "温哥华").any { it in title } -> "美洲"
        else -> "推荐"
    }

private fun latencyValue(value: String): Int =
    value.filter(Char::isDigit).toIntOrNull() ?: Int.MAX_VALUE

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
