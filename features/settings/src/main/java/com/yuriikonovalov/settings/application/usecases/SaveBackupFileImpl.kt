package com.yuriikonovalov.settings.application.usecases

import android.net.Uri
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.settings.application.usecases.sources.SaveBackupFileSource
import java.io.File
import javax.inject.Inject

class SaveBackupFileImpl @Inject constructor(
    private val source: SaveBackupFileSource
) : SaveBackupFile {
    override suspend operator fun invoke(treeUri: Uri, backupFile: File): Resource<Boolean> {
        val saved = source.saveFile(treeUri, backupFile)
        return Resource.Success(saved)
    }
}