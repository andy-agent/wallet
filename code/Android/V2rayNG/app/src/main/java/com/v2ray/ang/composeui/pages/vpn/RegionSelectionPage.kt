package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.v2ray.ang.composeui.theme.TextPrimary
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
    data object Idle : RegionSelectionState()
    data object Loading : RegionSelectionState()
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
            _state.value = RegionSelectionState.Loaded(regions, regions.firstOrNull()?.id, "")
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
                        it.city.contains(currentState.searchQuery, ignoreCase = true) ||
                        it.countryCode.contains(currentState.searchQuery, ignoreCase = true)
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
    val selectedRegionId = loadedState?.selectedRegionId
    val searchQuery = loadedState?.searchQuery.orEmpty()
    val filteredRegions = viewModel.getFilteredRegions()
    val selectedRegion = filteredRegions.firstOrNull { it.id == selectedRegionId }
        ?: loadedState?.regions?.firstOrNull { it.id == selectedRegionId }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                VpnLightPrimaryButton(
                    text = "确认区域",
                    onClick = { selectedRegion?.let(onRegionSelected) },
                    enabled = selectedRegion != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = VpnPageHorizontalPadding, vertical = 16.dp),
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = VpnPageHorizontalPadding,
                    end = VpnPageHorizontalPadding,
                    top = VpnPageTopPadding,
                    bottom = VpnPageBottomPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    VpnCenterTopBar(
                        title = "路由区域控制",
                        onBack = onNavigateBack,
                        backIcon = Icons.Default.Close,
                    )
                }
                item {
                    VpnSearchField(
                        value = searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        placeholder = "搜索区域",
                        trailingIcon = if (searchQuery.isNotBlank()) Icons.Default.Clear else null,
                        onTrailingClick = if (searchQuery.isNotBlank()) {
                            { viewModel.onSearchQueryChange("") }
                        } else {
                            null
                        },
                    )
                }

                when (val current = state) {
                    is RegionSelectionState.Loading,
                    RegionSelectionState.Idle,
                    -> {
                        item {
                            VpnLoadingPanel(
                                title = "正在加载节点列表",
                                subtitle = "同步节点延迟、负载与可用线路。",
                            )
                        }
                    }

                    is RegionSelectionState.Error -> {
                        item {
                            VpnEmptyPanel(
                                title = "区域列表不可用",
                                subtitle = current.message,
                            )
                        }
                    }

                    is RegionSelectionState.Loaded -> {
                        item {
                            VpnGlassCard(accent = VpnOutline, contentPadding = PaddingValues(vertical = 6.dp)) {
                                filteredRegions.forEachIndexed { index, region ->
                                    RegionSelectorRow(
                                        region = region,
                                        isSelected = region.id == current.selectedRegionId,
                                        onClick = { viewModel.selectRegion(region.id) },
                                    )
                                    if (index != filteredRegions.lastIndex) {
                                        VpnListDivider()
                                    }
                                }
                            }
                        }
                        if (filteredRegions.isEmpty()) {
                            item {
                                VpnEmptyPanel(
                                    title = "没有匹配的区域",
                                    subtitle = "请更换关键词，或清空搜索恢复完整路由列表。",
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
private fun RegionSelectorRow(
    region: RegionDetail,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    VpnGroupRow(
        title = region.name,
        subtitle = if (region.isPremium) "${region.city} · Premium" else region.city,
        selected = isSelected,
        onClick = onClick,
        leading = {
            VpnCodeBadge(
                text = region.countryCode,
                backgroundColor = if (region.isPremium) VpnAccentSoft else VpnSurfaceStrong,
                contentColor = if (region.isPremium) VpnAccent else TextPrimary,
            )
        },
        trailing = {
            Text(
                text = "${region.latency}ms",
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) VpnAccent else TextPrimary,
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = VpnAccent,
                )
            }
        },
    )
}

@Preview
@Composable
private fun RegionSelectionPagePreview() {
    MaterialTheme {
        RegionSelectionPage()
    }
}
