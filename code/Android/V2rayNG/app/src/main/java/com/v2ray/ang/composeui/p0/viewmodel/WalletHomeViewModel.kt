package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.p0.model.WalletHomeEvent
import com.v2ray.ang.composeui.p0.model.WalletHomeUiState
import com.v2ray.ang.composeui.p0.repository.P0Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletHomeViewModel(
    private val repository: P0Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletHomeUiState())
    val uiState: StateFlow<WalletHomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun onEvent(event: WalletHomeEvent) {
        when (event) {
            is WalletHomeEvent.ChainSelected -> {
                _uiState.value = _uiState.value.copy(selectedChainId = event.chainId)
            }

            is WalletHomeEvent.WalletContextSelected -> {
                val selectedWallet = _uiState.value.walletOptions.firstOrNull { it.walletId == event.walletId }
                    ?: return
                val selectedChain = selectedWallet.chainOptions.firstOrNull { it.chainId == event.chainId }
                    ?: selectedWallet.chainOptions.firstOrNull()
                    ?: return
                _uiState.value = _uiState.value.copy(
                    selectedWalletId = selectedWallet.walletId,
                    selectedChainId = selectedChain.chainId,
                    currentWalletLabel = selectedWallet.walletName,
                    currentWalletChainLabel = selectedChain.label,
                    currentWalletAddress = selectedChain.address,
                    currentWalletAddressSuffix = selectedChain.addressSuffix,
                )
            }

            WalletHomeEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repository.getCachedWalletHomeState()?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.getWalletHomeState()
        }
    }

    fun clearLocalWallet(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val result = repository.clearLocalWallet()
            if (result.isSuccess) {
                refresh()
                onSuccess(result.getOrNull().orEmpty())
            } else {
                onError(result.exceptionOrNull()?.message ?: "清除本地钱包失败")
            }
        }
    }
}
