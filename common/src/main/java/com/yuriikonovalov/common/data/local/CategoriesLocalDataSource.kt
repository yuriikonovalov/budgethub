package com.yuriikonovalov.common.data.local

import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import kotlinx.coroutines.flow.Flow


interface CategoriesLocalDataSource {
    suspend fun saveCategory(category: Category):Long
    suspend fun deleteCategory(id: Long)
    suspend fun deleteAllCustom()
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>
}