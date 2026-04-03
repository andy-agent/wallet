package com.cryptovpn.ui.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.CryptoVPNTheme

// ==================== Enums & Data Models ====================

enum class WalletOnboardingState {
    EMPTY,
    CREATING,
    IMPORTING,
    READY,
    ERROR
}

data class WalletOnboardingUiState(
    val state: WalletOnboardingState = WalletOnboardingState.EMPTY,
    val errorMessage: String? = null,
    val features: List<WalletFeature> = emptyList()
)

data class WalletFeature(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String
)

// ==================== ViewModel ====================

class WalletOnboardingViewModel {
    var state by mutableStateOf(WalletOnboardingUiState())
        private set

    init {
        loadFeatures()
    }

    private fun loadFeatures() {
        val features = listOf(
            WalletFeature(
                icon = Icons.Default.Security,
                title = "安全存储",
                description = "私钥本地加密，您的资产完全由您掌控"
            ),
            WalletFeature(
                icon = Icons.Default.Speed,
                title = "快速交易",
                description = "支持多链，秒级确认，低手续费"
            ),
            WalletFeature(
                icon = Icons.Default.VpnKey,
                title = "自主托管",
                description = "去中心化钱包，无需信任第三方"
            )
        )
        state = state.copy(features = features)
    }

    fun onCreateWallet() {
        state = state.copy(state = WalletOnboardingState.CREATING)
        // Simulate wallet creation
    }

    fun onImportWallet() {
        state = state.copy(state = WalletOnboardingState.IMPORTING)
        // Navigate to import screen
    }

    fun onWalletCreated() {
        state = state.copy(state = WalletOnboardingState.READY)
    }

    fun setError(message: String) {
        state = state.copy(state = WalletOnboardingState.ERROR, errorMessage = message)
    }
}

// ==================== Page Composable ====================

@Composable
fun WalletOnboardingPage(
    viewModel: WalletOnboardingViewModel = remember { WalletOnboardingViewModel() },
    onCreateWalletClick: () -> Unit = {},
    onImportWalletClick: () -> Unit = {},
    onWalletReady: () -> Unit = {}
) {
    val state = viewModel.state

    // Handle state changes
    LaunchedEffect(state.state) {
        if (state.state == WalletOnboardingState.READY) {
            onWalletReady()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
    ) {
        when (state.state) {
            WalletOnboardingState.CREATING -> {
                CreatingWalletView()
            }
            WalletOnboardingState.IMPORTING -> {
                ImportingWalletView()
            }
            else -> {
                OnboardingContent(
                    state = state,
                    onCreateWalletClick = {
                        viewModel.onCreateWallet()
                        onCreateWalletClick()
                    },
                    onImportWalletClick = {
                        viewModel.onImportWallet()
                        onImportWalletClick()
                    }
                )
            }
        }

        // Error Snackbar
        state.errorMessage?.let { message ->
            ErrorSnackbar(message = message)
        }
    }
}

@Composable
private fun OnboardingContent(
    state: WalletOnboardingUiState,
    onCreateWalletClick: () -> Unit,
    onImportWalletClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Security Icon with Animation
        SecurityIcon()

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "创建您的钱包",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtitle
        Text(
            text = "安全、快速地管理您的加密资产",
            color = Color(0xFF9CA3AF),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Feature Cards
        state.features.forEach { feature ->
            FeatureCard(feature = feature)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        ActionButtons(
            onCreateWalletClick = onCreateWalletClick,
            onImportWalletClick = onImportWalletClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hint Text
        Text(
            text = "创建即表示您同意我们的服务条款和隐私政策",
            color = Color(0xFF6B7280),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SecurityIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalanceWallet,
            contentDescription = "Wallet",
            tint = Color.White,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
private fun FeatureCard(feature: WalletFeature) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1D4ED8).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = feature.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = feature.description,
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onCreateWalletClick: () -> Unit,
    onImportWalletClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onCreateWalletClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            )
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Create",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "创建新钱包",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = onImportWalletClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color(0xFF374151)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Import",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "导入已有钱包",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CreatingWalletView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF1D4ED8),
                modifier = Modifier.size(64.dp),
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "正在创建钱包...",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "请稍候，正在生成安全密钥",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ImportingWalletView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF1D4ED8),
                modifier = Modifier.size(64.dp),
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "正在导入钱包...",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "正在验证助记词",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorSnackbar(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEF4444).copy(alpha = 0.9f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = Color.White
                )
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ==================== Preview ====================

@Preview(device = "id:pixel_5")
@Composable
private fun WalletOnboardingPagePreview() {
    CryptoVPNTheme {
        WalletOnboardingPage()
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun WalletOnboardingPageCreatingPreview() {
    CryptoVPNTheme {
        val viewModel = remember { WalletOnboardingViewModel() }
        viewModel.state = WalletOnboardingUiState(state = WalletOnboardingState.CREATING)
        WalletOnboardingPage(viewModel = viewModel)
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun WalletOnboardingPageErrorPreview() {
    CryptoVPNTheme {
        val viewModel = remember { WalletOnboardingViewModel() }
        viewModel.state = WalletOnboardingUiState(
            state = WalletOnboardingState.ERROR,
            errorMessage = "创建钱包失败，请重试"
        )
        WalletOnboardingPage(viewModel = viewModel)
    }
}