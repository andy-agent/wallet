package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.CreateWalletEvent
import com.v2ray.ang.composeui.p2extended.model.CreateWalletUiState
import com.v2ray.ang.composeui.p2extended.model.CreateWalletRouteArgs

class CreateWalletViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: CreateWalletRouteArgs = CreateWalletRouteArgs(),
) : BaseFeatureViewModel<CreateWalletUiState>(CreateWalletUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: CreateWalletEvent) {
        when (event) {
            is CreateWalletEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            CreateWalletEvent.PrimaryActionClicked -> Unit
            CreateWalletEvent.SecondaryActionClicked -> Unit
            CreateWalletEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getCreateWalletState(routeArgs)
        }
    }
}
