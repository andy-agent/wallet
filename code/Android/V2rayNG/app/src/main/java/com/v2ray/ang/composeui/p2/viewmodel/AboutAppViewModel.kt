package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.AboutAppEvent
import com.v2ray.ang.composeui.p2.model.AboutAppUiState

class AboutAppViewModel : BaseFeatureViewModel<AboutAppUiState>(AboutAppUiState()) {

    fun onEvent(event: AboutAppEvent) {
        when (event) {
            is AboutAppEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }
            AboutAppEvent.PrimaryActionClicked -> Unit
            AboutAppEvent.SecondaryActionClicked -> Unit
            AboutAppEvent.Refresh -> Unit
        }
    }
}
