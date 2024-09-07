package com.example.jsonplaceholderapp.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.jsonplaceholderapp.presentation.screens.MapScreen
import com.example.jsonplaceholderapp.presentation.screens.NewsDetailsScreen
import com.example.jsonplaceholderapp.presentation.screens.NewsScreen
import com.example.jsonplaceholderapp.presentation.screens.UsersScreen

@Composable
fun AppNavigationGraph(
    paddingValues: PaddingValues,
    navController: NavHostController,
    setBottomNavVisibility: (isVisible: Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.News.route) {
        composable(Screen.News.route) {
            NewsScreen(paddingValues, onNavigateToNewsDetails = { postId ->
                navController.navigate(
                    Screen.NewsDetails(id = postId)
                )
            })
        }

        composable<Screen.NewsDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.NewsDetails>()
            NewsDetailsScreen(
                newsId = args.id,
                popBack = {
                navController.popBackStack()
            })
        }

        composable(Screen.Users.route) {
            UsersScreen(
                paddingValues,
                onUserLocationClick = { id ->
                    navController.navigate(
                        Screen.UserLocation(userId = id)
                    )
                }
            )
        }

        composable<Screen.UserLocation> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.UserLocation>()
            MapScreen(
                userId = args.userId,
                onBack = {
                    navController.popBackStack()
                    setBottomNavVisibility(true)
                },
                setBottomNavVisibility = setBottomNavVisibility
            )
        }
    }
}
