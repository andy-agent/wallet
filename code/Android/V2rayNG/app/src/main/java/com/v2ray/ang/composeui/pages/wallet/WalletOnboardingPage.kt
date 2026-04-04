package com.v2ray.ang.composeui.pages.wallet

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 引导页数据
 */
data class OnboardingPageData(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String
)

/**
 * 钱包引导页状态
 */
sealed class WalletOnboardingState {
    object Idle : WalletOnboardingState()
    object NavigatingToCreate : WalletOnboardingState()
    object NavigatingToImport : WalletOnboardingState()
}

/**
 * 钱包引导页ViewModel
 */
class WalletOnboardingViewModel : ViewModel() {
    private val _state = MutableStateFlow<WalletOnboardingState>(WalletOnboardingState.Idle)
    val state: StateFlow<WalletOnboardingState> = _state

    fun onCreateWallet() {
        _state.value = WalletOnboardingState.NavigatingToCreate
    }

    fun onImportWallet() {
        _state.value = WalletOnboardingState.NavigatingToImport
    }

    fun resetState() {
        _state.value = WalletOnboardingState.Idle
    }
}

/**
 * 钱包引导页
 * 引导用户创建或导入钱包
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WalletOnboardingPage(
    viewModel: WalletOnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToCreate: () -> Unit = {},
    onNavigateToImport: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 })

    // 监听状态变化
    LaunchedEffect(state) {
        when (state) {
            WalletOnboardingState.NavigatingToCreate -> {
                viewModel.resetState()
                onNavigateToCreate()
            }
            WalletOnboardingState.NavigatingToImport -> {
                viewModel.resetState()
                onNavigateToImport()
            }
            else -> {}
        }
    }

    val pages = listOf(
        OnboardingPageData(
            icon = Icons.Default.Security,
            title = "安全自托管",
            description = "您的私钥只存储在您的设备上，您完全掌控自己的资产"
        ),
        OnboardingPageData(
            icon = Icons.Default.SwapHoriz,
            title = "多链支持",
            description = "支持以太坊、BSC、Polygon等多条主流公链"
        ),
        OnboardingPageData(
            icon = Icons.Default.Payments,
            title = "便捷支付",
            description = "使用钱包余额快速购买VPN套餐，享受更多优惠"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 跳过按钮
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                TextButton(onClick = onSkip) {
                    Text(
                        text = "跳过",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 页面内容
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(pageData = pages[page])
            }

            // 页面指示器
            PageIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // 操作按钮
            ActionButtons(
                onCreateWallet = { viewModel.onCreateWallet() },
                onImportWallet = { viewModel.onImportWallet() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun OnboardingPageContent(pageData: OnboardingPageData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 图标
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(120.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = pageData.icon,
                    contentDescription = pageData.title,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 标题
        Text(
            text = pageData.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 描述
        Text(
            text = pageData.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 10.dp else 8.dp)
                    .background(
                        color = if (index == currentPage) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )
            if (index < pageCount - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onCreateWallet: () -> Unit,
    onImportWallet: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        // 创建钱包按钮
        Button(
            onClick = onCreateWallet,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "创建新钱包",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 导入钱包按钮
        OutlinedButton(
            onClick = onImportWallet,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "导入已有钱包",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 提示文字
        Text(
            text = "创建或导入即表示您同意服务条款和隐私政策",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WalletOnboardingPagePreview() {
    MaterialTheme {
        WalletOnboardingPage()
    }
}
