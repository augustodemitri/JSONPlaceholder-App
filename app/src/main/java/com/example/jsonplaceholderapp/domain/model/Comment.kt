package com.example.jsonplaceholderapp.domain.model

import com.google.gson.annotations.SerializedName

data class Comment(
    val id: Int,
    val postId: Int,
    val userId: Int,
    @SerializedName("comment")
    val content: String
)