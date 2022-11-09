package com.yuriikonovalov.common.framework.data.local.database.backup

val BACKUP_ZIP_NAME: String get() = "backup_db_zip${System.currentTimeMillis()}"
val BACKUP_CRYPTO_ZIP_NAME: String get() = "backup_db_${System.currentTimeMillis()}"
const val BACKUP_DB_NAME = "database_backup.db"
const val BACKUP_DB_SHM_NAME = "database_backup.db-shm"
const val BACKUP_DB_WAL_NAME = "database_backup.db-wal"
const val BACKUP_DB_VERSION = "database_backup_version.txt"
