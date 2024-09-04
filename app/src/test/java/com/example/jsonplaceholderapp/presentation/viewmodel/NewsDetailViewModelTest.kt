package com.example.jsonplaceholderapp.presentation.viewmodel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jsonplaceholderapp.domain.model.Comment
import com.example.jsonplaceholderapp.domain.model.CommentWithUser
import com.example.jsonplaceholderapp.domain.model.News
import com.example.jsonplaceholderapp.domain.repository.NewsRepository
import com.example.jsonplaceholderapp.domain.usecases.GetCommentsWithUsersUseCase
import com.example.jsonplaceholderapp.util.Result
import com.example.jsonplaceholderapp.util.ServiceError
import com.example.jsonplaceholderapp.utils.TestUtils
import com.example.jsonplaceholderapp.utils.TestUtils.news1
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsDetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: NewsDetailViewModel
    private val newsRepository = mockk<NewsRepository>()
    private val getCommentsAndUsersUseCase = mockk<GetCommentsWithUsersUseCase>()

    @Before
    fun setup() {
        mockkStatic(Log::class)
        Dispatchers.setMain(TestUtils.testDispatcher)
        viewModel = NewsDetailViewModel(newsRepository, getCommentsAndUsersUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given valid newsId_When setNewsId is called_Then emits Loading and ShowNewsDetail with empty comments`() =
        runTest {
            // Given
            coEvery { newsRepository.getNewsDetails(1) } returns news1
            coEvery { getCommentsAndUsersUseCase(1) } returns Result.Success(emptyList())

            // When
            viewModel.setNewsId(1)
            advanceUntilIdle()

            // Then
            assertEquals(NewsDetailsUiState.Loading, viewModel.newsDetailsState.first())
            val state = viewModel.newsDetailsState.first { it is NewsDetailsUiState.ShowNewsDetail }
            assertTrue(state is NewsDetailsUiState.ShowNewsDetail)
            assertEquals(news1, (state as NewsDetailsUiState.ShowNewsDetail).news)
            assertTrue(state.comments.isEmpty())
        }

    @Test
    fun `Given network failure_When setNewsId is called_Then emits Error`() = runTest {
        // Given
        coEvery { newsRepository.getNewsDetails(1) } throws Exception("Network error")
        coEvery { getCommentsAndUsersUseCase(1) } returns Result.Success(emptyList())
        every { Log.e(any(), any()) } returns 0

        // When
        viewModel.setNewsId(1)
        advanceUntilIdle()

        // Then
        val state = viewModel.newsDetailsState.first { it is NewsDetailsUiState.Error }
        assertTrue(state is NewsDetailsUiState.Error)
    }

    @Test
    fun `Given bottomSheet is hidden_When toggleBottomSheet is called_Then bottomSheet becomes visible`() =
        runTest {
            // Given
            viewModel.toggleBottomSheet()
            advanceUntilIdle()

            // Then
            assertTrue(viewModel.showBottomSheet.value)
        }

    @Test
    fun `Given bottomSheet is visible_When dismissBottomSheet is called_Then bottomSheet becomes hidden`() =
        runTest {
            // Given
            viewModel.toggleBottomSheet()
            advanceUntilIdle()

            // When
            viewModel.dismissBottomSheet()
            advanceUntilIdle()

            // Then
            assertFalse(viewModel.showBottomSheet.value)
        }

    @Test
    fun `Given use case returns error_When setNewsId is called_Then emits ShowNewsDetail with empty comments`() =
        runTest {
            // Given
            val mockError = ServiceError("Failed to fetch comments")

            coEvery { newsRepository.getNewsDetails(1) } returns news1
            coEvery { getCommentsAndUsersUseCase(1) } returns Result.Error(mockError)

            // When
            viewModel.setNewsId(1)
            advanceUntilIdle()

            // Then
            val state = viewModel.newsDetailsState.first { it is NewsDetailsUiState.ShowNewsDetail }
            assertTrue(state is NewsDetailsUiState.ShowNewsDetail)
            assertTrue((state as NewsDetailsUiState.ShowNewsDetail).comments.isEmpty())
        }
}
