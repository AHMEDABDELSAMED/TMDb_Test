package com.pleac.agc.data.data

import com.pleac.tmdb_test.domain.model.FavoriteEntity
import com.pleac.tmdb_test.domain.model.PostEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class MoviesResponse(
    val page: Int,
    val results: List<Post>
)

@Serializable
data class Post(
    val id: Int,
    val title: String,
    @SerialName("original_title") val originalTitle: String,
    @SerialName("original_language") val originalLanguage: String,
    @SerialName("release_date") val releaseDate: String,
    val overview: String,
    val popularity: Double,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("backdrop_path") val backdropPath: String?,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("vote_count") val voteCount: Int,
    val adult: Boolean,
    val video: Boolean,
    @SerialName("genre_ids") val genreIds: List<Int>
)
fun Post.toEntity(page: Int) = PostEntity(
    id = id,
    title = title,
    originalTitle = originalTitle,
    originalLanguage = originalLanguage,
    releaseDate = releaseDate,
    overview = overview,
    popularity = popularity,
    posterPath = posterPath ?: "",
    backdropPath = backdropPath ?: "",
    voteAverage = voteAverage,
    voteCount = voteCount,
    adult = adult,
    video = video,
    genreIds = genreIds.joinToString(","),
    pageNumber = page
)
fun Post.toFavoriteEntity(): FavoriteEntity {
    return FavoriteEntity(
        id = this.id,
        title = this.title
    )
}


