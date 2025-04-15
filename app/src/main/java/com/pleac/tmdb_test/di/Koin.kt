package com.pleac.tmdb_test.di

import androidx.room.Room
import com.pleac.agc.data.local.PostDatabase
import com.pleac.agc.data.remote.ApiClient
import com.pleac.agc.data.remote.PostApiService
import com.pleac.agc.data.repository.PostRemoteMediator
import com.pleac.tmdb_test.domain.repository.PostRepository
import com.pleac.agc.data.repository.PostRepositoryImpl
import com.pleac.agc.domain.usecase.GetPostsUseCase
import com.pleac.tmdb_test.presentation.Viewmodels.DetailsViewModel
import com.pleac.tmdb_test.presentation.Viewmodels.PostViewModel
import io.ktor.client.HttpClient

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        PostRemoteMediator(
            apiService = get(),
            postDao = get()
        )
    }
    // ApiService
    single { PostApiService(get()) }
    single<PostRepository> { PostRepositoryImpl(get(), get()) }
    // DAO
    single<HttpClient> { ApiClient.provideClient() }
    single {
        Room.databaseBuilder(
            get(),
            PostDatabase::class.java,
            "post_database"
        ).build()
    }
    single { get<PostDatabase>().postDao() }
    single { get<PostDatabase>().favoriteDao() }
    // UseCase
    factory { GetPostsUseCase(get(),get()) }
    // ViewModel
    viewModel { PostViewModel(get(),get()) }
    viewModel { DetailsViewModel(get()) }
}

