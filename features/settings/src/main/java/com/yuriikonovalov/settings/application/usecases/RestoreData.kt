package com.yuriikonovalov.settings.application.usecases

import android.net.Uri
import com.yuriikonovalov.common.application.Resource

interface RestoreData {
    suspend operator fun invoke(backupFileUri: Uri): Resource<Boolean>
}