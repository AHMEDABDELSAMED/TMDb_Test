package com.pleac.tmdb_test.presentation.Viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pleac.tmdb_test.domain.model.FavoriteEntity
import com.pleac.agc.data.data.Post
import com.pleac.agc.domain.usecase.GetPostsUseCase
import com.pleac.tmdb_test.data.local.FavoriteMovieDao
import com.pleac.tmdb_test.domain.model.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.paging.cachedIn

class PostViewModel(
    private val getPostsUseCase: GetPostsUseCase,
    private val favoriteDao: FavoriteMovieDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<PagingData<Post>>>(UiState.Loading)
    val uiState: StateFlow<UiState<PagingData<Post>>> = _uiState

    val favoriteMovies: StateFlow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )



    fun getPosts() {
        viewModelScope.launch {
            try {
                getPostsUseCase.getPostsFlow()
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _uiState.value = UiState.Success(pagingData)
                    }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Something went wrong")
            }
        }
    }




    fun toggleFavorite(post: FavoriteEntity) {
        viewModelScope.launch {
            val isFav = favoriteDao.isFavorite(post.id)
            if (!isFav) {
                favoriteDao.insert(FavoriteEntity(post.id, post.title))
            } else {
                favoriteDao.delete(post)
            }
        }
    }

}
