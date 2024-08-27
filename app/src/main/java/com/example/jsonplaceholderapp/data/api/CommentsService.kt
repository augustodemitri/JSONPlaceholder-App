package com.example.jsonplaceholderapp.data.api

import com.example.jsonplaceholderapp.domain.model.Comment
import retrofit2.http.GET

interface CommentsService {
    @GET("comments")
    suspend fun getComments(): List<Comment>
}