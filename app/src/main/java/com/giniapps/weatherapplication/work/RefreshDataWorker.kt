package com.giniapps.weatherapplication.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.giniapps.weatherapplication.service.SharedLocationManager
import com.giniapps.weatherapplication.repository.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted params: WorkerParameters,
    val repository: WeatherRepository,
    private val sharedLocationManager: SharedLocationManager,

    ) : CoroutineWorker(appContext, params) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun doWork(): Result {
        try {
            sharedLocationManager.locationFlow().take(1).collectLatest {
                repository.refreshCache(it)

            }
        } catch (e: Throwable) {
            return Result.retry()
        }
        return Result.success()

    }

    companion object {
        const val WORK_NAME = "com.giniapps.weatherapplication.work.RefreshDataWorker"
    }
}
