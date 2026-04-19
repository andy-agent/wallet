package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.feedback.EmptyStateCard
import com.v2ray.ang.composeui.components.inputs.AppTextField
import com.v2ray.ang.composeui.components.wallet.chain.ChainManagementScaffold
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenCandidateUi
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenEvent
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenUiState
import com.v2ray.ang.composeui.p2extended.model.addCustomTokenPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.AddCustomTokenViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun AddCustomTokenRoute(
    viewModel: AddCustomTokenViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    AddCustomTokenScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onSearch = viewModel::search,
        onCandidateSelected = viewModel::selectCandidate,
        onSave = { viewModel.save(onPrimaryAction) },
        onBack = { onSecondaryAction?.invoke() },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun AddCustomTokenScreen(
    uiState: AddCustomTokenUiState,
    onEvent: (AddCustomTokenEvent) -> Unit,
    onSearch: () -> Unit,
    onCandidateSelected: (AddCustomTokenCandidateUi) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    ChainManagementScaffold(
        title = uiState.title,
        subtitle = uiState.subtitle,
        summary = uiState.summary,
        currentRoute = "wallet_home",
        onBottomNav = onBottomNav,
    ) {
        AddCustomContextCard(uiState)

        Surface(shape = RoundedCornerShape(24.dp), tonalElevation = 1.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppTextField(
                    value = uiState.query,
                    label = "搜索关键字",
                    onValueChange = { onEvent(AddCustomTokenEvent.QueryChanged(it)) },
                    placeholder = "代币名称、符号或粘贴精确地址",
                    supportingText = "搜索仅用于定位候选，最终保存必须有精确地址。",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onSearch,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (uiState.isSearching) "搜索中…" else "搜索")
                    }
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                        Text(if (uiState.isSaving) "保存中…" else "添加到资产列表")
                }
            }
        }

        SearchResultsSection(
            items = uiState.searchResults,
            onCandidateSelected = onCandidateSelected,
        )

        Surface(shape = RoundedCornerShape(24.dp), tonalElevation = 1.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppTextField(
                    value = uiState.tokenAddress,
                    label = "精确地址",
                    onValueChange = { onEvent(AddCustomTokenEvent.AddressChanged(it)) },
                    placeholder = "mint / contract address",
                    supportingText = "未命中搜索结果时，也可以直接手动录入。",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                AppTextField(
                    value = uiState.name,
                    label = "代币名称",
                    onValueChange = { onEvent(AddCustomTokenEvent.NameChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                AppTextField(
                    value = uiState.symbol,
                    label = "代币符号",
                    onValueChange = { onEvent(AddCustomTokenEvent.SymbolChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                AppTextField(
                    value = uiState.decimals,
                    label = "精度",
                    onValueChange = { onEvent(AddCustomTokenEvent.DecimalsChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        }

        ActionCluster(
            actions = listOf(
                ActionClusterAction(
                    label = "返回代币管理",
                    onClick = onBack,
                    variant = AppButtonVariant.Secondary,
                ),
            ),
        )
    }
}

@Composable
private fun AddCustomContextCard(
    uiState: AddCustomTokenUiState,
) {
    Surface(shape = RoundedCornerShape(24.dp), tonalElevation = 2.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(uiState.walletName.ifBlank { "当前钱包" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(uiState.chainLabel.ifBlank { "当前链" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            Text(uiState.note, style = MaterialTheme.typography.bodySmall)
            uiState.statusMessage?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            uiState.errorMessage?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun SearchResultsSection(
    items: List<AddCustomTokenCandidateUi>,
    onCandidateSelected: (AddCustomTokenCandidateUi) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("搜索结果", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (items.isEmpty()) {
            EmptyStateCard(
                title = "暂无候选",
                message = "可以继续手动填写名称、符号和精度，然后直接保存。",
            )
            return@Column
        }
        items.forEach { item ->
            Surface(
                shape = RoundedCornerShape(22.dp),
                tonalElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCandidateSelected(item) },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text("${item.symbol} · ${item.name}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(item.tokenAddress, style = MaterialTheme.typography.bodySmall)
                    Text("精度 ${item.decimals}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AddCustomTokenPreview() {
    CryptoVpnTheme {
        Surface {
            AddCustomTokenScreen(
                uiState = addCustomTokenPreviewState(),
                onEvent = {},
                onSearch = {},
                onCandidateSelected = {},
                onSave = {},
                onBack = {},
            )
        }
    }
}
