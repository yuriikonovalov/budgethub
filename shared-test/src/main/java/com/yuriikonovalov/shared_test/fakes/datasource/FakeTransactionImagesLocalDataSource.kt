package com.yuriikonovalov.shared_test.fakes.datasource

import android.net.Uri
import com.yuriikonovalov.common.data.local.TransactionImagesLocalDataSource

class FakeTransactionImagesLocalDataSource : TransactionImagesLocalDataSource {
    override suspend fun saveImage(originImageUri: Uri): String? {
        return null
    }

    override suspend fun deleteImage(imageUri: Uri): Boolean {
        return true
    }
}