package com.example.jsonplaceholderapp.presentation.components

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.jsonplaceholderapp.presentation.navigation.AppNavigationGraph
import com.example.jsonplaceholderapp.presentation.navigation.BottomNavigation
import com.example.jsonplaceholderapp.presentation.navigation.Screen

@Composable
fun ScaffoldComponent() {
    val navController = rememberNavController()
    var bottomNavigationVisibility by remember { mutableStateOf(true) }
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        selectedItemIndex = when (destination.route) {
            Screen.News.route -> 0
            Screen.Users.route -> 1
            else -> selectedItemIndex
        }
    }
    Scaffold(
        bottomBar = {
            BottomNavigation(
                isBottomBarVisible = bottomNavigationVisibility,
                selectedItemIndex = selectedItemIndex,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(route)
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { paddingValues ->
        AppNavigationGraph(paddingValues, navController) {
            bottomNavigationVisibility = it
        }
    }
}