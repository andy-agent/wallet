package com.v2ray.ang.composeui.p2extended.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.ImportWalletMethodEvent
import com.v2ray.ang.composeui.p2extended.model.ImportWalletMethodUiState
import com.v2ray.ang.composeui.p2extended.model.importWalletMethodLoadingState
import kotlinx.coroutines.launch

class ImportWalletMethodViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<ImportWalletMethodUiState>(importWalletMethodLoadingState()) {

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
        viewModelScope.launch {
            _uiState.value = importWalletMethodLoadingState()
            _uiState.value = repository.getImportWalletMethodState()
        }
    }
}
