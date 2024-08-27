package com.example.jsonplaceholderapp.data.repository

import com.example.jsonplaceholderapp.data.api.CommentsService
import com.example.jsonplaceholderapp.domain.model.Comment
import com.example.jsonplaceholderapp.domain.repository.CommentsRepository
import javax.inject.Inject

class CommentsRepositoryImpl @Inject constructor(
    private val commentsService: CommentsService
): CommentsRepository {
    override suspend fun getComments(): List<Comment> = commentsService.getComments()
}