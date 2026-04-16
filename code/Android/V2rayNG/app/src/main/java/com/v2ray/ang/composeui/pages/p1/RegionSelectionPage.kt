package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.app.AppPageBackgroundStyle
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.cards.AppCard
import com.v2ray.ang.composeui.components.cards.AppCardVariant
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.feedback.EmptyStateCard
import com.v2ray.ang.composeui.components.inputs.AppTextField
import com.v2ray.ang.composeui.components.listitems.AppListCardItem
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.navigation.AppTopBarMode
import com.v2ray.ang.composeui.components.navigation.CryptoVpnBottomBar
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p1.model.RegionOptionUi
import com.v2ray.ang.composeui.p1.model.RegionSelectionEvent
import com.v2ray.ang.composeui.p1.model.RegionSelectionUiState
import com.v2ray.ang.composeui.p1.model.regionSelectionPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.RegionSelectionViewModel
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens

private val RegionGlowBlue = Color(0x204F7CFF)
private val RegionGlowPurple = Color(0x188C7CFF)

private data class NodeUiModel(
    val id: String,
    val lineCode: String,
    val city: String,
    val code: String,
    val description: String,
    val latencyLabel: String,
    val speedLabel: String,
    val riskLabel: String,
    val recommended: Boolean = false,
    val isAllowed: Boolean = true,
)

private data class NodeFilter(
    val key: String,
    val label: String,
)

@Composable
fun RegionSelectionRoute(
    viewModel: RegionSelectionViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    emptyActionLabel: String = "返回首页继续连接",
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.selectionApplied) {
        if (uiState.selectionApplied) {
            viewModel.onEvent(RegionSelectionEvent.SelectionNavigated)
            onPrimaryAction()
        }
    }
    RegionSelectionScreen(
        uiState = uiState,
        onSearchChange = { viewModel.onEvent(RegionSelectionEvent.FieldChanged("search", it)) },
        onRefresh = { viewModel.onEvent(RegionSelectionEvent.Refresh) },
        onPrimaryAction = { lineCode, nodeId ->
            viewModel.onEvent(RegionSelectionEvent.NodeSelected(lineCode, nodeId))
        },
        onEmptyAction = onPrimaryAction,
        emptyActionLabel = emptyActionLabel,
        onSecondaryAction = {
            viewModel.onEvent(RegionSelectionEvent.SecondaryActionClicked)
            onSecondaryAction?.invoke()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun RegionSelectionScreen(
    uiState: RegionSelectionUiState,
    onSearchChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onPrimaryAction: (String, String) -> Unit,
    onEmptyAction: () -> Unit,
    emptyActionLabel: String,
    onSecondaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val baseline = OverviewBaselineTokens.primary
    val nodes = uiState.regions.map { it.toNodeUi() }
    var selectedFilterIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedNodeId by rememberSaveable { mutableStateOf(uiState.selectedNodeId ?: nodes.firstOrNull()?.id.orEmpty()) }
    val filters = remember {
        listOf(
            NodeFilter("recommend", "推荐"),
            NodeFilter("asia", "亚洲"),
            NodeFilter("europe", "欧洲"),
            NodeFilter("america", "美洲"),
            NodeFilter("lowdelay", "低延迟"),
        )
    }
    val filteredNodes = remember(nodes, selectedFilterIndex, uiState.searchQuery) {
        filterRegionNodes(nodes, selectedFilterIndex, uiState.searchQuery)
    }
    val selectedNode = filteredNodes.firstOrNull { it.id == selectedNodeId } ?: filteredNodes.firstOrNull()

    LaunchedEffect(filteredNodes) {
        if (filteredNodes.none { it.id == selectedNodeId }) {
            selectedNodeId = filteredNodes.firstOrNull()?.id.orEmpty()
        }
    }

    AppPageScaffold(
        backgroundStyle = AppPageBackgroundStyle.Hero,
        background = {
            RegionSelectionBackground()
        },
        bottomBar = {
            CryptoVpnBottomBar(
                currentRoute = CryptoVpnRouteSpec.plans.name,
                onRouteSelected = onBottomNav,
            )
        },
        contentPadding = PaddingValues(
            horizontal = baseline.pageHorizontal,
            vertical = baseline.pageTopSpacing,
        ),
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp),
            verticalArrangement = Arrangement.spacedBy(baseline.sectionGap),
        ) {
            AppTopBar(
                title = "选择节点",
                subtitle = uiState.subtitle,
                mode = AppTopBarMode.Hero,
                actions = {
                    AppChip(
                        text = "${filteredNodes.count { it.isAllowed }} 可用",
                        tone = AppChipTone.Info,
                    )
                },
            )

            AppTextField(
                value = uiState.searchQuery,
                label = "",
                placeholder = "搜索国家 / 城市 / 用途",
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                leading = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = AppTheme.colors.textTertiary,
                    )
                },
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
            ) {
                filters.forEachIndexed { index, filter ->
                    AppChip(
                        text = filter.label,
                        tone = AppChipTone.Brand,
                        selected = index == selectedFilterIndex,
                        onClick = { selectedFilterIndex = index },
                    )
                }
            }

            selectedNode?.let { node ->
                BestNodeCard(
                    node = node,
                    onUseNode = {
                        selectedNodeId = node.id
                        onPrimaryAction(node.lineCode, node.id)
                    },
                )
            }

            Text(
                text = "可用节点",
                style = MaterialTheme.typography.titleLarge,
                color = AppTheme.colors.textPrimary,
            )

            if (uiState.screenState.hasError || filteredNodes.isEmpty()) {
                EmptyStateCard(
                    title = if (uiState.screenState.hasError) "节点暂不可用" else "当前没有匹配节点",
                    message = uiState.screenState.unavailableMessage
                        ?: uiState.screenState.errorMessage
                        ?: uiState.screenState.emptyMessage
                        ?: uiState.note.ifBlank { "节点数据同步中" },
                    actionLabel = emptyActionLabel,
                    onAction = onEmptyAction,
                )
            } else {
                filteredNodes.forEach { node ->
                    AppListCardItem(
                        title = "${node.city} · ${node.code}",
                        subtitle = node.description,
                        supportingText = "延迟 ${node.latencyLabel} · 风险 ${node.riskLabel}",
                        value = node.speedLabel,
                        emphasized = node.id == selectedNodeId || node.recommended,
                        trailing = {
                            AppChip(
                                text = if (node.isAllowed) "可用" else "受限",
                                tone = if (node.isAllowed) AppChipTone.Success else AppChipTone.Warning,
                            )
                        },
                        onClick = {
                            selectedNodeId = node.id
                            onPrimaryAction(node.lineCode, node.id)
                        },
                    )
                }
            }

            if (uiState.secondaryActionLabel != null) {
                ActionCluster(
                    actions = listOf(
                        ActionClusterAction(
                            label = uiState.secondaryActionLabel,
                            onClick = onSecondaryAction,
                            variant = AppButtonVariant.Secondary,
                        ),
                    ),
                )
            }
        }
    }
}

@Composable
private fun BestNodeCard(
    node: NodeUiModel,
    onUseNode: () -> Unit,
) {
    AppCard(
        variant = AppCardVariant.Highlight,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space12),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.space4),
                ) {
                    Text(
                        text = "推荐节点",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppTheme.colors.textSecondary,
                    )
                    Text(
                        text = "${node.city} · ${node.code}",
                        style = AppTheme.typography.headlineM,
                        color = AppTheme.colors.textPrimary,
                    )
                    Text(
                        text = node.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppTheme.colors.textSecondary,
                    )
                }
                AppChip(
                    text = if (node.isAllowed) "可连接" else "受限",
                    tone = if (node.isAllowed) AppChipTone.Success else AppChipTone.Warning,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
            ) {
                AppChip(text = "延迟 ${node.latencyLabel}", tone = AppChipTone.Info)
                AppChip(text = node.speedLabel, tone = AppChipTone.Neutral)
                AppChip(text = node.riskLabel, tone = AppChipTone.Neutral)
            }

            ActionCluster(
                actions = listOf(
                    ActionClusterAction(
                        label = "使用该节点",
                        onClick = onUseNode,
                        variant = AppButtonVariant.Primary,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun RegionSelectionBackground() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp)
                .background(RegionGlowPurple, RoundedCornerShape(999.dp))
                .blur(48.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 280.dp)
                .size(260.dp)
                .background(RegionGlowBlue, RoundedCornerShape(999.dp))
                .blur(60.dp),
        )
    }
}

private fun RegionOptionUi.toNodeUi(): NodeUiModel = NodeUiModel(
    id = nodeId,
    lineCode = lineCode,
    city = regionName,
    code = nodeName,
    description = "$lineName · ${remark ?: "$host:$port"}",
    latencyLabel = healthStatus,
    speedLabel = host,
    riskLabel = if (isAllowed) "Low" else "Limited",
    recommended = isSelected,
    isAllowed = isAllowed,
)

private fun filterRegionNodes(
    nodes: List<NodeUiModel>,
    selectedTabIndex: Int,
    searchValue: String,
): List<NodeUiModel> {
    val tabbed = when (selectedTabIndex) {
        1 -> nodes.filter { it.city.contains("港") || it.city.contains("日") || it.city.contains("新") || it.city.contains("韩") || it.city.contains("台") }
        2 -> nodes.filter { it.city.contains("德") || it.city.contains("法") || it.city.contains("荷") || it.city.contains("英") || it.city.contains("意") || it.city.contains("西") }
        3 -> nodes.filter { it.city.contains("美") || it.city.contains("加") || it.city.contains("巴") || it.city.contains("墨") }
        4 -> nodes.sortedBy { if (it.isAllowed) 0 else 1 }
        else -> nodes.sortedBy { if (it.recommended) 0 else 1 }
    }
    if (searchValue.isBlank()) return tabbed
    return tabbed.filter { node ->
        listOf(node.city, node.code, node.description).any {
            it.contains(searchValue, ignoreCase = true)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F7FF)
@Composable
private fun RegionSelectionPreview() {
    CryptoVpnTheme {
        Surface {
            RegionSelectionScreen(
                uiState = regionSelectionPreviewState(),
                onSearchChange = {},
                onRefresh = {},
                onPrimaryAction = { _, _ -> },
                onEmptyAction = {},
                emptyActionLabel = "返回首页继续连接",
                onSecondaryAction = {},
            )
        }
    }
}
