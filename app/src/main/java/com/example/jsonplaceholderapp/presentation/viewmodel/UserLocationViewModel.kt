package com.example.jsonplaceholderapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsonplaceholderapp.domain.model.User
import com.example.jsonplaceholderapp.domain.repository.UserRepository
import com.example.jsonplaceholderapp.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.jsonplaceholderapp.util.Result
import javax.inject.Inject

@HiltViewModel
class UserLocationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userLocationUiState =
        MutableStateFlow<UserLocationUiState>(UserLocationUiState.Loading)
    val userLocationUiState: StateFlow<UserLocationUiState> = _userLocationUiState.asStateFlow()

    fun loadUserLocation(userId: Int?) = viewModelScope.launch {
        if (userId == null) {
            Log.e("UserLocationViewModel", "Null user id")
            _userLocationUiState.value = UserLocationUiState.Error
        } else {
            when (val user = safeApiCall { userRepository.getUserDetails(userId) }) {
                is Result.Success -> {
                    _userLocationUiState.value = UserLocationUiState.ShowUserLocation(user.data)
                }

                is Result.Error -> {
                    Log.e("UserLocationViewModel", user.error.message)
                    _userLocationUiState.value = UserLocationUiState.Error
                }
            }
        }
    }
}

sealed class UserLocationUiState {
    data object Loading : UserLocationUiState()
    data class ShowUserLocation(val user: User) : UserLocationUiState()
    data object Error : UserLocationUiState()
}