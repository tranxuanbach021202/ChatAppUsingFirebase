package com.example.chatappfirebase.data.repository

import com.example.chatappfirebase.constants.Constants
import com.example.chatappfirebase.data.datasource.FirebaseAuthSource
import com.example.chatappfirebase.di.IoDispatcher
import com.example.chatappfirebase.utils.RepoResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuthSource,
    @IoDispatcher val dispatcher: CoroutineDispatcher
) {

    suspend fun login(email : String, password: String) = withContext(dispatcher){
        when(val result = firebaseAuth.login(email, password)) {
            is RepoResult.Success -> {
                result.data
            }
            is RepoResult.Error -> {
                result.exception
            }

            is RepoResult.Loading -> {

            }
        }
    }

    suspend fun sigup(email: String, password: String) = withContext(dispatcher) {
        when(val result = firebaseAuth.sigup(email, password)) {
            is RepoResult.Success -> {
                result.data
            }
            is RepoResult.Error -> {
                result.exception
            }
            is RepoResult.Loading -> {
            }
        }
    }






    fun logout() {
        return firebaseAuth.logout()
    }
}