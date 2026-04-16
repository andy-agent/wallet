package com.app.data.model

data class RiskSignal(
    val id: String,
    val title: String,
    val description: String,
    val positive: Boolean = false,
)
