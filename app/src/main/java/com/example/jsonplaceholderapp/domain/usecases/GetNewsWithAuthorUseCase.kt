package com.example.jsonplaceholderapp.domain.usecases

import com.example.jsonplaceholderapp.di.DefaultDispatcher
import com.example.jsonplaceholderapp.di.IoDispatcher
import com.example.jsonplaceholderapp.domain.model.News
import com.example.jsonplaceholderapp.domain.model.User
import com.example.jsonplaceholderapp.domain.repository.NewsRepository
import com.example.jsonplaceholderapp.domain.repository.UserRepository
import com.example.jsonplaceholderapp.util.ServiceError
import com.example.jsonplaceholderapp.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNewsWithAuthorUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val newsRepository: NewsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Result<List<ArticleWithAuthor>, ServiceError> =
        withContext(ioDispatcher) {
            val newsResultDeferred = async { runCatching { newsRepository.getNewsList() } }
            val usersResultDeferred = async { runCatching { userRepository.getUserList() } }

            val newsResult = newsResultDeferred.await().getOrElse { return@withContext Result.Error(ServiceError(it.message ?: "")) }
            val usersResult = usersResultDeferred.await().getOrElse { return@withContext Result.Error(ServiceError(it.message ?: "")) }

            processResults(newsResult, usersResult)
        }

    private suspend fun processResults(
        newsList: List<News>,
        userList: List<User>
    ): Result<List<ArticleWithAuthor>, ServiceError> = withContext(defaultDispatcher) {
        val userMap = userList.associateBy { it.id }
        val chunkSize = 20

        val result = newsList.chunked(chunkSize).map { chunk ->
            async {
                chunk.map { article ->
                    val author = userMap[article.userId]
                    ArticleWithAuthor(article, author)
                }
            }
        }.awaitAll().flatten()

        Result.Success(result)
    }
}

data class ArticleWithAuthor(
    val news: News,
    val author: User?
)