package com.yuriikonovalov.common.framework.data.local.file

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.yuriikonovalov.common.data.local.TransactionImagesLocalDataSource
import com.yuriikonovalov.common.framework.common.DispatcherProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class TransactionImagesLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
) : TransactionImagesLocalDataSource {

    override suspend fun saveImage(originImageUri: Uri): String? {
        return withContext(dispatcherProvider.io) {
            try {
                val inputStream = context.contentResolver.openInputStream(originImageUri)
                val outputFile = createOutputFile()

                inputStream?.use { input ->
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                        // Returns the file uri as String. This will be saved in a DB row as the image path.
                        outputFile.toUri().toString()
                    }
                }
            } catch (e: Exception) {
                // Rethrow Coroutine Cancellation Exception, otherwise this suspend function can not be cancelled.
                if (e is CancellationException) {
                    throw e
                }
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun deleteImage(imageUri: Uri): Boolean {
        return withContext(dispatcherProvider.io) {
            try {
                imageUri.toFile().delete()
            } catch (e: Exception) {
                // Coroutine Cancellation Exception
                if (e is CancellationException) {
                    throw e
                }
                false
            }
        }
    }

    private fun createOutputFile(): File {
        val dir = context.getDir(DIR_NAME, Context.MODE_PRIVATE)
        return File(
            dir, "$NAME${System.currentTimeMillis()}.$FORMAT"
        )
    }

    companion object {
        const val DIR_NAME = "transaction_images"
        const val NAME = "transaction"
        const val FORMAT = "jpeg"
    }
}