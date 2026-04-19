package com.v2ray.ang.composeui.p2extended.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.ImportWatchWalletEvent
import com.v2ray.ang.composeui.p2extended.model.ImportWatchWalletUiState
import kotlinx.coroutines.launch

class ImportWatchWalletViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<ImportWatchWalletUiState>(ImportWatchWalletUiState()) {

    fun onEvent(event: ImportWatchWalletEvent) {
        when (event) {
            is ImportWatchWalletEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ImportWatchWalletEvent.PrimaryActionClicked -> Unit
            ImportWatchWalletEvent.SecondaryActionClicked -> Unit
            ImportWatchWalletEvent.Refresh -> refresh()
        }
    }

    fun submitImport(
        onSuccess: (String?) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val walletName = _uiState.value.fields.firstOrNull { it.key == "walletName" }?.value.orEmpty()
            val networkCode = _uiState.value.fields.firstOrNull { it.key == "networkCode" }?.value.orEmpty()
            val address = _uiState.value.fields.firstOrNull { it.key == "address" }?.value.orEmpty()
            val result = repository.importWatchOnlyWallet(
                walletName = walletName,
                networkCode = networkCode,
                address = address,
            )
            if (result.success) {
                onSuccess(result.walletId)
            } else {
                onError(result.errorMessage ?: "导入观察钱包失败")
            }
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getImportWatchWalletState()
        }
    }
}
