package com.pleac.agc.data.repository


import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.pleac.agc.data.data.Post
import com.pleac.agc.data.local.PostDao
import com.pleac.agc.data.remote.PostApiService
import com.pleac.tmdb_test.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class PostRepositoryImpl(
    private val apiService: PostApiService,
    private val postDao: PostDao
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            remoteMediator = PostRemoteMediator(apiService, postDao),
            pagingSourceFactory = { postDao.getAllPosts() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }
}