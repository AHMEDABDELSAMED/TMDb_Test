package com.pleac.tmdb_test

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.pleac.agc.data.data.Post
import com.pleac.agc.domain.usecase.GetPostsUseCase
import com.pleac.tmdb_test.domain.model.FavoriteEntity
import com.pleac.tmdb_test.data.local.FavoriteMovieDao
import com.pleac.tmdb_test.domain.model.UiState
import com.pleac.tmdb_test.presentation.Viewmodels.PostViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private lateinit var viewModel: PostViewModel
    private lateinit var getPostsUseCase: GetPostsUseCase
    private lateinit var favoriteDao: FavoriteMovieDao

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getPostsUseCase = mockk()
        favoriteDao = mockk()

        // Fake flow for favoriteMovies
        every { favoriteDao.getAllFavorites() } returns flowOf(emptyList())

        viewModel = PostViewModel(getPostsUseCase, favoriteDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPosts emits UiState_Success when data is loaded`() = runTest {
        // Arrange
        val fakePosts = listOf(Post(
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
        ))
        val pagingData = PagingData.from(fakePosts)
        val flow = flowOf(pagingData)

        coEvery { getPostsUseCase.getPostsFlow() } returns flow

        // Act
        viewModel.getPosts()

        // Assert
        val state = viewModel.uiState.first { it is UiState.Success }
        val items = (state as UiState.Success).data.collectData()
        Truth.assertThat(items.map { it.id }).containsExactly(1)
    }

    @Test
    fun `getPosts emits UiState_Error when exception is thrown`() = runTest {
        // Arrange
        coEvery { getPostsUseCase.getPostsFlow() } throws RuntimeException("error")

        // Act
        viewModel.getPosts()

        // Assert
        val state = viewModel.uiState.first { it is UiState.Error }
        Truth.assertThat((state as UiState.Error).message).isEqualTo("error")
    }

    // Helper extension to collect PagingData into a list
    private suspend fun <T : Any> PagingData<T>.collectData(): List<T> {
        val differ = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
                    oldItem == newItem
            },
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )

        differ.submitData(this)
        return differ.snapshot().items
    }

    // لازم يكون global:
    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
