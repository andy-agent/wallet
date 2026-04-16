package com.app.common.state

sealed interface LoadState {
    data object Loading : LoadState
    data object Ready : LoadState
    data class Error(val message: String) : LoadState
}
