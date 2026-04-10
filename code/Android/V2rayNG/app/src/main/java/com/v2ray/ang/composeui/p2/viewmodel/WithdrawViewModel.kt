package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.WithdrawEvent
import com.v2ray.ang.composeui.p2.model.WithdrawUiState

class WithdrawViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<WithdrawUiState>(WithdrawUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: WithdrawEvent) {
        when (event) {
            is WithdrawEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            WithdrawEvent.PrimaryActionClicked -> Unit
            WithdrawEvent.SecondaryActionClicked -> Unit
            WithdrawEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getWithdrawState()
        }
    }
}
