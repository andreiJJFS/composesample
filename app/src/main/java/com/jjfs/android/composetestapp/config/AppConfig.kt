package com.jjfs.android.composetestapp.config

import com.jjfs.android.composetestapp.business.cache.DriverFactory
import com.jjfs.android.composetestapp.business.cache.OrderCache
import com.jjfs.android.composetestapp.business.network.ApolloApiService
import com.jjfs.android.composetestapp.business.network.AuthService
import com.jjfs.android.composetestapp.business.network.HttpClientFactory
import com.jjfs.android.composetestapp.business.repository.AuthRepository
import com.jjfs.android.composetestapp.business.repository.NetworkService
import com.jjfs.android.composetestapp.business.repository.OrderRepository
import com.jjfs.android.composetestapp.business.repository.SitesRepository
import com.jjfs.android.composetestapp.ui.screens.auth.LoginViewModel
import com.jjfs.android.composetestapp.ui.screens.detail.DetailViewModel
import com.jjfs.android.composetestapp.ui.screens.graphql.GraphqlViewModel
import com.jjfs.android.composetestapp.ui.screens.graphql.SitesDataSource
import com.jjfs.android.composetestapp.ui.screens.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppConfig {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val viewModelModules = module {
        viewModel { LoginViewModel(get()) }
        viewModel { MainViewModel(get()) }
        viewModel { DetailViewModel(get()) }
        viewModel { GraphqlViewModel(get()) }
    }

    val dataSource = module {
        single { AuthRepository(get()) }
        single { OrderRepository(get(), get()) }
        single { SitesRepository(get()) }
    }

    val network = module {
        single(createdAtStart = true) { AuthService() }
        single(createdAtStart = true) { ApolloApiService(get()) }
    }

    val cache = module {
        single { DriverFactory(androidContext()) }
        single { OrderCache(get()) }
    }

    val services = module {
        single { NetworkService(androidContext(), applicationScope) }
    }

    val appModules = listOf(viewModelModules, network, services, cache, dataSource)
}
