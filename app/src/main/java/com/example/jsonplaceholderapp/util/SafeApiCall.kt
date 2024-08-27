package com.example.jsonplaceholderapp.util

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T, ServiceError> {
    return try {
        Result.Success(apiCall())
    } catch (e: Exception) {
        Result.Error(ServiceError(e.message ?: ""))
    }
}

sealed class Result<out D, out E> {
    data class Success<out D>(val data: D) : Result<D, Nothing>()
    data class Error<out E>(val error: E) : Result<Nothing, E>()
}

data class ServiceError(val message: String, val code: Int? = null)