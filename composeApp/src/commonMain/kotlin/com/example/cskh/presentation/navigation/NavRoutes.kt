package com.example.cskh.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Login : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Invoices : Screen

    @Serializable
    data class InvoiceDetail(val id: Long) : Screen

    @Serializable
    data object Notifications : Screen

    @Serializable
    data object CustomerProfile : Screen

    @Serializable
    data object WaterPrice : Screen

    @Serializable
    data object About : Screen

    @Serializable
    data object PhanAnh : Screen

    @Serializable
    data class PhanAnhDetail(val id: Long) : Screen

    @Serializable
    data class ArticleDetail(val title: String, val content: String) : Screen
}
