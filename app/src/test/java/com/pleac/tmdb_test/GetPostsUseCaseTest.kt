package com.pleac.tmdb_test

import android.content.Context
import androidx.paging.PagingData
import com.google.common.truth.Truth
import com.pleac.agc.data.data.Post
import com.pleac.agc.domain.usecase.GetPostsUseCase
import com.pleac.tmdb_test.domain.model.UiState
import com.pleac.tmdb_test.domain.repository.PostRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPostsUseCaseTest {

    private lateinit var useCase: GetPostsUseCase
    private val repository: PostRepository = mockk()
    private lateinit var context: Context

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakePagingData(): PagingData<Post> {
        val post = Post(
            id = 1,
            title = "Test",
            originalTitle = "Test",
            originalLanguage = "en",
            releaseDate = "2025-01-01",
            overview = "Overview",
            popularity = 10.0,
            posterPath = "",
            backdropPath = "",
            voteAverage = 7.0,
            voteCount = 100,
            adult = false,
            video = false,
            genreIds = listOf(1, 2, 3)
        )
        return PagingData.from(listOf(post))
    }

    @Test
    fun `getPostsState emits Loading then Success when internet is available`() = runTest {
        // Arrange

        val fakeData = fakePagingData()
        coEvery { repository.getPagedPosts() } returns flowOf(fakeData)

        useCase = GetPostsUseCase(repository, context)

        // Act
        val emissions = useCase.getPostsFlow().toList()

        // Assert
        Truth.assertThat(emissions[0]).isEqualTo(UiState.Loading)
        Truth.assertThat(emissions[1]).isInstanceOf(UiState.Success::class.java)
    }

    @Test
    fun `getPostsState emits NoInternet then Success when no internet`() = runTest {
        // Arrange

        val fakeData = fakePagingData()
        coEvery { repository.getPagedPosts() } returns flowOf(fakeData)

        useCase = GetPostsUseCase(repository, context)

        // Act
        val emissions = useCase.getPostsFlow().toList()

        // Assert
        Truth.assertThat(emissions[0]).isEqualTo(UiState.Loading)
        Truth.assertThat(emissions[1]).isEqualTo(UiState.NoInternet)
        Truth.assertThat(emissions[2]).isInstanceOf(UiState.Success::class.java)
    }

    @Test
    fun `getPostsState emits Error when exception is thrown`() = runTest {
        // Arrange
        coEvery { repository.getPagedPosts() } throws RuntimeException("Network error")

        useCase = GetPostsUseCase(repository, context)

        // Act
        val emissions = useCase.getPostsFlow().toList()

        // Assert
        Truth.assertThat(emissions[0]).isEqualTo(UiState.Loading)
        Truth.assertThat(emissions[1]).isInstanceOf(UiState.Error::class.java)
        val error = emissions[1] as UiState.Error
        Truth.assertThat(error.message).contains("Network error")
    }
}
