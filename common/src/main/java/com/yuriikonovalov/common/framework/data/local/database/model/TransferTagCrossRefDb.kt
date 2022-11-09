package com.yuriikonovalov.common.framework.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(
    tableName = "transfer_tag_cross_refs", primaryKeys = ["id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = TransferDb::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagDb::class,
            parentColumns = ["tag_id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransferTagCrossRefDb(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "tag_id")
    val tagId: Long
)