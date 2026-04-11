package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.P1StateInfo
import com.v2ray.ang.composeui.p1.model.RegionSelectionEvent
import com.v2ray.ang.composeui.p1.model.RegionSelectionUiState

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
                    searchField = FeatureField(
                        key = _uiState.value.searchField.key,
                        label = _uiState.value.searchField.label,
                        value = event.value,
                        supportingText = _uiState.value.searchField.supportingText,
                    ),
                )
            }

            RegionSelectionEvent.PrimaryActionClicked -> Unit
            RegionSelectionEvent.SecondaryActionClicked -> Unit
            RegionSelectionEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(
            stateInfo = P1StateInfo(P1ScreenState.Loading, message = "正在加载真实区域..."),
        )
        launchLoad {
            repository.getRegionSelectionState()
        }
    }
}
