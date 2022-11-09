package com.yuriikonovalov.common.framework.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yuriikonovalov.common.framework.data.local.database.converter.DateConverters
import com.yuriikonovalov.common.framework.data.local.database.dao.*
import com.yuriikonovalov.common.framework.data.local.database.model.*

@Database(
    entities = [
        AccountDb::class,
        CategoryDb::class,
        TagDb::class,
        TransactionDb::class,
        TransferDb::class,
        TransactionTagCrossRefDb::class,
        TransferTagCrossRefDb::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun tagDao(): TagDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transferDao(): TransferDao
}