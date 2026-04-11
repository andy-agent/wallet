package com.v2ray.ang.composeui.p1.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.P1StateInfo
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
            is PlansEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            PlansEvent.PrimaryActionClicked -> Unit
            PlansEvent.SecondaryActionClicked -> Unit
            PlansEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(
            stateInfo = P1StateInfo(P1ScreenState.Loading, message = "正在加载真实套餐..."),
        )
        launchLoad {
            repository.getPlansState()
        }
    }
}
