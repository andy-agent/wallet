package com.v2ray.ang.composeui.pages.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 可选更新弹窗状态
 */
sealed class OptionalUpdateState {
    object Idle : OptionalUpdateState()
    object Dismissed : OptionalUpdateState()
    object UpdateClicked : OptionalUpdateState()
}

/**
 * 可选更新弹窗ViewModel
 */
class OptionalUpdateViewModel : ViewModel() {
    private val _state = MutableStateFlow<OptionalUpdateState>(OptionalUpdateState.Idle)
    val state: StateFlow<OptionalUpdateState> = _state

    fun onDismiss() {
        _state.value = OptionalUpdateState.Dismissed
    }

    fun onUpdate() {
        _state.value = OptionalUpdateState.UpdateClicked
    }
}

/**
 * 可选更新弹窗
 * 当检测到可选更新时显示，用户可以选择更新或跳过
 */
@Composable
fun OptionalUpdateDialog(
    viewModel: OptionalUpdateViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    versionInfo: String = "v2.0.0",
    onDismiss: () -> Unit = {},
    onUpdate: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // 监听状态变化
    LaunchedEffect(state) {
        when (state) {
            OptionalUpdateState.Dismissed -> onDismiss()
            OptionalUpdateState.UpdateClicked -> onUpdate()
            else -> {}
        }
    }

    AlertDialog(
        onDismissRequest = { viewModel.onDismiss() },
        icon = {
            Icon(
                imageVector = Icons.Default.NewReleases,
                contentDescription = "New Release",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "发现新版本",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = versionInfo,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "新版本包含性能优化和新功能，建议更新以获得更好体验。",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 更新内容
                UpdateContentSummary()
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.onUpdate() },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "立即更新",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { viewModel.onDismiss() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "稍后提醒",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        iconContentColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun UpdateContentSummary() {
    val updates = listOf(
        "性能优化",
        "新功能上线",
        "Bug修复"
    )
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        updates.forEach { update ->
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = update,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OptionalUpdateDialogPreview() {
    MaterialTheme {
        OptionalUpdateDialog()
    }
}
