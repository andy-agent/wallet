package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.InviteShareEvent
import com.v2ray.ang.composeui.p2.model.InviteShareUiState

class InviteShareViewModel : BaseFeatureViewModel<InviteShareUiState>(InviteShareUiState()) {

    fun onEvent(event: InviteShareEvent) {
        when (event) {
            is InviteShareEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }
            InviteShareEvent.PrimaryActionClicked -> Unit
            InviteShareEvent.SecondaryActionClicked -> Unit
            InviteShareEvent.Refresh -> Unit
        }
    }
}
