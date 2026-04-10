package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.ExpiryReminderEvent
import com.v2ray.ang.composeui.p2extended.model.ExpiryReminderUiState
import com.v2ray.ang.composeui.p2extended.model.ExpiryReminderRouteArgs

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
