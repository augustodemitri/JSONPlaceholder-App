package com.example.jsonplaceholderapp.domain.usecases

import com.example.jsonplaceholderapp.domain.repository.CommentsRepository
import com.example.jsonplaceholderapp.domain.repository.UserRepository
import com.example.jsonplaceholderapp.util.Result
import com.example.jsonplaceholderapp.utils.TestUtils.comment1
import com.example.jsonplaceholderapp.utils.TestUtils.defaultDispatcher
import com.example.jsonplaceholderapp.utils.TestUtils.ioDispatcher
import com.example.jsonplaceholderapp.utils.TestUtils.testDispatcher
import com.example.jsonplaceholderapp.utils.TestUtils.user1
import com.example.jsonplaceholderapp.utils.TestUtils.user2
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.clearAllMocks
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetCommentsWithUsersUseCaseTest {

    private lateinit var commentsRepository: CommentsRepository
    private lateinit var userRepository: UserRepository
    private lateinit var getCommentsWithUsersUseCase: GetCommentsWithUsersUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        commentsRepository = mockk()
        userRepository = mockk()
        getCommentsWithUsersUseCase = GetCommentsWithUsersUseCase(
            userRepository,
            commentsRepository,
            ioDispatcher,
            defaultDispatcher
        )
        clearAllMocks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Main dispatcher
    }

    @Test
    fun `invoke returns success when comments and users are fetched successfully`() = runTest {
        // Given
        val mockComments = listOf(comment1)
        val mockUsers = listOf(user1)
        coEvery { commentsRepository.getComments() } returns mockComments
        coEvery { userRepository.getUserList() } returns mockUsers

        // When
        val result = getCommentsWithUsersUseCase(1)

        // Then
        assertTrue(result is Result.Success)

        val expectedCommentWithUser = CommentWithUser(
            comment = mockComments.first(),
            user = mockUsers.first()
        )
        assertEquals(listOf(expectedCommentWithUser), (result as Result.Success).data)
    }

    @Test
    fun `invoke returns error when fetching comments fails`() = runTest {
        // Given
        coEvery { commentsRepository.getComments() } throws Exception("Network error")

        // When
        val result = getCommentsWithUsersUseCase(1)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Network error", (result as Result.Error).error.message)
    }

    @Test
    fun `invoke returns error when fetching users fails`() = runTest {
        // Given
        coEvery { commentsRepository.getComments() } returns listOf(comment1)
        coEvery { userRepository.getUserList() } throws Exception("Network error fetching users")

        // When
        val result = getCommentsWithUsersUseCase(1)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Network error fetching users", (result as Result.Error).error.message)
    }


    @Test
    fun `invoke returns success with empty list when no comments are found`() = runTest {
        // Given
        coEvery { commentsRepository.getComments() } returns emptyList()
        coEvery { userRepository.getUserList() } returns listOf(user1)

        // When
        val result = getCommentsWithUsersUseCase(1)

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun `invoke returns success with empty list when no users are found`() = runTest {
        // Given
        coEvery { commentsRepository.getComments() } returns listOf(comment1)
        coEvery { userRepository.getUserList() } returns emptyList()

        // When
        val result = getCommentsWithUsersUseCase(1)

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun `invoke returns empty list when no matching users are found`() = runTest {
        // Given
        val mockComments = listOf(comment1)
        val mockUsers = listOf(user2)

        coEvery { commentsRepository.getComments() } returns mockComments
        coEvery { userRepository.getUserList() } returns mockUsers

        // When
        val result = getCommentsWithUsersUseCase(1)

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty()) // No user matches the comment's userId
    }

}
