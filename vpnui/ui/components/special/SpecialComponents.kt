package com.cryptovpn.ui.components.special

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.components.buttons.PrimaryButton
import com.cryptovpn.ui.theme.*

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon?.invoke() ?: Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = actionText,
                onClick = onActionClick,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@Composable
fun ErrorState(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = Error,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "重试",
            onClick = onRetry,
            modifier = Modifier.width(200.dp)
        )
    }
}

@Composable
fun LoadingState(
    message: String = "加载中...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

@Preview
@Composable
fun EmptyStatePreview() {
    CryptoVPNTheme {
        EmptyState(
            title = "暂无订单记录",
            message = "您还没有购买任何套餐",
            actionText = "去购买套餐",
            onActionClick = {}
        )
    }
}

@Preview
@Composable
fun ErrorStatePreview() {
    CryptoVPNTheme {
        ErrorState(
            title = "网络连接失败",
            message = "请检查网络设置后重试",
            onRetry = {}
        )
    }
}