package com.yuriikonovalov.common.framework.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagDb(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tag_id")
    val id: Long = 0,
    val name: String
)