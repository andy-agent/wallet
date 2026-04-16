package com.app.feature.market.components

import androidx.compose.runtime.Composable
import com.app.common.components.GradientCard
import com.app.data.model.MarketSignal

@Composable
fun SignalCard(signal: MarketSignal) {
    GradientCard(title = signal.symbol, subtitle = signal.title) {
        androidx.compose.material3.Text(signal.description)
    }
}
