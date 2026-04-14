package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p1.model.RegionOptionUi
import com.v2ray.ang.composeui.p1.model.RegionSelectionEvent
import com.v2ray.ang.composeui.p1.model.RegionSelectionUiState
import com.v2ray.ang.composeui.p1.model.regionSelectionPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.RegionSelectionViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

private val PageBgTop = Color(0xFFF5F7FF)
private val PageBgBottom = Color(0xFFEFF8FF)
private val TitleColor = Color(0xFF111827)
private val SubtleTextColor = Color(0xFF7B8794)
private val AccentBlue = Color(0xFF4C74FF)
private val AccentCyan = Color(0xFF28C7E8)
private val AccentPurple = Color(0xFF8A7CFF)

private data class NodeSelectTypeScale(
    val hero: TextStyle,
    val section: TextStyle,
    val cardTitle: TextStyle,
    val itemTitle: TextStyle,
    val body: TextStyle,
    val caption: TextStyle,
)

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
    onSecondaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val nodes = uiState.regions.map { it.toNodeUi() }
    val typeScale = rememberNodeSelectTypeScale()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedNodeId by rememberSaveable { mutableStateOf(uiState.selectedNodeId ?: nodes.firstOrNull()?.id.orEmpty()) }
    val filters = listOf(
        NodeFilter("recommend", "推荐"),
        NodeFilter("asia", "亚洲"),
        NodeFilter("europe", "欧洲"),
        NodeFilter("america", "美洲"),
        NodeFilter("lowdelay", "低延迟"),
    )

    val filteredNodes = remember(nodes, selectedTabIndex, uiState.searchQuery) {
        filterRegionNodes(nodes, selectedTabIndex, uiState.searchQuery)
    }
    LaunchedEffect(filteredNodes) {
        if (filteredNodes.none { it.id == selectedNodeId }) {
            selectedNodeId = filteredNodes.firstOrNull()?.id.orEmpty()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TechPageBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                HeaderSection(
                    eyebrow = "SMART ROUTING",
                    title = "选择节点",
                    subtitle = "",
                    typeScale = typeScale,
                )
            }

            item {
                SearchBarSection(
                    keyword = uiState.searchQuery,
                    onKeywordChange = onSearchChange,
                    onAiSortClick = onRefresh,
                    typeScale = typeScale,
                )
            }

            item {
                FilterSection(
                    filters = filters,
                    selectedFilterKey = filters.getOrNull(selectedTabIndex)?.key ?: "recommend",
                    onFilterSelected = { key ->
                        selectedTabIndex = filters.indexOfFirst { it.key == key }.coerceAtLeast(0)
                    },
                    typeScale = typeScale,
                )
            }

            item {
                SectionHeader(
                    title = "可用节点",
                    trailing = "${filteredNodes.count { it.isAllowed }} 可用",
                    typeScale = typeScale,
                )
            }

            item {
                if (uiState.screenState.hasError || filteredNodes.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = uiState.screenState.unavailableMessage
                                ?: uiState.screenState.errorMessage
                                ?: uiState.screenState.emptyMessage
                                ?: uiState.note.ifBlank { "节点数据来自业务后端真实目录。" },
                            modifier = Modifier.padding(16.dp),
                            color = SubtleTextColor,
                            style = typeScale.body,
                        )
                    }
                }
            }

            items(items = filteredNodes, key = { it.id }) { node ->
                NodeListItem(
                    node = node,
                    selected = node.id == selectedNodeId,
                    onClick = {
                        selectedNodeId = node.id
                        onPrimaryAction(node.lineCode, node.id)
                    },
                    typeScale = typeScale,
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    eyebrow: String,
    title: String,
    subtitle: String,
    typeScale: NodeSelectTypeScale,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = eyebrow,
            letterSpacing = 1.4.sp,
            color = Color(0xFF7D8FB3),
            style = typeScale.caption,
        )
        Text(
            text = title,
            color = TitleColor,
            style = typeScale.hero,
        )
        Text(
            text = subtitle,
            color = SubtleTextColor,
            style = typeScale.body,
        )
    }
}

@Composable
private fun SearchBarSection(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onAiSortClick: () -> Unit,
    typeScale: NodeSelectTypeScale,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = keyword,
            onValueChange = onKeywordChange,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            placeholder = {
                Text(text = "搜索国家 / 城市 / 用途", color = SubtleTextColor)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = SubtleTextColor,
                )
            },
        )

        Spacer(modifier = Modifier.width(10.dp))

        TextButton(
            onClick = onAiSortClick,
            modifier = Modifier
                .height(52.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.72f)),
        ) {
            Text(
                text = "AI排序",
                color = AccentBlue,
                style = typeScale.caption,
            )
        }
    }
}

@Composable
private fun FilterSection(
    filters: List<NodeFilter>,
    selectedFilterKey: String,
    onFilterSelected: (String) -> Unit,
    typeScale: NodeSelectTypeScale,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters, key = { it.key }) { filter ->
            FilterChip(
                selected = filter.key == selectedFilterKey,
                onClick = { onFilterSelected(filter.key) },
                label = { Text(text = filter.label, style = typeScale.caption) },
            )
        }
    }
}

@Composable
private fun BestNodeCard(
    node: NodeUiModel,
    onClick: () -> Unit,
    typeScale: NodeSelectTypeScale,
) {
    BoxWithConstraints {
        val compactScreen = maxWidth < 360.dp
        val ringSize = if (compactScreen) 72.dp else 92.dp

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "当前节点",
                        color = Color(0xFF94A3B8),
                        style = typeScale.caption,
                    )
                    Text(
                        text = "${node.city} ・ ${node.code}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = TitleColor,
                        style = if (compactScreen) typeScale.section else typeScale.hero,
                    )
                    Text(
                        text = node.description,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = SubtleTextColor,
                        style = typeScale.body,
                    )

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MetricBox(label = "LATENCY", value = node.latencyLabel)
                        MetricBox(label = "HOST", value = node.speedLabel)
                        MetricBox(label = "RISK", value = node.riskLabel)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier.size(ringSize),
                    contentAlignment = Alignment.Center,
                ) {
                    DefaultBestNodeVisual()
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .defaultMinSize(minWidth = 84.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.75f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = TitleColor,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    trailing: String,
    typeScale: NodeSelectTypeScale,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = title,
            color = TitleColor,
            style = typeScale.section,
        )
        Text(
            text = trailing,
            color = Color(0xFF94A3B8),
            style = typeScale.caption,
        )
    }
}

@Composable
private fun NodeListItem(
    node: NodeUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    typeScale: NodeSelectTypeScale,
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 20.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "${node.city} ・ ${node.code}",
                    color = TitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = typeScale.cardTitle,
                )
                Text(
                    text = node.description,
                    color = SubtleTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = typeScale.body,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = node.latencyLabel,
                    color = Color(0xFF6B7280),
                    style = typeScale.caption,
                )
                SpeedBar(progress = if (selected) 1f else 0.72f)
                Text(
                    text = node.speedLabel,
                    color = Color(0xFF94A3B8),
                    style = typeScale.caption,
                )
            }
        }
    }
}

@Composable
private fun SpeedBar(progress: Float) {
    Canvas(
        modifier = Modifier
            .width(96.dp)
            .height(8.dp),
    ) {
        drawRoundRect(
            color = Color(0xFFE5EDF8),
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(100f, 100f),
        )
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(AccentBlue, AccentCyan)),
            size = Size(width = size.width * progress.coerceIn(0f, 1f), height = size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(100f, 100f),
        )
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 24.dp,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.82f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color(0xFFF2FAFF).copy(alpha = 0.6f),
                        ),
                    ),
                ),
        ) {
            content()
        }
    }
}

@Composable
private fun TechPageBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(PageBgTop, PageBgBottom))),
    ) {
        BackgroundGlow(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp)
                .size(220.dp),
            color = AccentPurple.copy(alpha = 0.18f),
        )
        BackgroundGlow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
                .size(260.dp),
            color = AccentCyan.copy(alpha = 0.14f),
        )
        TechDots()
    }
}

@Composable
private fun BackgroundGlow(
    modifier: Modifier,
    color: Color,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color)
            .blur(36.dp),
    )
}

@Composable
private fun TechDots() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val points = listOf(
            Offset(size.width * 0.08f, size.height * 0.18f),
            Offset(size.width * 0.22f, size.height * 0.82f),
            Offset(size.width * 0.48f, size.height * 0.40f),
            Offset(size.width * 0.66f, size.height * 0.62f),
            Offset(size.width * 0.84f, size.height * 0.14f),
            Offset(size.width * 0.90f, size.height * 0.88f),
        )
        points.forEachIndexed { index, point ->
            drawCircle(
                color = if (index % 2 == 0) AccentCyan.copy(alpha = 0.24f) else AccentPurple.copy(alpha = 0.22f),
                radius = if (index % 2 == 0) 4f else 3f,
                center = point,
            )
        }
    }
}

@Composable
private fun DefaultBestNodeVisual() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val stroke = size.minDimension * 0.09f
        val outerRadius = size.minDimension / 2.2f
        val center = Offset(size.width / 2f, size.height / 2f)

        drawCircle(color = AccentCyan.copy(alpha = 0.10f), radius = outerRadius * 1.06f, center = center)
        drawCircle(color = Color.White.copy(alpha = 0.95f), radius = outerRadius * 0.54f, center = center)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AccentBlue.copy(alpha = 0.10f), Color.Transparent),
                center = center,
                radius = outerRadius * 1.25f,
            ),
            radius = outerRadius * 1.25f,
            center = center,
        )
        drawArc(
            brush = Brush.sweepGradient(listOf(AccentBlue, AccentCyan, AccentPurple, AccentBlue)),
            startAngle = -80f,
            sweepAngle = 300f,
            useCenter = false,
            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
            size = Size(outerRadius * 2, outerRadius * 2),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
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
                onSecondaryAction = {},
            )
        }
    }
}

@Composable
private fun rememberNodeSelectTypeScale(): NodeSelectTypeScale {
    val typography = MaterialTheme.typography
    return remember(typography) {
        NodeSelectTypeScale(
            hero = typography.headlineLarge,
            section = typography.headlineMedium,
            cardTitle = typography.titleLarge,
            itemTitle = typography.titleMedium,
            body = typography.bodyMedium,
            caption = typography.labelMedium,
        )
    }
}
