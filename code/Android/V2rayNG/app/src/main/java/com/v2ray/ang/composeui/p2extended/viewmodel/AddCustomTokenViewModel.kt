package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenEvent
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenUiState
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenRouteArgs

class AddCustomTokenViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: AddCustomTokenRouteArgs = AddCustomTokenRouteArgs(),
) : BaseFeatureViewModel<AddCustomTokenUiState>(AddCustomTokenUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: AddCustomTokenEvent) {
        when (event) {
            is AddCustomTokenEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            AddCustomTokenEvent.PrimaryActionClicked -> Unit
            AddCustomTokenEvent.SecondaryActionClicked -> Unit
            AddCustomTokenEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getAddCustomTokenState(routeArgs)
        }
    }
}
