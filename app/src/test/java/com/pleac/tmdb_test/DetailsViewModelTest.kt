package com.pleac.tmdb_test

import com.pleac.agc.data.local.PostDao
import com.pleac.tmdb_test.domain.model.PostEntity
import com.pleac.tmdb_test.presentation.Viewmodels.DetailsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var postDao: PostDao

    private val testDispatcher = StandardTestDispatcher()



    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        postDao = mockk()
        viewModel = DetailsViewModel(postDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMovieDetails sets post correctly`() = runTest {
        val dummyPost = PostEntity(
            id = 1,
            title = "Dummy Title",
            originalTitle = "Dummy Original",
            originalLanguage = "en",
            releaseDate = "2025-01-01",
            overview = "This is a dummy post",
            popularity = 100.0,
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            voteAverage = 8.5,
            voteCount = 200,
            adult = false,
            video = false,
            genreIds = listOf(1, 2, 3).joinToString(","),
            pageNumber = 1
        )
        coEvery { postDao.getPostById(1) } returns dummyPost

        val viewModel = DetailsViewModel(postDao)

        viewModel.loadMovieDetails(1)

        val job = launch {
            viewModel.post.collect { post ->
                if (post != null) {
                    assertEquals(dummyPost, post)
                    cancel()
                }
            }
        }

        job.join()
    }

    @Test
    fun `loadMovieDetails keeps post null when not found in DB`() = runTest {
        // arrange
        coEvery { postDao.getPostById(1) } returns null
        // act
        viewModel.loadMovieDetails(1)

        // assert
        val job = launch {
            viewModel.post.collect { value ->
                assertNull(value)
                cancel()
            }
        }
        job.join()
    }
}