package com.example.cskh

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cskh.data.session.SessionManager
import com.example.cskh.di.appModule
import com.example.cskh.presentation.navigation.NavRoutes
import com.example.cskh.presentation.screens.customer.CustomerProfileScreen
import com.example.cskh.presentation.screens.home.HomeScreen
import com.example.cskh.presentation.screens.invoices.InvoiceDetailScreen
import com.example.cskh.presentation.screens.invoices.InvoiceListScreen
import com.example.cskh.presentation.screens.login.LoginScreen
import com.example.cskh.presentation.screens.static.AboutScreen
import com.example.cskh.presentation.screens.static.WaterPriceScreen
import com.example.cskh.presentation.theme.CskhTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(appModule)
        },
    ) {
        CskhTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                MainNavHost()
            }
        }
    }
}

@Composable
private fun MainNavHost() {
    val sessionManager = koinInject<SessionManager>()
    val navController = rememberNavController()
    val startDestination = remember {
        if (!sessionManager.accessToken.isNullOrBlank()) {
            NavRoutes.HOME
        } else {
            NavRoutes.LOGIN
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
            )
        }
        composable(NavRoutes.HOME) {
            HomeScreen(
                onNavigateInvoices = { navController.navigate(NavRoutes.INVOICES) },
                onNavigateCustomerProfile = { navController.navigate(NavRoutes.CUSTOMER_PROFILE) },
                onNavigateWaterPrice = { navController.navigate(NavRoutes.WATER_PRICE) },
                onNavigateAbout = { navController.navigate(NavRoutes.ABOUT) },
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                },
            )
        }
        composable(NavRoutes.INVOICES) {
            InvoiceListScreen(
                onBack = { navController.popBackStack() },
                onOpenDetail = { id ->
                    navController.navigate(NavRoutes.invoiceDetail(id))
                },
            )
        }
        composable(
            route = NavRoutes.INVOICE_DETAIL,
            arguments = listOf(
                navArgument("id") { type = NavType.LongType },
            ),
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: return@composable
            InvoiceDetailScreen(
                invoiceId = id,
                onBack = { navController.popBackStack() },
            )
        }
        composable(NavRoutes.CUSTOMER_PROFILE) {
            CustomerProfileScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.WATER_PRICE) {
            WaterPriceScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
