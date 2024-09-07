package com.example.jsonplaceholderapp.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object News : Screen() {
        const val route = "news"
    }

    @Serializable
    data object Users : Screen() {
        const val route = "users"
    }

    @Serializable
    data class NewsDetails(val id: Int) : Screen()

    @Serializable
    data class UserLocation(val userId: Int) : Screen()
}
