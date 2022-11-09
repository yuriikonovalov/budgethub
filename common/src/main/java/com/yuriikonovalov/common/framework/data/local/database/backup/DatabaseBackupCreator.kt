package com.yuriikonovalov.common.framework.data.local.database.backup

import android.content.Context
import androidx.room.RoomDatabase
import org.zeroturnaround.zip.ZipUtil
import java.io.File

class DatabaseBackupCreator(private val context: Context, database: RoomDatabase) {
    private val currentVersion = database.openHelper.readableDatabase.version.toString()
    private val databaseName = database.openHelper.databaseName
    private val db: File get() = context.getDatabasePath(databaseName)
    private val dbShm: File get() = File(db.parent, "$databaseName-shm")
    private val dbWal: File get() = File(db.parent, "$databaseName-wal")


    fun getBackupFile(): File? {
        val dbVersion = File(context.cacheDir, BACKUP_DB_VERSION).apply {
            writeText(currentVersion)
        }
        val backupDb = File(context.cacheDir, BACKUP_DB_NAME)
        val backupDbShm = File(context.cacheDir, BACKUP_DB_SHM_NAME)
        val backupDbWal = File(context.cacheDir, BACKUP_DB_WAL_NAME)
        val backupZip = File(context.cacheDir, BACKUP_ZIP_NAME)

        return try {
            db.copyTo(backupDb, overwrite = true)
            dbShm.copyTo(backupDbShm, overwrite = true)
            dbWal.copyTo(backupDbWal, overwrite = true)

            val entries = arrayOf(dbVersion, backupDb, backupDbShm, backupDbWal)
            ZipUtil.packEntries(entries, backupZip)

            val cryptoBackupZip = File(context.cacheDir, BACKUP_CRYPTO_ZIP_NAME)
            DatabaseCryptoFile.encrypt(backupZip, cryptoBackupZip)

            // Backup file
            cryptoBackupZip
        } catch (e: Exception) {
            null
        } finally {
            dbVersion.delete()
            backupDb.delete()
            backupDbShm.delete()
            backupDbWal.delete()
            backupZip.delete()
        }
    }
}