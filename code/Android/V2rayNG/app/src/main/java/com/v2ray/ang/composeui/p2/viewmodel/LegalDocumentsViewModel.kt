package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.LegalDocumentsEvent
import com.v2ray.ang.composeui.p2.model.LegalDocumentsUiState

class LegalDocumentsViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<LegalDocumentsUiState>(LegalDocumentsUiState()) {

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
        launchLoad {
            repository.getLegalDocumentsState()
        }
    }
}
