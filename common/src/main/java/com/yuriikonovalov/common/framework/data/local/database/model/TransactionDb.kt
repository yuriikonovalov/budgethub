package com.yuriikonovalov.common.framework.data.local.database.model

import androidx.room.*
import com.yuriikonovalov.common.application.entities.transaction.TransactionType
import java.time.OffsetDateTime

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryDb::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AccountDb::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("category_id"), Index("account_id")]
)
data class TransactionDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "category_id")
    val categoryId: Long?,
    @ColumnInfo(name = "account_id")
    val accountId: Long,
    val type: TransactionType,
    val note: String?,
    val date: OffsetDateTime,
    val amount: Double,
    @ColumnInfo(name = "image_path")
    val imagePath: String?
)
