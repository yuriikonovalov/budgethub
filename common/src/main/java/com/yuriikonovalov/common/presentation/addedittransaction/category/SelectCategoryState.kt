package com.yuriikonovalov.common.presentation.addedittransaction.category

import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType

data class SelectCategoryState(
    val type: CategoryType = CategoryType.EXPENSE,
    val categories: List<Category> = emptyList()
) {
    fun updateCategories(categories: List<Category>): SelectCategoryState {
        return copy(categories = categories)
    }

    fun updateType(type: CategoryType): SelectCategoryState {
        return copy(type = type)
    }
}