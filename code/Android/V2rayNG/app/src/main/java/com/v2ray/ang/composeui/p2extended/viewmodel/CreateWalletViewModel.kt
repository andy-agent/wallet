package com.v2ray.ang.composeui.p2extended.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.CreateWalletEvent
import com.v2ray.ang.composeui.p2extended.model.CreateWalletUiState
import com.v2ray.ang.composeui.p2extended.model.CreateWalletRouteArgs
import com.v2ray.ang.composeui.p2extended.model.createWalletLoadingState
import kotlinx.coroutines.launch

class CreateWalletViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: CreateWalletRouteArgs = CreateWalletRouteArgs(),
) : BaseFeatureViewModel<CreateWalletUiState>(createWalletLoadingState()) {

    init {
        refresh()
    }

    fun onEvent(event: CreateWalletEvent) {
        when (event) {
            is CreateWalletEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            CreateWalletEvent.PrimaryActionClicked -> Unit
            CreateWalletEvent.SecondaryActionClicked -> Unit
            CreateWalletEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.value = createWalletLoadingState()
            _uiState.value = repository.getCreateWalletState(routeArgs)
        }
    }

    fun submitCreate(
        onSuccess: (String?) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val walletName = _uiState.value.fields.firstOrNull { it.key == "name" }?.value.orEmpty()
            val result = repository.createWallet(walletName)
            if (result.success) {
                refresh()
                onSuccess(result.walletId)
            } else {
                onError(result.errorMessage ?: "创建钱包失败")
            }
        }
    }
}
