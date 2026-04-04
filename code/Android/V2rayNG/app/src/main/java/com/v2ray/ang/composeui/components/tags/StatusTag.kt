package com.v2ray.ang.composeui.components.tags

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Status type enum
 */
enum class StatusType {
    SUCCESS,
    WARNING,
    ERROR,
    INFO,
    PENDING
}

/**
 * Status tag component skeleton
 * Full implementation will be added in subsequent tasks
 */

@Composable
fun StatusTag(
    text: String,
    type: StatusType,
    modifier: Modifier = Modifier
) {
    // Skeleton implementation
}
