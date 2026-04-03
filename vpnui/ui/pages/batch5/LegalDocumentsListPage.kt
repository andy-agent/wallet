package com.cryptovpn.ui.pages.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.BackgroundDark
import com.cryptovpn.ui.theme.BackgroundMedium
import com.cryptovpn.ui.theme.BackgroundLight
import com.cryptovpn.ui.theme.PrimaryBlue
import com.cryptovpn.ui.theme.SuccessGreen
import com.cryptovpn.ui.theme.WarningYellow
import com.cryptovpn.ui.theme.ErrorRed
import com.cryptovpn.ui.theme.TextPrimary
import com.cryptovpn.ui.theme.TextSecondary
import com.cryptovpn.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 法务文档列表页
 * 展示所有法务相关文档
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentsListPage(
    viewModel: LegalDocumentsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onDocumentClick: (LegalDocument) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "法务文档",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is LegalDocumentsState.Loading -> {
                    LoadingContent()
                }
                is LegalDocumentsState.Loaded -> {
                    DocumentsList(
                        documents = (state as LegalDocumentsState.Loaded).documents,
                        onDocumentClick = onDocumentClick
                    )
                }
                is LegalDocumentsState.Error -> {
                    ErrorContent(
                        message = (state as LegalDocumentsState.Error).message,
                        onRetry = { viewModel.loadDocuments() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryBlue)
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = ErrorRed,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "加载失败",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        androidx.compose.material3.Button(
            onClick = onRetry,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("重新加载")
        }
    }
}

@Composable
private fun DocumentsList(
    documents: List<LegalDocument>,
    onDocumentClick: (LegalDocument) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "请仔细阅读以下文档，了解我们的服务条款和隐私政策",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        items(documents) { document ->
            DocumentItem(
                document = document,
                onClick = { onDocumentClick(document) }
            )
            
            if (document != documents.last()) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun DocumentItem(
    document: LegalDocument,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 文档图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = document.type.iconColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = document.type.icon,
                    contentDescription = null,
                    tint = document.type.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = document.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "版本 ${document.version}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                color = TextTertiary,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "更新于 ${dateFormat.format(document.updateDate)}",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                }
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun LegalDocumentsListPagePreview() {
    MaterialTheme {
        val sampleDocuments = listOf(
            LegalDocument(
                id = "1",
                title = "服务协议",
                version = "2.1.0",
                updateDate = Date(),
                effectiveDate = Date(),
                type = LegalDocumentType.SERVICE_AGREEMENT,
                content = ""
            ),
            LegalDocument(
                id = "2",
                title = "隐私政策",
                version = "1.5.0",
                updateDate = Date(),
                effectiveDate = Date(),
                type = LegalDocumentType.PRIVACY_POLICY,
                content = ""
            ),
            LegalDocument(
                id = "3",
                title = "分佣协议",
                version = "1.0.0",
                updateDate = Date(),
                effectiveDate = Date(),
                type = LegalDocumentType.COMMISSION_AGREEMENT,
                content = ""
            ),
            LegalDocument(
                id = "4",
                title = "免责声明",
                version = "1.2.0",
                updateDate = Date(),
                effectiveDate = Date(),
                type = LegalDocumentType.DISCLAIMER,
                content = ""
            )
        )
        
        DocumentsList(
            documents = sampleDocuments,
            onDocumentClick = {}
        )
    }
}

@Preview
@Composable
fun DocumentItemPreview() {
    MaterialTheme {
        DocumentItem(
            document = LegalDocument(
                id = "1",
                title = "服务协议",
                version = "2.1.0",
                updateDate = Date(),
                effectiveDate = Date(),
                type = LegalDocumentType.SERVICE_AGREEMENT,
                content = ""
            ),
            onClick = {}
        )
    }
}
