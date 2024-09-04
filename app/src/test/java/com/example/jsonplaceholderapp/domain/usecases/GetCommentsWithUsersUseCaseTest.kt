package com.example.jsonplaceholderapp.domain.usecases

import com.example.jsonplaceholderapp.domain.model.Address
import com.example.jsonplaceholderapp.domain.model.Comment
import com.example.jsonplaceholderapp.domain.model.CommentWithUser
import com.example.jsonplaceholderapp.domain.model.Geo
import com.example.jsonplaceholderapp.domain.model.User
import com.example.jsonplaceholderapp.domain.repository.CommentsRepository
import com.example.jsonplaceholderapp.domain.repository.UserRepository
import com.example.jsonplaceholderapp.util.Result
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.clearAllMocks
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
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

    private val testScheduler = TestCoroutineScheduler()

    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private val ioDispatcher = StandardTestDispatcher(testScheduler)
    private val defaultDispatcher = StandardTestDispatcher(testScheduler)

    private val comment1 = Comment(
        id = 1,
        postId = 1,
        userId = 1,
        content = "This is the first comment."
    )

    private val comment2 = Comment(
        id = 2,
        postId = 1,
        userId = 1,
        content = "This is the second comment."
    )

    private val user1 = User(
        id = 1,
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        birthDate = "1985-06-15",
        address = Address(
            street = "123 Main St",
            suite = "Apt 4B",
            city = "New York",
            zipCode = "10001",
            geo = Geo(
                lat = "40.7128",
                lng = "-74.0060"
            )
        )
    )

    private val user2 = User(
        id = 2,
        firstName = "Jane",
        lastName = "Smith",
        email = "jane.smith@example.com",
        birthDate = "1985-06-15",
        address = Address(
            street = "123 Main St",
            suite = "Apt 4B",
            city = "New York",
            zipCode = "10001",
            geo = Geo(
                lat = "40.7128",
                lng = "-74.0060"
            )
        )
    )

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
