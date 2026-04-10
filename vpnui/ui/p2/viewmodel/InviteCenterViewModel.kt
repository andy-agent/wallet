package com.cryptovpn.ui.p2.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2.model.InviteCenterEvent
import com.cryptovpn.ui.p2.model.InviteCenterUiState

class InviteCenterViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<InviteCenterUiState>(InviteCenterUiState()) {

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
        launchLoad {
            repository.getInviteCenterState()
        }
    }
}
