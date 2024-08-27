package com.example.jsonplaceholderapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsonplaceholderapp.domain.model.CommentWithUser
import com.example.jsonplaceholderapp.domain.model.News
import com.example.jsonplaceholderapp.domain.repository.NewsRepository
import com.example.jsonplaceholderapp.domain.usecases.GetCommentsWithUsersUseCase
import com.example.jsonplaceholderapp.util.Result
import com.example.jsonplaceholderapp.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val getCommentsAndUsersUseCase: GetCommentsWithUsersUseCase
) : ViewModel() {

    private val _newsId = MutableStateFlow<Int?>(null)
    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    val newsDetailsState: StateFlow<NewsDetailsUiState> = _newsId
        .filterNotNull()
        .flatMapLatest { postId ->
            combine(
                getNewsDetailsFlow(postId),
                getNewsCommentsWithUsers(postId)
            ) { newsDetails, comments ->
                when (newsDetails) {
                    is NewsDetailsUiState.ShowNewsDetail -> {
                        NewsDetailsUiState.ShowNewsDetail(newsDetails.news, comments)
                    }

                    is NewsDetailsUiState.Error -> {
                        NewsDetailsUiState.Error(newsDetails.message)
                    }

                    else -> NewsDetailsUiState.Loading
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), NewsDetailsUiState.Loading)

    private fun getNewsDetailsFlow(id: Int): Flow<NewsDetailsUiState> = flow {
        when (val result = safeApiCall { newsRepository.getNewsDetails(id) }) {
            is Result.Success -> {
                emit(NewsDetailsUiState.ShowNewsDetail(result.data, emptyList()))
            }

            is Result.Error -> {
                emit(NewsDetailsUiState.Error(result.error.message))
            }
        }
    }

    private fun getNewsCommentsWithUsers(postId: Int): Flow<List<CommentWithUser>> = flow {
        when (val commentsWithUsers = getCommentsAndUsersUseCase(postId)) {
            is Result.Success -> {
                emit(commentsWithUsers.data)
            }

            is Result.Error -> {
                emit(emptyList())
            }
        }
    }

    fun toggleBottomSheet() = viewModelScope.launch {
        _showBottomSheet.value = !_showBottomSheet.value
    }

    fun dismissBottomSheet() = viewModelScope.launch {
        _showBottomSheet.value = false
    }

    fun setNewsId(id: Int) {
        _newsId.value = id
    }

}

sealed class NewsDetailsUiState {
    data object Loading : NewsDetailsUiState()
    data class ShowNewsDetail(val news: News, val comments: List<CommentWithUser>) : NewsDetailsUiState()
    data class Error(val message: String?) : NewsDetailsUiState()
}