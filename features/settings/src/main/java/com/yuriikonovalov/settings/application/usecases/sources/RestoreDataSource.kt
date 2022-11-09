package com.yuriikonovalov.settings.application.usecases.sources

import android.net.Uri

interface RestoreDataSource {
    suspend fun restoreData(backupUri: Uri): Boolean
}