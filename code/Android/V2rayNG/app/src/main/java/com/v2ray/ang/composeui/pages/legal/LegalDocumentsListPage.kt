package com.v2ray.ang.composeui.pages.legal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.v2ray.ang.composeui.bridge.legal.LegalBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private val LegalBg = Color(0xFF0A101A)
private val LegalSurface = Color(0xFF121A28)
private val LegalSurfaceSoft = Color(0xFF1A2638)
private val LegalPrimary = Color(0xFF00E5A8)
private val LegalPrimarySoft = Color(0x3313F1B2)
private val LegalText = Color(0xFFEAF0F7)
private val LegalMuted = Color(0xFF8D9AB0)
private val LegalDanger = Color(0xFFF45B69)

/**
 * 法务文档
 */
data class LegalDocument(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val category: String,
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
class LegalDocumentsListViewModel : ViewModel() {
    private val legalBridgeRepository = LegalBridgeRepository()
    private val _state = MutableStateFlow<LegalDocumentsListState>(LegalDocumentsListState.Idle)
    val state: StateFlow<LegalDocumentsListState> = _state

    init {
        loadDocuments()
    }

    private fun loadDocuments() {
        val documents = legalBridgeRepository.listDocuments().map {
            LegalDocument(
                id = it.id,
                title = it.title,
                description = it.description,
                icon = when (it.id) {
                    "terms" -> Icons.Default.Description
                    "privacy" -> Icons.Default.Security
                    "refund" -> Icons.Default.Replay
                    "affiliate" -> Icons.Default.People
                    else -> Icons.Default.Cookie
                },
                category = legalCategoryFor(it.id),
                lastUpdated = it.lastUpdated
            )
        }
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
        containerColor = LegalBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile / Legal",
                        color = LegalText,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LegalText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LegalBg,
                    titleContentColor = LegalText,
                    navigationIconContentColor = LegalText
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LegalBg)
                .padding(paddingValues)
        ) {
            when (val current = state) {
                is LegalDocumentsListState.Loaded -> {
                    LegalDocumentsContent(
                        documents = current.documents,
                        onDocumentClick = onDocumentClick
                    )
                }

                is LegalDocumentsListState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LegalPrimary)
                    }
                }

                is LegalDocumentsListState.Error -> {
                    LegalErrorView(message = current.message)
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun LegalDocumentsContent(
    documents: List<LegalDocument>,
    onDocumentClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            LegalHeroCard(count = documents.size)
        }
        items(documents) { document ->
            LegalDocumentItem(
                document = document,
                onClick = { onDocumentClick(document.id) }
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun LegalHeroCard(count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = LegalSurface),
        border = BorderStroke(1.dp, LegalPrimarySoft)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0x3322F5C6), Color.Transparent, Color(0x202D3E58))
                    )
                )
                .padding(18.dp)
        ) {
            Text(text = "Legal & Compliance", color = LegalText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "平台协议、隐私、退款和推广规则统一归档，保持 Profile 模块下的合规入口一致。",
                color = LegalMuted,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = LegalPrimarySoft
            ) {
                Text(
                    text = "共 $count 份文档",
                    color = LegalPrimary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LegalSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LegalSurfaceSoft,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = document.icon,
                        contentDescription = document.title,
                        tint = LegalPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LegalText
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = document.description,
                    fontSize = 12.sp,
                    color = LegalMuted,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = LegalPrimarySoft
                    ) {
                        Text(
                            text = document.category,
                            fontSize = 10.sp,
                            color = LegalPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = "更新于 ${document.lastUpdated}",
                        fontSize = 10.sp,
                        color = LegalMuted
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = LegalMuted
            )
        }
    }
}

@Composable
private fun LegalErrorView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(54.dp),
            tint = LegalDanger
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "加载失败",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = LegalDanger
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            fontSize = 12.sp,
            color = LegalMuted
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

private fun legalCategoryFor(documentId: String): String = when (documentId) {
    "terms" -> "核心协议"
    "privacy" -> "隐私与数据"
    "refund" -> "退款说明"
    "affiliate" -> "推广计划"
    else -> "Cookie 与偏好"
}
