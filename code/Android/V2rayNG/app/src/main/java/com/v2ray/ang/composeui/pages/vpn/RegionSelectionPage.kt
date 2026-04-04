package com.v2ray.ang.composeui.pages.vpn

import android.app.Application
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.vpn.RegionSelectionProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
                    isPremium = it.isPremium
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

            when (state) {
                is RegionSelectionState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is RegionSelectionState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = (state as RegionSelectionState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
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
