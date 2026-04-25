package com.example.cskh.domain.repository

import com.example.cskh.domain.model.FeedbackItem
import com.example.cskh.platform.PickedImage

interface FeedbackRepository {
    suspend fun createFeedback(
        baseUrl: String,
        issueType: String,
        location: String,
        description: String,
        images: List<PickedImage>,
    ): Result<String> // trackingCode

    suspend fun getFeedbacks(baseUrl: String): Result<List<FeedbackItem>>
}

