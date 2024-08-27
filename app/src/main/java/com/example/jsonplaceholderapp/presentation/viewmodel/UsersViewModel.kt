package com.example.jsonplaceholderapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsonplaceholderapp.domain.model.User
import com.example.jsonplaceholderapp.domain.repository.UserRepository
import com.example.jsonplaceholderapp.util.Result
import com.example.jsonplaceholderapp.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val userRepository: UserRepository
)  : ViewModel() {

    val userUiState = flow {
        when (val result = safeApiCall { userRepository.getUserList() }) {
            is Result.Success -> {
                emit(UserUiState.ShowUsers(result.data))
            }
            is Result.Error -> {
                emit(UserUiState.Error(result.error.message))
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UserUiState.Loading)

}

sealed class UserUiState {
    data object Loading: UserUiState()
    data class ShowUsers(val userList: List<User>) : UserUiState()
    data class Error(val errorMsg: String?) : UserUiState()
}