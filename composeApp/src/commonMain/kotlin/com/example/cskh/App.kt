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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.cskh.presentation.screens.feedback.FeedbackNotificationScreen
import com.example.cskh.presentation.screens.splash.SplashScreen
import com.example.cskh.presentation.screens.static.AboutScreen
import com.example.cskh.presentation.screens.register.RegisterScreen
import com.example.cskh.presentation.screens.static.WaterPriceScreen
import com.example.cskh.presentation.theme.CskhTheme
import com.example.cskh.platform.NotificationPermissionGate
import com.example.cskh.util.NavigationEvent
import com.example.cskh.util.PushNavigationBus
import org.koin.compose.koinInject

@Composable
fun App(
    pendingArticleTitle: String? = null,
    pendingArticleContent: String? = null,
    pendingFeedbackId: Long? = null,
    pendingInvoiceId: Long? = null,
    pendingNavigateTo: String? = null,
    onNavigationHandled: () -> Unit = {},
) {
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
            MainNavHost(
                    pendingArticleTitle = pendingArticleTitle,
                    pendingArticleContent = pendingArticleContent,
                    pendingFeedbackId = pendingFeedbackId,
                    pendingInvoiceId = pendingInvoiceId,
                    pendingNavigateTo = pendingNavigateTo,
                    onNavigationHandled = onNavigationHandled,
                )
            }
        }
    }
}

@Composable
private fun MainNavHost(
    pendingArticleTitle: String? = null,
    pendingArticleContent: String? = null,
    pendingFeedbackId: Long? = null,
    pendingInvoiceId: Long? = null,
    pendingNavigateTo: String? = null,
    onNavigationHandled: () -> Unit = {},
) {
    val sessionManager = koinInject<SessionManager>()
    val notificationBadgeStore = koinInject<NotificationBadgeStore>()
    val pushNavigationBus = koinInject<PushNavigationBus>()
    val unreadNotificationCount by notificationBadgeStore.unreadCount.collectAsState()
    val navController = rememberNavController()

    // ── Giai đoạn 1: Splash Screen ──
    var showSplash by remember { mutableStateOf(true) }

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

    // Spec phananh_reply.md §1: FEEDBACK push → navigate FeedbackDetailScreen(id)
    LaunchedEffect(pendingFeedbackId) {
        if (pendingFeedbackId != null && pendingFeedbackId > 0) {
            navController.navigate(Screen.PhanAnhDetail(pendingFeedbackId))
            onNavigationHandled()
        }
    }

    // Xử lý sự kiện từ PushNavigationBus (khi nhận notification ở foreground)
    LaunchedEffect(Unit) {
        pushNavigationBus.events.collect { event ->
            when (event) {
                is NavigationEvent.FeedbackDetail -> {
                    navController.navigate(Screen.PhanAnhDetail(event.id))
                }
                is NavigationEvent.InvoiceDetail -> {
                    navController.navigate(Screen.InvoiceDetail(event.id))
                }
            }
        }
    }

    // Hóa đơn: tap push có invoiceId → mở trực tiếp chi tiết hóa đơn
    LaunchedEffect(pendingInvoiceId) {
        if (pendingInvoiceId != null && pendingInvoiceId > 0) {
            navController.navigate(Screen.InvoiceDetail(pendingInvoiceId))
            onNavigationHandled()
        }
    }

    // Hóa đơn / Thanh toán: tap push không có invoiceId → mở màn hình Danh sách Thông báo, Tab Hóa đơn
    LaunchedEffect(pendingNavigateTo) {
        if (!pendingNavigateTo.isNullOrBlank()) {
            // Tăng delay để chắc chắn NavHost đã ổn định startDestination
            kotlinx.coroutines.delay(300)
            
            val targetRoute = when (pendingNavigateTo) {
                "notifications_billing" -> Screen.Notifications(initialTab = 0)
                "notifications_maintenance" -> Screen.Notifications(initialTab = 1)
                "notifications_featured" -> Screen.Notifications(initialTab = 2)
                else -> null
            }
            
            targetRoute?.let { route ->
                navController.navigate(route) {
                    launchSingleTop = true
                }
                onNavigationHandled()
            }
        }
    }

    // Wrap toàn bộ UI với AnimatedContent để chuyển từ Splash → App mượt
    AnimatedContent(
        targetState = showSplash,
        transitionSpec = {
            fadeIn(tween(400)) togetherWith fadeOut(tween(300))
        },
        label = "splashToAppTransition",
    ) { isSplash ->
        if (isSplash) {
            SplashScreen(onFinished = { showSplash = false })
            return@AnimatedContent
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
                        navController.navigate(Screen.Notifications(initialTab = 0)) {
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
                        onNavigateRegister = {
                            navController.navigate(Screen.Register)
                        },
                    )
                }
                composable<Screen.Home> {
                    HomeScreen(
                        onNavigateInvoices = { navController.navigate(Screen.Invoices) },
                        onNavigateInvoiceDetail = { id ->
                            navController.navigate(Screen.InvoiceDetail(id))
                        },
                        onNavigateNotifications = { navController.navigate(Screen.Notifications()) },
                        onNavigateCustomerProfile = { navController.navigate(Screen.CustomerProfile) },
                        onNavigateWaterPrice = { navController.navigate(Screen.WaterPrice) },
                        onNavigateAbout = { navController.navigate(Screen.About) },
                        onNavigatePhanAnh = { navController.navigate(Screen.PhanAnh) },
                        onNavigateFeedbackNotifications = { navController.navigate(Screen.FeedbackNotifications) },
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
                composable<Screen.Notifications> { entry ->
                    val route: Screen.Notifications = entry.toRoute()
                    NotificationListScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateArticle = { title, content ->
                            navController.navigate(Screen.ArticleDetail(title, content))
                        },
                        // Spec phananh_reply.md §4: type=FEEDBACK → PhanAnhDetailScreen
                        onNavigateFeedback = { feedbackId ->
                            navController.navigate(Screen.PhanAnhDetail(feedbackId))
                        },
                        // Hóa đơn / Thanh toán: click thẻ trong tab Thông báo → Danh sách Hóa đơn
                        onNavigateInvoices = { invoiceId ->
                            if (invoiceId != null) {
                                navController.navigate(Screen.InvoiceDetail(invoiceId))
                            } else {
                                navController.navigate(Screen.Invoices) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        initialTab = route.initialTab,
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
                composable<Screen.Register> {
                    RegisterScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateHome = {
                            if (!sessionManager.accessToken.isNullOrBlank()) {
                                navController.navigate(Screen.Home) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                    launchSingleTop = true
                                }
                            } else {
                                navController.popBackStack()
                            }
                        },
                    )
                }
                composable<Screen.FeedbackNotifications> {
                    FeedbackNotificationScreen(
                        onBack = { navController.popBackStack() },
                        onLogout = onLogout,
                        onNavigateFeedback = { feedbackId ->
                            navController.navigate(Screen.PhanAnhDetail(feedbackId))
                        },
                        onNavigateFeedbackList = {
                            navController.navigate(Screen.PhanAnh) {
                                launchSingleTop = true
                            }
                        },
                    )
                }
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
