package com.cryptovpn.ui.p2extended.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2extended.model.StakingEarnEvent
import com.cryptovpn.ui.p2extended.model.StakingEarnUiState

class StakingEarnViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<StakingEarnUiState>(StakingEarnUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: StakingEarnEvent) {
        when (event) {
            is StakingEarnEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            StakingEarnEvent.PrimaryActionClicked -> Unit
            StakingEarnEvent.SecondaryActionClicked -> Unit
            StakingEarnEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getStakingEarnState()
        }
    }
}
