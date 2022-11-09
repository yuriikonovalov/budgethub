package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.category.Category
import kotlinx.coroutines.flow.Flow

interface GetExpenseCategoriesSource {
    fun getCategories(): Flow<List<Category>>
}