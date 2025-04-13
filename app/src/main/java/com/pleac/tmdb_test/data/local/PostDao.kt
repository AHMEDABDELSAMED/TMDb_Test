package com.pleac.agc.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pleac.tmdb_test.domain.model.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY pageNumber ASC, id ASC")
    fun getAllPosts(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()

    @Query("SELECT * FROM posts WHERE id = :movieId")
    suspend fun getPostById(movieId: Int): PostEntity?

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostsCount(): Int
}
