package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.vpn.RegionSelectionProvider
import com.v2ray.ang.composeui.theme.Error
import com.v2ray.ang.composeui.theme.GlowBlue
import com.v2ray.ang.composeui.theme.Primary
import com.v2ray.ang.composeui.theme.Success
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.Warning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegionDetail(
    val id: String,
    val name: String,
    val countryCode: String,
    val city: String,
    val latency: Int,
    val load: Int,
    val isPremium: Boolean = false,
)

sealed class RegionSelectionState {
    object Idle : RegionSelectionState()
    object Loading : RegionSelectionState()
    data class Loaded(
        val regions: List<RegionDetail>,
        val selectedRegionId: String?,
        val searchQuery: String,
    ) : RegionSelectionState()
    data class Error(val message: String) : RegionSelectionState()
}

class RegionSelectionViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<RegionSelectionState>(RegionSelectionState.Idle)
    val state: StateFlow<RegionSelectionState> = _state

    init {
        loadRegions()
    }

    private fun loadRegions() {
        _state.value = RegionSelectionState.Loading
        viewModelScope.launch {
            val regions = RegionSelectionProvider.getRegions().map {
                RegionDetail(
                    id = it.id,
                    name = it.name,
                    countryCode = it.countryCode,
                    city = it.city,
                    latency = it.latency,
                    load = it.load,
                    isPremium = it.isPremium,
                )
            }
            _state.value = RegionSelectionState.Loaded(regions, null, "")
        }
    }

    fun selectRegion(regionId: String) {
        val currentState = _state.value
        if (currentState is RegionSelectionState.Loaded) {
            _state.value = currentState.copy(selectedRegionId = regionId)
        }
    }

    fun onSearchQueryChange(query: String) {
        val currentState = _state.value
        if (currentState is RegionSelectionState.Loaded) {
            _state.value = currentState.copy(searchQuery = query)
        }
    }

    fun getFilteredRegions(): List<RegionDetail> {
        val currentState = _state.value
        return if (currentState is RegionSelectionState.Loaded) {
            if (currentState.searchQuery.isBlank()) {
                currentState.regions
            } else {
                currentState.regions.filter {
                    it.name.contains(currentState.searchQuery, ignoreCase = true) ||
                        it.city.contains(currentState.searchQuery, ignoreCase = true)
                }
            }
        } else {
            emptyList()
        }
    }
}

@Composable
fun RegionSelectionPage(
    viewModel: RegionSelectionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onRegionSelected: (RegionDetail) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val loadedState = state as? RegionSelectionState.Loaded
    val searchQuery = loadedState?.searchQuery.orEmpty()
    val filteredRegions = viewModel.getFilteredRegions()
    val premiumRegions = filteredRegions.filter { it.isPremium }
    val standardRegions = filteredRegions.filterNot { it.isPremium }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = VpnPageHorizontalPadding,
                    end = VpnPageHorizontalPadding,
                    top = VpnPageTopPadding,
                    bottom = 32.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    VpnTopChrome(
                        title = "Regions",
                        subtitle = "Top search, premium nodes, and clean latency hierarchy for the VPN desk.",
                        onBack = onNavigateBack,
                    )
                }
                item {
                    VpnSearchField(
                        value = searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        placeholder = "Search region or city",
                        trailingIcon = if (searchQuery.isNotBlank()) Icons.Default.Clear else null,
                        onTrailingClick = if (searchQuery.isNotBlank()) {
                            { viewModel.onSearchQueryChange("") }
                        } else {
                            null
                        },
                    )
                }
                item {
                    VpnHeroCard(
                        eyebrow = "ROUTE MARKET",
                        title = "Choose a server region with the clearest latency-to-load balance",
                        subtitle = "保留现有 RegionSelectionProvider 数据，只重构为 Bitget 风格搜索和推荐节点层级。",
                        accent = GlowBlue,
                        metrics = listOf(
                            VpnHeroMetric("Premium", premiumRegions.size.toString()),
                            VpnHeroMetric("All Nodes", filteredRegions.size.toString()),
                            VpnHeroMetric("Query", if (searchQuery.isBlank()) "All" else searchQuery),
                        ),
                    )
                }

                when (val current = state) {
                    is RegionSelectionState.Loading,
                    RegionSelectionState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "Loading region desk",
                                subtitle = "正在聚合可选节点与区域标签。",
                            )
                        }
                    }

                    is RegionSelectionState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "Region list unavailable",
                                subtitle = current.message,
                            )
                        }
                    }

                    is RegionSelectionState.Loaded -> {
                        if (premiumRegions.isNotEmpty()) {
                            item {
                                VpnSectionHeading(
                                    title = "Premium Routes",
                                    subtitle = "Prioritized nodes surfaced like a featured market panel.",
                                )
                            }
                            items(premiumRegions, key = { it.id }) { region ->
                                RegionDeskCard(
                                    region = region,
                                    isSelected = current.selectedRegionId == region.id,
                                    onSelect = {
                                        viewModel.selectRegion(region.id)
                                        onRegionSelected(region)
                                    },
                                )
                            }
                        }

                        item {
                            VpnSectionHeading(
                                title = "All Regions",
                                subtitle = "Secondary inventory keeps detail rows clean and scannable.",
                            )
                        }
                        if (standardRegions.isEmpty()) {
                            item {
                                VpnEmptyPanel(
                                    title = "No region matched",
                                    subtitle = "换一个关键词，或者清空搜索恢复完整节点列表。",
                                )
                            }
                        } else {
                            items(standardRegions, key = { it.id }) { region ->
                                RegionDeskCard(
                                    region = region,
                                    isSelected = current.selectedRegionId == region.id,
                                    onSelect = {
                                        viewModel.selectRegion(region.id)
                                        onRegionSelected(region)
                                    },
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
private fun RegionDeskCard(
    region: RegionDetail,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    val latencyColor = when {
        region.latency <= 40 -> Success
        region.latency <= 80 -> Warning
        else -> Error
    }
    val loadColor = when {
        region.load <= 45 -> Success
        region.load <= 75 -> Warning
        else -> Error
    }
    val accent = when {
        isSelected -> Primary
        region.isPremium -> Warning
        else -> GlowBlue
    }

    VpnGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        accent = accent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = accent.copy(alpha = 0.16f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = region.countryCode,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = accent,
                        )
                    }
                }
                Box {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = region.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                        )
                        Text(
                            text = region.city,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                }
            }
            if (region.isPremium) {
                VpnStatusChip(
                    text = "PREMIUM",
                    containerColor = Warning.copy(alpha = 0.16f),
                    contentColor = Warning,
                )
            } else if (isSelected) {
                VpnStatusChip(text = "SELECTED")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "Latency",
                value = "${region.latency}ms",
            )
            VpnMetricPill(
                modifier = Modifier.weight(1f),
                label = "Load",
                value = "${region.load}%",
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VpnStatusChip(
                text = if (region.latency <= 50) "Fast Lane" else "Stable Route",
                containerColor = latencyColor.copy(alpha = 0.15f),
                contentColor = latencyColor,
            )
            VpnStatusChip(
                text = if (region.load <= 60) "Light Load" else "Busy Node",
                containerColor = loadColor.copy(alpha = 0.15f),
                contentColor = loadColor,
            )
            if (region.isPremium) {
                VpnStatusChip(
                    text = "Featured",
                    containerColor = GlowBlue.copy(alpha = 0.18f),
                    contentColor = GlowBlue,
                )
            }
        }

        VpnPrimaryButton(
            text = if (isSelected) "Current Region" else "Select Region",
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun RegionSelectionPagePreview() {
    MaterialTheme {
        RegionSelectionPage()
    }
}
