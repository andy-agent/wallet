package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.PlansEvent
import com.v2ray.ang.composeui.p1.model.PlansUiState

class PlansViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<PlansUiState>(PlansUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: PlansEvent) {
        when (event) {
            PlansEvent.PrimaryActionClicked -> Unit
            PlansEvent.SecondaryActionClicked -> Unit
            PlansEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getPlansState()
        }
    }
}
