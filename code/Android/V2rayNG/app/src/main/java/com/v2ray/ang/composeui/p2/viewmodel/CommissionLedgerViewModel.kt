package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.CommissionLedgerEvent
import com.v2ray.ang.composeui.p2.model.CommissionLedgerUiState

class CommissionLedgerViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<CommissionLedgerUiState>(CommissionLedgerUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: CommissionLedgerEvent) {
        when (event) {
            is CommissionLedgerEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            CommissionLedgerEvent.PrimaryActionClicked -> Unit
            CommissionLedgerEvent.SecondaryActionClicked -> Unit
            CommissionLedgerEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getCommissionLedgerState()
        }
    }
}
