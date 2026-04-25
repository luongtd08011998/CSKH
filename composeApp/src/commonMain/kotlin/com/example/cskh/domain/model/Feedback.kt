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

