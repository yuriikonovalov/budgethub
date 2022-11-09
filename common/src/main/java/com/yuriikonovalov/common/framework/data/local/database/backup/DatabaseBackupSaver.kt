package com.yuriikonovalov.common.framework.data.local.database.backup

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.yuriikonovalov.common.framework.common.extentions.writeFile
import java.io.File

class DatabaseBackupSaver(private val context: Context) {
    /**
     * @param treeUri A directory to save file.
     * @param backupFile A file to be saved.
     *
     * @return true - if saving is successful, otherwise - false.
     */
    fun saveFile(treeUri: Uri, backupFile: File): Boolean {
        return try {
            val documentFile = DocumentFile.fromTreeUri(context, treeUri)!!
            val newDocumentFile = documentFile.createFile("", backupFile.name)!!
            newDocumentFile.writeFile(context, backupFile)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            backupFile.delete()
        }
    }
}