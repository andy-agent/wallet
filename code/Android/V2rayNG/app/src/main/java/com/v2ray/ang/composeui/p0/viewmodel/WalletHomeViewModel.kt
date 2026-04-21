package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.p0.model.WalletHomeEvent
import com.v2ray.ang.composeui.p0.model.WalletHomeUiState
import com.v2ray.ang.composeui.p0.repository.P0Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalletHomeViewModel(
    private val repository: P0Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletHomeUiState())
    val uiState: StateFlow<WalletHomeUiState> = _uiState.asStateFlow()
    private var periodicRefreshJob: Job? = null

    init {
        refresh()
        startPeriodicRefresh()
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
                refresh(selectedWallet.walletId)
            }

            WalletHomeEvent.Refresh -> refresh(forceRefresh = true)
        }
    }

    private fun refresh(
        selectedWalletId: String? = _uiState.value.selectedWalletId,
        forceRefresh: Boolean = false,
    ) {
        viewModelScope.launch {
            if (!forceRefresh) {
                repository.getCachedWalletHomeState(selectedWalletId)?.let { cached ->
                    _uiState.value = cached
                }
            }
            _uiState.value = repository.getWalletHomeState(
                selectedWalletId = selectedWalletId,
                forceRefresh = forceRefresh,
            )
        }
    }

    private fun startPeriodicRefresh() {
        periodicRefreshJob?.cancel()
        periodicRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(60_000)
                refresh(forceRefresh = true)
            }
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

    override fun onCleared() {
        periodicRefreshJob?.cancel()
        super.onCleared()
    }
}
