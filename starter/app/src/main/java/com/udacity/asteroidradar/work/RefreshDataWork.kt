package com.udacity.asteroidradar.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRadarRepository
import retrofit2.HttpException

class RefreshDataWork(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWork"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidsRadarRepository(database)

        return try {
            repository.removePreviousDateAsteroids()
            repository.refreshAsteroids()
            repository.refreshImageOfTheDay()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}