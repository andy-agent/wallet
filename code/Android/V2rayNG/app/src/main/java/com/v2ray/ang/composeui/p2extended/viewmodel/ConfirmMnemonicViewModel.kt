package com.v2ray.ang.composeui.p2extended.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.confirmMnemonicLoadingState
import kotlinx.coroutines.launch

class ConfirmMnemonicViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: ConfirmMnemonicRouteArgs = ConfirmMnemonicRouteArgs(),
) : BaseFeatureViewModel<ConfirmMnemonicUiState>(confirmMnemonicLoadingState()) {

    init {
        refresh()
    }

    fun onEvent(event: ConfirmMnemonicEvent) {
        when (event) {
            is ConfirmMnemonicEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ConfirmMnemonicEvent.PrimaryActionClicked -> Unit
            ConfirmMnemonicEvent.SecondaryActionClicked -> Unit
            ConfirmMnemonicEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.value = confirmMnemonicLoadingState()
            _uiState.value = repository.getConfirmMnemonicState(routeArgs)
        }
    }

    fun submitConfirm(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val result = repository.confirmWalletBackup()
            if (result.isSuccess) {
                refresh()
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "钱包激活失败")
            }
        }
    }
}
