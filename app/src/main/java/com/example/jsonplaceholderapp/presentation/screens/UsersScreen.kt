package com.example.jsonplaceholderapp.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jsonplaceholderapp.presentation.components.UserAvatarComponent
import com.example.jsonplaceholderapp.presentation.viewmodel.UserUiState
import com.example.jsonplaceholderapp.presentation.viewmodel.UsersViewModel
import com.example.jsonplaceholderapp.domain.model.User

@Composable
fun UsersScreen(
    paddingValues: PaddingValues,
    usersViewModel: UsersViewModel = hiltViewModel(),
    onUserLocationClick: (id: Int) -> Unit
) {
    val userScreenState by usersViewModel.userUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when (userScreenState) {
            is UserUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is UserUiState.ShowUsers -> {
                val userList = (userScreenState as UserUiState.ShowUsers).userList
                LazyColumn(
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    items(userList) { user ->
                        UserItem(user = user, onLocationClick = {
                            onUserLocationClick(user.id)
                        })
                    }
                }
            }

            is UserUiState.Error -> {
                ErrorScreen()
            }
        }
    }
}

@Composable
fun UserItem(user: User, onLocationClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        UserAvatarComponent(initials = user.initials, modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Location",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onLocationClick.invoke() }
                    .align(Alignment.CenterEnd),
                tint = Color.Gray
            )
        }
    }
}

