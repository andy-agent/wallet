package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.p0.model.WalletCreationMode
import com.v2ray.ang.composeui.p0.model.WalletOnboardingEvent
import com.v2ray.ang.composeui.p0.model.WalletOnboardingUiState
import com.v2ray.ang.composeui.p0.repository.P0Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletOnboardingViewModel(
    private val repository: P0Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletOnboardingUiState())
    val uiState: StateFlow<WalletOnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = repository.getWalletOnboardingState()
        }
    }

    fun onEvent(event: WalletOnboardingEvent) {
        when (event) {
            is WalletOnboardingEvent.SelectMode -> {
                _uiState.value = _uiState.value.copy(selectedMode = event.value)
            }

            WalletOnboardingEvent.ContinueClicked -> Unit
        }
    }
}
