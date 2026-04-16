package com.app.feature.wallet.components

import androidx.compose.runtime.Composable
import com.app.common.widgets.TabSwitcher

@Composable
fun NetworkSelector(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    TabSwitcher(options, selected, onSelect)
}
