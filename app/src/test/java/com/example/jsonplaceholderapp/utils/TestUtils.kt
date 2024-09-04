package com.example.jsonplaceholderapp.utils

import com.example.jsonplaceholderapp.domain.model.Address
import com.example.jsonplaceholderapp.domain.model.Comment
import com.example.jsonplaceholderapp.domain.model.Geo
import com.example.jsonplaceholderapp.domain.model.News
import com.example.jsonplaceholderapp.domain.model.User
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler

object TestUtils {
    private val testScheduler = TestCoroutineScheduler()

    val testDispatcher = StandardTestDispatcher(testScheduler)
    val ioDispatcher = StandardTestDispatcher(testScheduler)
    val defaultDispatcher = StandardTestDispatcher(testScheduler)

    // Mock data for test files
    val news1 = News(
        id = 1,
        title = "Breaking News",
        content = "This is the content of the breaking news article.",
        image = "https://example.com/images/news1.jpg",
        thumbnail = "https://example.com/images/news1_thumbnail.jpg",
        status = "Published",
        category = "World",
        publishedAt = "2024-09-04T12:00:00Z",
        updatedAt = "2024-09-04T14:00:00Z",
        userId = 1
    )

    val user1 = User(
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

    val user2 = User(
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

    val comment1 = Comment(
        id = 1,
        postId = 1,
        userId = 1,
        content = "This is the first comment."
    )

    val comment2 = Comment(
        id = 2,
        postId = 1,
        userId = 1,
        content = "This is the second comment."
    )
}
