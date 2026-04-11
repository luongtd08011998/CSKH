package com.example.cskh.util

fun normalizeApiBaseUrl(raw: String): String {
    val t = raw.trim().trimEnd('/')
    require(t.isNotBlank()) { "Base URL không được để trống" }
    return t
}
