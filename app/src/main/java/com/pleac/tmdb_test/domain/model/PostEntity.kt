package com.pleac.tmdb_test.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pleac.agc.data.data.Post

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val originalTitle: String,
    val originalLanguage: String,
    val releaseDate: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val backdropPath: String,
    val voteAverage: Double,
    val voteCount: Int,
    val adult: Boolean,
    val video: Boolean,
    val genreIds: String,
    val pageNumber: Int
) {
    fun toDomainModel() = Post(
        id = id,
        title = title,
        originalTitle = originalTitle,
        originalLanguage = originalLanguage,
        releaseDate = releaseDate,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        adult = adult,
        video = video,
        genreIds = genreIds.split(",").mapNotNull { it.toIntOrNull() }
    )
}
