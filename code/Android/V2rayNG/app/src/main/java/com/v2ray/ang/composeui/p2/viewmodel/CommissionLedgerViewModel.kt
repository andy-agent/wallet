package com.v2ray.ang.composeui.p2.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.CommissionLedgerEvent
import com.v2ray.ang.composeui.p2.model.CommissionLedgerUiState
import kotlinx.coroutines.launch

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
        viewModelScope.launch {
            repository.getCachedCommissionLedgerState()?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.getCommissionLedgerState()
        }
    }
}
