package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.LegalDocumentsEvent
import com.v2ray.ang.composeui.p2.model.LegalDocumentsUiState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LegalDocumentsViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<LegalDocumentsUiState>(initialLegalDocumentsState()) {

    init {
        refresh()
    }

    fun onEvent(event: LegalDocumentsEvent) {
        when (event) {
            is LegalDocumentsEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            LegalDocumentsEvent.PrimaryActionClicked -> Unit
            LegalDocumentsEvent.SecondaryActionClicked -> Unit
            LegalDocumentsEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, emptyMessage = null)
        viewModelScope.launch {
            runCatching { repository.getLegalDocumentsState() }
                .onSuccess { _uiState.value = it.copy(isLoading = false) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.message ?: "加载法务文档失败") }
        }
    }
}

private fun initialLegalDocumentsState() = LegalDocumentsUiState(
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
