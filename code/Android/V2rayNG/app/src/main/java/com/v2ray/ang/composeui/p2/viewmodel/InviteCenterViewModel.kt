package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.InviteCenterEvent
import com.v2ray.ang.composeui.p2.model.InviteCenterUiState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class InviteCenterViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<InviteCenterUiState>(initialInviteCenterState()) {

    init {
        refresh()
    }

    fun onEvent(event: InviteCenterEvent) {
        when (event) {
            is InviteCenterEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            InviteCenterEvent.PrimaryActionClicked -> Unit
            InviteCenterEvent.SecondaryActionClicked -> Unit
            InviteCenterEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, emptyMessage = null)
        viewModelScope.launch {
            runCatching { repository.getInviteCenterState() }
                .onSuccess { _uiState.value = it.copy(isLoading = false) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.message ?: "加载邀请中心失败") }
        }
    }
}

private fun initialInviteCenterState() = InviteCenterUiState(
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
