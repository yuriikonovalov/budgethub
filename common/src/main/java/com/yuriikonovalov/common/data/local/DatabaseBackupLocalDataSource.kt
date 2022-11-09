package com.yuriikonovalov.common.data.local

import android.net.Uri
import java.io.File

interface DatabaseBackupLocalDataSource {
    suspend fun getBackupFile(): File?
    suspend fun restoreDatabaseBackup(backupUri: Uri): Boolean
    suspend fun saveFile(treeUri: Uri, backupFile: File): Boolean
}