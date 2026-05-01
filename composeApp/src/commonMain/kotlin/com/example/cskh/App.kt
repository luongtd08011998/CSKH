package com.example.cskh

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.cskh.data.session.SessionManager
import com.example.cskh.di.CskhKoinHost
import com.example.cskh.presentation.NotificationBadgeStore
import com.example.cskh.presentation.navigation.Screen
import com.example.cskh.presentation.screens.customer.CustomerProfileScreen
import com.example.cskh.presentation.screens.home.HomeScreen
import com.example.cskh.presentation.screens.invoices.InvoiceDetailScreen
import com.example.cskh.presentation.screens.invoices.InvoiceListScreen
import com.example.cskh.presentation.screens.login.LoginScreen
import com.example.cskh.presentation.screens.notifications.NotificationListScreen
import com.example.cskh.presentation.screens.article.ArticleDetailScreen
import com.example.cskh.presentation.screens.phananh.PhanAnhDetailScreen
import com.example.cskh.presentation.screens.phananh.PhanAnhScreen
import com.example.cskh.presentation.screens.static.AboutScreen
import com.example.cskh.presentation.screens.static.WaterPriceScreen
import com.example.cskh.presentation.theme.CskhTheme
import com.example.cskh.platform.NotificationPermissionGate
import org.koin.compose.koinInject

@Composable
fun App(pendingArticleTitle: String? = null, pendingArticleContent: String? = null) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .build()
    }
    CskhKoinHost {
        CskhTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                NotificationPermissionGate()
                MainNavHost(pendingArticleTitle = pendingArticleTitle, pendingArticleContent = pendingArticleContent)
            }
        }
    }
}

@Composable
private fun MainNavHost(
    pendingArticleTitle: String? = null,
    pendingArticleContent: String? = null,
) {
    val sessionManager = koinInject<SessionManager>()
    val notificationBadgeStore = koinInject<NotificationBadgeStore>()
    val unreadNotificationCount by notificationBadgeStore.unreadCount.collectAsState()
    val navController = rememberNavController()
    val startDestination: Screen = remember {
        if (!sessionManager.accessToken.isNullOrBlank()) {
            Screen.Home
        } else {
            Screen.Login
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = sessionManager.accessToken?.isNotBlank() == true &&
        currentDestination?.hierarchy?.any { it.route?.contains("Login") == true } != true

    LaunchedEffect(showBottomBar) {
        if (showBottomBar) {
            notificationBadgeStore.refreshFromNetwork()
        } else {
            notificationBadgeStore.clear()
        }
    }

    LaunchedEffect(pendingArticleTitle, pendingArticleContent) {
        if (pendingArticleTitle != null && pendingArticleContent != null) {
            navController.navigate(
                Screen.ArticleDetail(title = pendingArticleTitle, content = pendingArticleContent)
            )
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    unreadNotificationCount = unreadNotificationCount,
                    currentDestination = currentDestination,
                    onSelectHome = {
                        navController.navigate(Screen.Home) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onSelectNotifications = {
                        navController.navigate(Screen.Notifications) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onSelectProfile = {
                        navController.navigate(Screen.CustomerProfile) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Surface(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val onLogout = {
                navController.navigate(Screen.Login) {
                    popUpTo(0) { inclusive = true }
                }
            }
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
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
                        onNavigateInvoiceDetail = { id ->
                            navController.navigate(Screen.InvoiceDetail(id))
                        },
                        onNavigateNotifications = { navController.navigate(Screen.Notifications) },
                        onNavigateCustomerProfile = { navController.navigate(Screen.CustomerProfile) },
                        onNavigateWaterPrice = { navController.navigate(Screen.WaterPrice) },
                        onNavigateAbout = { navController.navigate(Screen.About) },
                        onNavigatePhanAnh = { navController.navigate(Screen.PhanAnh) },
                        onLogout = onLogout,
                    )
                }
                composable<Screen.Invoices> {
                    InvoiceListScreen(
                        onBack = { navController.popBackStack() },
                        onOpenDetail = { id ->
                            navController.navigate(Screen.InvoiceDetail(id))
                        },
                        onLogout = onLogout,
                    )
                }
                composable<Screen.InvoiceDetail> { entry ->
                    val route: Screen.InvoiceDetail = entry.toRoute()
                    InvoiceDetailScreen(
                        invoiceId = route.id,
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout,
                    )
                }
                composable<Screen.Notifications> {
                    NotificationListScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateArticle = { title, content ->
                            navController.navigate(Screen.ArticleDetail(title, content))
                        },
                        onLogout = onLogout,
                    )
                }
                composable<Screen.CustomerProfile> {
                    CustomerProfileScreen(
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout,
                    )
                }
                composable<Screen.WaterPrice> {
                    WaterPriceScreen(onBack = { navController.popBackStack() })
                }
                composable<Screen.About> {
                    AboutScreen(onBack = { navController.popBackStack() })
                }
                composable<Screen.PhanAnh> {
                    PhanAnhScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateDetail = { id ->
                            navController.navigate(Screen.PhanAnhDetail(id))
                        },
                        onLogout = onLogout,
                    )
                }
                composable<Screen.PhanAnhDetail> { entry ->
                    val route: Screen.PhanAnhDetail = entry.toRoute()
                    PhanAnhDetailScreen(
                        feedbackId = route.id,
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout,
                    )
                }
                composable<Screen.ArticleDetail> { entry ->
                    val route: Screen.ArticleDetail = entry.toRoute()
                    ArticleDetailScreen(
                        title = route.title,
                        content = route.content,
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBottomBar(
    unreadNotificationCount: Int,
    currentDestination: androidx.navigation.NavDestination?,
    onSelectHome: () -> Unit,
    onSelectNotifications: () -> Unit,
    onSelectProfile: () -> Unit,
) {
    val selectedHome = currentDestination?.hierarchy?.any { it.route?.contains("Home") == true } == true
    val selectedNotifications =
        currentDestination?.hierarchy?.any { it.route?.contains("Notifications") == true } == true
    val selectedProfile =
        currentDestination?.hierarchy?.any { it.route?.contains("CustomerProfile") == true } == true

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.navigationBarsPadding(),
    ) {
        NavigationBarItem(
            selected = selectedHome,
            onClick = onSelectHome,
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("Trang chủ") },
        )
        NavigationBarItem(
            selected = selectedNotifications,
            onClick = onSelectNotifications,
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadNotificationCount > 0) {
                            Badge {
                                Text(
                                    text = if (unreadNotificationCount > 99) "99+" else unreadNotificationCount.toString(),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    },
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = null)
                }
            },
            label = { Text("Thông báo") },
        )
        NavigationBarItem(
            selected = selectedProfile,
            onClick = onSelectProfile,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Tài khoản") },
        )
    }
}
