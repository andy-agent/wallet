package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicRouteArgs

class BackupMnemonicViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: BackupMnemonicRouteArgs = BackupMnemonicRouteArgs(),
) : BaseFeatureViewModel<BackupMnemonicUiState>(BackupMnemonicUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: BackupMnemonicEvent) {
        when (event) {
            is BackupMnemonicEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            BackupMnemonicEvent.PrimaryActionClicked -> Unit
            BackupMnemonicEvent.SecondaryActionClicked -> Unit
            BackupMnemonicEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getBackupMnemonicState(routeArgs)
        }
    }
}
