package com.yuriikonovalov.common.framework.data.local.file

import android.content.Context
import android.net.Uri
import com.yuriikonovalov.common.data.local.DatabaseBackupLocalDataSource
import com.yuriikonovalov.common.framework.common.DispatcherProvider
import com.yuriikonovalov.common.framework.data.local.database.AppDatabase
import com.yuriikonovalov.common.framework.data.local.database.backup.DatabaseBackupCreator
import com.yuriikonovalov.common.framework.data.local.database.backup.DatabaseBackupRestorer
import com.yuriikonovalov.common.framework.data.local.database.backup.DatabaseBackupSaver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class DatabaseBackupLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val dispatcherProvider: DispatcherProvider
) : DatabaseBackupLocalDataSource {
    override suspend fun getBackupFile(): File? {
        return withContext(dispatcherProvider.default) {
            DatabaseBackupCreator(context, database).getBackupFile()
        }
    }

    override suspend fun restoreDatabaseBackup(backupUri: Uri): Boolean {
        return withContext(dispatcherProvider.default) {
            DatabaseBackupRestorer(context, database).restoreData(backupUri)
        }
    }

    override suspend fun saveFile(treeUri: Uri, backupFile: File): Boolean {
        return withContext(dispatcherProvider.default) {
            DatabaseBackupSaver(context).saveFile(treeUri, backupFile)
        }
    }
}