package com.pleac.tmdb_test.domain.repository

import androidx.paging.PagingData
import com.pleac.agc.data.data.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface PostRepository {

    fun getPagedPosts(): Flow<PagingData<Post>>


}