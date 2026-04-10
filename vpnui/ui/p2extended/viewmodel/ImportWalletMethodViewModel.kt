package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.ImportWalletMethodEvent
import com.cryptovpn.ui.p2extended.model.ImportWalletMethodUiState

class ImportWalletMethodViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<ImportWalletMethodUiState>(ImportWalletMethodUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: ImportWalletMethodEvent) {
        when (event) {
            is ImportWalletMethodEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ImportWalletMethodEvent.PrimaryActionClicked -> Unit
            ImportWalletMethodEvent.SecondaryActionClicked -> Unit
            ImportWalletMethodEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getImportWalletMethodState()
        }
    }
}
