package com.pleac.tmdb_test.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pleac.tmdb_test.domain.model.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: FavoriteEntity)

    @Delete
    suspend fun delete(movie: FavoriteEntity)

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :movieId)")
    suspend fun isFavorite(movieId: Int): Boolean
}