package com.example.jsonplaceholderapp.domain.usecases

import com.example.jsonplaceholderapp.di.DefaultDispatcher
import com.example.jsonplaceholderapp.di.IoDispatcher
import com.example.jsonplaceholderapp.domain.model.Comment
import com.example.jsonplaceholderapp.domain.model.User
import com.example.jsonplaceholderapp.domain.repository.CommentsRepository
import com.example.jsonplaceholderapp.domain.repository.UserRepository
import com.example.jsonplaceholderapp.util.ServiceError
import com.example.jsonplaceholderapp.util.safeApiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.example.jsonplaceholderapp.util.Result
import javax.inject.Inject

class GetCommentsWithUsersUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val commentsRepository: CommentsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(postId: Int): Result<List<CommentWithUser>, ServiceError> {
        return withContext(ioDispatcher) {
            try {
                val commentsResult = safeApiCall { commentsRepository.getComments() }
                if (commentsResult is Result.Error) return@withContext commentsResult

                val usersResult = safeApiCall { userRepository.getUserList() }
                if (usersResult is Result.Error) return@withContext usersResult

                val comments = (commentsResult as Result.Success).data
                val users = (usersResult as Result.Success).data

                val commentsWithUsers = associateCommentsWithUsers(comments, users, postId)
                Result.Success(commentsWithUsers)
            } catch (e: Exception) {
                Result.Error(ServiceError(e.message ?: "Unknown error"))
            }
        }
    }

    private suspend fun associateCommentsWithUsers(
        comments: List<Comment>, users: List<User>, postId: Int
    ): List<CommentWithUser> {
        return withContext(defaultDispatcher) {
            val userMap = users.associateBy { it.id }
            comments.asSequence().mapNotNull { comment ->
                if (comment.postId == postId) {
                    userMap[comment.userId]?.let { user ->
                        CommentWithUser(comment, user)
                    }
                } else null
            }.toList()
        }
    }
}

data class CommentWithUser(
    val comment: Comment,
    val user: User
)


