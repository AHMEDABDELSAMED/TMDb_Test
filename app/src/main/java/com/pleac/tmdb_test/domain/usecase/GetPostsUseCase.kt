package com.pleac.agc.domain.usecase


import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pleac.agc.data.data.Post
import com.pleac.tmdb_test.domain.model.UiState
import com.pleac.tmdb_test.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class GetPostsUseCase(
    private val repository: PostRepository,
    private val context: Context
) {
    fun getPostsFlow(): Flow<PagingData<Post>> = flow {
        emitAll(repository.getPagedPosts())
    }.catch { e ->
        throw e
    }
}



