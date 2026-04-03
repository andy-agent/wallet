package com.cryptovpn.ui.pages.legal

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.BackgroundDark
import com.cryptovpn.ui.theme.BackgroundMedium
import com.cryptovpn.ui.theme.BackgroundLight
import com.cryptovpn.ui.theme.PrimaryBlue
import com.cryptovpn.ui.theme.SuccessGreen
import com.cryptovpn.ui.theme.TextPrimary
import com.cryptovpn.ui.theme.TextSecondary
import com.cryptovpn.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 法务文档详情页
 * 展示文档的完整内容，支持Markdown样式渲染
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentDetailPage(
    documentId: String,
    viewModel: LegalDocumentsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    var document by remember { mutableStateOf<LegalDocument?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(documentId) {
        isLoading = true
        // 模拟加载延迟
        kotlinx.coroutines.delay(300)
        document = viewModel.getDocumentById(documentId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = document?.title ?: "文档详情",
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
            when {
                isLoading -> {
                    LoadingContent()
                }
                document == null -> {
                    ErrorContent(onBackClick = onBackClick)
                }
                else -> {
                    DocumentDetailContent(document = document!!)
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
private fun ErrorContent(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "文档未找到",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        androidx.compose.material3.Button(
            onClick = onBackClick,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("返回列表")
        }
    }
}

@Composable
private fun DocumentDetailContent(document: LegalDocument) {
    val scrollState = rememberScrollState()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 文档信息卡片
        DocumentInfoCard(
            document = document,
            dateFormat = dateFormat
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 文档内容
        DocumentContent(content = document.content)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DocumentInfoCard(
    document: LegalDocument,
    dateFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 文档标题和版本
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = document.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "版本 ${document.version}",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = BackgroundLight)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 日期信息
            Row {
                DateInfoItem(
                    icon = Icons.Default.CalendarToday,
                    label = "更新日期",
                    value = dateFormat.format(document.updateDate)
                )
                
                Spacer(modifier = Modifier.width(24.dp))
                
                DateInfoItem(
                    icon = Icons.Default.Description,
                    label = "生效日期",
                    value = dateFormat.format(document.effectiveDate)
                )
            }
        }
    }
}

@Composable
private fun DateInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(6.dp))
        
        Column {
            Text(
                text = label,
                color = TextTertiary,
                fontSize = 12.sp
            )
            
            Text(
                text = value,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun DocumentContent(content: String) {
    val parsedContent = parseMarkdown(content)
    
    Column {
        parsedContent.forEach { element ->
            when (element) {
                is MarkdownElement.Heading1 -> {
                    Text(
                        text = element.text,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                is MarkdownElement.Heading2 -> {
                    Text(
                        text = element.text,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
                    )
                }
                is MarkdownElement.Heading3 -> {
                    Text(
                        text = element.text,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                is MarkdownElement.Paragraph -> {
                    Text(
                        text = element.text,
                        color = TextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                is MarkdownElement.BulletList -> {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        element.items.forEach { item ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "•",
                                    color = PrimaryBlue,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(20.dp)
                                )
                                Text(
                                    text = item,
                                    color = TextSecondary,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                is MarkdownElement.NumberedList -> {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        element.items.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    color = PrimaryBlue,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(24.dp)
                                )
                                Text(
                                    text = item,
                                    color = TextSecondary,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                is MarkdownElement.Link -> {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = PrimaryBlue,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append(element.text)
                            }
                        },
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                is MarkdownElement.Divider -> {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = BackgroundLight
                    )
                }
            }
        }
    }
}

/**
 * Markdown元素类型
 */
private sealed class MarkdownElement {
    data class Heading1(val text: String) : MarkdownElement()
    data class Heading2(val text: String) : MarkdownElement()
    data class Heading3(val text: String) : MarkdownElement()
    data class Paragraph(val text: String) : MarkdownElement()
    data class BulletList(val items: List<String>) : MarkdownElement()
    data class NumberedList(val items: List<String>) : MarkdownElement()
    data class Link(val text: String, val url: String) : MarkdownElement()
    object Divider : MarkdownElement()
}

/**
 * 简单的Markdown解析器
 */
private fun parseMarkdown(content: String): List<MarkdownElement> {
    val elements = mutableListOf<MarkdownElement>()
    val lines = content.lines()
    var i = 0
    
    while (i < lines.size) {
        val line = lines[i].trim()
        
        when {
            line.startsWith("# ") -> {
                elements.add(MarkdownElement.Heading1(line.substring(2)))
                i++
            }
            line.startsWith("## ") -> {
                elements.add(MarkdownElement.Heading2(line.substring(3)))
                i++
            }
            line.startsWith("### ") -> {
                elements.add(MarkdownElement.Heading3(line.substring(4)))
                i++
            }
            line.startsWith("- ") || line.startsWith("* ") -> {
                // 无序列表
                val items = mutableListOf<String>()
                while (i < lines.size && (lines[i].trim().startsWith("- ") || lines[i].trim().startsWith("* "))) {
                    items.add(lines[i].trim().substring(2))
                    i++
                }
                elements.add(MarkdownElement.BulletList(items))
            }
            line.matches(Regex("^\\d+\\.\\s+.+")) -> {
                // 有序列表
                val items = mutableListOf<String>()
                while (i < lines.size && lines[i].trim().matches(Regex("^\\d+\\.\\s+.+"))) {
                    items.add(lines[i].trim().replace(Regex("^\\d+\\.\\s+"), ""))
                    i++
                }
                elements.add(MarkdownElement.NumberedList(items))
            }
            line.startsWith("---") -> {
                elements.add(MarkdownElement.Divider)
                i++
            }
            line.isNotEmpty() -> {
                // 段落
                val paragraph = StringBuilder(line)
                i++
                while (i < lines.size && lines[i].trim().isNotEmpty() && 
                       !lines[i].trim().startsWith("#") &&
                       !lines[i].trim().startsWith("-") &&
                       !lines[i].trim().startsWith("*") &&
                       !lines[i].trim().matches(Regex("^\\d+\\."))) {
                    paragraph.append(" ").append(lines[i].trim())
                    i++
                }
                elements.add(MarkdownElement.Paragraph(paragraph.toString()))
            }
            else -> {
                i++
            }
        }
    }
    
    return elements
}

@Preview
@Composable
fun LegalDocumentDetailPagePreview() {
    MaterialTheme {
        val sampleDocument = LegalDocument(
            id = "1",
            title = "服务协议",
            version = "2.1.0",
            updateDate = Date(),
            effectiveDate = Date(),
            type = LegalDocumentType.SERVICE_AGREEMENT,
            content = """
# 服务协议

## 1. 服务概述

CryptoVPN 提供安全、高速的虚拟私人网络服务。

## 2. 用户责任

- 您必须年满18周岁
- 您不得将本服务用于非法活动
- 您有责任保护账户安全

## 3. 服务条款

### 3.1 服务可用性

我们致力于提供99.9%的服务可用性。

### 3.2 退款政策

1. 7天内无理由退款
2. 超过7天按比例退款

感谢您的使用！
            """.trimIndent()
        )
        
        DocumentDetailContent(document = sampleDocument)
    }
}

@Preview
@Composable
fun DocumentContentPreview() {
    MaterialTheme {
        val sampleContent = """
# 隐私政策

## 1. 信息收集

我们收集以下信息：
- 账户信息
- 支付信息
- 设备信息

## 2. 信息使用

我们使用您的信息用于：
1. 提供服务
2. 处理支付
3. 发送通知

感谢您的信任！
        """.trimIndent()
        
        DocumentContent(content = sampleContent)
    }
}
