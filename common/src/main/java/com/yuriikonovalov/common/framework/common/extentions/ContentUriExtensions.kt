package com.yuriikonovalov.common.framework.common.extentions

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.File


fun Uri.toTempFileFromContentUri(context: Context): File {
    val inputStream = context.contentResolver.openInputStream(this)
    val tempFile = File.createTempFile("temp_file${System.currentTimeMillis()}", null)

    inputStream?.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return tempFile
}

fun DocumentFile.writeFile(context: Context, file: File) {
    val contentSchemeUri = this.uri
    val outputStream = context.contentResolver.openOutputStream(contentSchemeUri)
    outputStream?.use { output ->
        file.inputStream().use { input ->
            input.copyTo(output)
        }
    }
}

fun File.toContentUri(context: Context): Uri {
    return FileProvider.getUriForFile(context, context.packageName + ".provider", this)
}
