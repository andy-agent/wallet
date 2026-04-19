package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.ManagedTokenUi
import com.v2ray.ang.composeui.p2extended.model.TokenManagerRouteArgs
import com.v2ray.ang.composeui.p2extended.model.TokenManagerUiState
import com.v2ray.ang.composeui.p2extended.model.TokenVisibilityAction
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TokenManagerViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: TokenManagerRouteArgs = TokenManagerRouteArgs(),
) : BaseFeatureViewModel<TokenManagerUiState>(TokenManagerUiState()) {

    init {
        refresh()
    }

    fun refresh() {
        launchLoad {
            repository.getTokenManagerState(routeArgs)
        }
    }

    fun mutateToken(
        token: ManagedTokenUi,
        action: TokenVisibilityAction,
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                errorMessage = null,
                actionMessage = when (action) {
                    TokenVisibilityAction.Hide -> "正在隐藏代币"
                    TokenVisibilityAction.Spam -> "正在标记垃圾币"
                    TokenVisibilityAction.Restore -> "正在恢复显示"
                    TokenVisibilityAction.DeleteCustom -> "正在删除自定义代币"
                },
            )
            val result = when (action) {
                TokenVisibilityAction.Hide -> repository.setTokenVisibility(
                    walletId = routeArgs.walletId,
                    chainId = routeArgs.chainId,
                    tokenKey = token.tokenKey,
                    visibilityState = "HIDDEN",
                )
                TokenVisibilityAction.Spam -> repository.setTokenVisibility(
                    walletId = routeArgs.walletId,
                    chainId = routeArgs.chainId,
                    tokenKey = token.tokenKey,
                    visibilityState = "SPAM",
                )
                TokenVisibilityAction.Restore -> repository.setTokenVisibility(
                    walletId = routeArgs.walletId,
                    chainId = routeArgs.chainId,
                    tokenKey = token.tokenKey,
                    visibilityState = null,
                )
                TokenVisibilityAction.DeleteCustom -> token.customTokenId?.let {
                    repository.deleteCustomToken(routeArgs.walletId, it)
                } ?: run {
                    repository.setTokenVisibility(
                        walletId = routeArgs.walletId,
                        chainId = routeArgs.chainId,
                        tokenKey = token.tokenKey,
                        visibilityState = "HIDDEN",
                    )
                }
            }
            if (result.success) {
                _uiState.value = repository.getTokenManagerState(routeArgs).copy(
                    actionMessage = result.message ?: _uiState.value.actionMessage,
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    errorMessage = result.errorMessage ?: "代币操作失败",
                    actionMessage = null,
                )
            }
        }
    }
}
