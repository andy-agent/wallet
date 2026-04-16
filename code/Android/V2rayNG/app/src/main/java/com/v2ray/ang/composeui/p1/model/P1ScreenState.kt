package com.v2ray.ang.composeui.p1.model

data class P1ScreenState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emptyMessage: String? = null,
    val unavailableMessage: String? = null,
) {
    val hasError: Boolean
        get() = !errorMessage.isNullOrBlank()

    val isEmpty: Boolean
        get() = !emptyMessage.isNullOrBlank()

    val isUnavailable: Boolean
        get() = !unavailableMessage.isNullOrBlank()
}
