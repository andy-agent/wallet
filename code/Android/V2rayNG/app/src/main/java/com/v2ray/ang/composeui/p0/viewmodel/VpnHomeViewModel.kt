package com.v2ray.ang.composeui.p0.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.p0.model.VpnHomeEvent
import com.v2ray.ang.composeui.p0.model.VpnHomeUiState
import com.v2ray.ang.composeui.p0.repository.P0Repository
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
        refresh()
    }

    fun onEvent(event: VpnHomeEvent) {
        when (event) {
            VpnHomeEvent.ToggleConnection -> refresh()
            VpnHomeEvent.Refresh -> refresh()

            is VpnHomeEvent.AutoConnectChanged -> {
                _uiState.value = _uiState.value.copy(autoConnectEnabled = event.value)
            }

            is VpnHomeEvent.RegionSelected -> {
                _uiState.value = _uiState.value.copy(selectedRegion = event.value)
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.value = repository.getVpnHomeState()
        }
    }
}
