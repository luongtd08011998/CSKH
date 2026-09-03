package com.example.cskh.data.remote.dto

import com.example.cskh.domain.model.AppConfig
import kotlinx.serialization.Serializable

@Serializable
data class AppConfigDataDto(
    val id: Long,
    val platform: String,
    val latestVersion: String,
    val latestVersionCode: Int,
    val minRequiredVersion: String,
    val minRequiredVersionCode: Int,
    val storeUrl: String,
    val releaseNotes: String? = null,
    val active: Int,
) {
    fun toDomain() = AppConfig(
        id = id,
        platform = platform,
        latestVersion = latestVersion,
        latestVersionCode = latestVersionCode,
        minRequiredVersion = minRequiredVersion,
        minRequiredVersionCode = minRequiredVersionCode,
        storeUrl = storeUrl,
        releaseNotes = releaseNotes,
        active = active,
    )
}

@Serializable
data class AppConfigResponseDto(
    val statusCode: Int? = null,
    val message: String? = null,
    val data: AppConfigDataDto? = null,
)
