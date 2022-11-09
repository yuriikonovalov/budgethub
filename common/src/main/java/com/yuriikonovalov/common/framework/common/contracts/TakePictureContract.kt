package com.yuriikonovalov.common.framework.common.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.yuriikonovalov.common.framework.common.extentions.toContentUri
import java.io.File

/**
 * A custom ActivityResultContract for taking a picture using a camera app.
 *
 * The difference between this realization and the library's
 * [androidx.activity.result.contract.ActivityResultContracts.TakePicture]
 * is that the library's one returns Boolean result telling
 * if a taken image has been saved to the provided Uri or not.
 *
 * This one returns the accepted File back if a taken image has been saved or null if not.
 * Thus it's possible to use that File in the result callback.
 * If null is passed as the input argument, then the outputFile is a cache file.
 */
class TakePictureContract : ActivityResultContract<File?, File?>() {
    private var outputFile: File = createOutputFile()

    override fun createIntent(context: Context, input: File?): Intent {
        input?.let { outputFile = it }
        val uri = outputFile.toContentUri(context)

        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, uri)
    }


    override fun parseResult(resultCode: Int, intent: Intent?): File? {
        return if (resultCode == Activity.RESULT_OK) {
            outputFile
        } else {
            null
        }
    }

    private fun createOutputFile(): File {
        return File.createTempFile("camera_pic${System.currentTimeMillis()}", ".jpeg")
    }
}