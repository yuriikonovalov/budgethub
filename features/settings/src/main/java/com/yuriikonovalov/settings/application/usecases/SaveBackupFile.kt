package com.yuriikonovalov.settings.application.usecases

import android.net.Uri
import com.yuriikonovalov.common.application.Resource
import java.io.File

interface SaveBackupFile {
    suspend operator fun invoke(treeUri: Uri, backupFile: File): Resource<Boolean>
}