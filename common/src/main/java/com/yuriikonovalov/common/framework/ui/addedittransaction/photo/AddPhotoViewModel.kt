package com.yuriikonovalov.common.framework.ui.addedittransaction.photo

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yuriikonovalov.common.presentation.addedittransaction.photo.AddPhotoIntent
import com.yuriikonovalov.common.presentation.addedittransaction.photo.AddPhotoState
import com.yuriikonovalov.common.presentation.addedittransaction.photo.AddPhotoEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*


class AddPhotoViewModel @AssistedInject constructor(
    @Assisted private val uri: Uri?
) : ViewModel() {
    private val initialState = AddPhotoState()
    private val _stateFlow = MutableStateFlow(initialState)
    private val currentState get() = stateFlow.value
    private val _eventFlow = MutableStateFlow<AddPhotoEvent?>(null)
    val stateFlow get() = _stateFlow.asStateFlow()
    val eventFlow get() = _eventFlow.asStateFlow()
    val eventConsumer: () -> Unit = { _eventFlow.value = null }

    init {
        _stateFlow.update { it.updateImageUri(uri) }
    }

    fun handleIntent(intent: AddPhotoIntent) {
        when (intent) {
            is AddPhotoIntent.ChangePhoto -> onChangePhoto(intent.imageUri)
            is AddPhotoIntent.ClickCameraButton -> onClickCameraButton()
            is AddPhotoIntent.ClickGalleryButton -> onClickGalleryButton()
            is AddPhotoIntent.ClickPositiveButton -> onClickPositiveButton()
            is AddPhotoIntent.ClickNegativeButton -> onClickNegativeButton()
        }
    }

    private fun onChangePhoto(uri: Uri?) {
        _stateFlow.update { it.updateImageUri(uri) }
    }

    private fun onClickCameraButton() {
        _eventFlow.value = AddPhotoEvent.CameraButtonClick
    }

    private fun onClickGalleryButton() {
        _eventFlow.value = AddPhotoEvent.GalleryButtonClick
    }

    private fun onClickPositiveButton() {
        _eventFlow.value = AddPhotoEvent.PositiveButtonClick(currentState.imageUri!!)
    }

    private fun onClickNegativeButton() {
        _eventFlow.value = AddPhotoEvent.NegativeButtonClick
    }

    @AssistedFactory
    interface Factory {
        fun create(uri: Uri?): AddPhotoViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            uri: Uri?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(uri) as T
            }
        }
    }
}