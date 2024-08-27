package com.example.jsonplaceholderapp.data.repository

import com.example.jsonplaceholderapp.data.api.UserService
import com.example.jsonplaceholderapp.domain.model.User
import com.example.jsonplaceholderapp.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
): UserRepository {
    override suspend fun getUserList(): List<User> = userService.getUserList()
    override suspend fun getUserDetails(userId: Int): User = userService.getUser(userId)
}