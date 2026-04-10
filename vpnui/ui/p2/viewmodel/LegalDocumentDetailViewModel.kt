package com.cryptovpn.ui.p2.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2.model.LegalDocumentDetailEvent
import com.cryptovpn.ui.p2.model.LegalDocumentDetailUiState
import com.cryptovpn.ui.p2.model.LegalDocumentDetailRouteArgs

class LegalDocumentDetailViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: LegalDocumentDetailRouteArgs = LegalDocumentDetailRouteArgs(),
) : BaseFeatureViewModel<LegalDocumentDetailUiState>(LegalDocumentDetailUiState()) {

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
        launchLoad {
            repository.getLegalDocumentDetailState(routeArgs)
        }
    }
}
