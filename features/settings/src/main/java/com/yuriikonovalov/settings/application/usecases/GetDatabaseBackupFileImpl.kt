package com.yuriikonovalov.settings.application.usecases

import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.settings.application.usecases.sources.GetDatabaseBackupFileSource
import java.io.File
import javax.inject.Inject

class GetDatabaseBackupFileImpl @Inject constructor(
    private val source: GetDatabaseBackupFileSource
) : GetDatabaseBackupFile {
    override suspend operator fun invoke(): Resource<File> {
        val file = source.getBackupFile()
        return Resource.successIfNotNull(file)
    }
}