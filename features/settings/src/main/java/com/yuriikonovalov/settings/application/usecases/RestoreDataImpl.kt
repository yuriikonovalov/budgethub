package com.yuriikonovalov.settings.application.usecases

import android.net.Uri
import com.yuriikonovalov.common.application.Resource
import com.yuriikonovalov.settings.application.usecases.sources.RestoreDataSource
import javax.inject.Inject

class RestoreDataImpl @Inject constructor(private val source: RestoreDataSource) : RestoreData {
    override suspend operator fun invoke(backupFileUri: Uri): Resource<Boolean> {
        val success = source.restoreData(backupFileUri)
        return Resource.Success(success)
    }
}