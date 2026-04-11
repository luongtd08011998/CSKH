package com.example.cskh.presentation.navigation

object NavRoutes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val INVOICES = "invoices"
    const val INVOICE_DETAIL = "invoice/{id}"
    const val WATER_PRICE = "water_price"
    const val ABOUT = "about"
    const val CUSTOMER_PROFILE = "customer_profile"

    fun invoiceDetail(id: Long): String = "invoice/$id"
}
