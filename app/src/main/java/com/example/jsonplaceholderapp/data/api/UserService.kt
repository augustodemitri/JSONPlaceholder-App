package com.example.jsonplaceholderapp.data.api

import com.example.jsonplaceholderapp.domain.model.User
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
    @GET("users")
    suspend fun getUserList(): List<User>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): User
}