package com.cryptovpn.ui.p0.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p0.model.ForceUpdateEvent
import com.cryptovpn.ui.p0.model.ForceUpdateUiState

class ForceUpdateViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<ForceUpdateUiState>(ForceUpdateUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: ForceUpdateEvent) {
        when (event) {
            is ForceUpdateEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ForceUpdateEvent.PrimaryActionClicked -> Unit
            ForceUpdateEvent.SecondaryActionClicked -> Unit
            ForceUpdateEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getForceUpdateState()
        }
    }
}
