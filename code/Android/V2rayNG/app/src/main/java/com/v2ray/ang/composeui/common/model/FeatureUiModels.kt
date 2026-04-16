package com.v2ray.ang.composeui.common.model

data class FeatureMetric(
    val label: String,
    val value: String,
)

data class FeatureField(
    val key: String,
    val label: String,
    val value: String,
    val supportingText: String = "",
    val placeholder: String = "",
)

data class FeatureListItem(
    val title: String,
    val subtitle: String = "",
    val trailing: String = "",
    val badge: String = "",
)

data class FeatureBullet(
    val title: String,
    val detail: String = "",
)
