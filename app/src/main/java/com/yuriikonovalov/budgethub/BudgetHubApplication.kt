package com.yuriikonovalov.budgethub

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.yuriikonovalov.common.framework.data.work.ClearCacheWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class BudgetHubApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        addClearCacheWork()
    }

    private fun addClearCacheWork() {
        val workRequest = PeriodicWorkRequestBuilder<ClearCacheWorker>(3, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            ClearCacheWorker.NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}