package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.LegalDocumentDetailEvent
import com.v2ray.ang.composeui.p2.model.LegalDocumentDetailUiState
import com.v2ray.ang.composeui.p2.model.LegalDocumentDetailRouteArgs
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LegalDocumentDetailViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: LegalDocumentDetailRouteArgs = LegalDocumentDetailRouteArgs(),
) : BaseFeatureViewModel<LegalDocumentDetailUiState>(initialLegalDocumentDetailState()) {

    init {
        refresh()
    }

    fun onEvent(event: LegalDocumentDetailEvent) {
        when (event) {
            is LegalDocumentDetailEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            LegalDocumentDetailEvent.PrimaryActionClicked -> Unit
            LegalDocumentDetailEvent.SecondaryActionClicked -> Unit
            LegalDocumentDetailEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, emptyMessage = null)
        viewModelScope.launch {
            runCatching { repository.getLegalDocumentDetailState(routeArgs) }
                .onSuccess { _uiState.value = it.copy(isLoading = false) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.message ?: "加载法务详情失败") }
        }
    }
}

private fun initialLegalDocumentDetailState() = LegalDocumentDetailUiState(
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
