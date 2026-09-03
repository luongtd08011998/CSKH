package com.example.cskh.domain.model

data class AppConfig(
    val id: Long,
    val platform: String,
    val latestVersion: String,
    val latestVersionCode: Int,
    val minRequiredVersion: String,
    val minRequiredVersionCode: Int,
    val storeUrl: String,
    val releaseNotes: String?,
    val active: Int,
)
