package com.pleac.agc.data.remote

import com.pleac.agc.data.data.MoviesResponse
import com.pleac.agc.data.data.Post
import io.ktor.client.call.body
import io.ktor.client.request.*


class PostApiService {
    private val client = ApiClient.client

    // fetchMovies
    suspend fun fetchPosts(page: Int): List<Post> {
        val maxRetries = 3
        var currentAttempt = 0

        while (currentAttempt < maxRetries) {
            try {
                val response: MoviesResponse = client.get("https://api.themoviedb.org/3/discover/movie") {
                    parameter("api_key", "25a106ac989fe8bfda1dfa5c92602aa4")
                    parameter("page", page)
                }.body()

                println("Page: $page, get posts: ${response.results.size}")
                return response.results
            } catch (e: Exception) {
                // try again if filled connection
                currentAttempt++
                if (currentAttempt >= maxRetries) {
                    e.printStackTrace()
                    return emptyList()
                }
                kotlinx.coroutines.delay(1000)
            }
        }

        return emptyList() // fallback
    }

}
