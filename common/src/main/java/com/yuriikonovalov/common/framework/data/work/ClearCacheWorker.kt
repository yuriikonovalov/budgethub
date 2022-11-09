package com.yuriikonovalov.common.framework.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class ClearCacheWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(applicationContext, workerParams) {
    override fun doWork(): Result {
        return try {
            clearCache(applicationContext.cacheDir)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun clearCache(dir: File) {
        when {
            dir.isDirectory -> {
                dir.list()?.forEach {
                    clearCache(File(dir, it))
                }
                dir.delete()
            }
            dir.isFile -> {
                dir.delete()
            }
        }
    }

    companion object {
        const val NAME = "ClearCacheWorker"
    }
}