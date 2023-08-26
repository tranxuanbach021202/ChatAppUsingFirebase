package com.example.chatappfirebase.data.repository

import android.net.Uri
import com.example.chatappfirebase.data.datasource.FirebaseStoreSource
import com.example.chatappfirebase.di.IoDispatcher
import com.example.chatappfirebase.utils.RepoResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val storageSource: FirebaseStoreSource,
    @IoDispatcher val dispatcher: CoroutineDispatcher
){
    suspend fun uploadImageToFirebase(uri: Uri)  = withContext(dispatcher) {
        when(val result = storageSource.uploadImageToFirebase(uri)) {
            is RepoResult.Success -> {
                RepoResult.Success(result.data)
            }

            is RepoResult.Error -> {
                RepoResult.Error(result.exception)
            }

            is RepoResult.Loading -> {
                RepoResult.Loading
            }
        }

    }
}