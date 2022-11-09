package com.yuriikonovalov.common.framework.data.local.database.model

import androidx.room.*
import java.time.OffsetDateTime

@Entity(
    tableName = "transfers", foreignKeys = [
        ForeignKey(
            entity = AccountDb::class,
            parentColumns = ["id"],
            childColumns = ["sender_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountDb::class,
            parentColumns = ["id"],
            childColumns = ["recipient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ], indices = [Index("sender_id"), Index("recipient_id")]
)
data class TransferDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "sender_id")
    val accountFromId: Long,
    @ColumnInfo(name = "recipient_id")
    val accountToId: Long,
    val note: String?,
    val date: OffsetDateTime,
    val amountFrom: Double,
    val amountTo: Double
)
