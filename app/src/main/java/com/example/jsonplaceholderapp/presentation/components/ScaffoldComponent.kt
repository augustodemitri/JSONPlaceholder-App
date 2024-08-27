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

@Composable
fun ScaffoldComponent() {
    val navController = rememberNavController()
    var bottomNavigationVisibility by remember { mutableStateOf(true) }
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        selectedItemIndex = when (destination.route) {
            "news" -> 0
            "users" -> 1
            else -> selectedItemIndex
        }
    }
    Scaffold(
        bottomBar = {
            BottomNavBarComponent(
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