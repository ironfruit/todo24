package com.wesleyfranks.todo24.util

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.withContext

class TaskTimeChecker(appContext: Context, workerParams: WorkerParameters):
        CoroutineWorker(appContext, workerParams) {

    companion object{
        private const val TAG:String = "TaskTimeChecker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        //checkTime()

        return@withContext try {
            // do something
            Result.success()
        } catch (error: Throwable){
            Log.e(TAG, "doWork: $error")
            Result.failure()
        }

    }


}