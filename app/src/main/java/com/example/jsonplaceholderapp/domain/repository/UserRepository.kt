package com.example.jsonplaceholderapp.domain.repository

import com.example.jsonplaceholderapp.domain.model.User

interface UserRepository {
    suspend fun getUserList(): List<User>
    suspend fun getUserDetails(userId: Int): User
}