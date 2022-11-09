package com.yuriikonovalov.common.framework.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yuriikonovalov.common.application.entities.category.CategoryType


@Entity(tableName = "categories")
data class CategoryDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: CategoryType,
    val name: String,
    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean,
    @ColumnInfo(name = "icon_path")
    val iconPath: String,
    val color: Int
)
