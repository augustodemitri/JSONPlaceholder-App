package com.example.jsonplaceholderapp.domain.repository

import com.example.jsonplaceholderapp.domain.model.Comment

interface CommentsRepository {
    suspend fun getComments(): List<Comment>
}