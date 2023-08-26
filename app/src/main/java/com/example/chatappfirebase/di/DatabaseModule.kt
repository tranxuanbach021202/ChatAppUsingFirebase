package com.example.chatappfirebase.di

import com.example.chatappfirebase.data.repository.DatabaseRepository
import com.example.chatappfirebase.data.datasource.FirebaseDatabaseSource
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFirebaseSourceDatabase(database: DatabaseReference): FirebaseDatabaseSource {
        return FirebaseDatabaseSource(database)
    }


    @Provides
    @Singleton
    fun provideDatabaseRepository(databaseSource: FirebaseDatabaseSource, @IoDispatcher dispatcher: CoroutineDispatcher) : DatabaseRepository {
        return DatabaseRepository(databaseSource, dispatcher)
    }
}