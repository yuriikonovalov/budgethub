package com.yuriikonovalov.common.presentation.addedittransaction.photo

import android.net.Uri

data class AddPhotoState(
    val imageUri: Uri? = null
) {
    fun updateImageUri(imageUri: Uri?): AddPhotoState {
        return copy(imageUri = imageUri)
    }
}
