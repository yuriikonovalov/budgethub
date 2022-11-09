package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.category.Category

interface SaveCategorySource {
    suspend fun saveCategory(category: Category)
}