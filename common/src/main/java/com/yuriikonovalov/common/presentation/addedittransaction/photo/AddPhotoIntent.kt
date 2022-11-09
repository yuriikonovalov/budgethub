package com.yuriikonovalov.common.presentation.addedittransaction.photo

import android.net.Uri

sealed class AddPhotoIntent {
    data class ChangePhoto(val imageUri: Uri?) : AddPhotoIntent()
    object ClickPositiveButton : AddPhotoIntent()
    object ClickNegativeButton : AddPhotoIntent()
    object ClickCameraButton : AddPhotoIntent()
    object ClickGalleryButton : AddPhotoIntent()
}

