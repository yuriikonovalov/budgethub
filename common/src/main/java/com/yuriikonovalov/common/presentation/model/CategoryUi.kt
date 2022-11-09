package com.yuriikonovalov.common.presentation.model

import android.content.Context
import androidx.core.graphics.ColorUtils
import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType


data class CategoryUi(
    val type: CategoryType,
    val name: String,
    val iconPath: String?,
    val colorIcon: Int,
    val colorBackground: Int,
) {
    companion object {
        fun from(context: Context, category: Category): CategoryUi {
            return CategoryUi(
                name = NameMapperUi.mapName(context, category.name),
                type = category.type,
                iconPath = category.iconPath,
                colorIcon = category.colorIconUi(),
                colorBackground = category.colorBackgroundUi()
            )
        }

        fun Category.colorIconUi(): Int {
            return this.color
        }

        fun Category.colorBackgroundUi(): Int {
            return ColorUtils.setAlphaComponent(color, 50)
        }
    }

}

