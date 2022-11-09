package com.yuriikonovalov.settings.application.usecases.sources

import android.net.Uri
import java.io.File

interface SaveBackupFileSource {
    suspend fun saveFile(treeUri: Uri, backupFile: File): Boolean
}