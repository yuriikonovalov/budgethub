package com.yuriikonovalov.settings.application.usecases

import com.yuriikonovalov.common.application.Resource
import java.io.File

interface GetDatabaseBackupFile {
    suspend operator fun invoke(): Resource<File>
}