package com.yuriikonovalov.settings.data.sources

import android.net.Uri
import com.yuriikonovalov.common.data.local.DatabaseBackupLocalDataSource
import com.yuriikonovalov.settings.application.usecases.sources.RestoreDataSource
import javax.inject.Inject

class RestoreDataSourceImpl @Inject constructor(
    private val databaseBackupLocalDataSource: DatabaseBackupLocalDataSource
) : RestoreDataSource {
    override suspend fun restoreData(backupUri: Uri): Boolean {
        return databaseBackupLocalDataSource.restoreDatabaseBackup(backupUri)
    }
}