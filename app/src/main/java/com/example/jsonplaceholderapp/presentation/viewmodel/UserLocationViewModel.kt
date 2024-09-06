package com.example.jsonplaceholderapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsonplaceholderapp.domain.model.User
import com.example.jsonplaceholderapp.domain.repository.UserRepository
import com.example.jsonplaceholderapp.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.jsonplaceholderapp.util.Result
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserLocationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val logger: Timber.Tree
) : ViewModel() {

    private val _userId = MutableStateFlow<Int?>(null)
    private val _cameraPositionState = MutableStateFlow(
        CameraPositionState(CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f))
    )
    val cameraPositionState: StateFlow<CameraPositionState> = _cameraPositionState.asStateFlow()

    val userLocationUiState: StateFlow<UserLocationUiState> = _userId
        .filterNotNull()
        .flatMapLatest { userId ->
            flow {
                when (val userDetails = safeApiCall { userRepository.getUserDetails(userId) }) {
                    is Result.Success -> {
                        val user = userDetails.data
                        val userLocation = safeLatLng(user)

                        if (userLocation != null) {
                            _cameraPositionState.value = CameraPositionState(
                                CameraPosition.fromLatLngZoom(userLocation, 10f)
                            )
                            emit(UserLocationUiState.ShowUserLocation(user, userLocation))
                        } else {
                            logger.e("Invalid Coordinates")
                            emit(UserLocationUiState.Error)
                        }
                    }

                    is Result.Error -> {
                        emit(UserLocationUiState.Error)
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UserLocationUiState.Loading)

    fun setUserId(id: Int?) {
        _userId.value = id
    }

    private fun safeLatLng(user: User): LatLng? {
        val lat = user.address.geo.lat?.toDoubleOrNull()
        val lng = user.address.geo.lng?.toDoubleOrNull()
        return if (lat != null && lng != null) {
            LatLng(lat.toDouble(), lng.toDouble())
        } else {
            null
        }
    }
}

sealed class UserLocationUiState {
    data object Loading : UserLocationUiState()
    data class ShowUserLocation(val user: User, val userLocation: LatLng) : UserLocationUiState()
    data object Error : UserLocationUiState()
}