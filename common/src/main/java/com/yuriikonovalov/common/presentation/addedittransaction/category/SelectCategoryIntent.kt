package com.yuriikonovalov.common.presentation.addedittransaction.category

import com.yuriikonovalov.common.application.entities.category.Category

sealed class SelectCategoryIntent {
    data class ClickCategory(val category: Category) : SelectCategoryIntent()
    data class DeleteCategory(val id: Long) : SelectCategoryIntent()
    object ClickNegativeButton : SelectCategoryIntent()
    object ClickNewCategoryButton : SelectCategoryIntent()
}


