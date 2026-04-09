package com.v2ray.ang.composeui.pages.legal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.v2ray.ang.composeui.bridge.legal.LegalBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LegalDocument(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val lastUpdated: String,
)

sealed class LegalDocumentsListState {
    data object Loading : LegalDocumentsListState()
    data class Loaded(val documents: List<LegalDocument>) : LegalDocumentsListState()
    data class Error(val message: String) : LegalDocumentsListState()
}

class LegalDocumentsListViewModel : ViewModel() {
    private val legalBridgeRepository = LegalBridgeRepository()
    private val _state = MutableStateFlow<LegalDocumentsListState>(LegalDocumentsListState.Loading)
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
                    "terms" -> Icons.Default.Gavel
                    "privacy" -> Icons.Default.PrivacyTip
                    "refund" -> Icons.Default.Replay
                    "affiliate" -> Icons.Default.Security
                    "cookies" -> Icons.Default.Cookie
                    else -> Icons.Default.Description
                },
                lastUpdated = it.lastUpdated,
            )
        }
        _state.value = if (documents.isEmpty()) {
            LegalDocumentsListState.Error("未找到法务文档")
        } else {
            LegalDocumentsListState.Loaded(documents)
        }
    }
}

@Composable
fun LegalDocumentsListPage(
    viewModel: LegalDocumentsListViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onDocumentClick: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LegalPageScaffold(
        topBar = {
            LegalTopBar(
                title = "法律与协议",
                subtitle = "条款、隐私处理与退款说明",
                onNavigateBack = onNavigateBack,
            )
        },
    ) { paddingValues ->
        when (val currentState = state) {
            LegalDocumentsListState.Loading -> Unit
            is LegalDocumentsListState.Error -> {
                LegalStatusView(
                    title = "无法加载 Legal 页面",
                    message = currentState.message,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is LegalDocumentsListState.Loaded -> {
                LegalDocumentsContent(
                    state = currentState,
                    paddingValues = paddingValues,
                    onDocumentClick = onDocumentClick,
                )
            }
        }
    }
}

@Composable
private fun LegalDocumentsContent(
    state: LegalDocumentsListState.Loaded,
    paddingValues: PaddingValues,
    onDocumentClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 8.dp,
            bottom = 28.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            LegalHighlightCard {
                LegalBadge(
                    text = "文档中心",
                    containerColor = LegalAccent.copy(alpha = 0.1f),
                    contentColor = LegalAccent,
                )
                Text(
                    text = "在一个入口查看所有协议与政策",
                    color = LegalTextPrimary,
                    fontSize = 28.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "当前共 ${state.documents.size} 份文档，先看摘要再进入正文，让白底页面里的阅读节奏更连贯。",
                    color = LegalTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
        }

        item {
            LegalCard {
                LegalSectionTitle(
                    title = "文档列表",
                    subtitle = "同类内容保持在连续卡片里，减少列表页的视觉跳跃。",
                )
                state.documents.forEachIndexed { index, document ->
                    LegalDocumentRow(
                        document = document,
                        onClick = { onDocumentClick(document.id) },
                    )
                    if (index != state.documents.lastIndex) {
                        LegalListDivider()
                    }
                }
            }
        }

        item {
            LegalCard {
                LegalSectionTitle(
                    title = "阅读提示",
                    subtitle = "把必要说明前置，但不过度强调，保持法律内容本身是主角。",
                )
                listOf(
                    "购买订阅、申请退款和参与推广前，建议先阅读对应规则。",
                    "文档内容来自当前本地 Provider，页面只重构容器与阅读节奏，不改变原始条款文本。",
                    "列表、提示与详情会共用同一套浅色容器与分隔规则，减少切换时的不适感。",
                ).forEach {
                    Text(
                        text = it,
                        color = LegalTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun LegalDocumentRow(
    document: LegalDocument,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = LegalCardRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, LegalBorder),
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = document.icon,
                    contentDescription = null,
                    tint = LegalAccentDeep,
                )
            }
        }

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = document.title,
                color = LegalTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            Text(
                text = document.description,
                color = LegalTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
            LegalBadge(
                text = "更新于 ${document.lastUpdated}",
                containerColor = LegalPageBackground,
                contentColor = LegalTextSecondary,
            )
        }

        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = LegalTextSecondary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LegalDocumentsListPagePreview() {
    MaterialTheme {
        LegalBitgetBackground {
            LegalDocumentsContent(
                state = LegalDocumentsListState.Loaded(
                    documents = listOf(
                        LegalDocument(
                            id = "terms",
                            title = "用户协议",
                            description = "使用服务的条款和条件",
                            icon = Icons.Default.Gavel,
                            lastUpdated = "2026-04-01",
                        ),
                        LegalDocument(
                            id = "privacy",
                            title = "隐私政策",
                            description = "数据收集、使用和保护说明",
                            icon = Icons.Default.PrivacyTip,
                            lastUpdated = "2026-04-01",
                        ),
                    ),
                ),
                paddingValues = PaddingValues(),
                onDocumentClick = {},
            )
        }
    }
}
