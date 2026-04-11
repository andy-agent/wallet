package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.InviteShareEvent
import com.v2ray.ang.composeui.p2.model.InviteShareUiState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class InviteShareViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<InviteShareUiState>(initialInviteShareState()) {

    init {
        refresh()
    }

    fun onEvent(event: InviteShareEvent) {
        when (event) {
            is InviteShareEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }
            InviteShareEvent.PrimaryActionClicked -> Unit
            InviteShareEvent.SecondaryActionClicked -> Unit
            InviteShareEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, emptyMessage = null)
        viewModelScope.launch {
            runCatching { repository.getInviteShareState() }
                .onSuccess { _uiState.value = it.copy(isLoading = false) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.message ?: "加载邀请分享失败") }
        }
    }
}

private fun initialInviteShareState() = InviteShareUiState(
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
