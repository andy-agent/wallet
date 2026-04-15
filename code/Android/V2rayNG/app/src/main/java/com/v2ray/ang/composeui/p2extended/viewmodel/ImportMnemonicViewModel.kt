package com.v2ray.ang.composeui.p2extended.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.importMnemonicLoadingState
import kotlinx.coroutines.launch

class ImportMnemonicViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: ImportMnemonicRouteArgs = ImportMnemonicRouteArgs(),
) : BaseFeatureViewModel<ImportMnemonicUiState>(importMnemonicLoadingState()) {

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
        viewModelScope.launch {
            _uiState.value = importMnemonicLoadingState()
            _uiState.value = repository.getImportMnemonicState(routeArgs)
        }
    }

    fun submitImport(
        onSuccess: (String?) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val mnemonic = _uiState.value.fields.firstOrNull { it.key == "mnemonic" }?.value.orEmpty()
            val walletName = _uiState.value.fields.firstOrNull { it.key == "walletName" }?.value.orEmpty()
            val result = repository.importWalletFromMnemonic(
                source = routeArgs.source,
                mnemonic = mnemonic,
                walletName = walletName,
            )
            if (result.success) {
                refresh()
                onSuccess(result.walletId)
            } else {
                onError(result.errorMessage ?: "导入钱包失败")
            }
        }
    }
}
