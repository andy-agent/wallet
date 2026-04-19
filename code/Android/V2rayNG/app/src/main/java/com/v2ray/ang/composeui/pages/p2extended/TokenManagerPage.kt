package com.v2ray.ang.composeui.pages.p2extended

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.feedback.EmptyStateCard
import com.v2ray.ang.composeui.components.wallet.chain.ChainManagementScaffold
import com.v2ray.ang.composeui.p2extended.model.ManagedTokenUi
import com.v2ray.ang.composeui.p2extended.model.TokenManagerUiState
import com.v2ray.ang.composeui.p2extended.model.TokenVisibilityAction
import com.v2ray.ang.composeui.p2extended.model.tokenManagerPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.TokenManagerViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import java.io.File

@Composable
fun TokenManagerRoute(
    viewModel: TokenManagerViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    TokenManagerScreen(
        uiState = uiState,
        onAddCustomToken = onPrimaryAction,
        onBack = { onSecondaryAction?.invoke() },
        onRefresh = viewModel::refresh,
        onMutateToken = viewModel::mutateToken,
        onBottomNav = onBottomNav,
    )
}

@Composable
fun TokenManagerScreen(
    uiState: TokenManagerUiState,
    onAddCustomToken: () -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onMutateToken: (ManagedTokenUi, TokenVisibilityAction) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    ChainManagementScaffold(
        title = uiState.title,
        subtitle = uiState.subtitle,
        summary = uiState.summary,
        currentRoute = "wallet_home",
        onBottomNav = onBottomNav,
    ) {
        TokenManagerContextCard(
            walletName = uiState.walletName,
            chainLabel = uiState.chainLabel,
            note = uiState.note,
            actionMessage = uiState.actionMessage,
            errorMessage = uiState.errorMessage,
        )

        TokenSection(
            title = "可见代币",
            emptyTitle = "暂无可见代币",
            emptyMessage = "当前钱包当前链下没有可显示的代币。",
            tokens = uiState.visibleTokens,
            actions = listOf(
                "隐藏" to TokenVisibilityAction.Hide,
                "垃圾币" to TokenVisibilityAction.Spam,
            ),
            onMutateToken = onMutateToken,
        )

        TokenSection(
            title = "已隐藏",
            emptyTitle = "暂无隐藏代币",
            emptyMessage = "隐藏的代币会保留在本机，可随时恢复。",
            tokens = uiState.hiddenTokens,
            actions = listOf("恢复" to TokenVisibilityAction.Restore),
            onMutateToken = onMutateToken,
        )

        TokenSection(
            title = "垃圾币",
            emptyTitle = "暂无垃圾币",
            emptyMessage = "标记为垃圾币的代币不会在首页显示。",
            tokens = uiState.spamTokens,
            actions = listOf("恢复" to TokenVisibilityAction.Restore),
            onMutateToken = onMutateToken,
        )

        ActionCluster(
            actions = listOf(
                ActionClusterAction(
                    label = "添加自定义代币",
                    onClick = onAddCustomToken,
                    variant = AppButtonVariant.Primary,
                ),
                ActionClusterAction(
                    label = "刷新",
                    onClick = onRefresh,
                    variant = AppButtonVariant.Secondary,
                ),
                ActionClusterAction(
                    label = "返回钱包首页",
                    onClick = onBack,
                    variant = AppButtonVariant.Secondary,
                ),
            ),
        )
    }
}

@Composable
private fun TokenManagerContextCard(
    walletName: String,
    chainLabel: String,
    note: String,
    actionMessage: String?,
    errorMessage: String?,
) {
    Surface(shape = RoundedCornerShape(24.dp), tonalElevation = 2.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = walletName.ifBlank { "当前钱包" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = chainLabel.ifBlank { "当前链" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            if (note.isNotBlank()) {
                Text(text = note, style = MaterialTheme.typography.bodySmall)
            }
            actionMessage?.takeIf { it.isNotBlank() }?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            errorMessage?.takeIf { it.isNotBlank() }?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun TokenSection(
    title: String,
    emptyTitle: String,
    emptyMessage: String,
    tokens: List<ManagedTokenUi>,
    actions: List<Pair<String, TokenVisibilityAction>>,
    onMutateToken: (ManagedTokenUi, TokenVisibilityAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (tokens.isEmpty()) {
            EmptyStateCard(title = emptyTitle, message = emptyMessage)
            return@Column
        }
        tokens.forEach { token ->
            TokenRowCard(token = token, actions = actions, onMutateToken = onMutateToken)
        }
    }
}

@Composable
private fun TokenRowCard(
    token: ManagedTokenUi,
    actions: List<Pair<String, TokenVisibilityAction>>,
    onMutateToken: (ManagedTokenUi, TokenVisibilityAction) -> Unit,
) {
    Surface(shape = RoundedCornerShape(22.dp), tonalElevation = 1.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TokenIcon(token)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(token.symbol, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(token.name, style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(token.balanceText, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(token.statusText, style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                actions.forEach { (label, action) ->
                    OutlinedButton(
                        onClick = { onMutateToken(token, action) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(label)
                    }
                }
                if (token.isCustom && token.customTokenId != null) {
                    Button(
                        onClick = { onMutateToken(token, TokenVisibilityAction.DeleteCustom) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("删除")
                    }
                }
            }
        }
    }
}

@Composable
private fun TokenIcon(token: ManagedTokenUi) {
    val localPath = token.iconLocalPath
    val bitmap = localPath
        ?.takeIf { it.isNotBlank() && File(it).exists() }
        ?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = token.symbol,
            modifier = Modifier.size(40.dp),
        )
        return
    }
    Image(
        painter = painterResource(id = chainIconRes(token.iconChainId) ?: R.drawable.chain_tron),
        contentDescription = token.symbol,
        modifier = Modifier.size(40.dp),
    )
}

private fun chainIconRes(chainId: String): Int? = when (chainId.lowercase()) {
    "solana" -> R.drawable.chain_solana
    "tron" -> R.drawable.chain_tron
    "ethereum" -> R.drawable.chain_ethereum
    "bsc", "smartchain" -> R.drawable.chain_bsc
    "polygon" -> R.drawable.chain_polygon
    "arbitrum" -> R.drawable.chain_arbitrum
    "optimism" -> R.drawable.chain_optimism
    "avalanche", "avalanche_c" -> R.drawable.chain_avalanche
    "base" -> R.drawable.chain_base
    else -> null
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun TokenManagerPreview() {
    CryptoVpnTheme {
        Surface {
            TokenManagerScreen(
                uiState = tokenManagerPreviewState(),
                onAddCustomToken = {},
                onBack = {},
                onRefresh = {},
                onMutateToken = { _, _ -> },
            )
        }
    }
}
