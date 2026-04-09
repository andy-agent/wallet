package com.cryptovpn.ui.p0.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptovpn.ui.p0.model.VpnConnectionStatus
import com.cryptovpn.ui.p0.model.VpnHomeEvent
import com.cryptovpn.ui.p0.model.VpnHomeUiState
import com.cryptovpn.ui.p0.repository.P0Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VpnHomeViewModel(
    private val repository: P0Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VpnHomeUiState())
    val uiState: StateFlow<VpnHomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = repository.getVpnHomeState()
        }
    }

    fun onEvent(event: VpnHomeEvent) {
        when (event) {
            VpnHomeEvent.ToggleConnection -> {
                val next = when (_uiState.value.connectionStatus) {
                    VpnConnectionStatus.DISCONNECTED -> VpnConnectionStatus.CONNECTING
                    VpnConnectionStatus.CONNECTING -> VpnConnectionStatus.CONNECTED
                    VpnConnectionStatus.CONNECTED -> VpnConnectionStatus.DISCONNECTED
                }
                _uiState.value = _uiState.value.copy(connectionStatus = next)
            }

            is VpnHomeEvent.AutoConnectChanged -> {
                _uiState.value = _uiState.value.copy(autoConnectEnabled = event.value)
            }

            is VpnHomeEvent.RegionSelected -> {
                _uiState.value = _uiState.value.copy(selectedRegion = event.value)
            }
        }
    }
}
