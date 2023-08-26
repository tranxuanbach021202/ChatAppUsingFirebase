package com.example.chatappfirebase.data.datasource
import android.util.Log
import com.example.chatappfirebase.utils.RepoResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await


class FirebaseAuthSource(private val auth: FirebaseAuth) {

    suspend fun login(email : String, password: String) : RepoResult<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d("FirebaseSAuth", "aaa")
            RepoResult.Success(true)
        } catch (ex: Exception) {
            RepoResult.Error(ex)
        }
    }
    suspend fun sigup(email: String, password: String) : RepoResult<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            RepoResult.Success(true)
        } catch (ex : Exception) {
            RepoResult.Error(ex)
        }

    }


    fun logout() {
        auth.signOut()
    }
}