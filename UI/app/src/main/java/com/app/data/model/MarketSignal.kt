package com.app.data.model

enum class SignalSeverity { Low, Medium, High }

data class MarketSignal(
    val id: String,
    val symbol: String,
    val title: String,
    val description: String,
    val severity: SignalSeverity,
)
