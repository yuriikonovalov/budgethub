package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.entities.category.CategoryType
import com.yuriikonovalov.common.application.usecases.datasources.GetIncomeCategoriesSource
import com.yuriikonovalov.common.data.local.CategoriesLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIncomeCategoriesSourceImpl @Inject constructor(
    private val categoriesSourceLocal: CategoriesLocalDataSource
) : GetIncomeCategoriesSource {
    override fun getCategories(): Flow<List<Category>> {
        return categoriesSourceLocal.getCategoriesByType(CategoryType.INCOME)
    }
}