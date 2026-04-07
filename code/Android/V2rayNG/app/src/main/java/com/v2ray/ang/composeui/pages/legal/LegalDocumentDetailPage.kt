package com.v2ray.ang.composeui.pages.legal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.v2ray.ang.composeui.bridge.legal.LegalBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private val LegalDetailBg = Color(0xFF0A101A)
private val LegalDetailSurface = Color(0xFF121A28)
private val LegalDetailSurfaceSoft = Color(0xFF1A2638)
private val LegalDetailPrimary = Color(0xFF00E5A8)
private val LegalDetailPrimarySoft = Color(0x3313F1B2)
private val LegalDetailText = Color(0xFFEAF0F7)
private val LegalDetailMuted = Color(0xFF8D9AB0)
private val LegalDetailDanger = Color(0xFFF45B69)

/**
 * 法务文档详情
 */
data class LegalDocumentDetail(
    val id: String,
    val title: String,
    val lastUpdated: String,
    val content: String
)

/**
 * 法务文档详情页状态
 */
sealed class LegalDocumentDetailState {
    object Idle : LegalDocumentDetailState()
    object Loading : LegalDocumentDetailState()
    data class Loaded(val document: LegalDocumentDetail) : LegalDocumentDetailState()
    data class Error(val message: String) : LegalDocumentDetailState()
}

/**
 * 法务文档详情页ViewModel
 */
class LegalDocumentDetailViewModel : ViewModel() {
    private val legalBridgeRepository = LegalBridgeRepository()
    private val _state = MutableStateFlow<LegalDocumentDetailState>(LegalDocumentDetailState.Idle)
    val state: StateFlow<LegalDocumentDetailState> = _state

    fun loadDocument(documentId: String) {
        val doc = legalBridgeRepository.getDocument(documentId)
        _state.value = if (doc != null) {
            LegalDocumentDetailState.Loaded(
                LegalDocumentDetail(
                    id = doc.id,
                    title = doc.title,
                    lastUpdated = doc.lastUpdated,
                    content = doc.content
                )
            )
        } else {
            LegalDocumentDetailState.Error("文档不存在")
        }
    }
}

/**
 * 法务文档详情页
 * 显示法务文档的完整内容
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentDetailPage(
    viewModel: LegalDocumentDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    documentId: String = "terms",
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }

    Scaffold(
        containerColor = LegalDetailBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (state) {
                            is LegalDocumentDetailState.Loaded -> (state as LegalDocumentDetailState.Loaded).document.title
                            else -> "文档详情"
                        },
                        color = LegalDetailText,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LegalDetailText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LegalDetailBg,
                    titleContentColor = LegalDetailText,
                    navigationIconContentColor = LegalDetailText
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LegalDetailBg)
                .padding(paddingValues)
        ) {
            when (val current = state) {
                is LegalDocumentDetailState.Loaded -> {
                    DocumentContent(document = current.document)
                }

                is LegalDocumentDetailState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LegalDetailPrimary)
                    }
                }

                is LegalDocumentDetailState.Error -> {
                    LegalDetailErrorView(message = current.message)
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun DocumentContent(document: LegalDocumentDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LegalDetailSurface),
            border = BorderStroke(1.dp, LegalDetailPrimarySoft)
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
                Text(
                    text = document.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = LegalDetailText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = LegalDetailSurfaceSoft
                ) {
                    Text(
                        text = "最后更新 ${document.lastUpdated}",
                        fontSize = 11.sp,
                        color = LegalDetailMuted,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LegalDetailSurface)
        ) {
            Text(
                text = document.content,
                fontSize = 14.sp,
                color = LegalDetailText,
                lineHeight = 24.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun LegalDetailErrorView(message: String) {
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
            tint = LegalDetailDanger
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "加载失败",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = LegalDetailDanger
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            fontSize = 12.sp,
            color = LegalDetailMuted
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LegalDocumentDetailPagePreview() {
    MaterialTheme {
        LegalDocumentDetailPage()
    }
}
