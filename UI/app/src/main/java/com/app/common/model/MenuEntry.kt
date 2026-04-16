package com.app.common.model

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuEntry(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val icon: ImageVector? = null,
    val trailing: String = "",
)
