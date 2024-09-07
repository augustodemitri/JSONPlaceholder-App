package com.example.jsonplaceholderapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigation(
    isBottomBarVisible: Boolean,
    selectedItemIndex: Int,
    onNavigate: (route: String) -> Unit
) {
    if (isBottomBarVisible) {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(4.dp)
        ) {
            navItems.forEachIndexed { i, navigationItem ->
                NavigationBarItem(
                    selected = selectedItemIndex == i,
                    onClick = { onNavigate(navigationItem.screen) },
                    icon = {
                        Icon(
                            imageVector = if (selectedItemIndex == i) {
                                navigationItem.selectedIcon
                            } else {
                                navigationItem.unselectedIcon
                            },
                            contentDescription = navigationItem.title
                        )
                    },
                    label = { Text(text = navigationItem.title) }
                )
            }
        }
    }
}

val navItems = listOf(
    BottomNavigationItem(
        title = "News",
        screen = Screen.News.route,
        selectedIcon = Icons.Default.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavigationItem(
        title = "Users",
        screen = Screen.Users.route,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)

data class BottomNavigationItem(
    val title: String,
    val screen: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)