package com.pleac.agc.domain.usecase



import androidx.paging.PagingData
import com.pleac.agc.data.data.Post
import com.pleac.tmdb_test.data.local.FavoriteMovieDao
import com.pleac.tmdb_test.domain.model.FavoriteEntity
import com.pleac.tmdb_test.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class GetPostsUseCase(
    private val repository: PostRepository,
    private val favoriteDao: FavoriteMovieDao
) {
    fun getPostsFlow(): Flow<PagingData<Post>> = flow {
        emitAll(repository.getPagedPosts())
    }.catch { e ->
        throw e
    }

    suspend operator fun invoke(post: FavoriteEntity): Int {
        val isFav = favoriteDao.isFavorite(post.id)
        if (!isFav) {
            favoriteDao.insert(FavoriteEntity(post.id, post.title))
        } else {
            favoriteDao.delete(post)
        }
        return post.id
    }

}



