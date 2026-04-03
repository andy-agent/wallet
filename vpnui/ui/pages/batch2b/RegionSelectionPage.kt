package com.cryptovpn.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.*

// Region Data Model
data class RegionInfo(
    val id: String,
    val flag: String,
    val name: String,
    val location: String,
    val latency: Int,
    val isPremium: Boolean = false,
    val isMaintenance: Boolean = false
)

// Continent Filter
enum class ContinentFilter(val displayName: String) {
    ALL("全部"),
    ASIA("亚洲"),
    EUROPE("欧洲"),
    AMERICAS("美洲"),
    OTHERS("其他")
}

// Region Selection State
sealed class RegionSelectionState {
    object Loading : RegionSelectionState()
    data class Loaded(
        val regions: List<RegionInfo>,
        val selectedRegionId: String? = null,
        val searchQuery: String = "",
        val selectedContinent: ContinentFilter = ContinentFilter.ALL
    ) : RegionSelectionState()
    data class Error(val message: String) : RegionSelectionState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionSelectionPage(
    state: RegionSelectionState = RegionSelectionState.Loading,
    onBackClick: () -> Unit = {},
    onRegionSelect: (RegionInfo) -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onContinentFilterChange: (ContinentFilter) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("选择区域", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundPrimary)
            )
        },
        bottomBar = {
            if (state is RegionSelectionState.Loaded && state.selectedRegionId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundPrimary)
                        .padding(16.dp)
                ) {
                    PrimaryButton(
                        text = "确认选择",
                        onClick = onConfirmClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            SearchInputField(
                value = if (state is RegionSelectionState.Loaded) state.searchQuery else "",
                onValueChange = onSearchQueryChange,
                placeholder = "搜索国家或地区...",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Continent Filter Chips
            if (state is RegionSelectionState.Loaded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ContinentFilter.values().forEach { continent ->
                        FilterChip(
                            selected = state.selectedContinent == continent,
                            onClick = { onContinentFilterChange(continent) },
                            label = { Text(continent.displayName, fontSize = 14.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.Transparent,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = BorderDefault,
                                selectedBorderColor = Primary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Region List
            when (state) {
                is RegionSelectionState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is RegionSelectionState.Error -> {
                    ErrorState(
                        title = "加载失败",
                        message = (state as RegionSelectionState.Error).message,
                        onRetry = { }
                    )
                }
                is RegionSelectionState.Loaded -> {
                    val filteredRegions = state.regions.filter { region ->
                        val matchesSearch = state.searchQuery.isEmpty() ||
                            region.name.contains(state.searchQuery, ignoreCase = true) ||
                            region.location.contains(state.searchQuery, ignoreCase = true)
                        val matchesContinent = when (state.selectedContinent) {
                            ContinentFilter.ALL -> true
                            ContinentFilter.ASIA -> region.id.startsWith("AS-")
                            ContinentFilter.EUROPE -> region.id.startsWith("EU-")
                            ContinentFilter.AMERICAS -> region.id.startsWith("AM-")
                            ContinentFilter.OTHERS -> region.id.startsWith("OT-")
                        }
                        matchesSearch && matchesContinent
                    }

                    // Premium Regions Section
                    val premiumRegions = filteredRegions.filter { it.isPremium }
                    if (premiumRegions.isNotEmpty()) {
                        Text(
                            text = "⭐ 高级区域",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        premiumRegions.forEach { region ->
                            RegionListItem(
                                region = region,
                                isSelected = state.selectedRegionId == region.id,
                                onClick = { onRegionSelect(region) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Regular Regions Section
                    val regularRegions = filteredRegions.filter { !it.isPremium }
                    if (regularRegions.isNotEmpty()) {
                        Text(
                            text = "普通区域",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(regularRegions) { region ->
                                RegionListItem(
                                    region = region,
                                    isSelected = state.selectedRegionId == region.id,
                                    onClick = { onRegionSelect(region) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegionListItem(
    region: RegionInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val latencyColor = when {
        region.latency < 100 -> Success
        region.latency < 200 -> Warning
        else -> Error
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !region.isMaintenance, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary.copy(alpha = 0.2f) else BackgroundSecondary
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, Primary)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag
            Text(
                text = region.flag,
                fontSize = 24.sp,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Region Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = region.name,
                    color = if (region.isMaintenance) TextDisabled else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = region.location,
                    color = TextTertiary,
                    fontSize = 12.sp
                )
            }

            // Latency
            if (region.isMaintenance) {
                Text(
                    text = "维护中",
                    color = TextDisabled,
                    fontSize = 12.sp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(latencyColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${region.latency}ms",
                        color = latencyColor,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Selection Indicator
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "已选中",
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Preview
@Preview
@Composable
fun RegionSelectionPagePreview() {
    val sampleRegions = listOf(
        RegionInfo("AM-US-LA", "🇺🇸", "美国", "洛杉矶", 45, isPremium = true),
        RegionInfo("AS-JP-TK", "🇯🇵", "日本", "东京", 52, isPremium = true),
        RegionInfo("EU-UK-LN", "🇬🇧", "英国", "伦敦", 68, isPremium = true),
        RegionInfo("AS-SG", "🇸🇬", "新加坡", "", 38),
        RegionInfo("EU-DE-FR", "🇩🇪", "德国", "法兰克福", 72),
        RegionInfo("EU-FR-PA", "🇫🇷", "法国", "巴黎", 75),
        RegionInfo("AM-CA", "🇨🇦", "加拿大", "", 89),
        RegionInfo("OC-AU", "🇦🇺", "澳大利亚", "", 95),
        RegionInfo("AS-KR-SL", "🇰🇷", "韩国", "首尔", 48)
    )

    CryptoVPNTheme {
        RegionSelectionPage(
            state = RegionSelectionState.Loaded(
                regions = sampleRegions,
                selectedRegionId = "AM-US-LA"
            )
        )
    }
}

@Preview
@Composable
fun RegionSelectionPageLoadingPreview() {
    CryptoVPNTheme {
        RegionSelectionPage(state = RegionSelectionState.Loading)
    }
}