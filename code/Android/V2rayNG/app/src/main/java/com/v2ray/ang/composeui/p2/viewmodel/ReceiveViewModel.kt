package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.ReceiveEvent
import com.v2ray.ang.composeui.p2.model.ReceiveUiState
import com.v2ray.ang.composeui.p2.model.ReceiveRouteArgs
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ReceiveViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: ReceiveRouteArgs = ReceiveRouteArgs(),
) : BaseFeatureViewModel<ReceiveUiState>(initialReceiveState()) {

    init {
        refresh()
    }

    fun onEvent(event: ReceiveEvent) {
        when (event) {
            is ReceiveEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ReceiveEvent.PrimaryActionClicked -> Unit
            ReceiveEvent.SecondaryActionClicked -> Unit
            ReceiveEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, emptyMessage = null)
        viewModelScope.launch {
            runCatching { repository.getReceiveState(routeArgs) }
                .onSuccess { _uiState.value = it.copy(isLoading = false) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.message ?: "加载收款页失败") }
        }
    }
}

private fun initialReceiveState() = ReceiveUiState(
    badge = "",
    summary = "",
    primaryActionLabel = null,
    secondaryActionLabel = null,
    metrics = emptyList(),
    fields = emptyList(),
    highlights = emptyList(),
    checklist = emptyList(),
    note = "",
    isLoading = true,
)
