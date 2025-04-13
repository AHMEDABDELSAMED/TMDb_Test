package com.pleac.tmdb_test

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.google.common.truth.Truth
import com.pleac.agc.data.data.Post
import com.pleac.agc.data.local.PostDao
import com.pleac.agc.data.remote.PostApiService
import com.pleac.agc.data.repository.PostRemoteMediator
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Test

import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediatorTest {

    private lateinit var apiService: PostApiService
    private lateinit var postDao: PostDao
    private lateinit var remoteMediator: PostRemoteMediator

    @Before
    fun setup() {
        apiService = mockk()
        postDao = mockk(relaxed = true)
        remoteMediator = PostRemoteMediator(apiService, postDao)
    }

    @Test
    fun testRefreshReturnsSuccess() = runTest {
        val fakePosts = listOf(
            Post(
                id = 1,
                title = "Test Movie 1",
                originalTitle = "Test Movie 1",
                originalLanguage = "en",
                releaseDate = "2025-01-01",
                overview = "Overview 1",
                popularity = 123.0,
                posterPath = "/poster1.jpg",
                backdropPath = "/backdrop1.jpg",
                voteAverage = 7.5,
                voteCount = 100,
                adult = false,
                video = false,
                genreIds = listOf(1, 2, 3)
            ),
            Post(
                id = 2,
                title = "Test Movie 2",
                originalTitle = "Test Movie 2",
                originalLanguage = "en",
                releaseDate = "2025-01-02",
                overview = "Overview 2",
                popularity = 456.0,
                posterPath = "/poster2.jpg",
                backdropPath = "/backdrop2.jpg",
                voteAverage = 6.2,
                voteCount = 80,
                adult = false,
                video = false,
                genreIds = listOf(4, 5)
            )
        )

        coEvery { apiService.fetchPosts(1) } returns fakePosts
        coEvery { postDao.clearAllPosts() } just Runs
        coEvery { postDao.insertPosts(any()) } just Runs

        val result = remoteMediator.load(
            LoadType.REFRESH,
            PagingState(
                pages = listOf(),
                anchorPosition = null,
                config = PagingConfig(pageSize = 20),
                leadingPlaceholderCount = 0
            )
        )

        Truth.assertThat(result is RemoteMediator.MediatorResult.Success).isTrue()
    }
}
