package com.v2ray.ang.composeui.p2extended.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.backupMnemonicLoadingState
import kotlinx.coroutines.launch

class BackupMnemonicViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: BackupMnemonicRouteArgs = BackupMnemonicRouteArgs(),
) : BaseFeatureViewModel<BackupMnemonicUiState>(backupMnemonicLoadingState()) {

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
        viewModelScope.launch {
            _uiState.value = backupMnemonicLoadingState()
            _uiState.value = repository.getBackupMnemonicState(routeArgs)
        }
    }

    fun submitBackupAcknowledgement(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val result = repository.acknowledgeWalletBackup()
            if (result.isSuccess) {
                refresh()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "备份确认失败")
            }
        }
    }
}
