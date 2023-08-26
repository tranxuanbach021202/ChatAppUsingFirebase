package com.example.chatappfirebase.worker

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.chatappfirebase.data.repository.DatabaseRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateStatusWorker @AssistedInject constructor(@Assisted context: Context, @Assisted parameters: WorkerParameters,
    private val databaseRepository: DatabaseRepository) : CoroutineWorker(context, parameters){

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        val userId = inputData.getString("userId")
        val status = inputData.getString("status")

        Log.d("Worker", "Worker status")
        databaseRepository.updateStatusUser(userId!!, status!!)
        return Result.Success()
    }


}