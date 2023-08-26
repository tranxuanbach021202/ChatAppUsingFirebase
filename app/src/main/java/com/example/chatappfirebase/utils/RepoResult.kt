package com.example.chatappfirebase.utils

import kotlin.Exception


sealed class RepoResult<out T: Any> {
    object Loading : RepoResult<Nothing>()
    data class Success<out T: Any>(val data: T) : RepoResult<T>()
    data class Error(val exception: Exception) : RepoResult<Nothing>()

    override fun toString(): String = when (this) {
        is Loading -> "Loading"
        is Success -> "Success[data=$data]"
        is Error -> "Error[exception=$exception]"
    }
}
