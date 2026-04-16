package com.app.common.state

data class SnackbarState(
    val message: String,
    val actionLabel: String? = null,
)
