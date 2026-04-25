package com.example.cskh.data.repository

import com.example.cskh.data.remote.dto.FeedbackCreateResponseDto
import com.example.cskh.data.remote.dto.FeedbackListResponseDto
import com.example.cskh.data.remote.dto.toDomain
import com.example.cskh.data.session.SessionManager
import com.example.cskh.domain.model.FeedbackItem
import com.example.cskh.domain.repository.FeedbackRepository
import com.example.cskh.platform.PickedImage
import com.example.cskh.platform.prepareUploadImage
import com.example.cskh.util.normalizeApiBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.ContentType
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.HeadersBuilder
import io.ktor.http.ContentDisposition
import kotlin.experimental.and

class FeedbackRepositoryImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager,
) : FeedbackRepository {

    override suspend fun createFeedback(
        baseUrl: String,
        issueType: String,
        location: String,
        description: String,
        images: List<PickedImage>,
    ): Result<String> = runCatching {
        val token = sessionManager.accessToken ?: error("Chưa đăng nhập")
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/customer/feedbacks"

        val safeImages = images.take(5)

        debugFeedbackLog(
            buildString {
                appendLine("request_create_feedback")
                appendLine("  url='$url'")
                appendLine("  issueType='$issueType'")
                appendLine("  location='$location'")
                appendLine("  description='$description'")
                appendLine("  imagesCount=${safeImages.size}")
                if (images.size > safeImages.size) appendLine("  imagesTruncated=true (max=5)")
            }.trimEnd(),
        )

        val content = MultiPartFormDataContent(
            formData {
                // Some backends are strict about part content-types for multipart.
                val textHeaders = Headers.build { append(HttpHeaders.ContentType, ContentType.Text.Plain.toString()) }
                append("issueType", issueType, headers = textHeaders)
                append("location", location, headers = textHeaders)
                append("description", description, headers = textHeaders)

                safeImages.forEach { img ->
                    val prepared = prepareUploadImage(img)
                    val bytes = prepared.bytes
                    val contentType = prepared.contentType
                    val uploadName = prepared.filename
                    debugFeedbackLog(
                        buildString {
                            append("upload_image")
                            append(" name='${img.name}'")
                            append(" uploadName='${uploadName}'")
                            append(" uri='${img.uri}'")
                            append(" sizeBytes=${bytes.size}")
                            append(" contentType='${contentType}'")
                            append(" magic='${bytesMagicHex(bytes, 16)}'")
                        },
                    )
                    append(
                        key = "images",
                        value = bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, contentType.toString())
                            append(HttpHeaders.ContentDisposition, "filename=\"$uploadName\"")
                        },
                    )
                }
            },
        )

        debugFeedbackLog("multipart_content_type='${content.contentType}'")

        val response = client.post(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(content)
        }

        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }

        val envelope = response.body<FeedbackCreateResponseDto>()
        envelope.data?.trackingCode ?: error(envelope.message ?: "Không nhận được trackingCode")
    }

    override suspend fun getFeedbacks(baseUrl: String): Result<List<FeedbackItem>> = runCatching {
        val token = sessionManager.accessToken ?: error("Chưa đăng nhập")
        val url = "${normalizeApiBaseUrl(baseUrl)}/api/v1/qlkh/customer/feedbacks"
        val response = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        if (response.status.value !in 200..299) {
            val text = runCatching { response.bodyAsText() }.getOrNull()
            error(text ?: "HTTP ${response.status.value}")
        }
        val envelope = response.body<FeedbackListResponseDto>()
        (envelope.data ?: emptyList()).map { it.toDomain() }
    }
}

private fun debugFeedbackLog(message: String) {
    // Works on both Android/iOS (KMP) and shows up in Logcat / Xcode console.
    println("CSKH_FEEDBACK: $message")
}

private fun bytesMagicHex(bytes: ByteArray, max: Int): String {
    if (bytes.isEmpty()) return ""
    val n = minOf(bytes.size, max)
    val sb = StringBuilder(n * 2)
    for (i in 0 until n) {
        val b = bytes[i].toInt() and 0xFF
        sb.append("0123456789ABCDEF"[b ushr 4])
        sb.append("0123456789ABCDEF"[b and 0x0F])
    }
    return sb.toString()
}

private fun guessImageContentType(filename: String): ContentType {
    val ext = filename.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "png" -> ContentType.Image.PNG
        "webp" -> ContentType.parse("image/webp")
        "gif" -> ContentType.Image.GIF
        else -> ContentType.Image.JPEG
    }
}

