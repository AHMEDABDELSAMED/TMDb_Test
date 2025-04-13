package com.pleac.tmdb_test.presentation.Viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pleac.tmdb_test.domain.model.PostEntity
import com.pleac.agc.data.local.PostDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val postDao: PostDao,
) : ViewModel() {

    private val _post = MutableStateFlow<PostEntity?>(null)
    val post: StateFlow<PostEntity?> = _post

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            // get from room
            val localPost = postDao.getPostById(movieId)
            if (localPost != null) {
                _post.value = localPost
            }
        }
    }
}