package com.example.cskh.di

import com.example.cskh.data.remote.JsonConfig
import com.example.cskh.data.remote.createAppHttpClient
import com.example.cskh.data.repository.AuthRepositoryImpl
import com.example.cskh.data.repository.CustomerRepositoryImpl
import com.example.cskh.data.repository.InvoiceRepositoryImpl
import com.example.cskh.data.session.SessionManager
import com.example.cskh.data.settings.UserPreferences
import com.example.cskh.domain.preferences.UserFormStore
import com.example.cskh.domain.repository.AuthRepository
import com.example.cskh.domain.repository.CustomerRepository
import com.example.cskh.domain.repository.InvoiceRepository
import com.example.cskh.domain.usecase.DownloadAndSaveEInvoiceZipUseCase
import com.example.cskh.domain.usecase.GetCustomerMeUseCase
import com.example.cskh.domain.usecase.GetInvoiceDetailUseCase
import com.example.cskh.domain.usecase.GetInvoicesUseCase
import com.example.cskh.domain.usecase.LoginUseCase
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
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { JsonConfig.json }
    single<HttpClient> { createAppHttpClient(get()) }
    single<BinaryGetDownloader> { createBinaryGetDownloader() }
    single { SessionManager() }
    single<Settings> { Settings() }
    single<UserFormStore> { UserPreferences(get()) }
    single { UserFormPreferencesUseCase(get()) }
    single { LoginUseCase(get()) }
    single { GetInvoicesUseCase(get()) }
    single { GetInvoiceDetailUseCase(get()) }
    single<InvoiceZipSaver> { InvoiceZipSaverImpl() }
    single<QrPngSaver> { QrPngSaverImpl() }
    single { DownloadAndSaveEInvoiceZipUseCase(get(), get()) }
    single { GetCustomerMeUseCase(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<InvoiceRepository> { InvoiceRepositoryImpl(get(), get(), get()) }
    single<CustomerRepository> { CustomerRepositoryImpl(get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { InvoiceListViewModel(get(), get()) }
    viewModel { CustomerProfileViewModel(get(), get()) }
    viewModel { (id: Long) -> InvoiceDetailViewModel(get(), get(), get(), id) }
}
