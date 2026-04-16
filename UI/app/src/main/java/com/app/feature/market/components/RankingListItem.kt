package com.app.feature.market.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.data.model.MarketTicker
import com.app.core.utils.Formatters

@Composable
fun RankingListItem(index: Int, ticker: MarketTicker, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("#$index ${ticker.symbol}", style = MaterialTheme.typography.bodyMedium)
        Text(Formatters.percent(ticker.change24h), style = MaterialTheme.typography.labelLarge)
    }
}
