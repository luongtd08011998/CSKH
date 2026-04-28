package com.example.cskh.domain.model

data class FeedbackItem(
    val id: Long,
    val trackingCode: String,
    val issueType: String,
    val location: String,
    val description: String,
    val status: String,
    val images: List<String>,
    val createdAt: String,
)

data class StaffReply(
    val id: Long,
    val staffName: String,
    val staffEmail: String,
    val staffAvatar: String,
    val content: String,
    val createdAt: String,
)

data class FeedbackDetail(
    val id: Long,
    val trackingCode: String,
    val issueType: String,
    val location: String,
    val description: String,
    val status: String,
    val images: List<String>,
    val replies: List<StaffReply>,
    val createdAt: String,
    val updatedAt: String,
)

