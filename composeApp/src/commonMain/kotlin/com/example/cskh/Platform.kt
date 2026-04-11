package com.example.cskh

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform