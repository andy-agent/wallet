package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.TokenManagerEvent
import com.v2ray.ang.composeui.p2extended.model.TokenManagerRouteArgs
import com.v2ray.ang.composeui.p2extended.model.TokenManagerUiState

class TokenManagerViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: TokenManagerRouteArgs = TokenManagerRouteArgs(),
) : BaseFeatureViewModel<TokenManagerUiState>(TokenManagerUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: TokenManagerEvent) {
        when (event) {
            is TokenManagerEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            TokenManagerEvent.PrimaryActionClicked -> Unit
            TokenManagerEvent.SecondaryActionClicked -> Unit
            TokenManagerEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getTokenManagerState(routeArgs)
        }
    }
}
