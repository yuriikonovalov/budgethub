package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.entities.category.Category
import com.yuriikonovalov.common.application.usecases.datasources.SaveCategorySource
import com.yuriikonovalov.common.data.local.CategoriesLocalDataSource
import javax.inject.Inject

class SaveCategorySourceImpl @Inject constructor(
    private val categoriesSourceLocal: CategoriesLocalDataSource
) : SaveCategorySource {
    override suspend fun saveCategory(category: Category) {
        categoriesSourceLocal.saveCategory(category)
    }
}