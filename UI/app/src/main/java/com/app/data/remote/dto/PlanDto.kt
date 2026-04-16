package com.app.data.remote.dto

import com.app.data.model.Plan

data class PlanDto(
    val id: String,
    val title: String,
    val durationLabel: String,
    val priceUsd: Double,
    val trafficGb: Int,
    val deviceLimit: Int,
    val recommended: Boolean,
)

fun PlanDto.toModel() = Plan(id, title, durationLabel, priceUsd, trafficGb, deviceLimit, recommended)
