package com.app.core.utils

fun String.maskMiddle(keepStart: Int = 6, keepEnd: Int = 4): String {
    if (length <= keepStart + keepEnd) return this
    val middle = "*".repeat(length - keepStart - keepEnd)
    return take(keepStart) + middle + takeLast(keepEnd)
}

fun Double.safePercent(): String = if (this > 0) "+${"%.2f".format(this)}%" else "${"%.2f".format(this)}%"
