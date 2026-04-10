package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.ExpiryReminderEvent
import com.cryptovpn.ui.p2extended.model.ExpiryReminderUiState
import com.cryptovpn.ui.p2extended.model.ExpiryReminderRouteArgs

class ExpiryReminderViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: ExpiryReminderRouteArgs = ExpiryReminderRouteArgs(),
) : BaseFeatureViewModel<ExpiryReminderUiState>(ExpiryReminderUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: ExpiryReminderEvent) {
        when (event) {
            is ExpiryReminderEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ExpiryReminderEvent.PrimaryActionClicked -> Unit
            ExpiryReminderEvent.SecondaryActionClicked -> Unit
            ExpiryReminderEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getExpiryReminderState(routeArgs)
        }
    }
}
