package com.pleac.tmdb_test.di

import com.pleac.agc.data.local.PostDatabase
import com.pleac.agc.data.remote.PostApiService
import com.pleac.agc.data.repository.PostRemoteMediator
import com.pleac.tmdb_test.domain.repository.PostRepository
import com.pleac.agc.data.repository.PostRepositoryImpl
import com.pleac.agc.domain.usecase.GetPostsUseCase
import com.pleac.tmdb_test.presentation.Viewmodels.DetailsViewModel
import com.pleac.tmdb_test.presentation.Viewmodels.PostViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {


   // single { androidContext() }
    single {
        PostRemoteMediator(
            apiService = get(),
            postDao = get()
        )
    }

    factory {
        PostRemoteMediator(
            apiService = get(),
            postDao = get()
        )
    }
    // ApiService
    single { PostApiService() }


    single<PostRepository> { PostRepositoryImpl(get(), get()) }
    //single{ PostRepositoryImpl(get(),get())  }


    // DAO
    single { PostDatabase.getDatabase(androidContext()).postDao() }
    single { PostDatabase.getDatabase(androidContext()).favoriteDao() }
    // UseCase
    factory { GetPostsUseCase(get(),androidContext()) }



    // ViewModel
    viewModel { PostViewModel(get(),get()) }
    viewModel { DetailsViewModel(get()) }
}

