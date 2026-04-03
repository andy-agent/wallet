package com.cryptovpn.ui.pages.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 强制更新页状态
 */
sealed class ForceUpdateState {
    object Idle : ForceUpdateState()
    object Downloading : ForceUpdateState()
    data class DownloadProgress(val progress: Int) : ForceUpdateState()
    data class Error(val message: String) : ForceUpdateState()
}

/**
 * 强制更新页ViewModel
 */
@HiltViewModel
class ForceUpdateViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<ForceUpdateState>(ForceUpdateState.Idle)
    val state: StateFlow<ForceUpdateState> = _state

    fun startUpdate() {
        _state.value = ForceUpdateState.Downloading
        // 实际应用中这里会调用下载逻辑
    }
}

/**
 * 强制更新页
 * 当检测到必须更新的版本时显示
 */
@Composable
fun ForceUpdatePage(
    viewModel: ForceUpdateViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    versionInfo: String = "v2.0.0",
    onUpdateClick: () -> Unit = {},
    onExitClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 更新图标
            Icon(
                imageVector = Icons.Default.SystemUpdate,
                contentDescription = "Update",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 标题
            Text(
                text = "发现新版本",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 版本号
            Text(
                text = versionInfo,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 说明文字
            Text(
                text = "为了获得更好的体验和安全性，请更新到最新版本。当前版本已停止服务。",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 更新内容列表
            UpdateFeaturesList()

            Spacer(modifier = Modifier.height(48.dp))

            // 更新按钮
            Button(
                onClick = onUpdateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (state is ForceUpdateState.Downloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "立即更新",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 退出按钮
            OutlinedButton(
                onClick = onExitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "退出应用",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UpdateFeaturesList() {
    val features = listOf(
        "全新UI设计，更加简洁美观",
        "优化连接速度，提升50%",
        "新增多区域节点支持",
        "修复已知问题，提升稳定性"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        features.forEach { feature ->
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "•",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = feature,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForceUpdatePagePreview() {
    MaterialTheme {
        ForceUpdatePage()
    }
}
