package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.StakingEarnEvent
import com.v2ray.ang.composeui.p2extended.model.StakingEarnUiState

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
