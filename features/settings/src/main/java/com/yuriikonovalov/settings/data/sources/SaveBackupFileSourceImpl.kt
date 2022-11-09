package com.yuriikonovalov.settings.data.sources

import android.net.Uri
import com.yuriikonovalov.common.data.local.DatabaseBackupLocalDataSource
import com.yuriikonovalov.settings.application.usecases.sources.SaveBackupFileSource
import java.io.File
import javax.inject.Inject

class SaveBackupFileSourceImpl @Inject constructor(
    private val databaseBackupLocalDataSource: DatabaseBackupLocalDataSource
) : SaveBackupFileSource {
    override suspend fun saveFile(treeUri: Uri, backupFile: File): Boolean {
        return databaseBackupLocalDataSource.saveFile(treeUri, backupFile)
    }
}