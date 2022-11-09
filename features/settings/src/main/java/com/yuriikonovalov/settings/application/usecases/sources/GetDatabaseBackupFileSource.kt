package com.yuriikonovalov.settings.application.usecases.sources

import java.io.File

interface GetDatabaseBackupFileSource {
    suspend fun getBackupFile(): File?
}