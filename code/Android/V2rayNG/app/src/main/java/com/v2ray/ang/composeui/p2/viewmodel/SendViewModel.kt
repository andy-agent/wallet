package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.SendEvent
import com.v2ray.ang.composeui.p2.model.SendUiState
import com.v2ray.ang.composeui.p2.model.SendRouteArgs
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SendViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: SendRouteArgs = SendRouteArgs(),
) : BaseFeatureViewModel<SendUiState>(initialSendState()) {

    init {
        refresh()
    }

    fun onEvent(event: SendEvent) {
        when (event) {
            is SendEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            SendEvent.PrimaryActionClicked -> Unit
            SendEvent.SecondaryActionClicked -> Unit
            SendEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, emptyMessage = null)
        viewModelScope.launch {
            runCatching { repository.getSendState(routeArgs) }
                .onSuccess { _uiState.value = it.copy(isLoading = false) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.message ?: "加载发送页失败") }
        }
    }
}

private fun initialSendState() = SendUiState(
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
