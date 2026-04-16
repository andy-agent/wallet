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
}
