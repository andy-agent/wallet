package com.cryptovpn.ui.p1.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p1.model.RegionSelectionEvent
import com.cryptovpn.ui.p1.model.RegionSelectionUiState

class RegionSelectionViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<RegionSelectionUiState>(RegionSelectionUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: RegionSelectionEvent) {
        when (event) {
            is RegionSelectionEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            RegionSelectionEvent.PrimaryActionClicked -> Unit
            RegionSelectionEvent.SecondaryActionClicked -> Unit
            RegionSelectionEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getRegionSelectionState()
        }
    }
}
