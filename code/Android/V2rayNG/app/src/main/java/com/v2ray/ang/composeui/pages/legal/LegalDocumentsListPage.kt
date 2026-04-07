package com.v2ray.ang.composeui.pages.legal

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.v2ray.ang.composeui.bridge.legal.LegalBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal val LegalPageBackground = Color(0xFFF5F7FA)
internal val LegalCardBackground = Color.White
internal val LegalAccent = Color(0xFF00C2A8)
internal val LegalAccentDeep = Color(0xFF0E8E7F)
internal val LegalTextSecondary = Color(0xFF667085)

data class LegalDocument(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val lastUpdated: String
)

sealed class LegalDocumentsListState {
    data object Idle : LegalDocumentsListState()
    data object Loading : LegalDocumentsListState()
    data class Loaded(val documents: List<LegalDocument>) : LegalDocumentsListState()
    data class Error(val message: String) : LegalDocumentsListState()
}

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
                lastUpdated = it.lastUpdated
            )
        }
        _state.value = LegalDocumentsListState.Loaded(documents)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentsListPage(
    viewModel: LegalDocumentsListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onDocumentClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = LegalPageBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Legal",
                        fontWeight = FontWeight.SemiBold
                    )
                },
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
                .background(LegalPageBackground)
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is LegalDocumentsListState.Loaded -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            LegalHeroCard(documentCount = currentState.documents.size)
                        }
                        item {
                            LegalSectionTitle(
                                title = "法律文档",
                                caption = "账户使用、隐私处理与退款等条款"
                            )
                        }
                        items(currentState.documents) { document ->
                            LegalDocumentItem(
                                document = document,
                                onClick = { onDocumentClick(document.id) }
                            )
                        }
                    }
                }

                LegalDocumentsListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LegalAccent)
                    }
                }

                is LegalDocumentsListState.Error -> ErrorView(currentState.message)
                LegalDocumentsListState.Idle -> Unit
            }
        }
    }
}

@Composable
private fun LegalHeroCard(documentCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = LegalCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF101828))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color.White.copy(alpha = 0.14f)
            ) {
                Text(
                    text = "Legal Center",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "统一查看协议、隐私与退款规则",
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "当前共 $documentCount 份文档，建议在购买服务或提交退款前先阅读对应条款。",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color.White.copy(alpha = 0.72f)
            )
        }
    }
}

@Composable
private fun LegalSectionTitle(title: String, caption: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF101828)
        )
        Text(
            text = caption,
            fontSize = 13.sp,
            color = LegalTextSecondary
        )
    }
}

@Composable
private fun LegalDocumentItem(document: LegalDocument, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LegalCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = LegalAccent.copy(alpha = 0.12f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = document.icon,
                        contentDescription = document.title,
                        tint = LegalAccentDeep,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = document.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF101828)
                )
                Text(
                    text = document.description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = LegalTextSecondary
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFFF2F4F7)
                ) {
                    Text(
                        text = "更新于 ${document.lastUpdated}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 11.sp,
                        color = LegalTextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = LegalTextSecondary
            )
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "无法加载 Legal 页面",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF101828)
                )
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = LegalTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LegalDocumentsListPagePreview() {
    MaterialTheme {
        LegalDocumentsListPage()
    }
}
