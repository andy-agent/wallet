package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.GasSettingsEvent
import com.v2ray.ang.composeui.p2extended.model.GasSettingsUiState
import com.v2ray.ang.composeui.p2extended.model.GasSettingsRouteArgs

class GasSettingsViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: GasSettingsRouteArgs = GasSettingsRouteArgs(),
) : BaseFeatureViewModel<GasSettingsUiState>(GasSettingsUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: GasSettingsEvent) {
        when (event) {
            is GasSettingsEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            GasSettingsEvent.PrimaryActionClicked -> Unit
            GasSettingsEvent.SecondaryActionClicked -> Unit
            GasSettingsEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getGasSettingsState(routeArgs)
        }
    }
}
