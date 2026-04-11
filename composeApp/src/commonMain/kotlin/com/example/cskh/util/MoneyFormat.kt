package com.example.cskh.util

fun Double.formatVnd(): String {
    val n = kotlin.math.round(this).toLong()
    val dotted = n.toString().reversed().chunked(3).joinToString(".").reversed()
    return "$dotted đ"
}
