package com.cryptovpn.ui.pages.legal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
 * 法务文档
 */
data class LegalDocument(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val lastUpdated: String
)

/**
 * 法务文档列表页状态
 */
sealed class LegalDocumentsListState {
    object Idle : LegalDocumentsListState()
    object Loading : LegalDocumentsListState()
    data class Loaded(val documents: List<LegalDocument>) : LegalDocumentsListState()
    data class Error(val message: String) : LegalDocumentsListState()
}

/**
 * 法务文档列表页ViewModel
 */
@HiltViewModel
class LegalDocumentsListViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<LegalDocumentsListState>(LegalDocumentsListState.Idle)
    val state: StateFlow<LegalDocumentsListState> = _state

    init {
        loadDocuments()
    }

    private fun loadDocuments() {
        val documents = listOf(
            LegalDocument(
                id = "terms",
                title = "用户协议",
                description = "使用CryptoVPN服务的条款和条件",
                icon = Icons.Default.Description,
                lastUpdated = "2024-01-01"
            ),
            LegalDocument(
                id = "privacy",
                title = "隐私政策",
                description = "我们如何收集、使用和保护您的个人信息",
                icon = Icons.Default.Security,
                lastUpdated = "2024-01-01"
            ),
            LegalDocument(
                id = "refund",
                title = "退款政策",
                description = "关于订单退款的规则和流程",
                icon = Icons.Default.Replay,
                lastUpdated = "2024-01-01"
            ),
            LegalDocument(
                id = "affiliate",
                title = "推广协议",
                description = "邀请推广计划的规则和佣金说明",
                icon = Icons.Default.People,
                lastUpdated = "2024-01-01"
            ),
            LegalDocument(
                id = "cookies",
                title = "Cookie政策",
                description = "我们如何使用Cookie和类似技术",
                icon = Icons.Default.Cookie,
                lastUpdated = "2024-01-01"
            )
        )
        
        _state.value = LegalDocumentsListState.Loaded(documents)
    }
}

/**
 * 法务文档列表页
 * 显示所有法务文档的入口
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentsListPage(
    viewModel: LegalDocumentsListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onDocumentClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("法务文档") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is LegalDocumentsListState.Loaded -> {
                    val documents = (state as LegalDocumentsListState.Loaded).documents
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(documents) { document ->
                            LegalDocumentItem(
                                document = document,
                                onClick = { onDocumentClick(document.id) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
                is LegalDocumentsListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is LegalDocumentsListState.Error -> {
                    ErrorView(message = (state as LegalDocumentsListState.Error).message)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun LegalDocumentItem(
    document: LegalDocument,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = document.icon,
                        contentDescription = document.title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 文档信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = document.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "更新于: ${document.lastUpdated}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // 箭头
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "加载失败",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LegalDocumentsListPagePreview() {
    MaterialTheme {
        LegalDocumentsListPage()
    }
}
