package com.example.jsonplaceholderapp.domain.model

import com.example.jsonplaceholderapp.util.getInitials
import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    @SerializedName("firstname")
    val firstName: String,
    @SerializedName("lastname")
    val lastName: String,
    val email: String,
    val birthDate: String,
    val address: Address
) {
    val initials: String
        get() = "$firstName $lastName".getInitials()
}

data class Address(
    val street: String,
    val suite: String? = "",
    val city: String,
    @SerializedName("zipcode")
    val zipCode: String,
    val geo: Geo
)

data class Geo(
    val lat: String,
    val lng: String
)

data class CommentWithUser(
    val comment: Comment,
    val user: User
)
