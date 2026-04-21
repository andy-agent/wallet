package com.v2ray.ang.composeui.p2.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.WithdrawEvent
import com.v2ray.ang.composeui.p2.model.WithdrawUiState
import kotlinx.coroutines.launch

class WithdrawViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<WithdrawUiState>(WithdrawUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: WithdrawEvent) {
        when (event) {
            is WithdrawEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            WithdrawEvent.PrimaryActionClicked -> Unit
            WithdrawEvent.SecondaryActionClicked -> Unit
            WithdrawEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repository.getCachedWithdrawState()?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.getWithdrawState()
        }
    }

    fun submitWithdrawal(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        val address = _uiState.value.fields.firstOrNull { it.key == "address" }?.value.orEmpty().trim()
        val amount = _uiState.value.fields.firstOrNull { it.key == "amount" }?.value.orEmpty().trim()
        if (address.isBlank() || amount.isBlank()) {
            onError("请填写提现地址和提现金额")
            return
        }
        viewModelScope.launch {
            val result = repository.submitWithdrawal(amount = amount, payoutAddress = address)
            if (result.isSuccess) {
                refresh()
                onSuccess(result.getOrNull().orEmpty())
            } else {
                onError(result.exceptionOrNull()?.message ?: "提交提现失败")
            }
        }
    }
}
