package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.ImportPrivateKeyEvent
import com.cryptovpn.ui.p2extended.model.ImportPrivateKeyUiState
import com.cryptovpn.ui.p2extended.model.ImportPrivateKeyRouteArgs

class ImportPrivateKeyViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: ImportPrivateKeyRouteArgs = ImportPrivateKeyRouteArgs(),
) : BaseFeatureViewModel<ImportPrivateKeyUiState>(ImportPrivateKeyUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: ImportPrivateKeyEvent) {
        when (event) {
            is ImportPrivateKeyEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ImportPrivateKeyEvent.PrimaryActionClicked -> Unit
            ImportPrivateKeyEvent.SecondaryActionClicked -> Unit
            ImportPrivateKeyEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getImportPrivateKeyState(routeArgs)
        }
    }
}
