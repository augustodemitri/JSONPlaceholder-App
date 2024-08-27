package com.example.jsonplaceholderapp.data.repository

import com.example.jsonplaceholderapp.data.api.NewsApiService
import com.example.jsonplaceholderapp.domain.model.News
import com.example.jsonplaceholderapp.domain.repository.NewsRepository
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService
) : NewsRepository {
    override suspend fun getNewsList(): List<News> {
        return newsApiService.getNewsList()
    }

    override suspend fun searchNews(query: String): List<News> {
        return newsApiService.searchNews(query)
    }

    override suspend fun getNewsDetails(newsId: Int): News {
        return newsApiService.getNewsDetail(newsId)
    }
}