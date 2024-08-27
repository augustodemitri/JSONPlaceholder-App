package com.example.jsonplaceholderapp.domain.model

import com.example.jsonplaceholderapp.util.toFormattedDate

data class News(
    val id: Int,
    val title: String,
    val content: String,
    val image: String,
    val thumbnail: String,
    val status: String,
    val category: String,
    val publishedAt: String,
    val updatedAt: String,
    val userId: Int
) {
    val publishedDateFormatted: String
        get() = publishedAt.toFormattedDate()
}
