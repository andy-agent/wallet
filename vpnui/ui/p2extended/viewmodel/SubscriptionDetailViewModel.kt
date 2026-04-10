package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.SubscriptionDetailEvent
import com.cryptovpn.ui.p2extended.model.SubscriptionDetailUiState
import com.cryptovpn.ui.p2extended.model.SubscriptionDetailRouteArgs

class SubscriptionDetailViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: SubscriptionDetailRouteArgs = SubscriptionDetailRouteArgs(),
) : BaseFeatureViewModel<SubscriptionDetailUiState>(SubscriptionDetailUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: SubscriptionDetailEvent) {
        when (event) {
            is SubscriptionDetailEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            SubscriptionDetailEvent.PrimaryActionClicked -> Unit
            SubscriptionDetailEvent.SecondaryActionClicked -> Unit
            SubscriptionDetailEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getSubscriptionDetailState(routeArgs)
        }
    }
}
