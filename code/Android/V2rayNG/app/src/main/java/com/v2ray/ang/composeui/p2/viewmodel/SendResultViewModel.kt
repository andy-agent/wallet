package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.SendResultEvent
import com.v2ray.ang.composeui.p2.model.SendResultUiState
import com.v2ray.ang.composeui.p2.model.SendResultRouteArgs
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SendResultViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: SendResultRouteArgs = SendResultRouteArgs(),
) : BaseFeatureViewModel<SendResultUiState>(initialSendResultState()) {

    init {
        refresh()
    }

    fun onEvent(event: SendResultEvent) {
        when (event) {
            is SendResultEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            SendResultEvent.PrimaryActionClicked -> Unit
            SendResultEvent.SecondaryActionClicked -> Unit
            SendResultEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, emptyMessage = null)
        viewModelScope.launch {
            runCatching { repository.getSendResultState(routeArgs) }
                .onSuccess { _uiState.value = it.copy(isLoading = false) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.message ?: "加载发送结果失败") }
        }
    }
}

private fun initialSendResultState() = SendResultUiState(
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
