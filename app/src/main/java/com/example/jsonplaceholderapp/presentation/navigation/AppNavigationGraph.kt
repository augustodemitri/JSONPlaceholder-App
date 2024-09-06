package com.example.jsonplaceholderapp.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
    NavHost(navController = navController, startDestination = "news") {
        composable("news") {
            NewsScreen(paddingValues, onNavigateToNewsDetails = { id ->
                navController.navigate("news_details/$id")
            })
        }

        composable(
            route = "news_details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            NewsDetailsScreen(
                newsId = id,
                popBack = {
                navController.popBackStack()
            })
        }

        composable("users") {
            UsersScreen(
                paddingValues,
                onUserLocationClick = { id ->
                    navController.navigate("map_screen/$id")
                }
            )
        }

        composable(
            route = "map_screen/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            MapScreen(
                userId = id,
                onBack = {
                    navController.popBackStack()
                    setBottomNavVisibility(true)
                },
                setBottomNavVisibility = setBottomNavVisibility
            )
        }
    }
}
