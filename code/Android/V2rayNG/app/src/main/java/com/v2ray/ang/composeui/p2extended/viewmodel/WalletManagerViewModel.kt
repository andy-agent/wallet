package com.v2ray.ang.composeui.p2extended.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.WalletManagerEvent
import com.v2ray.ang.composeui.p2extended.model.WalletManagerWalletItemUi
import com.v2ray.ang.composeui.p2extended.model.WalletManagerUiState
import com.v2ray.ang.composeui.p2extended.model.WalletManagerRouteArgs
import kotlinx.coroutines.launch

class WalletManagerViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: WalletManagerRouteArgs = WalletManagerRouteArgs(),
) : BaseFeatureViewModel<WalletManagerUiState>(WalletManagerUiState()) {

    init {
        preloadCached()
        refresh()
    }

    fun onEvent(event: WalletManagerEvent) {
        when (event) {
            is WalletManagerEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            WalletManagerEvent.PrimaryActionClicked -> Unit
            WalletManagerEvent.SecondaryActionClicked -> Unit
            is WalletManagerEvent.WalletSelected -> Unit
            WalletManagerEvent.Refresh -> refresh()
        }
    }

    fun submitSetDefault(
        walletId: String,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            if (_uiState.value.isSwitchingWallet) {
                return@launch
            }
            val previousState = _uiState.value
            val selectedWallet = previousState.wallets.firstOrNull { it.walletId == walletId }
                ?: return@launch
            if (selectedWallet.isDefault) {
                return@launch
            }
            _uiState.value = previousState.toSwitchingState(selectedWallet)
            val result = repository.setDefaultWallet(walletId)
            if (result.success) {
                refresh(previousState = previousState)
            } else {
                _uiState.value = previousState
                onError("当前网络不佳，请重试。")
            }
        }
    }

    private fun preloadCached() {
        viewModelScope.launch {
            repository.getCachedWalletManagerState(routeArgs)?.let { cached ->
                if (cached.wallets.isNotEmpty()) {
                    _uiState.value = cached
                }
            }
        }
    }

    private fun refresh(previousState: WalletManagerUiState? = null) {
        viewModelScope.launch {
            val nextState = repository.getWalletManagerState(routeArgs)
            _uiState.value = when {
                nextState.wallets.isNotEmpty() -> nextState
                previousState?.wallets?.isNotEmpty() == true -> previousState.copy(
                    summary = "当前网络不佳，已保留本地钱包列表。",
                    note = "钱包列表刷新失败，已自动保留上一次可用结果。",
                    isSwitchingWallet = false,
                    switchingWalletId = null,
                    switchingWalletName = null,
                )
                _uiState.value.wallets.isNotEmpty() -> _uiState.value.copy(
                    summary = "当前网络不佳，已保留本地钱包列表。",
                    note = "钱包列表刷新失败，已自动保留上一次可用结果。",
                    isSwitchingWallet = false,
                    switchingWalletId = null,
                    switchingWalletName = null,
                )
                else -> nextState
            }
        }
    }

    private fun WalletManagerUiState.toSwitchingState(selectedWallet: WalletManagerWalletItemUi): WalletManagerUiState {
        val switchedWallets = wallets.map { wallet ->
            wallet.copy(isDefault = wallet.walletId == selectedWallet.walletId)
        }
        return copy(
            wallets = switchedWallets,
            metrics = metrics.map { metric ->
                when (metric.label) {
                    "当前钱包" -> metric.copy(value = selectedWallet.walletName)
                    else -> metric
                }
            },
            highlights = highlights.map { item ->
                if (item.title == "当前钱包") {
                    item.copy(
                        subtitle = selectedWallet.walletName,
                        trailing = selectedWallet.walletKind,
                    )
                } else {
                    item
                }
            },
            summary = "正在切换默认钱包，请稍候。",
            isSwitchingWallet = true,
            switchingWalletId = selectedWallet.walletId,
            switchingWalletName = selectedWallet.walletName,
        )
    }
}
