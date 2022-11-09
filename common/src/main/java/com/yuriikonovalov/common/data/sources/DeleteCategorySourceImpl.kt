package com.yuriikonovalov.common.data.sources

import com.yuriikonovalov.common.application.usecases.datasources.DeleteCategorySource
import com.yuriikonovalov.common.data.local.CategoriesLocalDataSource
import javax.inject.Inject

class DeleteCategorySourceImpl @Inject constructor(
    private val categoriesLocalDataSource: CategoriesLocalDataSource
) : DeleteCategorySource {
    override suspend fun deleteCategory(id: Long) {
        categoriesLocalDataSource.deleteCategory(id)
    }

}