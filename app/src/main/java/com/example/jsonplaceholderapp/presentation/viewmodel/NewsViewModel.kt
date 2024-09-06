package com.example.jsonplaceholderapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsonplaceholderapp.domain.usecases.ArticleWithAuthor
import com.example.jsonplaceholderapp.domain.usecases.GetNewsWithAuthorUseCase
import com.example.jsonplaceholderapp.util.Result
import com.example.jsonplaceholderapp.util.ServiceError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsWithAuthorUseCase: GetNewsWithAuthorUseCase,
    private val logger: Timber.Tree
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _newsResult: Flow<Result<List<ArticleWithAuthor>, ServiceError>> = flow {
        emit(getNewsWithAuthorUseCase())
    }

    val newsUiState: StateFlow<NewsUiState> = combine(
        _newsResult, _query
    ) { result, query ->
        when (result) {
            is Result.Success -> {
                val newsList = result.data
                if (query.isEmpty()) {
                    NewsUiState.ShowNewsList(newsList)
                } else {
                    val filteredPosts = newsList.filter { articleWithAuthor ->
                        articleWithAuthor.news.title.contains(
                            query,
                            ignoreCase = true
                        ) || articleWithAuthor.news.content.contains(query, ignoreCase = true)
                    }
                    NewsUiState.ShowNewsList(filteredPosts)
                }
            }

            is Result.Error -> {
                logger.e(result.error.message)
                NewsUiState.Error
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), NewsUiState.Loading)

    fun updateQuery(typedQuery: String) {
        _query.value = typedQuery
    }
}

sealed class NewsUiState {
    data object Loading : NewsUiState()
    data class ShowNewsList(val data: List<ArticleWithAuthor>) : NewsUiState()
    data object Error : NewsUiState()
}