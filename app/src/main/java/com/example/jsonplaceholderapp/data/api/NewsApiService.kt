package com.example.jsonplaceholderapp.data.api

import com.example.jsonplaceholderapp.domain.model.News
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {
    @GET("posts")
    suspend fun getNewsList(): List<News>

    @GET("posts/search")
    suspend fun searchNews(@Query("query") query: String): List<News>

    @GET("posts/{id}")
    suspend fun getNewsDetail(@Path("id") id: Int): News
}