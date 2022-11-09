package com.yuriikonovalov.common.framework.data.local.database.backup

import android.content.Context
import android.net.Uri
import androidx.room.RoomDatabase
import com.yuriikonovalov.common.framework.common.extentions.toTempFileFromContentUri
import org.zeroturnaround.zip.ZipUtil
import java.io.File

class DatabaseBackupRestorer(private val context: Context, database: RoomDatabase) {
    private val currentVersion = database.openHelper.readableDatabase.version
    private val databaseName = database.openHelper.databaseName
    private val db: File get() = context.getDatabasePath(databaseName)
    private val dbShm: File get() = File(db.parent, "$databaseName-shm")
    private val dbWal: File get() = File(db.parent, "$databaseName-wal")

    fun restoreData(backupUri: Uri): Boolean {
        val backupFile = File(context.cacheDir, "temp_backup_file${System.currentTimeMillis()}")
        val backupDb = File(context.cacheDir, "temp_backup_db${System.currentTimeMillis()}.db")
        val backupDbShm =
            File(context.cacheDir, "temp_backup_db${System.currentTimeMillis()}.db-shm")
        val backupDbWal =
            File(context.cacheDir, "temp_backup_db${System.currentTimeMillis()}.db-wal")
        var backupCryptoFile: File? = null
        return try {
            backupCryptoFile = backupUri.toTempFileFromContentUri(context)
            DatabaseCryptoFile.decrypt(backupCryptoFile, backupFile)

            backupFile.throwIfNotValid()
            backupFile.throwIfVersionNotEqual()

            ZipUtil.unpackEntry(backupFile, BACKUP_DB_NAME, backupDb)
            ZipUtil.unpackEntry(backupFile, BACKUP_DB_SHM_NAME, backupDbShm)
            ZipUtil.unpackEntry(backupFile, BACKUP_DB_WAL_NAME, backupDbWal)

            backupDb.copyTo(db, overwrite = true)
            backupDbShm.copyTo(dbShm, overwrite = true)
            backupDbWal.copyTo(dbWal, overwrite = true)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            backupCryptoFile?.delete()
            backupFile.delete()
            backupDb.delete()
            backupDbShm.delete()
            backupDbWal.delete()
        }
    }

    private fun File.throwIfVersionNotEqual() {
        val backupDbVersion =
            File(context.cacheDir, "temp_backup_db_version${System.currentTimeMillis()}.txt")
        ZipUtil.unpackEntry(this, BACKUP_DB_VERSION, backupDbVersion)
        val backupVersion = backupDbVersion.readText().toInt()

        backupDbVersion.delete()

        if (backupVersion != currentVersion) {
            throw IllegalArgumentException(
                "The backup version($backupVersion) " +
                        "is not the same as the current version($currentVersion)."
            )
        }
    }

    private fun File.throwIfNotValid() {
        val fileValid = backupFileValid(this)
        if (!fileValid) {
            throw IllegalArgumentException("Zip file is not valid.")
        }
    }

    private fun backupFileValid(zipFile: File): Boolean {
        val containsVersionFile =
            ZipUtil.containsEntry(zipFile, BACKUP_DB_VERSION)
        val containsDbFile = ZipUtil.containsEntry(zipFile, BACKUP_DB_NAME)
        val containsDbShmFile =
            ZipUtil.containsEntry(zipFile, BACKUP_DB_SHM_NAME)
        val containsDbWalFile =
            ZipUtil.containsEntry(zipFile, BACKUP_DB_WAL_NAME)
        return containsVersionFile && containsDbFile && containsDbShmFile && containsDbWalFile
    }
}