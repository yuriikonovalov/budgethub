package com.yuriikonovalov.common.presentation.addedittransaction.photo

import android.net.Uri

sealed class AddPhotoEvent {
    data class PositiveButtonClick(val imageUri: Uri) : AddPhotoEvent()
    object NegativeButtonClick : AddPhotoEvent()
    object CameraButtonClick : AddPhotoEvent()
    object GalleryButtonClick : AddPhotoEvent()
}