package com.example.chatappfirebase.di

import com.example.chatappfirebase.data.repository.AuthRepository
import com.example.chatappfirebase.data.datasource.FirebaseAuthSource
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {


    @Provides
    fun provideFirebaseSourceAuth(auth: FirebaseAuth) : FirebaseAuthSource{
        return FirebaseAuthSource(auth)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(authSource: FirebaseAuthSource, @IoDispatcher dispatcher: CoroutineDispatcher) : AuthRepository {
        return AuthRepository(authSource, dispatcher)
    }

}