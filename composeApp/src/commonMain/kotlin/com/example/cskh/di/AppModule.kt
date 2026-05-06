package com.example.cskh.di

import com.example.cskh.data.remote.JsonConfig
import com.example.cskh.data.remote.createAppHttpClient
import com.example.cskh.data.repository.AuthRepositoryImpl
import com.example.cskh.data.repository.CustomerRepositoryImpl
import com.example.cskh.data.repository.DeviceRepositoryImpl
import com.example.cskh.data.repository.FeedbackRepositoryImpl
import com.example.cskh.data.session.TokenRefreshCoordinator
import com.example.cskh.data.repository.InvoiceRepositoryImpl
import com.example.cskh.data.repository.MaintenanceArticleRepositoryImpl
import com.example.cskh.data.repository.FeaturedArticleRepositoryImpl
import com.example.cskh.data.repository.NotificationRepositoryImpl
import com.example.cskh.data.session.SessionManager
import com.example.cskh.data.settings.UserPreferences
import com.example.cskh.domain.preferences.UserFormStore
import com.example.cskh.domain.repository.AuthRepository
import com.example.cskh.domain.repository.CustomerRepository
import com.example.cskh.domain.repository.DeviceRepository
import com.example.cskh.domain.repository.FeedbackRepository
import com.example.cskh.domain.repository.InvoiceRepository
import com.example.cskh.domain.repository.NotificationRepository
import com.example.cskh.domain.repository.MaintenanceArticleRepository
import com.example.cskh.domain.repository.FeaturedArticleRepository
import com.example.cskh.domain.usecase.DownloadAndSaveEInvoiceZipUseCase
import com.example.cskh.domain.usecase.CreateFeedbackUseCase
import com.example.cskh.domain.usecase.GetCustomerMeUseCase
import com.example.cskh.domain.usecase.GetFeedbackDetailUseCase
import com.example.cskh.domain.usecase.GetFeedbacksUseCase
import com.example.cskh.domain.usecase.GetInvoiceDetailUseCase
import com.example.cskh.domain.usecase.GetInvoicesUseCase
import com.example.cskh.domain.usecase.GetMaintenanceArticlesUseCase
import com.example.cskh.domain.usecase.GetFeaturedArticlesUseCase
import com.example.cskh.domain.usecase.GetNotificationsUseCase
import com.example.cskh.domain.usecase.LoginUseCase
import com.example.cskh.domain.usecase.LogoutUseCase
import com.example.cskh.domain.usecase.MarkNotificationsReadUseCase
import com.example.cskh.domain.usecase.BackfillReferenceIdUseCase
import com.example.cskh.domain.usecase.RefreshTokenUseCase
import com.example.cskh.domain.usecase.RegisterFcmDeviceUseCase
import com.example.cskh.domain.usecase.UnregisterFcmDeviceUseCase
import com.example.cskh.domain.usecase.UserFormPreferencesUseCase
import com.example.cskh.platform.BinaryGetDownloader
import com.example.cskh.platform.InvoiceZipSaver
import com.example.cskh.platform.InvoiceZipSaverImpl
import com.example.cskh.platform.QrPngSaver
import com.example.cskh.platform.QrPngSaverImpl
import com.example.cskh.platform.createBinaryGetDownloader
import com.example.cskh.presentation.screens.customer.CustomerProfileViewModel
import com.example.cskh.presentation.screens.home.HomeViewModel
import com.example.cskh.presentation.screens.invoices.InvoiceDetailViewModel
import com.example.cskh.presentation.screens.invoices.InvoiceListViewModel
import com.example.cskh.presentation.screens.login.LoginViewModel
import com.example.cskh.presentation.NotificationBadgeStore
import com.example.cskh.presentation.screens.notifications.NotificationListViewModel
import com.example.cskh.presentation.screens.phananh.PhanAnhDetailViewModel
import com.example.cskh.presentation.screens.phananh.PhanAnhListViewModel
import com.example.cskh.presentation.screens.phananh.PhanAnhViewModel
import com.example.cskh.platform.FcmDeviceSync
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { JsonConfig.json }
    single<HttpClient> { createAppHttpClient(get()) }
    single<BinaryGetDownloader> { createBinaryGetDownloader() }
    single<Settings> { Settings() }
    single<UserFormStore> { UserPreferences(get()) }

    // SessionManager – khởi tạo cả accessToken lẫn refreshToken từ storage
    single {
        val store = get<UserFormStore>()
        SessionManager().apply {
            val access = store.loadAccessToken().takeIf { it.isNotBlank() }
            val refresh = store.loadRefreshToken().takeIf { it.isNotBlank() }
            setToken(access, refresh)
        }
    }

    single { UserFormPreferencesUseCase(get()) }
    single { TokenRefreshCoordinator(get(), get(), get(), get(), get()) }
    single { LoginUseCase(get()) }
    single { RefreshTokenUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { GetInvoicesUseCase(get()) }
    single { GetInvoiceDetailUseCase(get()) }
    single<InvoiceZipSaver> { InvoiceZipSaverImpl() }
    single<QrPngSaver> { QrPngSaverImpl() }
    single { DownloadAndSaveEInvoiceZipUseCase(get(), get()) }
    single { GetCustomerMeUseCase(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<InvoiceRepository> { InvoiceRepositoryImpl(get(), get(), get()) }
    single<CustomerRepository> { CustomerRepositoryImpl(get(), get()) }
    single<DeviceRepository> { DeviceRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get(), get()) }
    single<FeedbackRepository> { FeedbackRepositoryImpl(get(), get()) }
    single { RegisterFcmDeviceUseCase(get(), get()) }
    single { UnregisterFcmDeviceUseCase(get(), get()) }
    single { GetNotificationsUseCase(get()) }
    single<MaintenanceArticleRepository> { MaintenanceArticleRepositoryImpl(get()) }
    single { GetMaintenanceArticlesUseCase(get()) }
    single<FeaturedArticleRepository> { FeaturedArticleRepositoryImpl(get()) }
    single { GetFeaturedArticlesUseCase(get()) }
    single { MarkNotificationsReadUseCase(get()) }
    single { BackfillReferenceIdUseCase(get()) }
    single { NotificationBadgeStore(get(), get()) }
    single { CreateFeedbackUseCase(get(), get()) }
    single { GetFeedbacksUseCase(get(), get()) }
    single { GetFeedbackDetailUseCase(get(), get()) }
    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { InvoiceListViewModel(get(), get(), get()) }
    viewModel { NotificationListViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { CustomerProfileViewModel(get(), get(), get(), get()) }
    viewModel { PhanAnhViewModel(get(), get(), get()) }
    viewModel { PhanAnhListViewModel(get()) }
    viewModel { (id: Long) -> PhanAnhDetailViewModel(get(), get(), id) }
    viewModel { (id: Long) -> InvoiceDetailViewModel(get(), get(), get(), get(), id) }
}
