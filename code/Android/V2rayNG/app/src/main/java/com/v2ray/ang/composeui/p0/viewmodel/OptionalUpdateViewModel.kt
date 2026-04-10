package com.v2ray.ang.composeui.p0.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p0.model.OptionalUpdateEvent
import com.v2ray.ang.composeui.p0.model.OptionalUpdateUiState

class OptionalUpdateViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<OptionalUpdateUiState>(OptionalUpdateUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: OptionalUpdateEvent) {
        when (event) {
            is OptionalUpdateEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            OptionalUpdateEvent.PrimaryActionClicked -> Unit
            OptionalUpdateEvent.SecondaryActionClicked -> Unit
            OptionalUpdateEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getOptionalUpdateState()
        }
    }
}
