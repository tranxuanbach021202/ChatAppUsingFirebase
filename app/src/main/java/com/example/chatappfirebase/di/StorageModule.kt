package com.example.chatappfirebase.di

import com.example.chatappfirebase.data.datasource.FirebaseStoreSource
import com.example.chatappfirebase.data.repository.StorageRepository
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideFirebaseStoreSource(storageRef: StorageReference) : FirebaseStoreSource {
        return FirebaseStoreSource(storageRef)
    }

    @Provides
    @Singleton
    fun provideStorageRepository(storage: FirebaseStoreSource, @IoDispatcher dispatcher: CoroutineDispatcher) : StorageRepository {
        return StorageRepository(storage, dispatcher)
    }
}