package com.example.jsonplaceholderapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jsonplaceholderapp.presentation.components.UserAvatarComponent
import com.example.jsonplaceholderapp.presentation.viewmodel.UserLocationUiState
import com.example.jsonplaceholderapp.presentation.viewmodel.UserLocationViewModel
import com.example.jsonplaceholderapp.domain.model.User
import com.example.jsonplaceholderapp.presentation.components.CircularProgressComponent
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    userId: Int?,
    onBack: () -> Unit,
    setBottomNavVisibility: (Boolean) -> Unit,
    userLocationViewModel: UserLocationViewModel = hiltViewModel()
) {
    val userLocationUiState by userLocationViewModel.userLocationUiState.collectAsState()
    val cameraPositionState by userLocationViewModel.cameraPositionState.collectAsState()

    LaunchedEffect(userId) {
        userLocationViewModel.setUserId(userId)
        delay(250)
        setBottomNavVisibility(false)
    }

    when (userLocationUiState) {
        is UserLocationUiState.Loading -> {
            CircularProgressComponent(modifier = Modifier.size(50.dp))
        }

        is UserLocationUiState.ShowUserLocation -> {
            val state = (userLocationUiState as UserLocationUiState.ShowUserLocation)

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "User Location") },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    UserInfoSection(state.user)
                },
                content = { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(mapType = MapType.NORMAL)
                        ) {
                            Marker(
                                state = MarkerState(position = state.userLocation),
                                title = "${state.user.firstName} ${state.user.lastName}"
                            )
                        }
                    }
                }
            )
        }

        is UserLocationUiState.Error -> {
            ErrorScreen()
        }
    }
}

@Composable
fun UserInfoSection(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatarComponent(initials = user.initials, modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Place, contentDescription = null, tint = Color.Red)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${user.address.street}, ${user.address.city}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


