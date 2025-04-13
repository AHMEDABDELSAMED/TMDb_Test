package com.pleac.agc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pleac.tmdb_test.domain.model.PostEntity
import com.pleac.tmdb_test.domain.model.FavoriteEntity
import com.pleac.tmdb_test.data.local.FavoriteMovieDao
@Database(entities = [PostEntity::class, FavoriteEntity::class], version = 1, exportSchema = false)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun favoriteDao(): FavoriteMovieDao

    companion object {
        @Volatile
        private var INSTANCE: PostDatabase? = null

        fun getDatabase(context: Context): PostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PostDatabase::class.java,
                    "post_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}