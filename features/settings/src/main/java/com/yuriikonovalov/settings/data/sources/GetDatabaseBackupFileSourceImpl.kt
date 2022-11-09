package com.yuriikonovalov.settings.data.sources

import com.yuriikonovalov.common.data.local.DatabaseBackupLocalDataSource
import com.yuriikonovalov.settings.application.usecases.sources.GetDatabaseBackupFileSource
import java.io.File
import javax.inject.Inject

class GetDatabaseBackupFileSourceImpl @Inject constructor(
    private val databaseBackupLocalDataSource: DatabaseBackupLocalDataSource
) : GetDatabaseBackupFileSource {
    override suspend fun getBackupFile(): File? {
        return databaseBackupLocalDataSource.getBackupFile()
    }
}