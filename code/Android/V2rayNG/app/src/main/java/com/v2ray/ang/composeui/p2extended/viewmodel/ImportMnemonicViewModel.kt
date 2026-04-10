package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicRouteArgs

class ImportMnemonicViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: ImportMnemonicRouteArgs = ImportMnemonicRouteArgs(),
) : BaseFeatureViewModel<ImportMnemonicUiState>(ImportMnemonicUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: ImportMnemonicEvent) {
        when (event) {
            is ImportMnemonicEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ImportMnemonicEvent.PrimaryActionClicked -> Unit
            ImportMnemonicEvent.SecondaryActionClicked -> Unit
            ImportMnemonicEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getImportMnemonicState(routeArgs)
        }
    }
}
