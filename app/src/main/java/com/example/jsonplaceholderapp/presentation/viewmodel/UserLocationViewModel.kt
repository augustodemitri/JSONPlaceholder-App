package com.example.jsonplaceholderapp.presentation.viewmodel

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
            _userLocationUiState.value = UserLocationUiState.Error("NULL user id")
        } else {
            when (val user = safeApiCall { userRepository.getUserDetails(userId) }) {
                is Result.Success -> {
                    _userLocationUiState.value = UserLocationUiState.ShowUserLocation(user.data)
                }

                is Result.Error -> {
                    _userLocationUiState.value = UserLocationUiState.Error(user.error.message)
                }
            }
        }
    }
}

sealed class UserLocationUiState {
    data object Loading : UserLocationUiState()
    data class ShowUserLocation(val user: User) : UserLocationUiState()
    data class Error(val errorMessage: String?) : UserLocationUiState()
}