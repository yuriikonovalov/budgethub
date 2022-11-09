package com.yuriikonovalov.common.framework.ui.addedittransaction.photo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yuriikonovalov.common.databinding.BottomSheetDialogAddPhotoBinding
import com.yuriikonovalov.common.framework.common.contracts.GetContentContract
import com.yuriikonovalov.common.framework.common.contracts.TakePictureContract
import com.yuriikonovalov.common.framework.common.extentions.collectEvent
import com.yuriikonovalov.common.framework.common.extentions.launchSafely
import com.yuriikonovalov.common.framework.common.extentions.resolveActivity
import com.yuriikonovalov.common.presentation.addedittransaction.photo.AddPhotoEvent
import com.yuriikonovalov.common.presentation.addedittransaction.photo.AddPhotoIntent
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AddPhotoBottomSheetDialog(
    private val uri: Uri? = null
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogAddPhotoBinding

    @Inject
    lateinit var viewModelFactory: AddPhotoViewModel.Factory
    private val viewModel: AddPhotoViewModel by viewModels {
        AddPhotoViewModel.provideFactory(viewModelFactory, uri)
    }
    private var listener: Listener? = null

    private val getPictureFromGalleryLauncher =
        registerForActivityResult(GetContentContract()) { file ->
            file?.let {
                lifecycleScope.launch {
                    val uri = it.toCompressedImageUri(requireContext())
                    viewModel.handleIntent(AddPhotoIntent.ChangePhoto(uri))
                }
            }
        }

    private val takePictureLauncher = registerForActivityResult(TakePictureContract()) { file ->
        file?.let {
            lifecycleScope.launch {
                val uri = it.toCompressedImageUri(requireContext())
                viewModel.handleIntent(AddPhotoIntent.ChangePhoto(uri))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogAddPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindNegativeButton()
        binding.bindPositiveButton()
        binding.bindCameraButton()
        binding.bindGalleryButton()
        binding.bindImage()
        binding.bindButtonGroups()
        collectEvents()
    }

    private suspend fun File.toCompressedImageUri(context: Context): Uri {
        val compressedImage = Compressor.compress(context, this) {
            default()
        }
        // Delete the raw image file as we won't use it any longer.
        this.delete()
        return compressedImage.toUri()
    }

    private fun BottomSheetDialogAddPhotoBinding.bindButtonGroups() {
        launchSafely {
            viewModel.stateFlow.map { it.imageUri }.distinctUntilChanged().collect { uri ->
                    if (uri == null) {
                        buttons.visibility = View.VISIBLE
                        buttonsSmall.visibility = View.GONE
                    } else {
                        buttons.visibility = View.GONE
                        buttonsSmall.visibility = View.VISIBLE
                    }
                }
        }
    }

    private fun BottomSheetDialogAddPhotoBinding.bindImage() {
        launchSafely {
            viewModel.stateFlow.map { it.imageUri }.distinctUntilChanged().collect { uri ->
                    if (uri == null) {
                        image.visibility = View.GONE
                    } else {
                        image.visibility = View.VISIBLE
                        image.load(uri)
                    }
                }
        }
    }

    private fun collectEvents() {
        collectEvent(viewModel.eventFlow, viewModel.eventConsumer) { event ->
            when (event) {
                is AddPhotoEvent.CameraButtonClick -> onCameraButtonClick()
                is AddPhotoEvent.GalleryButtonClick -> onGalleryButtonClicked()
                is AddPhotoEvent.PositiveButtonClick -> onPositiveButtonClicked(event.imageUri)
                is AddPhotoEvent.NegativeButtonClick -> onNegativeButtonClicked()
            }
        }
    }

    private fun onGalleryButtonClicked() {
        getPictureFromGalleryLauncher.launch("image/*")
    }

    private fun onCameraButtonClick() {
        takePictureLauncher.launch(null)
    }

    private fun onPositiveButtonClicked(imageUri: Uri) {
        listener?.onDialogPositiveClick(imageUri)
        dialog?.dismiss()
    }

    private fun onNegativeButtonClicked() {
        listener?.onDialogNegativeClick(null)
        dialog?.dismiss()
    }

    private fun BottomSheetDialogAddPhotoBinding.bindPositiveButton() {
        positiveButtonSmall.setOnClickListener {
            viewModel.handleIntent(AddPhotoIntent.ClickPositiveButton)
        }
    }

    private fun BottomSheetDialogAddPhotoBinding.bindNegativeButton() {
        negativeButtonSmall.setOnClickListener {
            viewModel.handleIntent(AddPhotoIntent.ClickNegativeButton)
        }
    }

    private fun BottomSheetDialogAddPhotoBinding.bindCameraButton() {
        cameraButton.setOnClickListener {
            viewModel.handleIntent(AddPhotoIntent.ClickCameraButton)
        }
        cameraButtonSmall.setOnClickListener {
            viewModel.handleIntent(AddPhotoIntent.ClickCameraButton)
        }

        val cameraActivityExist = requireContext().resolveActivity(INTENT_IMAGE_CAPTURE)
        val visibility = if (cameraActivityExist) View.VISIBLE else View.GONE
        cameraButton.visibility = visibility
        cameraButtonSmall.visibility = visibility
    }

    private fun BottomSheetDialogAddPhotoBinding.bindGalleryButton() {
        galleryButton.setOnClickListener {
            viewModel.handleIntent(AddPhotoIntent.ClickGalleryButton)
        }
        galleryButtonSmall.setOnClickListener {
            viewModel.handleIntent(AddPhotoIntent.ClickGalleryButton)
        }

        val galleryActivityExist = requireContext().resolveActivity(INTENT_GET_CONTENT_IMAGE)
        val visibility = if (galleryActivityExist) View.VISIBLE else View.GONE
        galleryButton.visibility = visibility
        galleryButtonSmall.visibility = visibility

    }

    fun setOnDialogClickListener(listener: Listener) {
        this.listener = listener
    }

    interface Listener {
        fun onDialogPositiveClick(imageUri: Uri)
        fun onDialogNegativeClick(imageUri: Uri?)
    }

    companion object {
        const val TAG = "AddPhotoBottomSheetDialog"

        val INTENT_GET_CONTENT_IMAGE =
            Intent(Intent.ACTION_GET_CONTENT).addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*")

        val INTENT_IMAGE_CAPTURE = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }
}