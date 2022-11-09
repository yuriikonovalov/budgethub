package com.yuriikonovalov.common.presentation.newcategory

import com.yuriikonovalov.common.application.entities.Icon
import com.yuriikonovalov.common.application.entities.category.CategoryType

sealed class NewCategoryIntent {
    data class ChangeType(val type: CategoryType) : NewCategoryIntent()
    data class ChangeName(val input: String) : NewCategoryIntent()
    data class ChangeIcon(val icon: Icon) : NewCategoryIntent()
    data class ChangeColor(val color: Int) : NewCategoryIntent()
    data class AddColor(val color: Int) : NewCategoryIntent()
    object ClickSaveButton : NewCategoryIntent()
    object ClickColorButton : NewCategoryIntent()
}

