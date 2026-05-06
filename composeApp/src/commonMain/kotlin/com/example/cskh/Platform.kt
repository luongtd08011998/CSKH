package com.example.cskh

interface Platform {
    val name: String
    val platformType: String
}

expect fun getPlatform(): Platform