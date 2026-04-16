package com.app.core.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val moneyFormat = DecimalFormat("#,##0.00")
    private val compactFormat = DecimalFormat("#,##0.#")
    private val dateTime = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

    fun money(value: Double): String = "$" + moneyFormat.format(value)
    fun percent(value: Double): String = if (value > 0) "+${"%.2f".format(value)}%" else "${"%.2f".format(value)}%"
    fun compact(value: Double): String = when {
        value >= 1_000_000_000 -> "$" + compactFormat.format(value / 1_000_000_000) + "B"
        value >= 1_000_000 -> "$" + compactFormat.format(value / 1_000_000) + "M"
        value >= 1_000 -> "$" + compactFormat.format(value / 1_000) + "K"
        else -> "$" + compactFormat.format(value)
    }
    fun dateTime(value: Long): String = dateTime.format(Date(value))
    fun speed(kbps: Double): String = if (kbps >= 1024) "${"%.1f".format(kbps / 1024)} MB/s" else "${"%.0f".format(kbps)} KB/s"
}
