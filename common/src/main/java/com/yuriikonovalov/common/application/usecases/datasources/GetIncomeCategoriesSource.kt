package com.yuriikonovalov.common.application.usecases.datasources

import com.yuriikonovalov.common.application.entities.category.Category
import kotlinx.coroutines.flow.Flow

interface GetIncomeCategoriesSource {
    fun getCategories(): Flow<List<Category>>
}