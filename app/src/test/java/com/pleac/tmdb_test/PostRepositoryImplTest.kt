package com.pleac.tmdb_test

import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.common.truth.Truth.assertThat
import com.pleac.agc.data.data.Post
import com.pleac.tmdb_test.domain.model.PostEntity
import com.pleac.agc.data.local.PostDao
import com.pleac.agc.data.remote.PostApiService
import com.pleac.agc.data.repository.PostRepositoryImpl
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostRepositoryImplTest {

    private lateinit var apiService: PostApiService
    private lateinit var postDao: PostDao
    private lateinit var repository: PostRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        postDao = mockk()
        repository = PostRepositoryImpl(apiService, postDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPagedPosts emits PagingData with correct mapped posts`() = runTest {
        // arrange
        val postEntities = listOf(
            PostEntity(
                id = 1,
                title = "Movie 1",
                originalTitle = "Movie 1",
                originalLanguage = "en",
                releaseDate = "2020-01-01",
                overview = "Overview 1",
                popularity = 8.1,
                posterPath = "/poster1.jpg",
                backdropPath = "/backdrop1.jpg",
                voteAverage = 7.5,
                voteCount = 100,
                adult = false,
                video = false,
                genreIds = "18,28",
                pageNumber = 1
            ),
            PostEntity(
                id = 2,
                title = "Movie 2",
                originalTitle = "Movie 2",
                originalLanguage = "en",
                releaseDate = "2021-01-01",
                overview = "Overview 2",
                popularity = 9.1,
                posterPath = "/poster2.jpg",
                backdropPath = "/backdrop2.jpg",
                voteAverage = 8.5,
                voteCount = 200,
                adult = false,
                video = false,
                genreIds = "35",
                pageNumber = 1
            )
        )

        val expectedPosts = postEntities.map { it.toDomainModel() }

        val pagingData = PagingData.from(postEntities.map { it.toDomainModel() })

        // act
        val result = mutableListOf<Post>()
        val job = launch {
            pagingData.collectData().let {
                result.addAll(it)
            }
        }

        testDispatcher.scheduler.advanceUntilIdle()
        job.cancel()

        // assert
        assertThat(result).isEqualTo(expectedPosts)
    }


    private suspend fun <T : Any> PagingData<T>.collectData(): List<T> {
        val differ = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
                override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
            },
            updateCallback = NoopListCallback(),
            mainDispatcher = testDispatcher,
            workerDispatcher = testDispatcher
        )

        differ.submitData(this)
        testDispatcher.scheduler.advanceUntilIdle()
        return differ.snapshot().items
    }

    class NoopListCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
