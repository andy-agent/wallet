package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicRouteArgs

class ConfirmMnemonicViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: ConfirmMnemonicRouteArgs = ConfirmMnemonicRouteArgs(),
) : BaseFeatureViewModel<ConfirmMnemonicUiState>(ConfirmMnemonicUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: ConfirmMnemonicEvent) {
        when (event) {
            is ConfirmMnemonicEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ConfirmMnemonicEvent.PrimaryActionClicked -> Unit
            ConfirmMnemonicEvent.SecondaryActionClicked -> Unit
            ConfirmMnemonicEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getConfirmMnemonicState(routeArgs)
        }
    }
}
