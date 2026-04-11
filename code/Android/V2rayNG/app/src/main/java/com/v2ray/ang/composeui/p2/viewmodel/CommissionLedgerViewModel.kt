package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.CommissionLedgerEvent
import com.v2ray.ang.composeui.p2.model.CommissionLedgerUiState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CommissionLedgerViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<CommissionLedgerUiState>(initialCommissionLedgerState()) {

    init {
        refresh()
    }

    fun onEvent(event: CommissionLedgerEvent) {
        when (event) {
            is CommissionLedgerEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            CommissionLedgerEvent.PrimaryActionClicked -> Unit
            CommissionLedgerEvent.SecondaryActionClicked -> Unit
            CommissionLedgerEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, emptyMessage = null)
        viewModelScope.launch {
            runCatching { repository.getCommissionLedgerState() }
                .onSuccess { _uiState.value = it.copy(isLoading = false) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.message ?: "加载佣金账本失败") }
        }
    }
}

private fun initialCommissionLedgerState() = CommissionLedgerUiState(
    badge = "",
    summary = "",
    primaryActionLabel = null,
    secondaryActionLabel = null,
    metrics = emptyList(),
    highlights = emptyList(),
    checklist = emptyList(),
    note = "",
    isLoading = true,
)
