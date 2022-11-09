package com.yuriikonovalov.common.data.local

import android.net.Uri

interface TransactionImagesLocalDataSource {
    /**
     * @return String - a saved file uri as String or null.
     */
    suspend fun saveImage(originImageUri: Uri): String?
    suspend fun deleteImage(imageUri: Uri): Boolean
}