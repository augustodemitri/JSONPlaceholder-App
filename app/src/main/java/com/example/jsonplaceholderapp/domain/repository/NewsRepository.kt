package com.example.jsonplaceholderapp.domain.repository

import com.example.jsonplaceholderapp.domain.model.News

interface NewsRepository {
    suspend fun getNewsList(): List<News>
    suspend fun searchNews(query: String): List<News>
    suspend fun getNewsDetails(newsId: Int): News
}