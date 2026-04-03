package com.cryptovpn.ui.pages.vpn

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 区域详细信息
 */
data class RegionDetail(
    val id: String,
    val name: String,
    val countryCode: String,
    val city: String,
    val latency: Int,
    val load: Int,  // 负载百分比
    val isPremium: Boolean = false
)

/**
 * 区域选择页状态
 */
sealed class RegionSelectionState {
    object Idle : RegionSelectionState()
    object Loading : RegionSelectionState()
    data class Loaded(
        val regions: List<RegionDetail>,
        val selectedRegionId: String?,
        val searchQuery: String
    ) : RegionSelectionState()
    data class Error(val message: String) : RegionSelectionState()
}

/**
 * 区域选择页ViewModel
 */
@HiltViewModel
class RegionSelectionViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<RegionSelectionState>(RegionSelectionState.Idle)
    val state: StateFlow<RegionSelectionState> = _state

    init {
        loadRegions()
    }

    private fun loadRegions() {
        val regions = listOf(
            RegionDetail("us-la", "美国 - 洛杉矶", "US", "洛杉矶", 45, 35),
            RegionDetail("us-ny", "美国 - 纽约", "US", "纽约", 68, 42),
            RegionDetail("us-sf", "美国 - 旧金山", "US", "旧金山", 52, 28, true),
            RegionDetail("uk-lon", "英国 - 伦敦", "UK", "伦敦", 85, 55),
            RegionDetail("de-ber", "德国 - 柏林", "DE", "柏林", 78, 48),
            RegionDetail("jp-tok", "日本 - 东京", "JP", "东京", 35, 62, true),
            RegionDetail("sg-sin", "新加坡", "SG", "新加坡", 25, 70),
            RegionDetail("hk-hk", "中国香港", "HK", "香港", 15, 85),
            RegionDetail("kr-seo", "韩国 - 首尔", "KR", "首尔", 42, 38),
            RegionDetail("au-syd", "澳大利亚 - 悉尼", "AU", "悉尼", 95, 25),
            RegionDetail("ca-tor", "加拿大 - 多伦多", "CA", "多伦多", 72, 33),
            RegionDetail("fr-par", "法国 - 巴黎", "FR", "巴黎", 80, 45)
        )
        _state.value = RegionSelectionState.Loaded(regions, null, "")
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

/**
 * 区域选择页
 * 显示可用区域列表和延迟信息
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionSelectionPage(
    viewModel: RegionSelectionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onRegionSelected: (RegionDetail) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val searchQuery = if (state is RegionSelectionState.Loaded) {
        (state as RegionSelectionState.Loaded).searchQuery
    } else ""

    val filteredRegions = viewModel.getFilteredRegions()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("选择区域") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索框
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 区域列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 推荐区域
                val recommendedRegions = filteredRegions.filter { it.isPremium }
                if (recommendedRegions.isNotEmpty()) {
                    item {
                        SectionHeader(title = "推荐节点")
                    }
                    items(recommendedRegions) { region ->
                        RegionItem(
                            region = region,
                            isSelected = (state as? RegionSelectionState.Loaded)?.selectedRegionId == region.id,
                            onSelect = {
                                viewModel.selectRegion(region.id)
                                onRegionSelected(region)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // 所有区域
                item {
                    SectionHeader(title = "所有节点")
                }
                items(filteredRegions.filter { !it.isPremium }) { region ->
                    RegionItem(
                        region = region,
                        isSelected = (state as? RegionSelectionState.Loaded)?.selectedRegionId == region.id,
                        onSelect = {
                            viewModel.selectRegion(region.id)
                            onRegionSelected(region)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("搜索区域或城市") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun RegionItem(
    region: RegionDetail,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val latencyColor = when {
        region.latency < 50 -> Color(0xFF22C55E)
        region.latency < 100 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    val loadColor = when {
        region.load < 50 -> Color(0xFF22C55E)
        region.load < 80 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onSelect),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 国家代码图标
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = region.countryCode,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 区域信息
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = region.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (region.isPremium) {
                        Spacer(modifier = Modifier.width(8.dp))
                        PremiumBadge()
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "负载: ${region.load}%",
                    fontSize = 12.sp,
                    color = loadColor
                )
            }

            // 延迟
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(latencyColor, MaterialTheme.shapes.small)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${region.latency}ms",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = latencyColor
                    )
                }
            }

            // 选中标记
            if (isSelected) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun PremiumBadge() {
    Surface(
        color = Color(0xFFF59E0B),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "VIP",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegionSelectionPagePreview() {
    MaterialTheme {
        RegionSelectionPage()
    }
}
