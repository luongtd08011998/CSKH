package com.example.cskh.domain.model

data class CustomerDocument(
    val filename: String,
    val fullname: String,
    val url: String = "",
    val filesize: Long,
)
