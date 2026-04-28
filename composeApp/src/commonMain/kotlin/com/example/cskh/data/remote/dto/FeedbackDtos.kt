package com.example.cskh.data.remote.dto

import com.example.cskh.domain.model.FeedbackDetail
import com.example.cskh.domain.model.FeedbackItem
import com.example.cskh.domain.model.StaffReply
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

@Serializable
data class FeedbackDetailResponseDto(
    val statusCode: Int? = null,
    val message: String? = null,
    val data: FeedbackDetailDto? = null,
)

@Serializable
data class FeedbackDetailDto(
    val id: Long,
    val trackingCode: String,
    val issueType: String,
    val location: String,
    val description: String,
    val status: String,
    val images: List<String> = emptyList(),
    val replies: List<StaffReplyDto> = emptyList(),
    val createdAt: String,
    val updatedAt: String? = null,
)

@Serializable
data class StaffReplyDto(
    val id: Long,
    val staff: StaffDto? = null,
    val content: String,
    val createdAt: String,
)

@Serializable
data class StaffDto(
    val id: Long,
    val name: String? = null,
    val email: String? = null,
    val avatar: String? = null,
)

fun FeedbackDetailDto.toDomain(): FeedbackDetail = FeedbackDetail(
    id = id,
    trackingCode = trackingCode,
    issueType = issueType,
    location = location,
    description = description,
    status = status,
    images = images,
    replies = replies.map { it.toDomain() },
    createdAt = createdAt,
    updatedAt = updatedAt.orEmpty(),
)

fun StaffReplyDto.toDomain(): StaffReply = StaffReply(
    id = id,
    staffName = staff?.name.orEmpty(),
    staffEmail = staff?.email.orEmpty(),
    staffAvatar = staff?.avatar.orEmpty(),
    content = content,
    createdAt = createdAt,
)

