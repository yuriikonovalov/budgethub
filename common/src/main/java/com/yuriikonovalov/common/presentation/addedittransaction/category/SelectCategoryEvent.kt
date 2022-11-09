package com.yuriikonovalov.common.presentation.addedittransaction.category

import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType

sealed class SelectCategoryEvent {
    object NavigateBack : SelectCategoryEvent()
    data class NewCategoryButtonClick(val type: CategoryType) : SelectCategoryEvent()
    data class CategoryClick(val category: Category) : SelectCategoryEvent()
}