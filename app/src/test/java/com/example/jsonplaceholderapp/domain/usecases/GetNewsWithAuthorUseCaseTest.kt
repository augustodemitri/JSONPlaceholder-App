package com.example.jsonplaceholderapp.domain.usecases

import com.example.jsonplaceholderapp.domain.repository.NewsRepository
import com.example.jsonplaceholderapp.domain.repository.UserRepository
import com.example.jsonplaceholderapp.util.Result
import com.example.jsonplaceholderapp.utils.TestUtils.defaultDispatcher
import com.example.jsonplaceholderapp.utils.TestUtils.ioDispatcher
import com.example.jsonplaceholderapp.utils.TestUtils.news1
import com.example.jsonplaceholderapp.utils.TestUtils.testDispatcher
import com.example.jsonplaceholderapp.utils.TestUtils.user1
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetNewsWithAuthorUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var newsRepository: NewsRepository
    private lateinit var getNewsWithAuthorUseCase: GetNewsWithAuthorUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        newsRepository = mockk()
        getNewsWithAuthorUseCase = GetNewsWithAuthorUseCase(
            userRepository,
            newsRepository,
            ioDispatcher,
            defaultDispatcher
        )
        clearAllMocks()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Main dispatcher
    }

    @Test
    fun `invoke returns success when news and users are fetched successfully`() = runTest {
        // Given
        val mockNews = listOf(news1)
        val mockUsers = listOf(user1)
        coEvery { newsRepository.getNewsList() } returns mockNews
        coEvery { userRepository.getUserList() } returns mockUsers

        // When
        val result = getNewsWithAuthorUseCase()

        // Then
        assertTrue(result is Result.Success)
        val expectedArticleWithAuthor = ArticleWithAuthor(
            news = mockNews.first(),
            author = mockUsers.first()
        )
        assertEquals(listOf(expectedArticleWithAuthor), (result as Result.Success).data)
    }

    @Test
    fun `invoke returns error when fetching news fails`() = runTest {
        // Given
        coEvery { newsRepository.getNewsList() } throws Exception("Network error fetching news")

        // When
        val result = getNewsWithAuthorUseCase()

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Network error fetching news", (result as Result.Error).error.message)
    }

    @Test
    fun `invoke returns error when fetching users fails`() = runTest {
        // Given
        coEvery { newsRepository.getNewsList() } returns listOf(news1)
        coEvery { userRepository.getUserList() } throws Exception("Network error fetching users")

        // When
        val result = getNewsWithAuthorUseCase()

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Network error fetching users", (result as Result.Error).error.message)
    }

    @Test
    fun `invoke returns success with empty list when no news are found`() = runTest {
        // Given
        coEvery { newsRepository.getNewsList() } returns emptyList()
        coEvery { userRepository.getUserList() } returns listOf(user1)

        // When
        val result = getNewsWithAuthorUseCase()

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun `invoke returns success with empty list when no users are found`() = runTest {
        // Given
        coEvery { newsRepository.getNewsList() } returns listOf(news1)
        coEvery { userRepository.getUserList() } returns emptyList()

        // When
        val result = getNewsWithAuthorUseCase()

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isNotEmpty())
        assertNull(result.data.first().author) // No users
    }

    @Test
    fun `invoke processes news in chunks when news list is large`() = runTest {
        // Given
        val mockNews = List(100) { news1.copy(id = it) } // 100 articles
        val mockUsers = listOf(user1)

        coEvery { newsRepository.getNewsList() } returns mockNews
        coEvery { userRepository.getUserList() } returns mockUsers

        // When
        val result = getNewsWithAuthorUseCase()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(100, (result as Result.Success).data.size)
    }
}