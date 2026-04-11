package com.example.cskh

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.cskh.data.session.SessionManager
import com.example.cskh.di.appModule
import com.example.cskh.presentation.navigation.Screen
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
    val startDestination: Screen = remember {
        if (!sessionManager.accessToken.isNullOrBlank()) {
            Screen.Home
        } else {
            Screen.Login
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Screen.Login> {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Login> { inclusive = true }
                    }
                },
            )
        }
        composable<Screen.Home> {
            HomeScreen(
                onNavigateInvoices = { navController.navigate(Screen.Invoices) },
                onNavigateCustomerProfile = { navController.navigate(Screen.CustomerProfile) },
                onNavigateWaterPrice = { navController.navigate(Screen.WaterPrice) },
                onNavigateAbout = { navController.navigate(Screen.About) },
                onLogout = {
                    navController.navigate(Screen.Login) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
            )
        }
        composable<Screen.Invoices> {
            InvoiceListScreen(
                onBack = { navController.popBackStack() },
                onOpenDetail = { id ->
                    navController.navigate(Screen.InvoiceDetail(id))
                },
            )
        }
        composable<Screen.InvoiceDetail> { entry ->
            val route: Screen.InvoiceDetail = entry.toRoute()
            InvoiceDetailScreen(
                invoiceId = route.id,
                onBack = { navController.popBackStack() },
            )
        }
        composable<Screen.CustomerProfile> {
            CustomerProfileScreen(onBack = { navController.popBackStack() })
        }
        composable<Screen.WaterPrice> {
            WaterPriceScreen(onBack = { navController.popBackStack() })
        }
        composable<Screen.About> {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
