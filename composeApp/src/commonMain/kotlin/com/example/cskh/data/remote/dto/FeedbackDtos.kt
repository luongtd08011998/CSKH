package com.example.cskh.data.remote.dto

import com.example.cskh.domain.model.FeedbackItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackCreateResponseDto(
    val statusCode: Int? = null,
    val message: String? = null,
    val data: FeedbackCreateDataDto? = null,
)

@Serializable
data class FeedbackCreateDataDto(
    val trackingCode: String? = null,
)

@Serializable
data class FeedbackListResponseDto(
    val statusCode: Int? = null,
    val message: String? = null,
    val data: List<FeedbackItemDto>? = null,
)

@Serializable
data class FeedbackItemDto(
    val id: Long,
    val trackingCode: String,
    val issueType: String,
    val location: String,
    val description: String,
    val status: String,
    val images: List<String> = emptyList(),
    val createdAt: String,
)

fun FeedbackItemDto.toDomain(): FeedbackItem = FeedbackItem(
    id = id,
    trackingCode = trackingCode,
    issueType = issueType,
    location = location,
    description = description,
    status = status,
    images = images,
    createdAt = createdAt,
)

