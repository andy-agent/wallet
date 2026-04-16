package com.app.common.state

sealed interface ActionState {
    data object Idle : ActionState
    data object Running : ActionState
    data class Success(val message: String) : ActionState
    data class Error(val message: String) : ActionState
}
