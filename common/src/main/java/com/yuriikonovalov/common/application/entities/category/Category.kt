package com.yuriikonovalov.common.application.entities.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Long,
    val type: CategoryType,
    val name: String,
    val isCustom: Boolean,
    val iconPath: String,
    val color: Int
) : Parcelable
